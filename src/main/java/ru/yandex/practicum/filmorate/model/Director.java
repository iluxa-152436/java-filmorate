package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Director {
    private int id;
    @NotBlank(message = "name cannot be empty")
    private String name;
}
