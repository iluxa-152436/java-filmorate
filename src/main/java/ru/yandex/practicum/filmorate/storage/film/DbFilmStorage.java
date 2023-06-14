package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@Qualifier("DB")
@Slf4j
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveFilm(Film film) {
        String sqlFilms = "insert into films(film_id, name, release_date, description, duration, mpa_rating_id) " +
                "values(?,?,?,?,?,?)";
        if (film.getMpa() != null) {
            jdbcTemplate.update(sqlFilms,
                    film.getId(),
                    film.getName(),
                    film.getReleaseDate(),
                    film.getDescription(),
                    film.getDuration(),
                    film.getMpa().getId());
        } else {
            jdbcTemplate.update(sqlFilms,
                    film.getId(),
                    film.getName(),
                    film.getReleaseDate(),
                    film.getDescription(),
                    film.getDuration(),
                    null);
        }
        saveFilmGenre(film);
        saveFilmDirector(film);
    }

    @Override
    public void updateFilm(Film film) {
        String sqlFilm = "update films set name=?, release_date=?, description=?, duration=?, mpa_rating_id=? " +
                "where film_id=?";
        if (film.getMpa() != null) {
            jdbcTemplate.update(sqlFilm,
                    film.getName(),
                    film.getReleaseDate(),
                    film.getDescription(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
        } else {
            jdbcTemplate.update(sqlFilm,
                    film.getName(),
                    film.getReleaseDate(),
                    film.getDescription(),
                    film.getDuration(),
                    null,
                    film.getId());
        }

        deleteFilmGenre(film);
        saveFilmGenre(film);
        deleteFilmDirector(film);
        saveFilmDirector(film);
    }

    private void saveFilmDirector(Film film) {
        log.debug("Film contains directors: {}", film.getDirectors().toString());
        if (!film.getDirectors().isEmpty()) {
            String sql = "insert into film_director(film_id, director_id) values(?,?)";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sql, film.getId(), director.getId());
            }
        }
    }

    private void deleteFilmDirector(Film film) {
        String sql = "delete from film_director where film_id=?";
        jdbcTemplate.update(sql, film.getId());
    }

    private void deleteFilmGenre(Film film) {
        String sqlRemoveFilmGenre = "delete from film_genre where film_id=?";
        jdbcTemplate.update(sqlRemoveFilmGenre, film.getId());
    }

    private void saveFilmGenre(Film film) {
        log.debug("Film contains genres: {}", film.getGenres().toString());
        if (!film.getGenres().isEmpty()) {
            String sqlFilmGenre = "insert into film_genre(film_id, genre_id) values(?,?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlFilmGenre, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public List<Film> getFilms() {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select f.film_id as film_id, " +
                "f.name as filmname, " +
                "f.release_date as release_date, " +
                "f.description as description, " +
                "f.duration as duration, " +
                "f.mpa_rating_id as mpa_rating_id, " +
                "g.name as name, " +
                "g.genre_id as genre_id, " +
                "m.name as mpa_rating_name, " +
                "fd.director_id as director_id, " +
                "d.name as directorname " +
                "from films as f " +
                "left join film_genre as fg on f.film_id = fg.film_id " +
                "left join genres as g on fg.genre_id = g.genre_id " +
                "left join mpa_ratings m on f.mpa_rating_id = m.mpa_rating_id " +
                "left join film_director as fd on f.film_id = fd.film_id " +
                "left join directors as d on fd.director_id = d.director_id "+
                "order by film_id, genre_id");
        return makeFilmList(rowSet);
    }

    @Override
    public boolean containsFilm(int filmId) {
        return jdbcTemplate.queryForObject("select count(*) from films where film_id=?", Integer.class, filmId) == 1;
    }

    @Override
    public Film getFilm(int filmId) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select f.film_id as film_id, " +
                "f.name as filmname, " +
                "f.release_date as release_date, " +
                "f.description as description, " +
                "f.duration as duration, " +
                "f.mpa_rating_id as mpa_rating_id, " +
                "g.name as name, " +
                "g.genre_id as genre_id, " +
                "m.name as mpa_rating_name, " +
                "fd.director_id as director_id, " +
                "d.name as directorname " +
                "from films as f " +
                "left join film_genre as fg on f.film_id = fg.film_id " +
                "left join genres as g on fg.genre_id = g.genre_id " +
                "left join mpa_ratings as m on f.mpa_rating_id = m.mpa_rating_id " +
                "left join film_director as fd on f.film_id = fd.film_id " +
                "left join directors as d on fd.director_id = d.director_id " +
                "where f.film_id=? " +
                "order by film_id, genre_id", filmId);
        return makeFilmList(rowSet).get(0);
    }

    @Override
    public int getNextId() {
        return jdbcTemplate.query("select count(film_id), max(film_id), from films",
                (rs, rowNum) -> makeNextId(rs)).get(0);
    }

    private Integer makeNextId(ResultSet rs) throws SQLException {
        int nextId = 1;
        if (rs.getInt(1) >= 1) {
            nextId = rs.getInt(2) + 1;
        }
        return nextId;
    }

    private static List<Film> makeFilmList(SqlRowSet rowSet) {
        Map<Integer, Film> films = new HashMap<>();

        while (rowSet.next()) {
            //получить все значения из строки
            int id = rowSet.getInt("film_id");
            String description = rowSet.getString("description");
            int duration = rowSet.getInt("duration");
            LocalDate releaseDate = rowSet.getDate("release_date").toLocalDate();
            int mpaId = rowSet.getInt("mpa_rating_id");
            int genreId = rowSet.getInt("genre_id");
            String genreName = rowSet.getString(7);
            String name = rowSet.getString(2);
            String mpaName = rowSet.getString(9);
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
                //создаем новый объект режиссер
                Set<Director> directors;
                if (directorId != 0) {
                    directors = new HashSet<>();
                    directors.add(new Director(directorId, directorName));
                } else {
                    directors = Collections.emptySet();
                }
                //создаем фильм
                film = new Film(id, name, description, releaseDate, duration, genres, mpa, directors);

                //сохраняем фильм в список результата
                films.put(film.getId(), film);
            } else {
                //к существующему фильму добавляем еще один жанр
                film.getGenres().add(new Genre(genreId, genreName));
            }
        }
        return List.copyOf(films.values());
    }
}
