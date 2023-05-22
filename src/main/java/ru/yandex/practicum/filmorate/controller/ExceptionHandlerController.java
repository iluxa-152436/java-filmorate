package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.FindFilmException;
import ru.yandex.practicum.filmorate.exception.FindUserException;
import ru.yandex.practicum.filmorate.exception.ValidateFilmException;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.ApiErrorMessage;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = {FindFilmException.class, FindUserException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<ApiErrorMessage> handleNotFoundException(Exception exception) {
        log.info("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(value = {ValidateFilmException.class, ValidateUserException.class})
    public ResponseEntity<ApiErrorMessage> handleValidateException(Exception exception) {
        log.info("Получен статус 400 Bad request {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiErrorMessage> handleArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.info("Получен статус 400 Bad request {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorMessage(exception.getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorMessage> handleThrowable(final Throwable exception) {
        log.info("Получен статус 500 Internal server error {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorMessage("Произошла непредвиденная ошибка."));
    }
}
