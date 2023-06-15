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

import java.util.*;
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
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "left join film_genre as fg on f.film_id=fg.film_id " +
                "where fg.genre_id = ? and EXTRACT(YEAR FROM f.release_date) = ? " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, genreId, releaseDate, limit);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, String releaseDate) {
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "where EXTRACT(YEAR FROM f.release_date) = ? " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, releaseDate, limit);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public List<Integer> getSortedFilmLikes(long limit, int genreId) {
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "left join film_genre as fg on f.film_id=fg.film_id " +
                "where fg.genre_id = ? " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, genreId, limit);
        return makeFilmLikesList(rowSet);
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
    public List<Integer> getSortedFilmLikes(long limit) {
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "group by f.film_id " +
                "order by count(l.user_id) desc " +
                "limit ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, limit);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public List<Integer> getSortedByLikesFilteredByFilmIds(String query, List<String> by) {
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
        SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sql, namedParameters);
        return makeFilmLikesList(rowSet);
    }

    @Override
    public List<Integer> getSortedFilmIdsFilteredByFilmIds(List<Integer> filmIds) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("ids", filmIds);
        String sql = "select f.film_id, count(l.user_id) from films as f " +
                "left join likes as l on f.film_id=l.film_id " +
                "where f.film_id in (:ids) " +
                "group by f.film_id " +
                "order by count(l.user_id) desc";
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
