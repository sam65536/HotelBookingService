package com.geekhub.repositories.Room;

import com.geekhub.domain.entities.Room;
import com.geekhub.domain.entities.RoomType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RoomRepositoryImpl implements RoomRepository{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = this.jdbcTemplate.query("SELECT room.id, room.type_id, " +
                "room_type.description, room_type.occupancy FROM room " +
                "LEFT JOIN room_type ON room.type_id = room_type.id", (rs, rowNum) -> {
                    Room room = new Room();
                    room.setId(rs.getLong("id"));
                    RoomType roomType = new RoomType();
                    roomType.setId(rs.getLong("type_id"));
                    roomType.setDescription(rs.getString("description"));
                    roomType.setOccupancy(rs.getString("occupancy"));
                    room.setType(roomType);
                    return room;
                });
        return rooms;
    }

    @Override
    public Room findOne(long id) {
        String sql = "SELECT id, floor, price, room_number FROM room WHERE id=" + id;
        List<Room> rooms = this.jdbcTemplate.query(sql, new RoomRowMapper());
        return rooms.get(0);
    }

    @Override
    public void save(Room room) {
        if (room.getId() == null) {
            String sql = "INSERT INTO room" + "(id, floor, price, room_number) " +
                    "VALUES (?, ?, ?, ?)";
            this.jdbcTemplate.update(sql,
                    new Object[]{room.getId(), room.getFloor(), room.getPrice(), room.getRoomNumber()});
        } else {
            String sql = "UPDATE room SET floor = ?, room_number = ?, price = ? " +
                    "WHERE id = " + room.getId();
            this.jdbcTemplate.update(sql, new Object[]{room.getFloor(), room.getPrice(), room.getRoomNumber()});
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM room WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }
}
