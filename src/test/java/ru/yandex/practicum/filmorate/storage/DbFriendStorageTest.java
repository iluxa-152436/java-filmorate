package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbFriendStorageTest {
    @Autowired
    @Qualifier("DB")
    private FriendStorage friendStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Order(1)
    @Sql("classpath:test_data.sql")
    void addFriend() {
        friendStorage.addFriend(1, 2);
        assertEquals(2, jdbcTemplate.queryForObject("select count(*) from friends", Integer.class));
    }

    @Test
    @Order(2)
    void deleteFriend() {
        friendStorage.deleteFriend(1, 2);
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from friends", Integer.class));
    }

    @Test
    @Order(3)
    void getFriends() {
        assertEquals(Set.of(1), friendStorage.getFriends(2));
    }

    @Test
    @Order(4)
    void hasFriends() {
        assertTrue(friendStorage.hasFriends(2));
    }
}