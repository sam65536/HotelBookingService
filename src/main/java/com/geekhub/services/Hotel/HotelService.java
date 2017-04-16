package com.geekhub.services.Hotel;

import com.geekhub.domain.entities.Hotel;
import com.geekhub.domain.entities.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HotelService {
    List<Hotel> findAll();
    Hotel findOne(Long id);
    List<Hotel> getUserHotels(Long userId);
    void save(Hotel hotel);
    void delete(Long id);
    Map<Room, Map<LocalDate, Boolean>> getOccupancy(Hotel hotel, LocalDate from, LocalDate to);
}
