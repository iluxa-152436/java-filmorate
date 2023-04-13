package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.*;
import static ru.yandex.practicum.filmorate.Constants.FIRST_RELEASE_DATE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class FilmControllerTest {
    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FilmService filmService;

    @Test
    void createFilm() {
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "name",
                "description",
                LocalDate.now(),
                30));
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
                30));
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
                30));
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
                30));
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
                30));
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
                30));
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
                -1));
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
                0));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/films",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void getFilms() {
        Film film = new Film(1,
                "name",
                "description",
                LocalDate.now(),
                30);
        filmService.setFilms(Map.of(1, film));
        ResponseEntity<Film[]> response = restTemplate.getForEntity("http://localhost:" + port + "/films",
                Film[].class);
        Film[] films = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(films);
        assertEquals(film, films[0]);
    }

    @Test
    void updateFilm() {
        Film film = new Film(1,
                "name",
                "description",
                LocalDate.now(),
                30);
        filmService.setFilms(new HashMap<>(Map.of(1, film)));
        HttpEntity<Film> request = new HttpEntity<>(new Film(1,
                "update",
                "update",
                LocalDate.now(),
                30));
        Film updatedFilm = restTemplate.exchange("http://localhost:" + port + "/films/" + film.getId(),
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
}