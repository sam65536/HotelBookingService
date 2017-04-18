package com.geekhub.services.Hotel;

import com.geekhub.domain.entities.Hotel;
import com.geekhub.domain.entities.Room;
import com.geekhub.repositories.Booking.BookingRepository;
import com.geekhub.repositories.Hotel.HotelRepository;
import com.geekhub.repositories.Room.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

@Service
public class HotelServiceImpl implements HotelService {

    private HotelRepository hotelRepository;
    private RoomRepository roomRepository;
    private BookingRepository bookingRepository;

    @Autowired
    public void setHotelRepository(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Autowired
    public void setRoomRepository(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Autowired
    public void setBookingRepository(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel findOne(Long id) {
        return hotelRepository.findOne(id);
    }

    @Override
    public List<Hotel> getUserHotels(Long userId) {
        return hotelRepository.getUserHotels(userId);
    }

    @Override
    public void save(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    @Override
    public void delete(Long id) {
        hotelRepository.delete(id);
    }

    @Override
    public Map<Room, Map<LocalDate, Boolean>> getOccupancy(Hotel hotel, LocalDate from, LocalDate to) {
        List<Room> hotelRooms = roomRepository.getHotelRooms(hotel.getId());
        hotelRooms.forEach(room -> room.setReservedDays(bookingRepository.getReservedDays(room.getId())));
        Map<Room, Map<LocalDate, Boolean>> result = new TreeMap<>();
        hotelRooms.forEach(room -> {
            Map<LocalDate, Boolean> roomOccupancy = new TreeMap<>();
            Stream.iterate(from, date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(from, to) + 1)
                    .forEach(date -> roomOccupancy.put(date, room.getReservedDays().contains(date)));
            result.put(room, roomOccupancy);
        });
        return result;
    }
}