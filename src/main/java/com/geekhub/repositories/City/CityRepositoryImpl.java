package com.geekhub.repositories.City;

import com.geekhub.domain.entities.City;
import com.geekhub.domain.entities.Hotel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CityRepositoryImpl implements CityRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<City> findAll() {
        String sql = "SELECT id, name FROM city";
        List<City> cities = this.jdbcTemplate.query(sql, new CityRowMapper());
        return cities;
    }

    @Override
    public City findOne(Long id) {
        String sql = "SELECT id, name FROM city WHERE id=" + id;
        List<City> cities = this.jdbcTemplate.query(sql, new CityRowMapper());
        List<Hotel> hotels = this.jdbcTemplate.query(
                "SELECT hotel.id, hotel.name, hotel.rating FROM hotel WHERE hotel.city_id=" + id,
                (rs, rowNum) -> {
                    Hotel hotel = new Hotel();
                    hotel.setId(rs.getLong("id"));
                    hotel.setName(rs.getString("name"));
                    hotel.setRating(rs.getInt("rating"));
                    return hotel;
                });

        cities.get(0).setHotels(hotels);
        return cities.get(0);
    }

    private static final class CityRowMapper implements RowMapper<City> {
        @Override
        public City mapRow(ResultSet rs, int rowNum) throws SQLException {
            City city = new City();
            city.setId(rs.getLong("id"));
            city.setName(rs.getString("name"));
            return city;
        }
    }
}