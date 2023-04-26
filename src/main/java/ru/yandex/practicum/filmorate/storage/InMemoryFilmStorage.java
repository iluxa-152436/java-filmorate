package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;

    @Autowired
    public InMemoryFilmStorage(HashMap<Integer, Film> films) {
        this.films = films;
    }

    @Override
    public void createFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public boolean containsFilm(int filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public Film getFilm(int filmId) {
        return films.get(filmId);
    }
}
