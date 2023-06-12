package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(Integer id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(Integer id);

    List<Film> getFilmsOfDirectorById(Integer id);
}
