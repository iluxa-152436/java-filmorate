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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                "left join likes as l on f.film_id = l.film_id " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, limit);
        return makeFilmLikesMap(rowSet);
    }

    @Override
    public Map<Integer, Integer> getSortedByLikesFilteredByFilmIds(String query, List<String> by) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("query",
                "%" + query.toLowerCase() + "%");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("select f.film_id, count(l.user_id) ");
        stringBuilder.append("from films as f ");
        stringBuilder.append("left join likes as l on f.film_id = l.film_id ");
        if (by.contains("director") && by.contains("title")) {
            stringBuilder.append("left join film_director as fd on f.film_id = fd.film_id ");
            stringBuilder.append("left join directors as d on fd.director_id = d.director_id ");
            stringBuilder.append("where LOWER(d.name) like :query or LOWER(f.name) like :query ");
        } else if (by.contains("director")) {
            stringBuilder.append("left join film_director as fd on f.film_id = fd.film_id ");
            stringBuilder.append("left join directors as d on fd.director_id = d.director_id ");
            stringBuilder.append("where LOWER(d.name) like :query ");
        } else if (by.contains("title")) {
            stringBuilder.append("where LOWER(f.name) like :query ");
        }
        stringBuilder.append("group by f.film_id ");
        stringBuilder.append("order by count(l.user_id) desc");

        String sql = String.valueOf(stringBuilder);
        return makeFilmLikesMap(namedParameterJdbcTemplate.queryForRowSet(sql, namedParameters));
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
