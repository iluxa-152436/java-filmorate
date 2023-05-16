package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.Constants.MAX_FILM_DESCRIPTION_LENGTH;

@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
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
}
