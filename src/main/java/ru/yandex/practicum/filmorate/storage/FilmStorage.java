package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    void createFilm(Film film);

    void updateFilm(Film film);

    Collection<Film> getFilms();

    boolean containsFilm(int filmId);

    Film getFilm(int filmId);
}
