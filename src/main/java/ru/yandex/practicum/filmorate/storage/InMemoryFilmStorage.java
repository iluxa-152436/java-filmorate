package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;

    @Autowired
    public InMemoryFilmStorage(Map<Integer, Film> films) {
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
    public boolean containsFilm(int id) {
        return films.containsKey(id);
    }
}
