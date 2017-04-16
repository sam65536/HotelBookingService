package com.geekhub.services.Room;

import com.geekhub.domain.entities.Room;

import java.util.List;

public interface RoomService {
    List<Room> findAll();
    Room findOne(Long id);
    void save(Room room);
    void delete(Long id);
    List<Room> getHotelRooms(Long hotelId);
}
