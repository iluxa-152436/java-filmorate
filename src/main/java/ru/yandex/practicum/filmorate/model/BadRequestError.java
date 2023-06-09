package ru.yandex.practicum.filmorate.model;

public class BadRequestError extends RuntimeException{
    public BadRequestError (String msg) {
        super(msg);
    }
}
