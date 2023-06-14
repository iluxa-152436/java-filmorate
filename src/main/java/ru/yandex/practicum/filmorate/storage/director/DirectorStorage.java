package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    void saveDirector(Director director);

    Optional<Director> getDirector(int directorId);

    List<Director> getAllDirectors();

    int getNextId();

    void updateDirector(Director director);

    boolean containsDirector(int id);

    void deleteDirectorById(int directorId);

    List<Integer> getFilmIdsByDirectorId(Integer directorId);
}
