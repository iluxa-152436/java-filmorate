package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
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
    }

    private void deleteFilmGenre(Film film) {
        String sqlRemoveFilmGenre = "delete from film_genre where film_id=?";
        jdbcTemplate.update(sqlRemoveFilmGenre, film.getId());
    }

    private void saveFilmGenre(Film film) {
        log.debug("Film contains genres: {}", film.getGenres());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
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
                "m.name as mpa_rating_name " +
                "from films as f " +
                "left join film_genre as fg on f.film_id = fg.film_id " +
                "left join genres as g on fg.genre_id = g.genre_id " +
                "left join mpa_ratings m on f.mpa_rating_id = m.mpa_rating_id");
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
                "m.name as mpa_rating_name " +
                "from films as f " +
                "left join film_genre as fg on f.film_id = fg.film_id " +
                "left join genres as g on fg.genre_id = g.genre_id " +
                "left join mpa_ratings m on f.mpa_rating_id = m.mpa_rating_id " +
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
        Integer nextId = 1;
        if (rs.getInt(1) >= 1) {
            nextId = rs.getInt(2) + 1;
        }
        return nextId;
    }

    private static List<Film> makeFilmList(SqlRowSet rowSet) {
        Map<Integer, Film> films = new HashMap<>();

        while (rowSet.next()) {
            //получить все значения из строки
            Integer id = rowSet.getInt(1);
            String name = rowSet.getString(2);
            LocalDate releaseDate = rowSet.getDate(3).toLocalDate();
            String description = rowSet.getString(4);
            Integer duration = rowSet.getInt(5);
            Integer mpaId = rowSet.getInt(6);
            String genreName = rowSet.getString(7);
            Integer genreId = rowSet.getInt(8);
            String mpaName = rowSet.getString(9);
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
                film = new Film(id, name, description, releaseDate, duration, genres, mpa);

                //сохраняем фильм в список результата
                films.put(film.getId(), film);
            } else {
                //к существующему фильму добавляем еще один жанр
                film.getGenres().add(new Genre(genreId, genreName));
            }
        }
        return List.copyOf(films.values());
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sqlQuery = "  \n" +
                "                                SELECT    \n" +
                "                                f.film_id as film_id,  \n" +
                "                                f.name as film_name,  \n" +
                "                                f.release_date as release_date,  \n" +
                "                                f.description as description,  \n" +
                "                                f.duration as duration,  \n" +
                "                                f.mpa_rating_id as mpa_rating_id \n" +
                "                                , g.name as genre_name \n" +
                "                                ,fg.genre_id as genre_id \n" +
                "                                ,m.name as mpa_rating_name \n" +
                "                                FROM likes u1 JOIN likes u2    \n" +
                "                                    ON u1.film_id=u2.film_id    \n" +
                "                                JOIN (SELECT film_id, COUNT(DISTINCT user_id) AS c   \n" +
                "                                    FROM likes GROUP BY film_id) as f_count   \n" +
                "                                   ON u1.film_id=f_count.film_id    \n" +
                "                                     JOIN films f   \n" +
                "                                     ON u1.film_id=f.film_id   \n" +
                "                                       JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id   \n" +
                "                                    LEFT JOIN FILM_GENRE fg ON fg.FILM_ID  = f.FILM_ID  \n" +
                "                                    LEFT JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID  \n" +
                "                                                WHERE     \n" +
                "                                                  (u1.user_id = ? \n" +
                "                                                  OR u2.user_id = ?) ";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);

        return makeFilmList(sqlRowSet);
    }
}

