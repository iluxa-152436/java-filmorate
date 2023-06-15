package ru.yandex.practicum.filmorate.storage.feedstorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindUserException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public class DbFeedStorageImpl implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    private final UserStorage userStorage;
    private final ReviewService reviewService;


    @Autowired
    public DbFeedStorageImpl(JdbcTemplate jdbcTemplate, @Qualifier("DB") UserStorage userStorage, @Lazy ReviewService reviewService) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
        this.reviewService = reviewService;
    }

    @Override
    public void addFeed(int userId, int entityId, String eventType, String operation) {
        String sqlQuery = "INSERT INTO feed(user_id, time_stamp, entity_id," +
                " event_type, operation) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, Date.from(Instant.now()), entityId, eventType, operation);
    }

    @Override
    public List<Feed> getFeedsByUserId(int userId) {
        if (userStorage.containsUser(userId)) {
            String sqlQuery = "SELECT * FROM feed WHERE user_id = ?";
            return jdbcTemplate.query(sqlQuery, this::makeFeed, userId);
        } else {
            throw new FindUserException("User с id = " + userId + " получение ленты невозможно.");
        }
    }

    private Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .entityId(rs.getInt("entity_id"))
                .operation(rs.getString("operation"))
                .eventType(rs.getString("event_type"))
                .timestamp(rs.getTimestamp("time_stamp"))
                .build();
    }
}
