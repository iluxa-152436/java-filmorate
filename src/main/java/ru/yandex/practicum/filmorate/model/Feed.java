package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.service.FeedEventType;
import ru.yandex.practicum.filmorate.service.FeedOperation;

import java.util.Date;

@Data
@Builder
public class Feed {
    private int userId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date timestamp;
    private int eventId;
    private int entityId;
    private FeedEventType eventType;
    private FeedOperation operation;
}
