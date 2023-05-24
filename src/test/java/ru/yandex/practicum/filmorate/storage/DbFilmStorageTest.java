package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbFilmStorageTest {

    @Autowired
    @Qualifier("DB")
    private FilmStorage filmStorage;

    @Test
    void saveFilm() {
        Film film = new Film(3,
                "name 3",
                "description 3",
                LocalDate.of(1999, 04, 30),
                120,
                Collections.emptySet(),
                new MpaRating(1, "G"));
        filmStorage.saveFilm(film);
        assertEquals(film, filmStorage.getFilm(3));
    }

    @Test
    void updateFilm() {
        Film film = new Film(2, "updated", "updated", LocalDate.of(1999, 04, 30), 100, Collections.emptySet(),
                new MpaRating(1, "G"));
        filmStorage.updateFilm(film);
        assertEquals(film, filmStorage.getFilm(2));
    }

    @Test
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
    @Order(1)
    @Sql("classpath:test_data.sql")
    void containsFilm() {
        assertTrue(filmStorage.containsFilm(1));
    }

    @Test
    void getFilm() {
        Film film = filmStorage.getFilm(1);
        assertEquals(1, film.getId());
    }

    @Test
    void getNextId() {
        assertEquals(4, filmStorage.getNextId());
    }
}