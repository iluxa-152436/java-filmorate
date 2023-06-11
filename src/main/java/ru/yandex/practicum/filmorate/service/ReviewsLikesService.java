package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.NotFoundInDB;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewsLikes.ReviewsLikesStorage;

@Slf4j
@Service
public class ReviewsLikesService {
    private final ReviewsLikesStorage reviewsLikesStorage;
    private final UserService userService;
    private final ReviewStorage reviewStorage;

    public ReviewsLikesService(@Qualifier("DB") ReviewsLikesStorage reviewsLikesStorage, UserService userService, FilmService filmService, @Qualifier("DB") ReviewStorage reviewStorage) {
        this.reviewsLikesStorage = reviewsLikesStorage;
        this.userService = userService;
        this.reviewStorage = reviewStorage;
    }

    public void addLike(int reviewId, int userId) {
        checkReviewUserID(reviewId, userId);
        int isLikeDislikeExist = reviewsLikesStorage.isLikeDislikeExist(reviewId, userId);
        if (isLikeDislikeExist == 0) {
            reviewsLikesStorage.addLike(reviewId, userId);
        } else if (isLikeDislikeExist == 1) {
            log.info("Уже есть лайк даному отзыву");
        } else if (isLikeDislikeExist == -1) {
            reviewsLikesStorage.deleteDislike(reviewId, userId);
        }
    }

    public void addDislike(int reviewId, int userId) {
        checkReviewUserID(reviewId, userId);
        int isLikeDislikeExist = reviewsLikesStorage.isLikeDislikeExist(reviewId, userId);
        if (isLikeDislikeExist == 0) {
            reviewsLikesStorage.addDislike(reviewId, userId);
        } else if (isLikeDislikeExist == -1) {
            log.debug("Уже есть дизлайк даному отзыву");
        } else if (isLikeDislikeExist == 1) {
            reviewsLikesStorage.deleteLike(reviewId, userId);
        }
    }

    public void deleteLike(int reviewId, int userId) {
        checkReviewUserID(reviewId, userId);
        int isLikeDislikeExist = reviewsLikesStorage.isLikeDislikeExist(reviewId, userId);
        if (isLikeDislikeExist == 1) {
            reviewsLikesStorage.deleteLike(reviewId, userId);
        } else {
            throw new NotFoundInDB("Объект для удаления не найден");
        }
    }

    public void deleteDislike(int reviewId, int userId) {
        checkReviewUserID(reviewId, userId);
        int isLikeDislikeExist = reviewsLikesStorage.isLikeDislikeExist(reviewId, userId);
        if (isLikeDislikeExist == -1) {
            reviewsLikesStorage.deleteLike(reviewId, userId);
        } else {
            throw new NotFoundInDB("Объект для удаления не найден");
        }
    }

    private void checkReviewUserID(int reviewId, int userId) {
        userService.checkId(userId);
        reviewStorage.containsReview(reviewId);
    }
}
