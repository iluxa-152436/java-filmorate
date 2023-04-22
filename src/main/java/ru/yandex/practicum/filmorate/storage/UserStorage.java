package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void createUser(User user);
    void updateUser(User user);
    Collection<User> getUsers();
    boolean containsUser(int id);
}
