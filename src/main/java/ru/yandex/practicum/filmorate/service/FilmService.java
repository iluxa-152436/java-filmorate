package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Service
public class FilmService {
    private int id;
    private final FilmStorage storage;

    @Autowired
    public FilmService(FilmStorage storage) {
        this.storage = storage;
        id = 0;
    }

    public Film createFilm(Film film) {
        film.setId(++id);
        storage.createFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        checkId(film);
        storage.updateFilm(film);
        return film;
    }

    private void checkId(Film film) {
        if (!storage.containsFilm(film.getId())) {
            throw new FindFilmException("Фильм с id: " + film.getId() + " не найден");
        }
    }

    protected void checkId(int filmId) {
        if (!storage.containsFilm(filmId)) {
            throw new FindFilmException("Фильм с id: " + filmId + " не найден");
        }
    }

    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    public Film getFilm(int filmId) {
        checkId(filmId);
        return storage.getFilm(filmId);
    }
}
