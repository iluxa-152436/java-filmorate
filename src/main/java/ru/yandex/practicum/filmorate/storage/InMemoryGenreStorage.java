package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Qualifier("inMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Integer, Genre> genres;
    private int id;

    @Autowired
    public InMemoryGenreStorage(Map<Integer, Genre> genres) {
        this.genres = genres;
        id = 0;
    }

    @Override
    public void saveGenre(Genre genre) {
        genre.setId(++id);
        genres.put(genre.getId(), genre);
    }

    @Override
    public Optional<Genre> getGenre(int genreId) {
        return Optional.ofNullable(genres.get(genreId));
    }

    @Override
    public List<Genre> getAllGenres() {
        return List.copyOf(genres.values());
    }
}
