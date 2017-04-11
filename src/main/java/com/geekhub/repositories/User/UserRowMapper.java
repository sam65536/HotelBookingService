package com.geekhub.repositories.User;

import com.geekhub.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));
        user.setUsername(rs.getString("username"));
        return user;
    }
}