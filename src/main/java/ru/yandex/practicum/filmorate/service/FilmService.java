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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        storage.saveFilm(film);
        return storage.getFilm(film.getId());
    }

    private void fillInGenres(Film film) {
        log.debug("Film {} fill in genres previous value = {}", film.getId(), film.getGenres());
        Set<Genre> genres = new HashSet<>();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genres.add(genreService.getGenre(genre.getId()));
            }
        }
        film.setGenres(genres);
        log.debug("New value = {}", film.getGenres());
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

    public void deleteFilmById(int filmId) {
        storage.deleteFilmById(filmId);
    }

    public List<Film> getFilmsOfDirectorById(Integer directorId, String sortBy) {
        List<Film> films = directorService.getFilmsOfDirectorById(directorId);
        if (sortBy.equals("year")) {
            return films.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
        }
        return films.stream().sorted(Comparator.comparingInt((Film film) ->
                        storage.getNumberOfLikesByFilmId(film.getId()))
                .thenComparingInt(Film::getId)).collect(Collectors.toList());
    }

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        return storage.getCommonFilms(userId,
                friendId);
    }
}
