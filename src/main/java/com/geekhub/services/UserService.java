package com.geekhub.services;

import com.geekhub.domain.entities.Booking;
import com.geekhub.repositories.Booking.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final BookingRepository bookings;

    @Autowired
    public UserService(BookingRepository bookings) {
        this.bookings = bookings;
    }

    public List<Booking> getUserBookings(long userId) {
        List<Booking> bookingsList = new ArrayList<>();
        for (Booking booking : bookings.findAll()) {
            if (booking.getUser().getId() == userId) {
                bookingsList.add(booking);
            }
        }
        return bookingsList;
    }
}