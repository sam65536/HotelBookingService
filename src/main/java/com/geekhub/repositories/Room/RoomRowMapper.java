package com.geekhub.repositories.Room;

import com.geekhub.domain.entities.Room;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomRowMapper implements RowMapper<Room> {

    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setFloor(rs.getInt("floor"));
        room.setPrice(rs.getInt("price"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setHotelId(rs.getLong("hotel_id"));
        return room;
    }
}