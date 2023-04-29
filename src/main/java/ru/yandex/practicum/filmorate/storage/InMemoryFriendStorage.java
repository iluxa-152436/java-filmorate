package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class InMemoryFriendStorage implements FriendStorage {
    private final Map<Integer, Set<Integer>> friends;

    @Autowired
    public InMemoryFriendStorage(Map<Integer, Set<Integer>> friends) {
        this.friends = friends;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        Set<Integer> friendsSet = friends.computeIfAbsent(userId, k -> new HashSet<>());
        friendsSet.add(friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        friends.get(userId).remove(friendId);
        if (friends.get(userId).isEmpty()) {
            friends.remove(userId);
        }
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        return friends.get(userId);
    }

    @Override
    public boolean hasFriends(int userId) {
        return friends.containsKey(userId);
    }
}
