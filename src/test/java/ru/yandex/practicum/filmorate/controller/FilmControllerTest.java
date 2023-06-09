package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.*;
import static ru.yandex.practicum.filmorate.Constants.FIRST_RELEASE_DATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidateFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
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

    @Test
    void createFilm() {
        HttpEntity<Film> request = new HttpEntity<>(getFilmObj());
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
                null));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void getFilms() throws ValidateFilmException {
        Film film = getFilmObj();
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
                        new MpaRating(1, "G")),
                films[0]);
    }

    private static Film getFilmObj() {
        return new Film(1,
                "name",
                "description",
                LocalDate.now(),
                30,
                null,
                new MpaRating(1, "G"));
    }

    @Test
    void updateFilm() throws ValidateFilmException {
        filmService.createFilm(getFilmObj());
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "update",
                "update",
                LocalDate.now(),
                30,
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
        Film film = getFilmObj();
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
        Film film = getFilmObj();
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
        Film film1 = getFilmObj();
        filmService.createFilm(film1);
        Film film2 = getFilmObj();
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

    private static User getUser() {
        return new User(1,
                "abc@abc.ru",
                "login",
                "name",
                LocalDate.of(1989, 4, 13));
    }
}
