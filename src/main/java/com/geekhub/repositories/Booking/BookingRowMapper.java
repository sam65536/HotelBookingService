package com.geekhub.repositories.Booking;

import com.geekhub.domain.entities.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookingRowMapper implements RowMapper<Booking> {

    @Override
    public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
        Booking booking = new Booking();
        User user = new User();
        Room room = new Room();
        Hotel hotel = new Hotel();
        User manager = new User();
        RoomType roomType = new RoomType();
        booking.setId(rs.getLong("id"));
        booking.setBeginDate(rs.getDate("begin_date").toLocalDate());
        booking.setEndDate(rs.getDate("end_date").toLocalDate());
        booking.setState(rs.getBoolean("state"));
        user.setId(rs.getLong("user_id"));
        booking.setUser(user);
        room.setId(rs.getLong("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        hotel.setId(rs.getLong("hotel_id"));
        hotel.setName(rs.getString("name"));
        manager.setId(rs.getLong("manager_id"));
        roomType.setDescription(rs.getString("description"));
        room.setType(roomType);
        hotel.setManager(manager);
        room.setHotel(hotel);
        booking.setRoom(room);
        return booking;
    }
}