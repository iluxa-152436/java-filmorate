package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindFilmException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final GenreService genreService;
    private final MpaRatingService mpaRatingService;
    private final DirectorService directorService;

    @Autowired
    public FilmService(@Qualifier("DB") FilmStorage storage,
                       GenreService genreService,
                       MpaRatingService mpaRatingService,
                       DirectorService directorService) {
        this.storage = storage;
        this.genreService = genreService;
        this.mpaRatingService = mpaRatingService;
        this.directorService = directorService;
    }

    public Film createFilm(Film film) {
        film.setId(storage.getNextId());
        fillInMpaRating(film);
        fillInGenres(film);
        fillInDirector(film);
        storage.saveFilm(film);
        return storage.getFilm(film.getId());
    }

    private void fillInGenres(Film film) {
        log.debug("Film {} fill in genres previous value = {}", film.getId(), film.getGenres());
        Set<Genre> genres = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            genres.add(genreService.getGenre(genre.getId()));
        }
        film.setGenres(genres);
        log.debug("New value = {}", film.getGenres());
    }

    private void fillInDirector(Film film) {
        log.debug("Film {} fill in director previous value = {}", film.getId(), film.getDirectors());
        Set<Director> directors = new HashSet<>();
        for (Director director : film.getDirectors()) {
            directors.add(directorService.getDirector(director.getId()));
        }
        film.setDirectors(directors);
        log.debug("New value = {}", film.getDirectors());
    }


    private void fillInMpaRating(Film film) {
        if (film.getMpa() != null) {
            log.debug("Film {} fill in mpaRating previous value = {}", film.getId(), film.getMpa());
            film.setMpa(mpaRatingService.getMpaRating(film.getMpa().getId()));
            log.debug("New value = {}", film.getMpa());
        }

    }

    public Film updateFilm(Film film) {
        checkId(film);
        fillInMpaRating(film);
        fillInGenres(film);
        fillInDirector(film);
        storage.updateFilm(film);
        return storage.getFilm(film.getId());
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

    public List<Film> getFilms() {
        return storage.getFilms();
    }

    public Film getFilm(int filmId) {
        checkId(filmId);
        return storage.getFilm(filmId);
    }

    public List<Film> getSortedFilmsFilteredByDirectorId(Integer directorId) {
        List<Integer> filmIds = directorService.getFilmsOfDirectorById(directorId);
        List<Film> sortedFilms = new LinkedList<>();

        for (Integer filmId : filmIds) {
            sortedFilms.add(getFilm(filmId));
        }
        return sortedFilms.stream()
                .sorted(Comparator.comparing(Film::getReleaseDate))
                .collect(Collectors.toList());
    }
}
