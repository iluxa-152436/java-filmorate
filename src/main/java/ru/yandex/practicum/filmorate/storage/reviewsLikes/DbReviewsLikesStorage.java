package ru.yandex.practicum.filmorate.storage.reviewsLikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Qualifier("DB")
public class DbReviewsLikesStorage implements ReviewsLikesStorage{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbReviewsLikesStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(int reviewId, int userId) {
        String sql = "INSERT INTO reviews_likes VALUES (?,?,1) ";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        String sql = "INSERT INTO reviews_likes VALUES (?,?,-1) ";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id=? AND user_id=? AND like_or_dislike=1";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        String sql = "DELETE FROM reviews_likes WHERE review_id=? AND user_id=? AND like_or_dislike=-1";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    public Optional<Integer> isLikeDislikeExist (int reviewId, int userId) {
        Integer count = jdbcTemplate.queryForObject("SELECT like_or_dislike FROM reviews_likes " +
                "WHERE review_id=? AND user_id=?", Integer.class, reviewId, userId);
        if (count==null) return Optional.empty();
        else return Optional.of(count);
    }
}
