package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {
    private int id;
    @NotBlank(message = "email cannot be empty")
    @Email(message = "email should be valid")
    private String email;
    @NotBlank(message = "login cannot be empty")
    private String login;
    private String name;
    @PastOrPresent(message = "birthday should be in past or present")
    private LocalDate birthday;
}
