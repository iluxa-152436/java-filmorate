package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public ReviewService(@Qualifier("DB") ReviewStorage reviewStorage, UserService userService, FilmService filmService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
    }

    public Review add(Review review) {
        userService.checkId(review.getUserId());
        filmService.checkId(review.getFilmId());
        return reviewStorage.add(review);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void delete(int id) {
        reviewStorage.delete(id);
    }

    public Review getById(int id) {
        return reviewStorage.getById(id);
    }

    public List<Review> getList(Integer filmId, int count) {
        return reviewStorage.getList(filmId, count);
    }
}
