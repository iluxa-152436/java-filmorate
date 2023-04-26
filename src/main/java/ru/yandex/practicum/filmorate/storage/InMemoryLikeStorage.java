package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryLikeStorage implements LikeStorage {
    private final Map<Integer, Like> likes;

    @Autowired
    public InMemoryLikeStorage(Map<Integer, Like> likes) {
        this.likes = likes;
    }

    @Override
    public void saveLike(Like like) {
        likes.put(like.getFilmId(), like);
    }

    @Override
    public Like getLike(int filmId) {
        return likes.get(filmId);
    }

    @Override
    public List<Like> getSortedLikes(Comparator comparator, long limit) {
        return (ArrayList<Like>) likes.values().stream()
                .sorted(comparator)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
