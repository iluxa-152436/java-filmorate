package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Feed {

    int userId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    Date timestamp;
    int eventId;
    int entityId;
    String eventType;
    String operation;
}
