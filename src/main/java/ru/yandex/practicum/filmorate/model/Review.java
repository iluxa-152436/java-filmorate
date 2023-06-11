package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

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
    @JsonProperty("isPositive")
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    private int useful;

    public Object isPositive() {
        return isPositive;
    }
}
