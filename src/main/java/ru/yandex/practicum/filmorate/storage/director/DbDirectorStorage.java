package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
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
        String sqlQuery = "select director_id, name as director_name from directors";
        return jdbcTemplate.query(sqlQuery, DbDirectorStorage::makeDirector);
    }

    @Override
    public Director getDirectorById(Integer id) {
        String sqlQuery = "select director_id, name as director_name from directors where director_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, DbDirectorStorage::makeDirector, id);
    }

    @Override
    public Director createDirector(Director director) {
        if (!checkDirector(director.getName())) {
            String sqlQuery = "insert into directors (name) " +
                    "values (?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            PreparedStatementCreator connection = con -> {
                PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"director_id"});
                stmt.setString(1, director.getName());
                return stmt;

            };
            jdbcTemplate.update(connection, keyHolder);
            director.setId(keyHolder.getKey().intValue());
            return director;
        } else {
            throw new FindDirectorException("Director with name " + director.getName() + " already exists");
        }
    }

    @Override
    public Director updateDirector(Director director) {
        if (checkDirector(director.getId())) {
            String sqlQuery = "update directors set name=? where director_id = ?";
            jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
            return director;
        } else {
            throw new DirectorNotFoundException("Director with id " + director.getId() + " not found");
        }
    }

    @Override
    public void deleteDirectorById(Integer id) {
        if (checkDirector(id)) {
            String sqlQuery = "delete from directors where director_id = ?";
            jdbcTemplate.update(sqlQuery, id);
        } else {
            throw new DirectorNotFoundException("Director with id " + id + " not found");
        }
    }

    @Override
    public List<Film> getFilmsOfDirectorById(Integer id) {
        if (checkDirector(id)) {
            String sqlQuery = ("select f.film_id as film_id, " +
                    "f.name as film_name, " +
                    "f.release_date as release_date, " +
                    "f.description as description, " +
                    "f.duration as duration, " +
                    "f.mpa_rating_id as mpa_rating_id, " +
                    "m.name as mpa_name, " +
                    "g.genre_id as genre_id, " +
                    "g.name as genre_name, " +
                    "l.user_id as user_id, " +
                    "d.director_id as director_id, " +
                    "d.name as director_name " +
                    "from films as f " +
                    "left join film_genre as fg on f.film_id = fg.film_id " +
                    "left join genres as g on fg.genre_id = g.genre_id " +
                    "left join mpa_ratings as m on f.mpa_rating_id = m.mpa_rating_id " +
                    "left join film_director as fd on fd.film_id = f.film_id " +
                    "left join directors as d on fd.director_id = d.director_id " +
                    "left join likes as l on f.film_id = l.film_id " +
                    "where d.director_id = ? " +
                    "group by film_id");
            return makeFilmList(jdbcTemplate.queryForRowSet(sqlQuery, id));
        } else {
            throw new DirectorNotFoundException("Director with id " + id + " not found");
        }
    }

    private boolean checkDirector(String name) {
        return jdbcTemplate.queryForObject("select count(*) from directors where name=?",
                Integer.class, name) == 1;
    }

    private boolean checkDirector(Integer id) {
        return jdbcTemplate.queryForObject("select count(*) from directors where director_id=?",
                Integer.class, id) == 1;
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
            int userLikeId = rowSet.getInt(10);
            int directorId = rowSet.getInt(11);
            String directorName = rowSet.getString(12);

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

                Set<Integer> likes;
                if (userLikeId != 0) {
                    likes = new HashSet<>();
                    likes.add(userLikeId);
                } else {
                    likes = Collections.emptySet();
                }

                film = new Film(id, name, description, releaseDate, duration, genres, mpa, directors, likes);

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
                if (userLikeId != 0) {
                    film.getLikes().add(userLikeId);
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
