package com.geekhub.repositories.Hotel;

import com.geekhub.domain.entities.Hotel;

import java.util.List;

public interface HotelRepository {
    List<Hotel> findAll();
    Hotel findOne(Long id);
    List<Hotel> getUserHotels(Long userId);
    void save(Hotel hotel);
    void delete(Long id);
}
