package com.geekhub.repositories.Booking;

import com.geekhub.domain.entities.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT booking.id, begin_date, end_date, state, user_id, room_id, hotel_id, manager_id\n" +
                "FROM booking LEFT JOIN room ON booking.room_id = room.id\n" +
                "LEFT JOIN hotel ON room.hotel_id = hotel.id";
        List<Booking> bookings = this.jdbcTemplate.query(sql, new BookingRowMapper());
        return bookings;
    }

    @Override
    public Booking findOne(long id) {
        String sql = "SELECT booking.id, begin_date, end_date, state, user_id, room_id, hotel_id, manager_id\n" +
                "FROM booking LEFT JOIN room ON booking.room_id = room.id\n" +
                "LEFT JOIN hotel ON room.hotel_id = hotel.id WHERE booking.id=" + id;
        List<Booking> bookings = this.jdbcTemplate.query(sql, new BookingRowMapper());
        return bookings.get(0);
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
    public void delete(long id) {
        String sql = "DELETE FROM booking WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }
}