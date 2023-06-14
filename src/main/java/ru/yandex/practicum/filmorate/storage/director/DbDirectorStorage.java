package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindDirectorException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("DB")
public class DbDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbDirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveDirector(Director director) {
        String sql = "insert into directors(director_id, name) values(?,?)";
        jdbcTemplate.update(sql, director.getId(), director.getName());
    }

    @Override
    public Optional<Director> getDirector(int directorId) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select * from directors where director_id=?",
                (rs, rowNum) -> makeDirector(rs),
                directorId));
    }

    @Override
    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("select * from directors", (rs, rowNum) -> makeDirector(rs));
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        int id = rs.getInt("director_id");
        String name = rs.getString("name");
        return new Director(id, name);
    }

    @Override
    public int getNextId() {
        return jdbcTemplate.query("select count(director_id), max(director_id), from directors",
                (rs, rowNum) -> makeNextId(rs)).get(0);
    }

    @Override
    public void updateDirector(Director director) {
        String sql = "update directors set name=? where director_id=?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
    }

    @Override
    public boolean containsDirector(int id) {
        return jdbcTemplate.queryForObject("select count(*) from directors where director_id=?",
                Integer.class, id) == 1;
    }

    @Override
    public void deleteDirectorById(int directorId) {
        String sql = "delete from directors where director_id=?";
        if (jdbcTemplate.update(sql, directorId) == 0) {
            throw new FindDirectorException("Режиссер с id = " + directorId + " не найден, удаление невозможно.");
        }
    }

    @Override
    public List<Integer> getFilmIdsByDirectorId(Integer directorId) {
        String sqlQuery = ("select fd.film_id as film_id, " +
                "from film_director as fd " +
                "where fd.director_id = ?");
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, directorId);
    }

    private Integer makeNextId(ResultSet rs) throws SQLException {
        int nextId = 1;
        if (rs.getInt(1) >= 1) {
            nextId = rs.getInt(2) + 1;
        }
        return nextId;
    }
}
