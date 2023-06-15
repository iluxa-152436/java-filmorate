package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static ru.yandex.practicum.filmorate.Constants.MAX_FILM_DESCRIPTION_LENGTH;


@EqualsAndHashCode
@Setter
@Getter
@ToString
public class Film {
    private int id;
    @NotBlank(message = "name cannot be empty")
    private String name;
    @Size(max = MAX_FILM_DESCRIPTION_LENGTH,
            message = "description should not be greater than " + MAX_FILM_DESCRIPTION_LENGTH)
    @NotNull
    private String description;
    @NotNull(message = "releaseDate cannot be empty")
    @ReleaseDateConstraint
    private LocalDate releaseDate;
    @Positive(message = "duration should be greater than 0")
    private int duration;
    private Set<Genre> genres;
    private MpaRating mpa;
    private Set<Director> directors;

    public Film(int id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                Set<Genre> genres,
                MpaRating mpa,
                Set<Director> directors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;

        this.genres = Objects.requireNonNullElseGet(genres, HashSet::new);
        this.directors = Objects.requireNonNullElseGet(directors, HashSet::new);

        this.mpa = mpa;
    }

    public Film(int id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                Set<Genre> genres,
                MpaRating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;

        this.genres = Objects.requireNonNullElseGet(genres, HashSet::new);

        this.mpa = mpa;
    }

    public Film() {
        this.genres = Objects.requireNonNullElseGet(genres, HashSet::new);
    }
}
