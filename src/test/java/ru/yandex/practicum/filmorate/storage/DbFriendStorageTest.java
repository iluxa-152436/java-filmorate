package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
class DbFriendStorageTest {
    @Autowired
    @Qualifier("DB")
    private FriendStorage friendStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void deleteTestData() {
        jdbcTemplate.update("DELETE FROM films;" +
                "DELETE FROM friends;" +
                "DELETE FROM app_users;");
    }

    @Test
    @Sql("classpath:test_data.sql")
    void addFriend() {
        friendStorage.addFriend(1, 2);
        assertEquals(2, jdbcTemplate.queryForObject("select count(*) from friends", Integer.class));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void deleteFriend() {
        friendStorage.deleteFriend(2, 1);
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from friends", Integer.class));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getFriends() {
        assertEquals(Set.of(1), friendStorage.getFriends(2));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void hasFriends() {
        assertTrue(friendStorage.hasFriends(2));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void addFriendShouldAddFeed() {
        friendStorage.addFriend(1, 2);
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from feed", Integer.class));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void deleteFriendShouldAddFeed() {
        friendStorage.addFriend(1, 2);
        friendStorage.deleteFriend(1, 2);
        assertEquals(2, jdbcTemplate.queryForObject("select count(*) from feed", Integer.class));
    }
}