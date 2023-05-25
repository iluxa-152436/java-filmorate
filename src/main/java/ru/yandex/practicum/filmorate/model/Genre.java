package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Genre {
    private int id;
    @NotBlank(message = "name cannot be empty")
    private String name;
}
