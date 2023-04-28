package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@EqualsAndHashCode
@AllArgsConstructor
@Setter
@Getter
public class Like {
    @NotNull
    private final int filmId;
    private Set<Integer> userIds;

    public int getLikeCount() {
        return userIds.size();
    }
}
