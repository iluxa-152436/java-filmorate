package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Comparator;
import java.util.List;

public interface LikeStorage {
    void saveLike(Like like);

    List<Like> getSortedLikes(Comparator comparator, long limit);

    Like getLike(int filmId);
}
