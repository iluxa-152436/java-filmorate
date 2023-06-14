package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<Integer> getSortedFilmLikes(long limit) {
        return filmLikes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, int genreId, String releaseDate) {
        return null;
        //TODO сделать реализацию
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, String releaseDate) {
        return null;
        //TODO сделать реализацию
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, int genreId) {
        return null;
        //TODO сделать реализацию
    }

    @Override
    public List<Integer> getSortedFilmIdsFilteredByFilmIds(List<Integer> filmIds) {
        return null;
    }

    @Override
    public List<Integer> getSortedByLikesFilteredByFilmIds(String query, List<String> by) {
        return null;
    }

    public void addFilmToLikeList(Integer filmId) {
        filmLikes.put(filmId, 0);
    }
}
