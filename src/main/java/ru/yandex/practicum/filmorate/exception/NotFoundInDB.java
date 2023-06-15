package ru.yandex.practicum.filmorate.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class NotFoundInDB extends DataIntegrityViolationException {

    public NotFoundInDB(String msg) {
        super(msg);
    }
}
