package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LikeService {
    private final LikeStorage likeStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final FeedService feedService;

    @Autowired
    public LikeService(@Qualifier("DB") LikeStorage likeStorage, FilmService filmService, UserService userService, FeedService feedService) {
        this.likeStorage = likeStorage;
        this.filmService = filmService;
        this.userService = userService;
        this.feedService = feedService;
    }

    public void addLike(Like like) {
        userService.checkId(like.getUserId());
        filmService.checkId(like.getFilmId());
        likeStorage.saveLike(like);
        feedService.addFeed(like.getUserId(), like.getFilmId(), FeedEventType.LIKE, FeedOperation.ADD);
    }

    public void deleteLike(Like like) {
        userService.checkId(like.getUserId());
        filmService.checkId(like.getFilmId());
        likeStorage.deleteLike(like);
        feedService.addFeed(like.getUserId(), like.getFilmId(), FeedEventType.LIKE, FeedOperation.REMOVE);
    }

    public List<Film> getSortedFilms(long limit, Optional<Integer> genreId, Optional<String> releaseDate) {
        log.debug("Limit on the number of returned values = {}, filters: genreId = {}, releaseDate = {}",
                limit,
                genreId,
                releaseDate);
        List<Integer> sortedIds = likeStorage.getSortedFilmLikes(limit, genreId, releaseDate);
        List<Film> sortedFilms = new ArrayList<>();
        for (Integer filmId : sortedIds) {
            sortedFilms.add(filmService.getFilm(filmId));
        }
        return sortedFilms;
    }

    public List<Film> getSortedAndFilteredFilms(String query, List<String> by) {
        log.debug("query = {}, search in fields = {}", query, by);
        List<Integer> sortedFilmIds = likeStorage.getSortedByLikesFilteredByFilmIds(query, by);
        log.debug("sortedFilmIds {}", sortedFilmIds);
        List<Film> sortedFilms = new ArrayList<>();
        for (Integer filmId : sortedFilmIds) {
            sortedFilms.add(filmService.getFilm(filmId));
        }
        return sortedFilms;
    }
}
