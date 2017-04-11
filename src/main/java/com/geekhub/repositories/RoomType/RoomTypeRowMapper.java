package com.geekhub.repositories.RoomType;

import com.geekhub.domain.entities.RoomType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomTypeRowMapper implements RowMapper<RoomType> {
    @Override
    public RoomType mapRow(ResultSet rs, int rowNum) throws SQLException {
        RoomType roomType = new RoomType();
        roomType.setId(rs.getLong("id"));
        roomType.setDescription(rs.getString("description"));
        roomType.setOccupancy(rs.getString("occupancy"));
        return roomType;
    }
}
