package com.geekhub.repositories.Booking;

import com.geekhub.domain.entities.Booking;

import java.util.List;

public interface BookingRepository {
    List<Booking> findAll();
    Booking findOne(long id);
    void save(Booking booking);
    void delete(long id);
}
