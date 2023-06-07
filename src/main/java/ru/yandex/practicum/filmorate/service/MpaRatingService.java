package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindMpaRatingException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingStorage;

import java.util.List;

@Service
public class MpaRatingService {
    private final MpaRatingStorage storage;

    @Autowired
    public MpaRatingService(@Qualifier("DB") MpaRatingStorage storage) {
        this.storage = storage;
    }

    public List<MpaRating> getMpaRatings() {
        return storage.getAllMpaRatings();
    }

    public MpaRating getMpaRating(int mpaRatingId) {
        return storage.getMpaRating(mpaRatingId)
                .orElseThrow(() -> new FindMpaRatingException("Рейтинг с id: " + mpaRatingId + " не найден"));
    }
}
