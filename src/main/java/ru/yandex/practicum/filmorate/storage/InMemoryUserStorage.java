package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;

    @Autowired
    public InMemoryUserStorage(Map<Integer, User> users) {
        this.users = users;
    }

    @Override
    public void createUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public void updateUser(User user) {
        createUser(user);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public Collection<User> getUsers(Set<Integer> userIds) {
        return userIds.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsUser(int id) {
        return users.containsKey(id);
    }

    @Override
    public User getUser(int userId) {
        return users.get(userId);
    }
}
