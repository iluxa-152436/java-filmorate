package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
        String sqlQuery = "insert into app_users(user_id, email, login, name, birthday) values (?,?,?,?,?)";
        jdbcTemplate.update(sqlQuery,
                user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
    }

    @Override
    public void updateUser(User user) {
        String sqlQuery = "update app_users set email=?, login=?, name=?, birthday=? where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "select * from app_users";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public List<User> getUsers(Set<Integer> userIds) {
        SqlParameterSource parameters = new MapSqlParameterSource("ids", userIds);
        return namedParameterJdbcTemplate.query("select * from app_users where user_id in (:ids)",
                parameters,
                (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User getUser(int userId) {
        return jdbcTemplate.queryForObject("select * from app_users where user_id=?",
                (rs, rowNum) -> makeUser(rs),
                userId);
    }

    @Override
    public boolean containsUser(int userId) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from app_users where user_id=?", Integer.class, userId);
        return count == 1;
    }

    @Override
    public int getNexId() {
        return jdbcTemplate.query("select count(user_id), max(user_id), from app_users",
                (rs, rowNum) -> makeNextId(rs)).get(0);
    }

    private Integer makeNextId(ResultSet rs) throws SQLException {
        Integer nextId = 1;
        if (rs.getInt(1) >= 1) {
            nextId = rs.getInt(2) + 1;
        }
        return nextId;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }
}
