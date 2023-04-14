package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindFilmException;
import ru.yandex.practicum.filmorate.exception.ValidateFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.Constants.*;

@Slf4j
@Service
public class FilmService {
    private Map<Integer, Film> films;
    private int id;

    public FilmService() {
        id = 0;
        films = new HashMap<>();
    }

    public Film createFilm(Film film) {
        film.setId(++id);
        checkReleaseDate(film);
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) {
        checkId(film);
        checkReleaseDate(film);
        films.put(film.getId(), film);
        return film;
    }

    private void checkId(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FindFilmException("Фильм с id: " + film.getId() + " не найден");
        }
    }

    private void checkReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_RELEASE_DATE)) {
            throw new ValidateFilmException("Дата релиза не может быть ранее " + FIRST_RELEASE_DATE);
        }
    }

    public Collection<Film> getFilms() {
        return films.values();
    }
}
