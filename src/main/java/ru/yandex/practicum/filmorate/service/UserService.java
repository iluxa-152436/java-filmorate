package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.FindUserException;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private int id;
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendStorage friendStorage) {
        id = 0;
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(int userId) {
        checkId(userId);
        return userStorage.getUser(userId);
    }

    public User createUser(User user) {
        user.setId(++id);
        checkName(user);
        checkLogin(user);
        userStorage.createUser(user);
        return user;
    }

    private void checkLogin(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidateUserException("Name не должен содержать пробелы");
        }
    }

    private void checkName(User user) {
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public User updateUser(User user) {
        checkId(user);
        checkName(user);
        checkLogin(user);
        userStorage.updateUser(user);
        return user;
    }

    private void checkId(User user) {
        if (!userStorage.containsUser(user.getId())) {
            throw new FindUserException("Пользователь с id: " + user.getId() + " не найден");
        }
    }

    private void checkId(int userId) {
        if (!userStorage.containsUser(userId)) {
            throw new FindUserException("Пользователь с id: " + userId + " не найден");
        }
    }

    public void addFriend(int userId, int friendId) {
        checkId(userId);
        checkId(friendId);
        friendStorage.addFriend(userId, friendId);
        friendStorage.addFriend(friendId, userId);
    }

    public void deleteFriend(int userId, int friendId) {
        checkId(userId);
        checkId(friendId);
        friendStorage.deleteFriend(userId, friendId);
        friendStorage.deleteFriend(friendId, userId);
    }

    public Collection<User> getFriends(int userId) {
        checkId(userId);
        if (friendStorage.hasFriends(userId)) {
            Set<Integer> userIds = friendStorage.getFriends(userId);
            return userStorage.getUsers(userIds);
        } else {
            return Collections.emptyList();
        }
    }

    public Collection<User> getMutualFriends(int userId, int otherUserId) {
        checkId(userId);
        checkId(otherUserId);
        if (friendStorage.hasFriends(userId) && friendStorage.hasFriends(otherUserId)) {
            Collection<Integer> friendsCollection1 = friendStorage.getFriends(userId);
            Collection<Integer> friendsCollection2 = friendStorage.getFriends(otherUserId);
            Set<Integer> userIds = friendsCollection1.stream()
                    .filter(friendsCollection2::contains)
                    .collect(Collectors.toSet());
            return userStorage.getUsers(userIds);
        } else {
            return Collections.emptyList();
        }
    }
}
