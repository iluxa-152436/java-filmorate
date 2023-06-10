package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    void saveUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    List<User> getUsers(Set<Integer> userIds);

    User getUser(int userId);

    boolean containsUser(int userId);

    int getNexId();
    void deleteUserById(int userId);
}
