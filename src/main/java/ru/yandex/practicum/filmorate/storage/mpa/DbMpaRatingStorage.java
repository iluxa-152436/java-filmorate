package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindGenreException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("DB")
public class DbMpaRatingStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpaRatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveMpaRating(MpaRating mpaRating) {
        String sql = "INSERT INTO mpa_ratings(mpa_rating_id, name) VALUES(?,?)";
        jdbcTemplate.update(sql, mpaRating.getId(), mpaRating.getName());
    }

    @Override
    public Optional<MpaRating> getMpaRating(int mpaRatingId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM mpa_ratings WHERE mpa_rating_id =?",
                    (rs, rowNum) -> makeMpaRating(rs), mpaRatingId));
        } catch (EmptyResultDataAccessException e) {
            throw new FindGenreException("Жанр не найден");
        }
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpaRating(rs));
    }

    private MpaRating makeMpaRating(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_rating_id");
        String name = rs.getString("name");
        return new MpaRating(id, name);
    }
}
