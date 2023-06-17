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
import java.util.Optional;

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
    public List<Integer> getSortedFilmLikes(long limit, Optional<Integer> genreId, Optional<String> releaseDate) {
        StringBuilder stringBuilder = new StringBuilder("SELECT f.film_id, COUNT(l.user_id) FROM films AS f ");
        stringBuilder.append("LEFT JOIN likes AS l ON f.film_id=l.film_id ");
        stringBuilder.append("LEFT JOIN film_genre AS fg ON f.film_id=fg.film_id ");
        switch (checkFilterOptions(genreId, releaseDate)) {
            case GENRE:
                stringBuilder.append("WHERE fg.genre_id = ? ");
                break;
            case RELEASE_DATE:
                stringBuilder.append("WHERE EXTRACT(YEAR FROM f.release_date) = ? ");
                break;
            case GENRE_AND_RELEASE_DATE:
                stringBuilder.append("WHERE fg.genre_id = ? AND EXTRACT(YEAR FROM f.release_date) = ? ");
                break;
            case EMPTY_FILTER:
        }
        stringBuilder.append("GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?");
        String sql = stringBuilder.toString();
        switch (checkFilterOptions(genreId, releaseDate)) {
            case GENRE:
                return makeFilmLikesList(jdbcTemplate.queryForRowSet(sql, genreId.get(), limit));
            case RELEASE_DATE:
                return makeFilmLikesList(jdbcTemplate.queryForRowSet(sql, releaseDate.get(), limit));
            case GENRE_AND_RELEASE_DATE:
                return makeFilmLikesList(jdbcTemplate.queryForRowSet(sql, genreId.get(), releaseDate.get(), limit));
            case EMPTY_FILTER:
                return makeFilmLikesList(jdbcTemplate.queryForRowSet(sql, limit));
            default:
                throw new UnsupportedOperationException("Параметр фильтрации не поддерживается");
        }
    }

    private FilterOptions checkFilterOptions(Optional<Integer> genreId, Optional<String> releaseDate) {
        if (genreId.isEmpty() && releaseDate.isEmpty()) {
            return FilterOptions.EMPTY_FILTER;
        } else if (genreId.isPresent() && releaseDate.isPresent()) {
            return FilterOptions.GENRE_AND_RELEASE_DATE;
        } else if (genreId.isEmpty()) {
            return FilterOptions.RELEASE_DATE;
        } else {
            return FilterOptions.GENRE;
        }
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

    private enum FilterOptions {
        GENRE_AND_RELEASE_DATE,
        RELEASE_DATE,
        GENRE,
        EMPTY_FILTER
    }
}
