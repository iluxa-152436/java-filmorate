package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("DB")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void saveUser(User user) {
        String sqlQuery = "INSERT INTO app_users(user_id, email, login, name, birthday) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
    }

    @Override
    public void updateUser(User user) {
        String sqlQuery = "UPDATE app_users SET email=?, login=?, name=?, birthday=? WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM app_users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public List<User> getUsers(Set<Integer> userIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", userIds);
        return namedParameterJdbcTemplate.query("SELECT * FROM app_users WHERE user_id in (:ids)",
                parameters,
                (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUser(int userId) {
        return jdbcTemplate.queryForObject("SELECT * FROM app_users WHERE user_id=?",
                (rs, rowNum) -> makeUser(rs),
                userId);
    }

    @Override
    public boolean containsUser(int userId) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_users WHERE user_id=?",
                Integer.class,
                userId);
        return count == 1;
    }

    @Override
    public int getNexId() {
        return jdbcTemplate.query("SELECT COUNT(user_id), MAX(user_id), FROM app_users",
                (rs, rowNum) -> makeNextId(rs)).get(0);
    }

    @Override
    public void deleteUserById(int userId) {
        String sqlQuery = "DELETE FROM app_users WHERE user_id = ?";
        if (jdbcTemplate.update(sqlQuery, userId) == 0) {
            throw new FindUserException("User с id = " + userId + " не найден, удаление невозможно.");
        }
    }

    @Override
    public List<Film> getRecommendations(Integer id) {
        String sqlQuery =
                "WITH user_with_max_common_likes AS (" +
                        "                  SELECT u1.user_id, COUNT(u2.film_id) c FROM likes u1 LEFT JOIN likes u2 " +
                        "                        ON u1.film_id = u2.film_id  " +
                        "                        WHERE u2.user_id = " + id + " AND u1.USER_ID <> " + id +
                        "                  GROUP BY u1.user_id  " +
                        "                  ORDER BY c DESC LIMIT 1 " +
                        "                )" +
                        "              , user_films AS (" +
                        "                SELECT DISTINCT  l.film_id FROM user_with_max_common_likes u JOIN LIKES l " +
                        "                ON u.user_id = l.USER_ID" +
                        "                WHERE l.FILM_ID NOT IN (SELECT DISTINCT film_id FROM likes WHERE USER_ID = " + id + ") " +
                        "                ) " +
                        "                " +
                        "                SELECT o.film_id AS film_id, " +
                        "                         f.name AS film_name, " +
                        "                         f.release_date AS release_date, " +
                        "                         f.description AS description, " +
                        "                         f.duration AS duration, " +
                        "                         f.mpa_rating_id AS mpa_rating_id, " +
                        "                         m.name AS mpa_rating_name " +
                        "                FROM user_films o  " +
                        "                           JOIN films f  " +
                        "                           ON o.film_id = f.film_id " +
                        "                           JOIN mpa_ratings m " +
                        "                           ON f.mpa_rating_id = m.mpa_rating_id" +
                        "               ";


        List<Film> recommendedFilms = jdbcTemplate.query(sqlQuery,
                (rs, rowNum) -> {

                    MpaRating mpaRating = new MpaRating(rs.getInt("mpa_rating_id"),
                            rs.getString("mpa_rating_name"));
                    Film film = new Film(rs.getInt("film_id"),
                            rs.getString("film_name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            null,
                            mpaRating,
                            new HashSet<>()
                    );
                    return film;
                });

        recommendedFilms.stream().forEach(film -> {
            System.out.println(film.toString());
            String sqlQueryFilmGenres = "SELECT fg.genre_id AS genre_id, " +
                    "g.name AS name FROM films f " +
                    "JOIN film_genre fg " +
                    "ON f.film_id=fg.film_id " +
                    "JOIN genres g " +
                    "ON fg.genre_id=g.genre_id " +
                    "WHERE f.film_id = " + film.getId();
            List<Genre> genres = jdbcTemplate.query(sqlQueryFilmGenres,
                    (rs, rowNum) -> {
                        Genre genre = new Genre();
                        genre.setId(rs.getInt("genre_id"));
                        genre.setName(rs.getString("name"));
                        return genre;
                    }
            );
            film.setGenres(new HashSet<>(genres));
        });

        return recommendedFilms;
    }


    private Integer makeNextId(ResultSet rs) throws SQLException {
        int nextId = 1;
        if (rs.getInt(1) >= 1) {
            nextId = rs.getInt(2) + 1;
        }
        return nextId;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}
