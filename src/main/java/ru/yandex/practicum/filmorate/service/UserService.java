package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.FindUserException;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("DB") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(int userId) {
        checkId(userId);
        return userStorage.getUser(userId);
    }

    public List<User> getUsers(Set<Integer> userIds) {
        return userStorage.getUsers(userIds);
    }

    public User createUser(User user) {
        user.setId(userStorage.getNexId());
        checkName(user);
        userStorage.saveUser(user);
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

    protected void checkId(int userId) {
        if (!userStorage.containsUser(userId)) {
            throw new FindUserException("Пользователь с id: " + userId + " не найден");
        }
    }
}
