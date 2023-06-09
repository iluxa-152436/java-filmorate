package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@Qualifier("DB")
public class DbLikeStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbLikeStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        String sql = "select l.film_id, count(l.user_id) from likes as l " +
                "group by l.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, limit);
        return makeFilmLikesMap(rowSet);
    }

    @Override
    public Map<Integer, Integer> getSortedByLikesFilteredByFilmIds(Set<Integer> filmIds) {

        return null;
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
