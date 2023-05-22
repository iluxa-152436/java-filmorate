package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class MpaRating {
    private int id;
    @NotBlank(message = "name cannot be empty")
    private String name;
}
