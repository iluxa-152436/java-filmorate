package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    void createUser(User user);

    void updateUser(User user);

    Collection<User> getAllUsers();

    Collection<User> getUsers(Set<Integer> userIds);

    User getUser(int userId);

    boolean containsUser(int userId);
}
