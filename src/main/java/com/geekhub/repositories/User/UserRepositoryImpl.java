package com.geekhub.repositories.User;

import com.geekhub.domain.entities.User;
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
        String sql = "SELECT \"user\".id, email, name, password, username, authority_id, role FROM \"user\"\n" +
                "LEFT JOIN authority ON authority_id=authority.id";
        List<User> users = this.jdbcTemplate.query(sql, new UserRowMapper());
        return users;
    }

    @Override
    public User findOne(Long id) {
        String sql = "SELECT \"user\".id, email, name, password, username, authority_id, role FROM \"user\"\n" +
                "LEFT JOIN authority ON authority_id=authority.id WHERE \"user\".id=" + id;
        List<User> users = this.jdbcTemplate.query(sql, new UserRowMapper());
        return users.get(0);
    }

    @Override
    public User findByUsername(String username) {
        StringBuilder sql = new StringBuilder(
                "SELECT \"user\".id, email, name, password, username, authority_id, role FROM \"user\"\n" +
                        "LEFT JOIN authority ON authority_id=authority.id WHERE username=");
        sql.append("\'").append(username).append("\'");
        List<User> users = this.jdbcTemplate.query(sql.toString(), new UserRowMapper());
        return users.get(0);
    }
    @Override
    public void save(User user) {
        if (user.getId() == null) {
            String sql = "INSERT INTO \"user\" (email, name, password, username, authority_id) VALUES (?, ?, ?, ?, ?)";
            this.jdbcTemplate.update(sql, new Object[]{user.getEmail(), user.getName(), user.getPassword(),
                user.getUsername(), user.getAuthority().getId()});
        } else {
            String sql = "UPDATE \"user\" SET email = ?, name = ?, password = ?, username = ?, authority_id = ? WHERE id = " + user.getId();
            this.jdbcTemplate.update(sql, new Object[]{user.getEmail(), user.getName(), user.getPassword(),
                user.getUsername(), user.getAuthority().getId()});
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM \"user\" WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }
}