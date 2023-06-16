package ru.yandex.practicum.filmorate.storage.feedstorage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.FeedEventType;
import ru.yandex.practicum.filmorate.service.FeedOperation;

import java.util.List;

public interface FeedStorage {
    void addFeed(int userId, int entityId, FeedEventType eventType, FeedOperation operation);

    List<Feed> getFeedsByUserId(int userId);
}
