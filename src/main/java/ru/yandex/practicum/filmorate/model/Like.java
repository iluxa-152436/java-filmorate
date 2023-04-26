package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@EqualsAndHashCode
@AllArgsConstructor
@Setter
@Getter
public class Like {
    private final int filmId;
    private Set<Integer> userIds;
    private int likeCount;
}
