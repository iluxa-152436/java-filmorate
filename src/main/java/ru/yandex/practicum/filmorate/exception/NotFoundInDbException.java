package ru.yandex.practicum.filmorate.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class NotFoundInDbException extends DataIntegrityViolationException {

    public NotFoundInDbException(String message) {
        super(message);
    }
}
