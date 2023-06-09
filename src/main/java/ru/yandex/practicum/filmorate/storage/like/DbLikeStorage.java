package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashMap;
import java.util.Map;

@Repository
@Qualifier("DB")
public class DbLikeStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Integer> getSortedFilmLikes(long limit, int genreId, String releaseDate) {
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "left join film_genre as fg on f.film_id=fg.film_id " +
                "where fg.genre_id = ? and EXTRACT(YEAR FROM f.release_date) = ? " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, genreId, releaseDate, limit);
        return makeFilmLikesMap(rowSet);
    }

    @Override
    public Map<Integer, Integer> getSortedFilmLikes(long limit, String releaseDate) {
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "where EXTRACT(YEAR FROM f.release_date) = ? " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, releaseDate, limit);
        return makeFilmLikesMap(rowSet);
    }

    @Override
    public Map<Integer, Integer> getSortedFilmLikes(long limit, int genreId) {
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "left join film_genre as fg on f.film_id=fg.film_id " +
                "where fg.genre_id = ? " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, genreId, limit);
        return makeFilmLikesMap(rowSet);
    }

    @Override
    public void saveLike(Like like) {
        String sql = "insert into likes(user_id, film_id) values(?,?)";
        jdbcTemplate.update(sql, like.getUserId(), like.getFilmId());
    }

    @Override
    public void deleteLike(Like like) {
        String sql = "delete from likes where user_id=? and film_id=?";
        jdbcTemplate.update(sql, like.getUserId(), like.getFilmId());
    }

    @Override
    public Map<Integer, Integer> getSortedFilmLikes(long limit) {
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, limit);
        return makeFilmLikesMap(rowSet);
    }

    private Map<Integer, Integer> makeFilmLikesMap(SqlRowSet rowSet) {
        Map<Integer, Integer> filmLikesList = new HashMap<>();

        while (rowSet.next()) {
            Integer filmId = rowSet.getInt(1);
            Integer count = rowSet.getInt(2);
            filmLikesList.put(filmId, count);
        }
        return filmLikesList;
    }
}
