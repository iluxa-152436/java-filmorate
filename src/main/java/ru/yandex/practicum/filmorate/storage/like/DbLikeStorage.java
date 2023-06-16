package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.LinkedList;
import java.util.List;

@Repository
@Qualifier("DB")
public class DbLikeStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public DbLikeStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, int genreId, String releaseDate) {
        String sql = "SELECT f.film_id, COUNT(l.user_id) FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id=l.film_id " +
                "LEFT JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, genreId, releaseDate, limit);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, String releaseDate) {
        String sql = "SELECT f.film_id, COUNT(l.user_id) FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id=l.film_id " +
                "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, releaseDate, limit);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, int genreId) {
        String sql = "SELECT f.film_id, COUNT(l.user_id) FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id=l.film_id " +
                "LEFT JOIN film_genre AS fg ON f.film_id=fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, genreId, limit);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public void saveLike(Like like) {
        String sql = "MERGE INTO likes(user_id, film_id) VALUES(?,?)";
        jdbcTemplate.update(sql, like.getUserId(), like.getFilmId());
    }

    @Override
    public void deleteLike(Like like) {
        String sql = "DELETE FROM likes WHERE user_id=? AND film_id=?";
        jdbcTemplate.update(sql, like.getUserId(), like.getFilmId());
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit) {
        String sql = "SELECT f.film_id, COUNT(l.user_id) FROM films AS f " +
                "LEFT JOIN likes AS l ON f.film_id=l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, limit);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public List<Integer> getSortedByLikesFilteredByFilmIds(String query, List<String> by) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("query",
                "%" + query.toLowerCase() + "%");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT f.film_id, COUNT(l.user_id) ");
        stringBuilder.append("FROM films AS f ");
        stringBuilder.append("LEFT JOIN likes AS l ON f.film_id = l.film_id ");
        if (by.contains("director") && by.contains("title")) {
            stringBuilder.append("LEFT JOIN film_director AS fd ON f.film_id = fd.film_id ");
            stringBuilder.append("LEFT JOIN directors AS d ON fd.director_id = d.director_id ");
            stringBuilder.append("WHERE LOWER(d.name) like :query OR LOWER(f.name) like :query ");
        } else if (by.contains("director")) {
            stringBuilder.append("LEFT JOIN film_director AS fd ON f.film_id = fd.film_id ");
            stringBuilder.append("LEFT JOIN directors AS d ON fd.director_id = d.director_id ");
            stringBuilder.append("WHERE LOWER(d.name) LIKE :query ");
        } else if (by.contains("title")) {
            stringBuilder.append("WHERE LOWER(f.name) LIKE :query ");
        }
        stringBuilder.append("GROUP BY f.film_id ");
        stringBuilder.append("ORDER BY COUNT(l.user_id) DESC");

        String sql = String.valueOf(stringBuilder);
        SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sql, namedParameters);
        return makeFilmLikesList(rowSet);
    }

    private List<Integer> makeFilmLikesList(SqlRowSet rowSet) {
        List<Integer> filmLikesList = new LinkedList<>();
        while (rowSet.next()) {
            Integer filmId = rowSet.getInt(1);
            filmLikesList.add(filmId);
        }
        return filmLikesList;
    }
}
