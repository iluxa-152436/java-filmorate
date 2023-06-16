package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidateFilmException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.yandex.practicum.filmorate.Constants.FIRST_RELEASE_DATE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private DirectorService directorService;

    @Test
    void createFilm() {
        HttpEntity<Film> request = new HttpEntity<>(prepareFilmObj());
        Film film = restTemplate.postForObject("http://localhost:" + port + "/films", request, Film.class);
        Assertions.assertNotNull(film);
        assertEquals(1, film.getId());
        assertEquals("name", film.getName());
        assertEquals("description", film.getDescription());
        assertEquals(LocalDate.now(), film.getReleaseDate());
        assertEquals(30, film.getDuration());
    }

    @Test
    void createFilmEmptyName() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "",
                "description",
                LocalDate.now(),
                30,
                null,
                null,
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createFilmBadDescriptionLengthMoreThaMaxFilmDescriptionLength() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "name",
                "descriptiondescriptiondescriptiondescriptiondescri"
                        + "ptiondescriptiondescriptiondescriptiondescription1"
                        + "descriptiondescriptiondescriptiondescriptiondescri"
                        + "ptiondescriptiondescriptiondescriptiondescription1"
                        + "d",
                LocalDate.now(),
                30,
                null,
                null,
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createFilmDescriptionLengthEqualsMaxFilmDescriptionLength() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "name",
                "descriptiondescriptiondescriptiondescriptiondescri"
                        + "ptiondescriptiondescriptiondescriptiondescription1"
                        + "descriptiondescriptiondescriptiondescriptiondescri"
                        + "ptiondescriptiondescriptiondescriptiondescription1",
                LocalDate.now(),
                30,
                null,
                null,
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void createFilmBadReleaseDate() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "name",
                "description",
                FIRST_RELEASE_DATE.minusDays(1),
                30,
                null,
                null,
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createFilmReleaseDate() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "name",
                "description",
                FIRST_RELEASE_DATE,
                30,
                null,
                null,
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void createFilmNegativeDuration() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "name",
                "description",
                FIRST_RELEASE_DATE,
                -1,
                null,
                null,
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createFilmZeroDuration() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "name",
                "description",
                FIRST_RELEASE_DATE,
                0,
                null,
                null,
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void getFilms() throws ValidateFilmException {
        Film film = prepareFilmObj();
        filmService.createFilm(film);
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:" + port + "/films",
                Film[].class);
        Film[] films = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(films);
        assertEquals(new Film(1,
                        "name",
                        "description",
                        LocalDate.now(),
                        30,
                        Collections.emptySet(),
                        new MpaRating(1, "G"),
                        null),
                films[0]);
    }

    private static Film prepareFilmObj() {
        return new Film(1,
                "name",
                "description",
                LocalDate.now(),
                30,
                null,
                new MpaRating(1, "G"),
                null);
    }

    private static Film prepareFilmObjWithGenreAndYear() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(2, "Драма");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);
        return new Film(1,
                "name",
                "description",
                LocalDate.of(1999, 04, 20),
                30,
                genres,
                new MpaRating(1, "G"),
                null);
    }

    @Test
    void updateFilm() throws ValidateFilmException {
        filmService.createFilm(prepareFilmObj());
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "update",
                "update",
                LocalDate.now(),
                30,
                null,
                null,
                null));
        Film updatedFilm = restTemplate.exchange("http://localhost:" + port + "/films/",
                HttpMethod.PUT,
                request,
                Film.class).getBody();
        System.out.println(updatedFilm);
        Assertions.assertNotNull(updatedFilm);
        assertEquals(1, updatedFilm.getId());
        assertEquals("update", updatedFilm.getName());
        assertEquals("update", updatedFilm.getDescription());
        assertEquals(LocalDate.now(), updatedFilm.getReleaseDate());
        assertEquals(30, updatedFilm.getDuration());
    }

    @Test
    void addLike() {
        Film film = prepareFilmObj();
        filmService.createFilm(film);
        User user = getUser();
        userService.createUser(user);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/films/1/like/1",
                HttpMethod.PUT,
                null,
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deleteLike() {
        Film film = prepareFilmObj();
        filmService.createFilm(film);
        User user = getUser();
        userService.createUser(user);
        likeService.addLike(new Like(1, 1));
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/films/1/like/1",
                HttpMethod.DELETE,
                null,
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deleteLikeNegative() {
        User user = getUser();
        userService.createUser(user);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/films/1/like/1",
                HttpMethod.DELETE,
                null,
                String.class);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getPopularFilms() {
        Film film1 = prepareFilmObj();
        filmService.createFilm(film1);
        Film film2 = prepareFilmObj();
        filmService.createFilm(film2);
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        likeService.addLike(new Like(1, 1));
        likeService.addLike(new Like(1, 2));
        likeService.addLike(new Like(2, 1));
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:" + port + "/films/popular",
                Film[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Film[] films = response.getBody();
        assertEquals(2, films.length);
        assertEquals(1, films[0].getId());
    }

    @Test
    void getFilmsOfDirectorById() {
        Director director = new Director(1, "Dir");
        directorService.createDirector(director);
        Film film = new Film(1, "Name", "Des", LocalDate.of(2010, 5, 10),
                150, null, new MpaRating(1, "G"), Set.of(director));

        restTemplate.put("http://localhost:" + port +
                "/1/like/1", Integer.class);

        Film film2 = new Film(2, "Name2", "Descr", LocalDate.of(2000, 5, 10),
                150, null, new MpaRating(1, "G"), Set.of(director));
        filmService.createFilm(film);
        filmService.createFilm(film2);

        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:" + port +
                "/films/director/1?sortBy=year", Film[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Film[] films = response.getBody();
        assertEquals(1, films[1].getId());
        assertEquals(2, films[0].getId());

        ResponseEntity<Film[]> response2 = restTemplate.getForEntity("http://localhost:" + port +
                "/films/director/1?sortBy=likes", Film[].class);
        assertTrue(response2.getStatusCode().is2xxSuccessful());
        Film[] films2 = response2.getBody();
        assertEquals(1, films2[0].getId());
        assertEquals(2, films2[1].getId());
    }

    @Test
    void getPopularFilmsFilterByYear() {
        Film film1 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film1);
        Film film2 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film2);
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        likeService.addLike(new Like(1, 1));
        likeService.addLike(new Like(1, 2));
        likeService.addLike(new Like(2, 1));
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:" + port + "/films/popular?year=1999",
                Film[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Film[] films = response.getBody();
        assertEquals(2, films.length);
        assertEquals(1, films[0].getId());
    }

    @Test
    void getPopularFilmsFilterByGenre() {
        Film film1 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film1);
        Film film2 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film2);
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        likeService.addLike(new Like(1, 1));
        likeService.addLike(new Like(1, 2));
        likeService.addLike(new Like(2, 1));
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:"
                        + port
                        + "/films/popular?genreId=2",
                Film[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Film[] films = response.getBody();
        assertEquals(2, films.length);
        assertEquals(1, films[0].getId());
    }

    @Test
    void getPopularFilmsFilterByGenreEmptyResult() {
        Film film1 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film1);
        Film film2 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film2);
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        likeService.addLike(new Like(1, 1));
        likeService.addLike(new Like(1, 2));
        likeService.addLike(new Like(2, 1));
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:"
                        + port
                        + "/films/popular?genreId=3",
                Film[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Film[] films = response.getBody();
        assertEquals(0, films.length);
    }

    @Test
    void getPopularFilmsFilterByGenreAndYear() {
        Film film1 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film1);
        Film film2 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film2);
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        likeService.addLike(new Like(1, 1));
        likeService.addLike(new Like(1, 2));
        likeService.addLike(new Like(2, 1));
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:"
                        + port
                        + "/films/popular?genreId=2&year=1999",
                Film[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Film[] films = response.getBody();
        assertEquals(2, films.length);
        assertEquals(1, films[0].getId());
    }

    @Test
    void getPopularFilmsFilterByYearEmptyResult() {
        Film film1 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film1);
        Film film2 = prepareFilmObjWithGenreAndYear();
        filmService.createFilm(film2);
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        likeService.addLike(new Like(1, 1));
        likeService.addLike(new Like(1, 2));
        likeService.addLike(new Like(2, 1));
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:"
                        + port
                        + "/films/popular?year=2000",
                Film[].class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Film[] films = response.getBody();
        assertEquals(0, films.length);
    }

    @Test
    void deleteFilmByIdShouldDeleteFilm() {
        Film film1 = prepareFilmObj();
        filmService.createFilm(film1);
        Film film2 = prepareFilmObj();
        filmService.createFilm(film2);

        Optional<Integer> optionalUserSize = Optional.of(filmService.getFilms().size());

        assertThat(optionalUserSize).isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(2));
        filmService.deleteFilmById(1);
        optionalUserSize = Optional.of(filmService.getFilms().size());
        assertThat(optionalUserSize).isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(1));

    }

    private static User getUser() {
        return new User(1,
                "abc@abc.ru",
                "login",
                "name",
                LocalDate.of(1989, 4, 13));
    }
}
