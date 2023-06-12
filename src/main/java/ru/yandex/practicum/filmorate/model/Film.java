package ru.yandex.practicum.filmorate.model;

import lombok.*;
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

@NoArgsConstructor
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
    @EqualsAndHashCode.Exclude
    private Set<Director> directors;
    @EqualsAndHashCode.Exclude
    private Set<Integer> likes = new HashSet<>();

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


    public Film(int id,
                String name,
                String description,
                LocalDate releaseDate,
                int duration,
                Set<Genre> genres,
                MpaRating mpa,
                Set<Director> directors,
                Set<Integer> likes) {
        this(id, name, description, releaseDate, duration, genres, mpa);
        this.directors = Objects.requireNonNullElseGet(directors, HashSet::new);
        this.likes = Objects.requireNonNullElseGet(likes, HashSet::new);
    }
}
