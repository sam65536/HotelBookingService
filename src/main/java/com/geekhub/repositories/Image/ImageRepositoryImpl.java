package com.geekhub.repositories.Image;

import com.geekhub.domain.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ImageRepositoryImpl implements ImageRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Image> findAll() {
        String sql = "SELECT id, insertion_date, path FROM image";
        List<Image> images = this.jdbcTemplate.query(sql, new ImageRowMapper());
        return images;
    }

    @Override
    public Image findOne(long id) {
        String sql = "SELECT id, insertion_date, path FROM image";
        List<Image> images = this.jdbcTemplate.query(sql, new ImageRowMapper());
        return images.get(0);
    }

    @Override
    public void save(Image image) {
        String sql = "INSERT INTO image" + "(id, insertion_date, path) " + "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, new Object[] {image.getId(), image.getInsertionDate(), image.getPath()});
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM image WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }
}
