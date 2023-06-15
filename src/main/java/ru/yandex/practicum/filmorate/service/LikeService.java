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

@Slf4j
@Service
public class LikeService {
    private final LikeStorage likeStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final FeedService feedService;
    private final DirectorService directorService;

    @Autowired
    public LikeService(@Qualifier("DB") LikeStorage likeStorage, FilmService filmService, UserService userService, FeedService feedService, DirectorService directorService) {
        this.likeStorage = likeStorage;
        this.filmService = filmService;
        this.userService = userService;
        this.directorService = directorService;
        this.feedService = feedService;
    }

    public void addLike(Like like) {
        userService.checkId(like.getUserId());
        filmService.checkId(like.getFilmId());
        likeStorage.saveLike(like);
        feedService.addFeed(like.getUserId(), like.getFilmId(), "LIKE", "ADD");
    }

    public void deleteLike(Like like) {
        userService.checkId(like.getUserId());
        filmService.checkId(like.getFilmId());
        likeStorage.deleteLike(like);
        feedService.addFeed(like.getUserId(), like.getFilmId(), "LIKE", "REMOVE");
    }

    public List<Film> getSortedFilms(long limit, Integer genreId, String releaseDate) {
        log.debug("Limit on the number of returned values = {}, filters: genreId = {}, releaseDate = {}",
                limit,
                genreId,
                releaseDate);
        List<Integer> sortedIds;
        if (genreId != null && releaseDate != null) {

            sortedIds = likeStorage.getSortedFilmLikes(limit, genreId, releaseDate);
        } else if (genreId == null && releaseDate == null) {
            sortedIds = likeStorage.getSortedFilmLikes(limit);
        } else if (genreId != null) {
            sortedIds = likeStorage.getSortedFilmLikes(limit, genreId);
        } else {
            sortedIds = likeStorage.getSortedFilmLikes(limit, releaseDate);
        }
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

    public List<Integer> sortFilmIdsByLikes(List<Integer> filmIds) {
        log.debug("FilmIds before sorting = {}", filmIds);
        List<Integer> sortedFilmIds = likeStorage.getSortedFilmIdsFilteredByFilmIds(filmIds);
        log.debug("FilmIds after sorting = {}", filmIds);
        return sortedFilmIds;
    }

    public List<Film> getSortedFilmsFilteredByDirectorId(Integer directorId) {
        List<Integer> filmIds = directorService.getFilmsOfDirectorById(directorId);
        List<Film> sortedFilms = new LinkedList<>();

        List<Integer> sortedFilmIds = sortFilmIdsByLikes(filmIds);
        for (Integer filmId : sortedFilmIds) {
            sortedFilms.add(filmService.getFilm(filmId));
        }
        return sortedFilms;
    }
}
