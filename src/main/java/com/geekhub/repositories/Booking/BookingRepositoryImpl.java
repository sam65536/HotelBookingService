package com.geekhub.repositories.Booking;

import com.geekhub.domain.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT booking.id, begin_date, end_date, state, user_id, room_id, room_number, hotel_id, hotel.name, manager_id, description\n" +
                "FROM booking LEFT JOIN room ON booking.room_id = room.id\n" +
                "LEFT JOIN hotel ON room.hotel_id = hotel.id\n" +
                "LEFT JOIN room_type ON room.type_id = room_type.id";
        List<Booking> bookings = this.jdbcTemplate.query(sql, new BookingRowMapper());
        return bookings;
    }

    @Override
    public Booking findOne(Long id) {
        String sql = "SELECT booking.id, begin_date, end_date, state, user_id, room_id, room_number, hotel_id, hotel.name, manager_id, description\n" +
                "FROM booking LEFT JOIN room ON booking.room_id = room.id\n" +
                "LEFT JOIN hotel ON room.hotel_id = hotel.id\n" +
                "LEFT JOIN room_type ON room.type_id = room_type.id WHERE booking.id=" + id;
        List<Booking> bookings = this.jdbcTemplate.query(sql, new BookingRowMapper());
        return bookings.get(0);
    }

    @Override
    public List<Booking> getUserBookings(Long userId) {
        String sql = "SELECT booking.id, begin_date, end_date, user_id,\n" +
                "\"user\".name AS user_name, room_id, room_number, description,\n" +
                "hotel.id AS hotel_id, hotel.name AS hotel_name\n" +
                "FROM booking LEFT JOIN \"user\" ON user_id=\"user\".id\n" +
                "LEFT JOIN room ON room_id=room.id\n" +
                "LEFT JOIN room_type ON room.type_id = room_type.id\n" +
                "LEFT JOIN hotel ON room.hotel_id = hotel.id WHERE \"user\".id=" + userId;
        List<Booking> bookings = this.jdbcTemplate.query(sql, (rs, rowNum) -> {
           Booking booking = new Booking();
           booking.setId(rs.getLong("id"));
           booking.setBeginDate(rs.getDate("begin_date").toLocalDate());
           booking.setEndDate(rs.getDate("end_date").toLocalDate());
           User user = new User();
           user.setId(rs.getLong("id"));
           user.setName(rs.getString("user_name"));
           booking.setUser(user);
           Room room = new Room();
           room.setId(rs.getLong("room_id"));
           room.setRoomNumber(rs.getString("room_number"));
           RoomType roomType = new RoomType();
           roomType.setDescription(rs.getString("description"));
           room.setType(roomType);
           Hotel hotel = new Hotel();
           hotel.setId(rs.getLong("hotel_id"));
           hotel.setName(rs.getString("hotel_name"));
           room.setHotel(hotel);
           booking.setRoom(room);
           return booking;
        });
        return bookings;
    }

    @Override
    public void save(Booking booking) {
        if (booking.getId() == null) {
            String sql = "INSERT INTO booking" + "(id, begin_date, end_date, state) " +
                    "VALUES (?, ?, ?, ?)";
           this.jdbcTemplate.update(sql, new Object[]{booking.getId(), java.sql.Date.valueOf(booking.getBeginDate()),
                   java.sql.Date.valueOf(booking.getEndDate()), booking.getState()});
        } else {
            String sql = "UPDATE booking SET begin_date = ?, end_date = ?, state = ? " +
                    "WHERE id = " + booking.getId();
            this.jdbcTemplate.update(sql,
                    new Object[]{java.sql.Date.valueOf(booking.getBeginDate()),
                            java.sql.Date.valueOf(booking.getEndDate()),
                            booking.getState()});
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM booking WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }

    @Override
    public Set<LocalDate> getReservedDays(Long roomId) {
        String sql = "SELECT booking.id, begin_date, end_date FROM booking\n" +
                "LEFT JOIN room ON booking.room_id = room.id WHERE room_id=" + roomId;
        List<Booking> roomBookings = this.jdbcTemplate.query(sql, (rs, rowNum) -> {
           Booking booking = new Booking();
           booking.setId(rs.getLong("id"));
           booking.setBeginDate(rs.getDate("begin_date").toLocalDate());
           booking.setEndDate(rs.getDate("end_date").toLocalDate());
           return booking;
        });
        Set<LocalDate> reservedDays = new TreeSet<>();
        for (Booking booking : roomBookings) {
            LocalDate from = booking.getBeginDate();
            LocalDate to = booking.getEndDate();
            Stream.iterate(from, date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(from, to) + 1)
                    .forEach(reservedDays::add);
        }
        return reservedDays;
    }
}