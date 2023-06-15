package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Feed {
    private int userId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date timestamp;
    private int eventId;
    private int entityId;
    private String eventType;
    private String operation;
}
