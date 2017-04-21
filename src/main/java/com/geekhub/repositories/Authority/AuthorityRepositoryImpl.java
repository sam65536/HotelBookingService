package com.geekhub.repositories.Authority;

import com.geekhub.domain.UserRole;
import com.geekhub.domain.entities.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AuthorityRepositoryImpl implements AuthorityRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Authority> findAll() {
        String sql = "SELECT id, role FROM authority";
        List<Authority> authorities = this.jdbcTemplate.query(sql, new AuthorityRowMapper());
        return authorities;
    }

    @Override
    public Authority findOne(Long id) {
        String sql = "SELECT id, role from authority WHERE id= " + id;
        List<Authority> authorities = this.jdbcTemplate.query(sql, new AuthorityRowMapper());
        return authorities.get(0);
    }

    @Override
    public Authority findByRole(UserRole role) {
        StringBuilder sql = new StringBuilder("SELECT id, role from authority WHERE role=");
        sql.append("\'").append(String.valueOf(role)).append("\'");
        List<Authority> authorities = jdbcTemplate.query(sql.toString(), new AuthorityRowMapper());
        return authorities.get(0);
    }

    @Override
    public void save(Authority authority) {
        String sql = "INSERT INTO authority" + "(id, role) " + "VALUES (?, ?)";
        this.jdbcTemplate.update(sql, new Object[] {authority.getId(), authority.getRole()});
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM authority WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }

    private static final class AuthorityRowMapper implements RowMapper<Authority> {
        @Override
        public Authority mapRow(ResultSet rs, int rowNum) throws SQLException {
            Authority authority = new Authority();
            authority.setId(rs.getLong("id"));
            authority.setRole(UserRole.valueOf(rs.getString("role")));
            return authority;
        }
    }
}