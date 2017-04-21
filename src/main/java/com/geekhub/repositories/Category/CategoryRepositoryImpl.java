package com.geekhub.repositories.Category;

import com.geekhub.domain.entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT id,name FROM category";
        List<Category> categories = this.jdbcTemplate.query(sql, new CategoryRowMapper());
        return categories;
    }

    @Override
    public Category findOne(Long id) {
        String sql = "SELECT id,name FROM category WHERE id=" + id;
        List<Category> categories = this.jdbcTemplate.query(sql, new CategoryRowMapper());
        return categories.get(0);
    }
}