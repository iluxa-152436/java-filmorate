package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(Integer id) {
        return directorStorage.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public void deleteDirectorById(Integer id) {
        directorStorage.deleteDirectorById(id);
    }

    public List<Film> getFilmsOfDirectorById(Integer directorId, String sortBy) {
        List<Film> films = directorStorage.getFilmsOfDirectorById(directorId);
        if (sortBy.equals("year")) {
            return films.stream().sorted(Comparator.comparing(Film::getReleaseDate)).collect(Collectors.toList());
        }
        return films.stream().sorted(Comparator.comparingInt((Film film) -> film.getLikes().size())
                .thenComparingInt(Film::getId)).collect(Collectors.toList());
    }
}
