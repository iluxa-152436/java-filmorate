package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.feedstorage.DbFeedStorageImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class DbFeedStorageImplTest {

    final DbFeedStorageImpl dbFeedStorage;

    final UserService userService;

    final ReviewService reviewService;

    final FilmService filmService;

    User user1;
    User user2;

    Review review;

    Film film;


    @BeforeEach
    public void setup() {
        user1 = User.builder()
                .id(1)
                .email("first@user.ru")
                .login("first_user")
                .name("name")
                .birthday(LocalDate.of(1989, 4, 13))
                .build();

        user2 = User.builder()
                .id(2)
                .email("second@user.ru")
                .login("second_user")
                .name("name")
                .birthday(LocalDate.of(1990, 01, 21))
                .build();

        film = Film.builder()
                .id(1)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.now())
                .duration(30)
                .mpa(new MpaRating(1, "G"))
                .build();

        review = Review.builder()
                .id(1)
                .content("Test Content")
                .userId(1)
                .filmId(1)
                .isPositive(true)
                .useful(5)
                .build();

        userService.createUser(user1);
        userService.createUser(user2);
        filmService.createFilm(film);
    }

    @Test
    void addFeedShouldAddFeed() {
        Optional<Integer> optionalFeedsSize = Optional.of(dbFeedStorage.getFeedsByUserId(user1.getId()).size());

        assertThat(optionalFeedsSize).isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(0));

        dbFeedStorage.addFeed(user1.getId(), user2.getId(), "FRIEND", "ADD");

        optionalFeedsSize = Optional.of(dbFeedStorage.getFeedsByUserId(user1.getId()).size());

        assertThat(optionalFeedsSize).isPresent()
                .hasValueSatisfying(size -> AssertionsForClassTypes.assertThat(size).isEqualTo(1));

    }
}
