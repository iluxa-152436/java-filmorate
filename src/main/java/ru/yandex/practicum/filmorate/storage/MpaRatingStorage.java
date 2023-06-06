package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaRatingStorage {
    void saveMpaRating(MpaRating mpaRating);

    Optional<MpaRating> getMpaRating(int mpaRatingId);

    List<MpaRating> getAllMpaRatings();
}
