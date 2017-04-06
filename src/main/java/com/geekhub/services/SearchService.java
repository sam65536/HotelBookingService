package com.geekhub.services;

import com.geekhub.domain.Hotel;
import com.geekhub.domain.Room;
import com.geekhub.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final RoomRepository rooms;

    @Autowired
    public SearchService(RoomRepository rooms) {
        this.rooms = rooms;
    }

    public List<Room> searchAvailableRooms(long hotelId, int persons, Date startDate, Date endDate) {
        List<Room> result = new ArrayList<>((Collection<? extends Room>) rooms.findAll());
        result.stream()
                .filter(room -> (room.getHotel().getId() == hotelId))
                .filter(room -> (room.getCapacity() >= persons))
                .filter(room -> (room.isAvailable(startDate, endDate)))
                .collect(Collectors.toList());
        return result;
    }
}