package ru.yandex.practicum.filmorate.storage.feedstorage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void addFeed(int userId, int entityId, String eventType, String operation);

    void addDeleteFeed(int userId, int entityId, String eventType, String operation);

    void addUpdateFeed(int reviewId,String eventType, String operation);

    List<Feed> getFeedsByUserId(int userId);
}
