package com.geekhub.services;

import com.geekhub.domain.*;
import com.geekhub.repositories.Hotel.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class BookingService {

    private final HotelRepository hotels;

    @Autowired
    public BookingService(HotelRepository hotels) {
        this.hotels = hotels;
    }

    public List<LocalDate> getBookingDays(Booking booking) {
        LocalDate start = booking.getBeginDate();
        LocalDate end = booking.getEndDate();
        List<LocalDate> dates = new ArrayList<>();
        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .forEach(dates::add);
        return dates;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail myUser = (CustomUserDetail) authentication.getPrincipal();
        return myUser.getUser();
    }

    public List<Room> getAvailableRooms(int numberRooms, Hotel hotel, RoomType roomType, Booking booking, List<LocalDate> dates) {
        Map<Long, Room> roomsFromHotel = hotel.getRooms();
        List<Room> availableRooms = new ArrayList<>();
        int counter = 1;
        for (Long hotelRoomsId : roomsFromHotel.keySet()) {
            Room room = roomsFromHotel.get(hotelRoomsId);
            Map<LocalDate, Long> roomBookings = room.getReservedDays();
            boolean found = false;
            for (LocalDate day : dates) {
                if (roomBookings.get(day) != null) {
                    found = true;
                    break;
                }
            }
            if ((!found) && (room.getType() == roomType) && (counter <= numberRooms)) {
                availableRooms.add(room);
                for (LocalDate date : dates) {
                    roomBookings.put(date, booking.getId());
                }
                counter++;
            } else if (counter > numberRooms) {
                break;
            }
        }
        return availableRooms;
    }

    public List<Room> getAvailableRooms(int numberRooms, RoomType roomType, List<LocalDate> dates) {
        List<Room> availableRooms = new ArrayList<>();
        for (Hotel hotel : hotels.findAll()) {
            if (hotel.getStatus()) {
                Map<Long, Room> rooms = hotel.getRooms();
                int counter = 0;
                Room currentRoom = null;
                for (Map.Entry<Long, Room> roomsEntry : rooms.entrySet()) {
                    Room room = roomsEntry.getValue();
                    Map<LocalDate, Long> roomBookings = room.getReservedDays();
                    boolean found = false;
                    for (LocalDate day : dates) {
                        if (roomBookings.get(day) != null) {
                            found = true;
                            break;
                        }
                    }
                    if (!found && room.getType().getDescription().equals(roomType.getDescription())) {
                        counter++;
                        currentRoom = room;
                    }
                }
                if (counter >= numberRooms) {
                    availableRooms.add(currentRoom);
                }
            }
        }
        return availableRooms;
    }
}