package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("dbUserStorage")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        StringBuilder ids = new StringBuilder();
        for (Integer userId : userIds) {
            ids.append(userId);
            if (userIds.iterator().hasNext()) {
                ids.append(",");
            }
        }
        return jdbcTemplate.query("select * from app_users where user_id in (?)",
                (rs, rowNum) -> makeUser(rs),
                ids);
    }

    @Override
    public User getUser(int userId) {
        return jdbcTemplate.queryForObject("select * from app_users where user_id=?",
                (rs, rowNum) -> makeUser(rs),
                userId);
    }

    @Override
    public boolean containsUser(int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select count(*) from app_users where user_id=?", userId);
        return userRows.next();
    }

    @Override
    public int getNexId() {
        int nextId;
        try {
            nextId = jdbcTemplate.queryForObject("select max(user_id) from app_users", Integer.class) + 1;
        } catch (NullPointerException e) {
            nextId = 1;
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
