package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add (Review review);

    Review update (Review review);

    void delete(int id);

    Review getById (int id);

    List<Review> getList (int filmId, int amount);
    public void containsReview(int id);

}
