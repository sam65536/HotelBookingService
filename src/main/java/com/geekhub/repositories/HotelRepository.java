package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Hotel;

public interface HotelRepository extends CrudRepository<Hotel, Long> {
}