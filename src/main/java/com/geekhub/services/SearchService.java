package com.geekhub.services;

import com.geekhub.domain.entities.Room;
import com.geekhub.repositories.Room.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final RoomRepository rooms;

    @Autowired
    public SearchService(RoomRepository rooms) {
        this.rooms = rooms;
    }

    public List<Room> searchAvailableRooms(Long hotelId, Integer persons, LocalDate startDate, LocalDate endDate) {
        List<Room> roomsList = rooms.findAll();
        List<Room> result = roomsList.stream()
                .filter(room -> (room.getHotelId() == hotelId))
                .filter(room -> (room.getCapacity() >= persons))
                .filter(room -> (room.isAvailable(startDate, endDate)))
                .collect(Collectors.toList());
        return result;
    }
}