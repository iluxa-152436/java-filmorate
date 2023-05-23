package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("inMemoryLikeStorage")
public class InMemoryLikeStorage implements LikeStorage {
    private final Map<Integer, Set<Like>> userLikes;
    private final Map<Integer, Integer> filmLikes;

    @Autowired
    public InMemoryLikeStorage(HashMap<Integer, Set<Like>> userLikes, Map<Integer, Integer> filmLikes) {
        this.userLikes = userLikes;
        this.filmLikes = filmLikes;
    }

    @Override
    public void saveLike(Like like) {
        userLikes.computeIfAbsent(like.getUserId(), f -> new HashSet<>()).add(like);
        filmLikes.computeIfAbsent(like.getFilmId(), n -> n + 1);
    }

    @Override
    public void deleteLike(Like like) {
        userLikes.get(like.getUserId()).remove(like);
        filmLikes.get(like.getFilmId() - 1);
    }

    @Override
    public Map<Integer, Integer> getSortedFilmLikes(long limit) {
        return filmLikes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
    }

    public void addFilmToLikeList(Integer filmId) {
        filmLikes.put(filmId, 0);
    }
}
