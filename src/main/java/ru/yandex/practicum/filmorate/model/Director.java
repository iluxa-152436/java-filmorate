package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Director {
    @EqualsAndHashCode.Exclude
    private Integer id;
    @NotBlank(message = "name cannot be empty")
    private String name;
}
