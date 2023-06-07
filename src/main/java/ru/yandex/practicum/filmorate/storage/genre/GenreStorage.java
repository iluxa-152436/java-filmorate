package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    void saveGenre(Genre genre);

    Optional<Genre> getGenre(int genreId);

    List<Genre> getAllGenres();
}
