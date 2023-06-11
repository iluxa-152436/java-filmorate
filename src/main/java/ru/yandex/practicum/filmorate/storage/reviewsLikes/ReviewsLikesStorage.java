package ru.yandex.practicum.filmorate.storage.reviewsLikes;

public interface ReviewsLikesStorage {
    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);

    int isLikeDislikeExist(int reviewId, int userId);
}
