package com.geekhub.repositories.RoomType;

import com.geekhub.domain.entities.RoomType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RoomTypeRepositoryImpl implements RoomTypeRepository  {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<RoomType> findAll() {
        String sql = "SELECT id, description, occupancy FROM room_type";
        List<RoomType> roomTypes = this.jdbcTemplate.query(sql, new RoomTypeRowMapper());
        return roomTypes;
    }

    @Override
    public RoomType findOne(Long id) {
        String sql = "SELECT id, description, occupancy FROM room_type WHERE id=" + id;
        List<RoomType> roomTypes = this.jdbcTemplate.query(sql, new RoomTypeRowMapper());
        return roomTypes.get(0);
    }
}