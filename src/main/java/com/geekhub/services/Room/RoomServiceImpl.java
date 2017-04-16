package com.geekhub.services.Room;

import com.geekhub.domain.entities.Room;
import com.geekhub.repositories.Room.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;

    @Autowired
    public void setRoomRepository(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Room findOne(Long id) {
        return roomRepository.findOne(id);
    }

    @Override
    public void save(Room room) {
        roomRepository.save(room);
    }

    @Override
    public void delete(Long id) {
        roomRepository.delete(id);
    }

    @Override
    public List<Room> getHotelRooms(Long hotelId) {
        return roomRepository.getHotelRooms(hotelId);
    }
}
