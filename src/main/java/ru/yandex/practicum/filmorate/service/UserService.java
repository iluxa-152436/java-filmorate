package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.FindUserException;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private int id;
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        id = 0;
        this.storage = storage;
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public User createUser(User user) {
        user.setId(++id);
        checkName(user);
        checkLogin(user);
        storage.createUser(user);
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
        storage.updateUser(user);
        return user;
    }

    private void checkId(User user) {
        if (!storage.containsUser(user.getId())) {
            throw new FindUserException("Пользователь с id: " + user.getId() + " не найден");
        }
    }
}
