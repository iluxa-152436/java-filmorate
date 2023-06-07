package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("InMemory")
public class InMemoryUserStorage implements UserStorage {
    private int id;
    private final Map<Integer, User> users;

    @Autowired
    public InMemoryUserStorage(Map<Integer, User> users) {
        this.users = users;
    }

    @Override
    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        saveUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public List<User> getUsers(Set<Integer> userIds) {
        return userIds.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsUser(int id) {
        return users.containsKey(id);
    }

    @Override
    public int getNexId() {
        return ++id;
    }

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }
}
