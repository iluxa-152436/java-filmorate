package ru.yandex.practicum.filmorate.exception;

public class ValidateUserException extends RuntimeException {
    public ValidateUserException(String message) {
        super(message);
    }
}
