package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class DbFilmStorageTest {

    @Autowired
    @Qualifier("DB")
    private FilmStorage filmStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void deleteTestData() {
        jdbcTemplate.update("DELETE FROM films;" +
                "DELETE FROM friends;" +
                "DELETE FROM app_users;");
    }

    @Test
    @Sql("classpath:test_data.sql")
    void saveFilm() {
        Film film = new Film(3,
                "name 3",
                "description 3",
                LocalDate.of(1999, 04, 30),
                120,
                Collections.emptySet(),
                new MpaRating(1, "G"),
                null);
        filmStorage.saveFilm(film);
        assertEquals(film, filmStorage.getFilm(3));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void updateFilm() {
        Film film = new Film(2,
                "updated",
                "updated",
                LocalDate.of(1999, 04, 30),
                100,
                Collections.emptySet(),
                new MpaRating(1, "G"),
                null);
        filmStorage.updateFilm(film);
        assertEquals(film, filmStorage.getFilm(2));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getFilms() {
        List<Film> films = filmStorage.getFilms();
        System.out.println(films.get(0).getId());
        System.out.println(films.get(0).getMpa().getName());
        assertEquals("name 1", films.get(0).getName());
        assertEquals("description 1", films.get(0).getDescription());
        assertEquals(120, films.get(0).getDuration());
        assertEquals(Collections.emptySet(), films.get(0).getGenres());
        assertEquals(LocalDate.of(1999, 04, 30), films.get(0).getReleaseDate());
        assertEquals(new MpaRating(1, "G"), films.get(0).getMpa());
    }

    @Test
    @Sql("classpath:test_data.sql")
    void containsFilm() {
        assertTrue(filmStorage.containsFilm(1));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getFilm() {
        Film film = filmStorage.getFilm(1);
        assertEquals(1, film.getId());
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getNextId() {
        assertEquals(3, filmStorage.getNextId());
    }
}