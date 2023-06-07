package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendService {
    private final UserService userService;
    private final FriendStorage friendStorage;

    @Autowired
    public FriendService(UserService userService, @Qualifier("DB") FriendStorage friendStorage) {
        this.userService = userService;
        this.friendStorage = friendStorage;
    }

    public void addFriend(int userId, int friendId) {
        userService.checkId(userId);
        userService.checkId(friendId);
        friendStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userService.checkId(userId);
        userService.checkId(friendId);
        friendStorage.deleteFriend(userId, friendId);
        friendStorage.deleteFriend(friendId, userId);
    }

    public Collection<User> getFriends(int userId) {
        userService.checkId(userId);
        if (friendStorage.hasFriends(userId)) {
            Set<Integer> userIds = friendStorage.getFriends(userId);
            return userService.getUsers(userIds);
        } else {
            return Collections.emptyList();
        }
    }

    public Collection<User> getMutualFriends(int userId, int otherUserId) {
        userService.checkId(userId);
        userService.checkId(otherUserId);
        if (friendStorage.hasFriends(userId) && friendStorage.hasFriends(otherUserId)) {
            Collection<Integer> friendsCollection1 = friendStorage.getFriends(userId);
            log.debug("Friends collection of user {} = {}", userId, friendsCollection1.toString());
            Collection<Integer> friendsCollection2 = friendStorage.getFriends(otherUserId);
            log.debug("Friends collection of user {} = {}", userId, friendsCollection2.toString());
            Set<Integer> userIds = friendsCollection1.stream()
                    .filter(friendsCollection2::contains)
                    .collect(Collectors.toSet());
            return userService.getUsers(userIds);
        } else {
            return Collections.emptyList();
        }
    }
}
