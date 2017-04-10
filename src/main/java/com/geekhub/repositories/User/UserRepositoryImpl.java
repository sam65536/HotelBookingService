package com.geekhub.repositories.User;

import com.geekhub.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, email, name, password, username FROM \"user\"";
        List<User> users = this.jdbcTemplate.query(sql, new UserRowMapper());
        return users;
    }

    @Override
    public User findOne(Long id) {
        String sql = "SELECT id, email, name, password, username FROM \"user\" WHERE id=" + id;
        List<User> users = this.jdbcTemplate.query(sql, new UserRowMapper());
        return users.get(0);
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT id, email, name, password, username FROM \"user\" WHERE username=" + username;
        List<User> users = this.jdbcTemplate.query(sql, new UserRowMapper());
        return users.get(0);
    }

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            String sql = "INSERT INTO \"user\"" + "(id, email, name, password, username) " +
                    "VALUES (?, ?, ?, ?, ?)";
            this.jdbcTemplate.update(sql, new Object[]{user.getId(), user.getEmail(), user.getName(),
                            user.getPassword(), user.getUsername()});
        }
        else {
            String sql = "UPDATE \"user\" SET email = ?, name = ?, password = ?, username = ? " +
                    "WHERE id = " + user.getId();
            this.jdbcTemplate.update(sql, new Object[]{user.getEmail(), user.getName(),
                            user.getPassword(), user.getUsername()});
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM \"user\" WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }
}
