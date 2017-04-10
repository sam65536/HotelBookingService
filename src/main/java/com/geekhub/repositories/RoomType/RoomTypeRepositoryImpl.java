package com.geekhub.repositories.RoomType;

import com.geekhub.domain.RoomType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final class RoomTypeRowMapper implements RowMapper<RoomType> {
        @Override
        public RoomType mapRow(ResultSet rs, int rowNum) throws SQLException {
            RoomType roomType = new RoomType();
            roomType.setId(rs.getLong("id"));
            roomType.setDescription(rs.getString("description"));
            roomType.setOccupancy(rs.getString("occupancy"));
            return roomType;
        }
    }
}