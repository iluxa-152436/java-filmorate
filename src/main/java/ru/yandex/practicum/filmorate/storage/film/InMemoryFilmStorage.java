package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.like.InMemoryLikeStorage;

import java.util.*;

@Repository
@Qualifier("InMemory")
public class InMemoryFilmStorage implements FilmStorage {
    private int id;
    private final Map<Integer, Film> films;

    private final InMemoryLikeStorage likeStorage;

    @Autowired
    public InMemoryFilmStorage(HashMap<Integer, Film> films, InMemoryLikeStorage likeStorage) {
        this.films = films;
        this.likeStorage = likeStorage;
        id = 0;
    }

    @Override
    public void saveFilm(Film film) {
        films.put(film.getId(), film);
        likeStorage.addFilmToLikeList(film.getId());
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public boolean containsFilm(int filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public Film getFilm(int filmId) {
        return films.get(filmId);
    }

    @Override
    public int getNextId() {
        return ++id;
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return List.of();
    }
}
