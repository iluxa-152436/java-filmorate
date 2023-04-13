package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.practicum.filmorate.exception.FindUserException;
import ru.yandex.practicum.filmorate.exception.ValidateUserException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    private Map<Integer, User> users;
    private int id;

    public UserService() {
        id = 0;
        users = new HashMap<>();
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public User createUser(User user) throws ValidateUserException {
        user.setId(++id);
        checkName(user);
        checkLogin(user);
        users.put(user.getId(), user);
        return user;
    }

    private void checkLogin(User user) throws ValidateUserException {
        if (user.getLogin().contains(" ")) {
            throw new ValidateUserException("Name не должен содержать пробелы");
        }
    }

    private void checkName(User user) {
        if (StringUtils.isBlank(user.getName())) {
            user.setName(user.getLogin());
        }
    }

    public User updateUser(User user) throws ValidateUserException, FindUserException {
        checkId(user);
        checkName(user);
        checkLogin(user);
        users.put(user.getId(), user);
        return user;
    }

    private void checkId(User user) throws FindUserException {
        if (!users.containsKey(user.getId())) {
            throw new FindUserException("Пользователь с id: " + user.getId() + " не найден");
        }
    }
}
