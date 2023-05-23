package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;

@Slf4j
@Service
public class LikeService {
    private final LikeStorage likeStorage;
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public LikeService(@Qualifier("DB") LikeStorage likeStorage, FilmService filmService, UserService userService) {
        this.likeStorage = likeStorage;
        this.filmService = filmService;
        this.userService = userService;
    }

    public void addLike(Like like) {
        userService.checkId(like.getUserId());
        filmService.checkId(like.getFilmId());
        likeStorage.saveLike(like);
    }

    public void deleteLike(Like like) {
        userService.checkId(like.getUserId());
        filmService.checkId(like.getFilmId());
        likeStorage.deleteLike(like);
    }

    public List<Film> getSortedFilms(long limit) {
        Map<Integer, Integer> sortedIds = likeStorage.getSortedFilmLikes(limit);
        List<Film> sortedFilms = new ArrayList<>();
        for (Integer filmId : sortedIds.keySet()) {
            sortedFilms.add(filmService.getFilm(filmId));
        }
        return sortedFilms;
    }
}
