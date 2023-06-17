package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FriendService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.yandex.practicum.filmorate.controller.UserControllerTest.getUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedControllerTest {
    final UserService userService;
    final FriendService friendService;
    final FeedService feedService;

    @Test
    void getFeedsByUserIdShouldReturnFeed() {
        User user1 = getUser();
        userService.createUser(user1);
        User user2 = getUser();
        userService.createUser(user2);

        friendService.addFriend(1, 2);

        Optional<Integer> optionalFeedsSize = Optional.of(feedService.getFeedsByUserId(1).size());
        assertThat(optionalFeedsSize).isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(1));

        friendService.deleteFriend(1, 2);

        optionalFeedsSize = Optional.of(feedService.getFeedsByUserId(1).size());
        assertThat(optionalFeedsSize).isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(2));
    }
}
