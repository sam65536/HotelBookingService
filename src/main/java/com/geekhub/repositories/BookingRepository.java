package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Booking;

public interface BookingRepository extends CrudRepository<Booking, Long> {
}