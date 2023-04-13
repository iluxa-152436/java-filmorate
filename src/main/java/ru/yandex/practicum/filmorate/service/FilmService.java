package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class FilmService {
    private Map<Integer, Film> films;
    int id;

    public FilmService() {
        id = 0;
        films = new HashMap<>();
    }

    public Film createFilm(Film film) throws ValidateFilmException {
        film.setId(++id);
        checkReleaseDate(film);
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film film) throws ValidateFilmException, FindFilmException {
        checkId(film);
        checkReleaseDate(film);
        films.put(film.getId(), film);
        return film;
    }

    private void checkId(Film film) throws FindFilmException {
        if (!films.containsKey(film.getId())) {
            throw new FindFilmException("Фильм с id: " + film.getId() + " не найден");
        }
    }

    private void checkReleaseDate(Film film) throws ValidateFilmException {
        if (film.getReleaseDate().isBefore(FIRST_RELEASE_DATE)) {
            throw new ValidateFilmException("Дата релиза не может быть ранее " + FIRST_RELEASE_DATE);
        }
    }

    public Collection<Film> getFilms() {
        return films.values();
    }
}
