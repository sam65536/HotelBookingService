package com.geekhub.services.Booking;

import com.geekhub.domain.entities.Booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingService {
    List<Booking> findAll();
    Booking findOne(Long id);
    List<Booking> getUserBookings(Long userId);
    void save(Booking booking);
    void delete(Long id);
    Set<LocalDate> getReservedDays(Long roomId);
}
