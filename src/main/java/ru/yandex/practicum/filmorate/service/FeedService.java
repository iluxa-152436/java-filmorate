package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feedstorage.FeedStorage;

import java.util.List;

@Slf4j
@Service
public class FeedService {
    FeedStorage feedStorage;

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

    public void addDeleteFeed(int userId, int entityId, String eventType, String operation) {
        feedStorage.addDeleteFeed(userId, entityId, eventType, operation);
    }

    public void addUpdateFeed(int userId,String eventType, String operation) {
        feedStorage.addUpdateFeed(userId, eventType, operation);
    }
}
