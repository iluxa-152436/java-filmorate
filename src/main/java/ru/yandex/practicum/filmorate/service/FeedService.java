package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feedstorage.FeedStorage;

import java.util.List;

@Service
public class FeedService {
    private final FeedStorage feedStorage;

    @Autowired
    public FeedService(FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    public List<Feed> getFeedsByUserId(int userId) {
        return feedStorage.getFeedsByUserId(userId);
    }

    public void addFeed(int userId, int entityId, String eventType, String operation) {
        feedStorage.addFeed(userId, entityId, eventType, operation);
    }
}
