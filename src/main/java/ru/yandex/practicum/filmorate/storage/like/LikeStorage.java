package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Map;
import java.util.Set;

public interface LikeStorage {
    void saveLike(Like like);

    void deleteLike(Like like);

    Map<Integer, Integer> getSortedFilmLikes(long limit);

    Map<Integer, Integer> getSortedByLikesFilteredByFilmIds(Set<Integer> filmIds);
}
