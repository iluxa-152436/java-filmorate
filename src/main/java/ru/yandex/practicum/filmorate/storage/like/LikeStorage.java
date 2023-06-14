package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

public interface LikeStorage {
    void saveLike(Like like);

    void deleteLike(Like like);

    List<Integer> getSortedFilmLikes(long limit);

    List<Integer> getSortedFilmLikes(long limit, int genreId, String releaseDate);

    List<Integer> getSortedFilmLikes(long limit, String releaseDate);

    List<Integer> getSortedFilmLikes(long limit, int genreId);

    List<Integer> getSortedFilmIdsFilteredByFilmIds(List<Integer> filmIds);

    List<Integer> getSortedByLikesFilteredByFilmIds(String query, List<String> by);
}
