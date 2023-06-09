package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ApiErrorMessage;
import ru.yandex.practicum.filmorate.model.BadRequestError;
import ru.yandex.practicum.filmorate.model.NotFoundInDB;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> validationBadRequestError(BadRequestError e) {
        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
        return Map.of("error", e.getMessage());
    }
    @ExceptionHandler(value = {FindFilmException.class,
            FindUserException.class,
            EmptyResultDataAccessException.class,
            FindMpaRatingException.class,
            FindGenreException.class,
            NotFoundInDB.class})
    public ResponseEntity<ApiErrorMessage> handleNotFoundException(Exception exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(value = {ValidateFilmException.class, ValidateUserException.class})
    public ResponseEntity<ApiErrorMessage> handleValidateException(Exception exception) {
        log.debug("Получен статус 400 Bad request {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiErrorMessage> handleArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.debug("Получен статус 400 Bad request {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorMessage(exception.getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ApiErrorMessage> handleThrowable(final Throwable exception) {
        log.debug("Получен статус 500 Internal server error {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorMessage("Произошла непредвиденная ошибка."));
    }
}
