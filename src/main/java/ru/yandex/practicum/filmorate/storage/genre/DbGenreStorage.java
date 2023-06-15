package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.exception.NotFoundInDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("DB")
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveGenre(Genre genre) {
        String sql = "insert into genres(genre_id, name) values(?,?)";
        jdbcTemplate.update(sql, genre.getId(), genre.getName());
    }

    @Override
    public Optional<Genre> getGenre(int genreId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from genres where genre_id=?",
                    (rs, rowNum) -> makeGenre(rs), genreId));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundInDB("Жанр не найден");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("genre_id");
        String name = rs.getString("name");
        return new Genre(id, name);
    }
}
