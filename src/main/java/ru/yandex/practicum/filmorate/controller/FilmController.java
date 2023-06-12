package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.service.DirectorService;
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
    private final DirectorService directorService;

    @Autowired
    public FilmController(FilmService filmService, LikeService likeService, DirectorService directorService) {
        this.filmService = filmService;
        this.likeService = likeService;
        this.directorService = directorService;
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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive long count) {
        log.debug("Requested {} most popular films", count);
        return likeService.getSortedFilms(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsOfDirectorById(@PathVariable Integer directorId, @RequestParam String sortBy) {
        switch(sortBy) {
            case "year":
            case "likes":
                log.debug("Requested films of director with id = {}, sort by {}", directorId, sortBy);
                return directorService.getFilmsOfDirectorById(directorId, sortBy);
            default:
                throw new IllegalArgumentException("Incorrect sorting order");
        }
    }
}
