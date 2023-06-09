package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Review {
    @JsonProperty("reviewId")
    private int id;
    @NotNull
    private String content;
    @NotNull
    @JsonProperty("isPositive")
    private boolean isPositive;
    @NotNull
    @Positive
    private int userId;
    @NotNull
    @Positive
    private int filmId;
    private int useful;
}
