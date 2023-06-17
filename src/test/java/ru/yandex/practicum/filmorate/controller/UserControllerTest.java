package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private FriendService friendService;

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
    void updateUser() throws ValidateUserException {
        User user = getUser();
        userService.createUser(user);
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

    public static User getUser() {
        return new User(1,
                "abc@abc.ru",
                "login",
                "name",
                LocalDate.of(1989, 4, 13));
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
        User user = getUser();
        userService.createUser(user);
        ResponseEntity<User[]> response = restTemplate.getForEntity("http://localhost:" + port + "/users",
                User[].class);
        User[] users = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(users);
        assertEquals(user, users[0]);
    }

    @Test
    void addFriend() {
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/users/1/friends/2",
                HttpMethod.PUT,
                null,
                String.class);
        ResponseEntity<String> response2 = restTemplate.exchange("http://localhost:" + port + "/users/2/friends/1",
                HttpMethod.PUT,
                null,
                String.class);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, friendService.getFriends(1).size());
        assertEquals(1, friendService.getFriends(2).size());
    }

    @Test
    void deleteFriend() {
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        friendService.addFriend(1, 2);
        assertEquals(1, friendService.getFriends(1).size());
        assertEquals(0, friendService.getFriends(2).size());
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/users/1/friends/2",
                HttpMethod.DELETE,
                null,
                String.class);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, friendService.getFriends(1).size());
        assertEquals(0, friendService.getFriends(2).size());
    }

    @Test
    void getFriends() {
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        friendService.addFriend(1, 2);
        ResponseEntity<User[]> response = restTemplate.getForEntity("http://localhost:" + port + "/users/1/friends",
                User[].class);
        User[] users = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, users[0].getId());
    }

    @Test
    void getCommonFriends() {
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        User user3 = getUser();
        userService.createUser(user3);
        friendService.addFriend(1, 2);
        friendService.addFriend(2, 1);
        friendService.addFriend(2, 3);
        friendService.addFriend(3, 2);
        ResponseEntity<User[]> response = restTemplate.getForEntity("http://localhost:"
                        + port
                        + "/users/1/friends/common/3",
                User[].class);
        User[] users = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, users[0].getId());
    }

    @Test
    void deleteUserById() {
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);
        User user3 = getUser();
        userService.createUser(user3);

        Optional<Integer> optionalUserSize = Optional.of(userService.getUsers().size());
        assertThat(optionalUserSize).isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(3));

        userService.deleteUserById(userService.getUsers().get(0).getId());

        optionalUserSize = Optional.of(userService.getUsers().size());
        assertThat(optionalUserSize)
                .isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(2));
    }
}