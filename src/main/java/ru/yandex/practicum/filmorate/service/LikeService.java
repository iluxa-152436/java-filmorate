package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.Constants.FILM_BY_COUNT_OF_LIKES_DESC;

@Slf4j
@Service
public class LikeService {
    private final LikeStorage likeStorage;
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public LikeService(LikeStorage likeStorage, FilmService filmService, UserService userService) {
        this.likeStorage = likeStorage;
        this.filmService = filmService;
        this.userService = userService;
    }

    public void addLike(int filmId, int userId) {
        filmService.checkId(filmId);
        userService.checkId(userId);
        Like like = likeStorage.getLike(filmId);
        if (like == null) {
            Set<Integer> userIds = new HashSet<>();
            userIds.add(userId);
            like = new Like(filmId, userIds);
            likeStorage.saveLike(like);
        } else {
            Set<Integer> newUserIds = like.getUserIds();
            newUserIds.add(userId);
        }
    }

    public void deleteLike(int filmId, int userId) {
        filmService.checkId(filmId);
        userService.checkId(userId);
        Like like = likeStorage.getLike(filmId);
        if (like != null) {
            like.getUserIds().remove(userId);
        }
    }

    public Collection<Film> getSortedFilms(long limit) {
        List<Like> sortedLikes = likeStorage.getSortedLikes(FILM_BY_COUNT_OF_LIKES_DESC, limit);
        Collection<Film> sortedFilms = new ArrayList<>();
        for (Like like : sortedLikes) {
            sortedFilms.add(filmService.getFilm(like.getFilmId()));
        }
        return sortedFilms;
    }

    public void addFilmToList(int filmId) {
        filmService.checkId(filmId);
        Like like = new Like(filmId, new HashSet<>());
        likeStorage.saveLike(like);
    }
}
