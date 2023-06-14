package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.HashSet;
import java.util.Set;

@Repository
@Qualifier("DB")
public class DbFriendStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    private final FeedService feedService;

    @Autowired
    public DbFriendStorage(JdbcTemplate jdbcTemplate, FeedService feedService) {
        this.jdbcTemplate = jdbcTemplate;
        this.feedService = feedService;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "insert into friends(user_id, friend_id) values(?,?)";
        if (jdbcTemplate.update(sql, userId, friendId) != 0) {
            feedService.addFeed(userId, friendId, "FRIEND", "ADD");
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "delete from friends where user_id=? and friend_id=?";
        if (jdbcTemplate.update(sql, userId, friendId) != 0) {
            feedService.addDeleteFeed(userId, friendId, "FRIEND", "REMOVE");
        }
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        String sql = "select friend_id from friends where user_id=?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        return makeFriendIdsSet(rowSet);
    }

    private Set<Integer> makeFriendIdsSet(SqlRowSet rowSet) {
        Set<Integer> friendIds = new HashSet<>();
        while (rowSet.next()) {
            Integer friendId = rowSet.getInt(1);
            friendIds.add(friendId);
        }
        return friendIds;
    }

    @Override
    public boolean hasFriends(int userId) {
        String sql = "select count(*) from friends where user_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId) >= 1;
    }

}
