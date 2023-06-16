package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;

    @Autowired
    public FilmController(FilmService filmService, LikeService likeService) {
        this.filmService = filmService;
        this.likeService = likeService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        log.debug("Received value filmId = {}", filmId);
        return filmService.getFilm(filmId);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.debug("Received values filmId = {}, userId = {}", filmId, userId);
        likeService.addLike(new Like(userId, filmId));
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        log.debug("Received values filmId = {}, userId = {}", filmId, userId);
        likeService.deleteLike(new Like(userId, filmId));
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive long count,
                                      @RequestParam(required = false) @Positive Integer genreId,
                                      @RequestParam(required = false) String year) {
        return likeService.getSortedFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam List<String> by) {
        return likeService.getSortedAndFilteredFilms(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable int filmId) {
        log.debug("Received values filmId = {}", filmId);
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsOfDirectorById(@PathVariable Integer directorId,
                                             @RequestParam FilmSortingParameter sortBy) {
        switch (sortBy) {
            case year:
            case likes:
                log.debug("Requested films of director with id = {}, sort by {}", directorId, sortBy);
                return filmService.getFilmsOfDirectorById(directorId, sortBy);
            default:
                throw new IllegalArgumentException("Incorrect sorting order");
        }
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}