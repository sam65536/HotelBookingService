package com.geekhub.repositories.Booking;

import com.geekhub.domain.Booking;

import java.util.List;

public interface BookingRepository {
    public List<Booking> findAll();
    public Booking findOne(Long id);
    public void save (Booking booking);
    public void delete (Long id);
}
