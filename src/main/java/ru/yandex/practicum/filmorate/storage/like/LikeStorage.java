package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;
import java.util.Optional;

public interface LikeStorage {
    void saveLike(Like like);

    void deleteLike(Like like);

    List<Integer> getSortedFilmLikes(long limit, Optional<Integer> genreId, Optional<String> releaseDate);

    List<Integer> getSortedByLikesFilteredByFilmIds(String query, List<String> by);
}
