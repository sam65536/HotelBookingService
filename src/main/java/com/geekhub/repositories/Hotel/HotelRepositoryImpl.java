package com.geekhub.repositories.Hotel;

import com.geekhub.domain.*;
import com.geekhub.repositories.Category.CategoryRowMapper;
import com.geekhub.repositories.Comment.CommentRowMapper;
import com.geekhub.repositories.Image.ImageRowMapper;
import com.geekhub.repositories.Room.RoomRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class HotelRepositoryImpl implements HotelRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Hotel> findAll() {
        String sql = "SELECT id, address, name, rating, status FROM hotel";
        List<Hotel> hotels = this.jdbcTemplate.query(sql, new HotelRowMapper());
        for (Hotel hotel : hotels) {
            List<Image> images = this.jdbcTemplate.query(
                    "SELECT image.id, image.insertion_date, image.path FROM image WHERE image.hotel_id=" + hotel.getId(), new ImageRowMapper());
            Map<Long, Image> imagesMap = images.stream().collect(
                    Collectors.toMap(image -> image.getId(), image -> image));
            hotel.setImages(imagesMap);
        }
        return hotels;
    }

    @Override
    public Hotel findOne(Long id) {
        String sql = "SELECT id, address, name, rating, status FROM hotel WHERE id= " + id;
        List<Hotel> hotels = this.jdbcTemplate.query(sql, new HotelRowMapper());

        List<Room> rooms = this.jdbcTemplate.query(
                "SELECT room.id, room.floor, room.price, room.room_number FROM room " +
                        "WHERE room.hotel_id=" + id, new RoomRowMapper());
        Map<Long, Room> roomsMap = rooms.stream().collect(
                Collectors.toMap(room -> room.getId(), room -> room));
        hotels.get(0).setRooms(roomsMap);

        List<Comment> comments = this.jdbcTemplate.query(
                "SELECT comment.id, comment.date, comment.is_answer, comment.status, comment.text" +
                        " FROM comment WHERE comment.hotel_id=" + id, new CommentRowMapper());
        Map<Long, Comment>commentsMap = comments.stream().collect(
                Collectors.toMap(image -> image.getId(), image -> image));
        hotels.get(0).setComments(commentsMap);

        List<Image> images = this.jdbcTemplate.query(
                "SELECT image.id, image.path FROM image WHERE image.hotel_id=" + id, new ImageRowMapper());
        Map<Long, Image> imagesMap = images.stream().collect(
                Collectors.toMap(image -> image.getId(), image -> image));
        hotels.get(0).setImages(imagesMap);

        List<Category> categories = this.jdbcTemplate.query(
                "SELECT hotel.category_id, category.name FROM hotel\n" +
                        "LEFT JOIN category ON category.id=hotel.category_id\n" +
                        "WHERE hotel.id=" + id, new CategoryRowMapper());
        hotels.get(0).setCategory(categories.get(0));

        List<City> cities = this.jdbcTemplate.query(
                "SELECT hotel.city_id, city.name FROM hotel\n" +
                        "LEFT JOIN city ON city.id=hotel.city_id\n" +
                        "WHERE hotel.id=" +id,
                (rs, rowNum) -> {
                    City city = new City();
                    city.setId(rs.getLong("city_id"));
                    city.setName(rs.getString("name"));
                    return city;
                });
        hotels.get(0).setCity(cities.get(0));
        return hotels.get(0);
    }

    @Override
    public void save(Hotel hotel) {
        String sql = "INSERT INTO hotel" +
                "(id, address, name, rating, status, category_id, city_id, manager_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        this.jdbcTemplate.update(sql, hotel.getId(), hotel.getAddress(),
                hotel.getName(), hotel.getRating(), hotel.getStatus(),
                hotel.getCategory().getId(), hotel.getCity().getId(), hotel.getManager().getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM hotel WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }

    private static final class HotelRowMapper implements RowMapper<Hotel> {
        @Override
        public Hotel mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hotel hotel = new Hotel();
            hotel.setId(rs.getLong("id"));
            hotel.setAddress(rs.getString("address"));
            hotel.setName(rs.getString("name"));
            hotel.setRating(rs.getInt("rating"));
            hotel.setStatus(rs.getBoolean("status"));
            return hotel;
        }
    }
}