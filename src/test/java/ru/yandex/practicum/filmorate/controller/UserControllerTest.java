package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class UserControllerTest {
    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc@abc.ru",
                "login",
                "name name",
                LocalDate.of(1989, 4, 13)));
        User user = restTemplate.postForObject("http://localhost:" + port + "/users", request, User.class);
        Assertions.assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("name name", user.getName());
        assertEquals("abc@abc.ru", user.getEmail());
        assertEquals("login", user.getLogin());
        assertEquals("1989-04-13", user.getBirthday().toString());
    }

    @Test
    void createUserEmptyName() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc@abc.ru",
                "login",
                "",
                LocalDate.of(1989, 4, 13)));
        User user = restTemplate.postForObject("http://localhost:" + port + "/users", request, User.class);
        Assertions.assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("login", user.getName());
        assertEquals("abc@abc.ru", user.getEmail());
        assertEquals("login", user.getLogin());
        assertEquals("1989-04-13", user.getBirthday().toString());
    }

    @Test
    void createUserNullName() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc@abc.ru",
                "login",
                null,
                LocalDate.of(1989, 4, 13)));
        User user = restTemplate.postForObject("http://localhost:" + port + "/users", request, User.class);
        Assertions.assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("login", user.getName());
        assertEquals("abc@abc.ru", user.getEmail());
        assertEquals("login", user.getLogin());
        assertEquals("1989-04-13", user.getBirthday().toString());
    }

    @Test
    void createUserEmptyLogin() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc@abc.ru",
                "login",
                "name name",
                LocalDate.of(1989, 4, 13)));
        User user = restTemplate.postForObject("http://localhost:" + port + "/users", request, User.class);
        Assertions.assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("name name", user.getName());
        assertEquals("abc@abc.ru", user.getEmail());
        assertEquals("login", user.getLogin());
        assertEquals("1989-04-13", user.getBirthday().toString());
    }

    @Test
    void updateUser() {
        User user = new User(1,
                "abc@abc.ru",
                "login",
                "name",
                LocalDate.of(1989, 4, 13));
        userService.setUsers(new HashMap<>(Map.of(1, user)));
        HttpEntity<User> request = new HttpEntity<>(new User(user.getId(),
                "updated@abc.ru",
                "updated",
                "updated",
                LocalDate.of(1989, 4, 13)));
        User updatedUser = restTemplate.exchange("http://localhost:" + port + "/users",
                HttpMethod.PUT,
                request,
                User.class).getBody();
        Assertions.assertNotNull(updatedUser);
        assertEquals(1, updatedUser.getId());
        assertEquals("updated", updatedUser.getName());
        assertEquals("updated@abc.ru", updatedUser.getEmail());
        assertEquals("updated", updatedUser.getLogin());
        assertEquals("1989-04-13", updatedUser.getBirthday().toString());
    }

    @Test
    void createUserBadEmail() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc.ru",
                "login",
                "name name",
                LocalDate.of(1989, 4, 13)));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/users",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createUserBadLoginWithSpace() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc@abc.ru",
                "l ogin",
                "name name",
                LocalDate.of(1989, 4, 13)));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/users",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createUserEmptyEmail() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "",
                "login",
                "name name",
                LocalDate.of(1989, 4, 13)));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/users",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createUserBadBirthdayInFuture() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc@abc.ru",
                "login",
                "name name",
                LocalDate.now().plusDays(1)));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/users",
                request,
                String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void createUserBirthdayToday() {
        HttpEntity<User> request = new HttpEntity<>(new User(1,
                "abc@abc.ru",
                "login",
                "name name",
                LocalDate.now()));
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/users",
                request,
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void getUsers() {
        User user = new User(1,
                "abc@abc.ru",
                "login",
                "name name",
                LocalDate.now());
        userService.setUsers(Map.of(1, user));
        ResponseEntity<User[]> response = restTemplate.getForEntity("http://localhost:" + port + "/users",
                User[].class);
        User[] users = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(users);
        assertEquals(user, users[0]);
    }
}