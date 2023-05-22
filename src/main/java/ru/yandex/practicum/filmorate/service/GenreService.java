package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindGenreException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage storage;

    @Autowired
    public GenreService(@Qualifier("dbGenreStorage") GenreStorage storage) {
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
