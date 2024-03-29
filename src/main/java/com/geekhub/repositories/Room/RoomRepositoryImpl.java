package com.geekhub.repositories.Room;

import com.geekhub.domain.entities.Hotel;
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
        List<Room> rooms = this.jdbcTemplate.query(
                "SELECT room.id, room.room_number, room.price, room.hotel_id, hotel.name AS hotel_name,\n" +
                        "room.type_id, room_type.description, room_type.occupancy FROM room\n" +
                        "LEFT JOIN room_type ON room.type_id = room_type.id\n" +
                        "LEFT JOIN hotel ON room.hotel_id = hotel.id", (rs, rowNum) -> {
                    Room room = new Room();
                    RoomType roomType = new RoomType();
                    Hotel hotel = new Hotel();
                    room.setId(rs.getLong("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setPrice(rs.getInt("price"));
                    hotel.setId(rs.getLong("hotel_id"));
                    hotel.setName(rs.getString("hotel_name"));
                    roomType.setId(rs.getLong("type_id"));
                    roomType.setDescription(rs.getString("description"));
                    roomType.setOccupancy(rs.getInt("occupancy"));
                    room.setType(roomType);
                    room.setHotel(hotel);
                    return room;
                });
        return rooms;
    }

    @Override
    public Room findOne(Long id) {
        String sql = "SELECT room.id, floor, price, room_number, type_id, room_type.description, room_type.occupancy\n" +
                "FROM room LEFT JOIN room_type ON room.type_id = room_type.id WHERE room.id=" + id;
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
    public void delete(Long id) {
        String sql = "DELETE FROM room WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }

    @Override
    public List<Room> getHotelRooms(Long hotelId) {
        String sql = "SELECT room.id, floor, price, room_number, type_id, room_type.description, room_type.occupancy\n" +
                "FROM room LEFT JOIN room_type ON room.type_id = room_type.id WHERE hotel_id=" + hotelId;
        List<Room> rooms = this.jdbcTemplate.query(sql, new RoomRowMapper());
        return rooms;
    }
}