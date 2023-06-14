package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Director {
    private int id;
    @NotBlank(message = "name cannot be empty")
    private String name;
}
