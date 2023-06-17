package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindUserException;

import java.util.HashSet;
import java.util.Set;

@Repository
@Qualifier("DB")
public class DbFriendStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFriendStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO friends(user_id, friend_id) VALUES(?,?)";
        try {
            jdbcTemplate.update(sql, userId, friendId);
        } catch (DataAccessException e) {
            throw new FindUserException("Ошибка добавления пользователя c id = " + friendId + "в друзья к пользователю c id = " + userId);
        }
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        try {
            jdbcTemplate.update(sql, userId, friendId);
        } catch (DataAccessException e) {
            throw new FindUserException("Ошибка удаления друга c id = " + friendId + "в друзья к пользователю c id = " + userId);
        }
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id=?";
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
        String sql = "SELECT COUNT(*) FROM friends WHERE user_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId) >= 1;
    }

}
