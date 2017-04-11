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
        String sql = "SELECT id, begin_date, end_date, state FROM booking";
        List<Booking> bookings = this.jdbcTemplate.query(sql, new BookingRowMapper());
        return bookings;
    }

    @Override
    public Booking findOne(Long id) {
        String sql = "SELECT id, begin_date, end_date, state FROM booking WHERE id=" + id;
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
    public void delete(Long id) {
        String sql = "DELETE FROM booking WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }
}