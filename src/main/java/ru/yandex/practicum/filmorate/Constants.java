package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.Like;

import java.time.LocalDate;
import java.util.Comparator;

public final class Constants {
    public static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    public static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final Comparator<Like> FILM_BY_COUNT_OF_LIKES_DESC =
            Comparator.comparingInt(Like::getLikeCount).reversed();
}
