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
    private final FeedService feedService;

    @Autowired
    public ReviewService(@Qualifier("DB") ReviewStorage reviewStorage, UserService userService, FilmService filmService, FeedService feedService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.feedService = feedService;
    }

    public Review add(Review review) {
        userService.checkId(review.getUserId());
        filmService.checkId(review.getFilmId());
        Review result = reviewStorage.add(review);
        feedService.addFeed(result.getUserId(), result.getId(), "REVIEW", "ADD");
        return result;
    }

    public Review update(Review review) {
        Review result = reviewStorage.update(review);
        feedService.addFeed(result.getUserId(), result.getId(), "REVIEW", "UPDATE");
        return result;
    }

    public void delete(int id) {
        Review review = reviewStorage.getById(id);
        reviewStorage.delete(id);
        feedService.addFeed(review.getUserId(), review.getId(), "REVIEW", "REMOVE");
    }

    public Review getById(int id) {
        return reviewStorage.getById(id);
    }

    public List<Review> getList(Integer filmId, int count) {
        return reviewStorage.getList(filmId, count);
    }
}
