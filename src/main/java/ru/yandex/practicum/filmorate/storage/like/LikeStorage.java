package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Map;

public interface LikeStorage {
    void saveLike(Like like);

    void deleteLike(Like like);

    Map<Integer, Integer> getSortedFilmLikes(long limit);
}
