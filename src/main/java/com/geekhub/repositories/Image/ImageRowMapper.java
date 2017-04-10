package com.geekhub.repositories.Image;

import com.geekhub.domain.Image;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageRowMapper implements RowMapper<Image> {
    @Override
    public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        Image image = new Image();
        image.setId(rs.getLong("id"));
        image.setInsertionDate(rs.getTimestamp("insertion_date").toLocalDateTime());
        image.setPath(rs.getString("path"));
        return image;
    }
}
