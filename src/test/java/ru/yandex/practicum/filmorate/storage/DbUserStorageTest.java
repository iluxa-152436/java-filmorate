package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class DbUserStorageTest {
    @Autowired
    @Qualifier("DB")
    private UserStorage userStorage;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void deleteTestData() {
        jdbcTemplate.update("DELETE FROM films;"+
                "DELETE FROM friends;" +
                "DELETE FROM app_users;");
    }

    @Test
    @Sql("classpath:test_data.sql")
    void saveUser() {
        User user = new User(3,
                "abc@abc.ru",
                "login",
                "name name",
                LocalDate.of(1989, 4, 13));
        userStorage.saveUser(user);
        assertEquals(3, jdbcTemplate.queryForObject("select count(*) from app_users", Integer.class));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void updateUser() {
        User user = new User(2,
                "update@abc.ru",
                "update",
                "update",
                LocalDate.of(1989, 4, 13));
        userStorage.updateUser(user);
        assertEquals(user, userStorage.getUser(2));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        assertEquals(2, users.size());
        assertTrue(users.contains(new User(1,
                "email1@email.ru",
                "login1",
                "name1",
                LocalDate.of(1999, 4, 30))));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getUsers() {
        List<User> users = userStorage.getUsers(Set.of(1, 2));
        assertEquals(2, users.size());
        assertTrue(users.contains(new User(1,
                "email1@email.ru",
                "login1",
                "name1",
                LocalDate.of(1999, 4, 30))));
        assertTrue(users.contains(new User(2,
                "email2@email.ru",
                "login2",
                "name2",
                LocalDate.of(1999, 4, 30))));
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getUser() {
        User user = userStorage.getUser(1);
        assertEquals(new User(1,
                "email1@email.ru",
                "login1",
                "name1",
                LocalDate.of(1999, 4, 30)), user);
    }

    @Test
    @Sql("classpath:test_data.sql")
    void containsUser() {
        userStorage.containsUser(1);
    }

    @Test
    @Sql("classpath:test_data.sql")
    void getNexId() {
        assertEquals(3, userStorage.getNexId());
    }
}