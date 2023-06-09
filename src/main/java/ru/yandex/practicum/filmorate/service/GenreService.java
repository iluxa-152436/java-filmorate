package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindGenreException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage storage;

    @Autowired
    public GenreService(@Qualifier("DB") GenreStorage storage) {
        this.storage = storage;
    }

    public List<Genre> getGenres() {
        return storage.getAllGenres();
    }

    public Genre getGenre(int genreId) {
        return storage.getGenre(genreId)
                .orElseThrow(() -> new FindGenreException("Жанр с id:" + genreId + " не найден"));
    }
}
