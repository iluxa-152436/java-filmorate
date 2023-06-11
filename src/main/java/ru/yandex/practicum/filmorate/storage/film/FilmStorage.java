package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void saveFilm(Film film);

    void updateFilm(Film film);

    List<Film> getFilms();

    boolean containsFilm(int filmId);

    Film getFilm(int filmId);

    int getNextId();

    void deleteFilmById(int filmId);
}
