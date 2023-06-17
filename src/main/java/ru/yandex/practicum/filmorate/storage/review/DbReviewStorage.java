package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundInDbException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("DB")
public class DbReviewStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public DbReviewStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review add(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("is_positive", review.getIsPositive())
                .addValue("film_id", review.getFilmId())
                .addValue("user_id", review.getUserId());
        int id = simpleJdbcInsert.executeAndReturnKey(params).intValue();
        return getById(id);
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content=?, is_positive=? " +
                "WHERE review_id=?";
        int result = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
        if (result == 0) {
            throw new NotFoundInDbException("Объекты для обновления не найдены");
        }
        return getById(review.getId());
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews " +
                "WHERE review_id=?";
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            throw new NotFoundInDbException("Объекты для удаления не найдены");
        }
    }

    @Override
    public Review getById(int id) {
        String sqlQuery = "SELECT r.review_id AS id, " +
                "r.content AS content, " +
                "r.is_positive AS is_positive, " +
                "r.user_id AS user_id, " +
                "r.film_id AS film_id," +
                "SUM(like_or_dislike) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN reviews_likes l ON r.review_id=l.review_id " +
                "WHERE r.review_id = ? " +
                "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapReview, id);
        } catch (DataAccessException e) {
            throw new NotFoundInDbException("Нет отзыва с таким id");
        }
    }

    private Review mapReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .id(resultSet.getInt("id"))
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }

    @Override
    public List<Review> getList(Integer filmId, int amount) {
        String sql;
        List<Review> reviews;
        if (filmId == null) {
            sql = "SELECT r.review_id AS id, " +
                    "r.content AS content, " +
                    "r.is_positive AS is_positive, " +
                    "r.user_id AS user_id, " +
                    "r.film_id AS film_id, " +
                    "CASE " +
                    "WHEN like_or_dislike is null THEN 0 " +
                    "ELSE SUM(like_or_dislike) " +
                    "END AS useful " +
                    "FROM reviews r " +
                    "LEFT JOIN reviews_likes l ON r.review_id=l.review_id " +
                    "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
            reviews = jdbcTemplate.query(sql, this::mapReview, amount);
        } else {
            sql = "SELECT r.review_id AS id, " +
                    "r.content AS content, " +
                    "r.is_positive AS is_positive, " +
                    "r.user_id AS user_id, " +
                    "r.film_id AS film_id," +
                    "CASE " +
                    "WHEN like_or_dislike is null THEN 0 " +
                    "ELSE SUM(like_or_dislike) " +
                    "END AS useful " +
                    "FROM reviews r " +
                    "LEFT JOIN reviews_likes l ON r.review_id=l.review_id " +
                    "WHERE r.film_id = ?" +
                    "GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
            reviews = jdbcTemplate.query(sql, this::mapReview, filmId, amount);
        }

        return reviews;
    }

    @Override
    public void containsReview(int id) {
        Integer rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reviews WHERE review_id=?",
                Integer.class,
                id);
        if (rowCount == null) {
            throw new NotFoundInDbException("Отзыва с id=" + id + " не существует");
        }
    }
}
