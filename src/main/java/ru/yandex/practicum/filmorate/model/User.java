package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
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
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$",
            message = "login must not contain any special characters or spaces")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent(message = "birthday should be in past or present")
    private LocalDate birthday;
}
