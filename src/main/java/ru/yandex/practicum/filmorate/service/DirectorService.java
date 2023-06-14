package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindDirectorException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {
    private final DirectorStorage storage;

    @Autowired
    public DirectorService(@Qualifier("DB") DirectorStorage storage) {
        this.storage = storage;
    }

    public List<Director> getDirectors() {
        return storage.getAllDirectors();
    }

    public Director getDirector(int directorId) {
        return storage.getDirector(directorId)
                .orElseThrow(() -> new FindDirectorException("Режиссер с id:" + directorId + " не найден"));
    }

    public Director createDirector(Director director) {
        director.setId(storage.getNextId());
        storage.saveDirector(director);
        return director;
    }

    public Director updateDirector(Director director) {
        checkId(director);
        storage.updateDirector(director);
        return director;
    }

    private void checkId(Director director) {
        if (!storage.containsDirector(director.getId())) {
            throw new FindDirectorException("Режиссер с id:" + director.getId() + " не найден");
        }
    }

    public void checkId(int directorId) {
        if (!storage.containsDirector(directorId)) {
            throw new FindDirectorException("Режиссер с id:" + directorId + " не найден");
        }
    }

    public void deleteDirectorById(int directorId) {
        storage.deleteDirectorById(directorId);
    }

    public List<Integer> getFilmsOfDirectorById(Integer directorId) {
        checkId(directorId);
        return storage.getFilmIdsByDirectorId(directorId);
    }
}
