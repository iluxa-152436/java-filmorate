package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("inMemoryMpaRatingStorage")
public class InMemoryMpaRatingStorage implements MpaRatingStorage {
    private final Map<Integer, MpaRating> mpaRatings;
    private int id;

    @Autowired
    public InMemoryMpaRatingStorage(Map<Integer, MpaRating> mpaRatings) {
        this.mpaRatings = mpaRatings;
        id = 0;
    }

    @Override
    public void saveMpaRating(MpaRating mpaRating) {
        mpaRating.setId(++id);
        mpaRatings.put(mpaRating.getId(), mpaRating);
    }

    @Override
    public Optional<MpaRating> getMpaRating(int mpaRatingId) {
        return Optional.ofNullable(mpaRatings.get(mpaRatingId));
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        return List.copyOf(mpaRatings.values());
    }
}
