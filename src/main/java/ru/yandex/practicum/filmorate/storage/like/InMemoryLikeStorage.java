package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.*;

@Repository
@Qualifier("InMemory")
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
    public List<Integer> getSortedFilmLikes(long limit, Optional<Integer> genreId, Optional<String> releaseDate) {
        throw new UnsupportedOperationException("Не поддерживается в InMemory");
    }

    @Override
    public List<Integer> getSortedByLikesFilteredByFilmIds(String query, List<String> by) {
        return null;
    }

    public void addFilmToLikeList(Integer filmId) {
        filmLikes.put(filmId, 0);
    }
}
