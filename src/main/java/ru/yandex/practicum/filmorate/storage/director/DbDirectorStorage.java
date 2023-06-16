package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindDirectorException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class DbDirectorStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbDirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "SELECT director_id, name AS director_name FROM directors";
        return jdbcTemplate.query(sqlQuery, DbDirectorStorage::makeDirector);
    }

    @Override
    public Director getDirectorById(Integer id) {
        String sqlQuery = "SELECT director_id, name AS director_name FROM directors WHERE director_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, DbDirectorStorage::makeDirector, id);
    }

    @Override
    public Director createDirector(Director director) {
        if (isDirectorExists(director.getName())) {
            throw new FindDirectorException("Director with name " + director.getName() + " already exists");
        }
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator connection = con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        };

        jdbcTemplate.update(connection, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        checkDirector(director.getId());
        String sqlQuery = "UPDATE directors SET name=? WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirectorById(Integer id) {
        checkDirector(id);
        String sqlQuery = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getFilmsOfDirectorById(Integer id) {
        checkDirector(id);
        String sqlQuery = ("SELECT f.film_id AS film_id, " +
                "f.name AS film_name, " +
                "f.release_date AS release_date, " +
                "f.description AS description, " +
                "f.duration AS duration, " +
                "f.mpa_rating_id AS mpa_rating_id, " +
                "m.name AS mpa_name, " +
                "g.genre_id AS genre_id, " +
                "g.name AS genre_name, " +
                "d.director_id AS director_id, " +
                "d.name AS director_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.mpa_rating_id " +
                "LEFT JOIN film_director AS fd ON fd.film_id = f.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE d.director_id = ? " +
                "GROUP BY film_id");
        return makeFilmList(jdbcTemplate.queryForRowSet(sqlQuery, id));
    }

    private void checkDirector(Integer id) {
        if (jdbcTemplate.queryForObject("SELECT COUNT(*) FROM directors WHERE director_id=?",
                Integer.class, id) != 1) {
            throw new FindDirectorException("Director with id " + id + " not found");
        }
    }

    private boolean isDirectorExists(String name) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM directors WHERE name=?", Integer.class, name) == 1;
    }

    private static List<Film> makeFilmList(SqlRowSet rowSet) {
        Map<Integer, Film> films = new HashMap<>();

        while (rowSet.next()) {
            //получить все значения из строки
            int id = rowSet.getInt(1);
            String name = rowSet.getString(2);
            LocalDate releaseDate = rowSet.getDate(3).toLocalDate();
            String description = rowSet.getString(4);
            int duration = rowSet.getInt(5);
            int mpaId = rowSet.getInt(6);
            String mpaName = rowSet.getString(7);
            int genreId = rowSet.getInt(8);
            String genreName = rowSet.getString(9);
            int directorId = rowSet.getInt(10);
            String directorName = rowSet.getString(11);

            //определяем был ли такой фильм в списке результата
            Film film = films.get(id);
            if (film == null) {
                //создаем новый объект жанр
                Set<Genre> genres;
                if (genreId != 0) {
                    genres = new HashSet<>();
                    genres.add(new Genre(genreId, genreName));
                } else {
                    genres = Collections.emptySet();
                }
                //создаем новый объект mpa
                MpaRating mpa;
                if (mpaId != 0) {
                    mpa = new MpaRating(mpaId, mpaName);
                } else {
                    mpa = null;
                }

                Set<Director> directors;
                if (directorId != 0) {
                    directors = new HashSet<>();
                    directors.add(new Director(directorId, directorName));
                } else {
                    directors = Collections.emptySet();
                }

                film = new Film(id, name, description, releaseDate, duration, genres, mpa, directors);

                //сохраняем фильм в список результата
                films.put(film.getId(), film);
            } else {
                //к существующему фильму добавляем еще один жанр
                if (genreId != 0) {
                    film.getGenres().add(new Genre(genreId, genreName));
                }
                if (directorId != 0) {
                    film.getDirectors().add(new Director(directorId, directorName));
                }
            }
        }

        return List.copyOf(films.values());
    }

    private static Director makeDirector(ResultSet resultSet, int rowNum) throws SQLException {
        int directorId = resultSet.getInt("director_id");
        String directorName = resultSet.getString("director_name");
        if (directorId == 0) {
            throw new FindDirectorException("Not found director with this id");
        }
        return new Director(directorId, directorName);
    }
}
