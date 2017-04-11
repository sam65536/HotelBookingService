package com.geekhub.repositories.Hotel;

import com.geekhub.domain.entities.Hotel;

import java.util.List;

public interface HotelRepository {
    public List<Hotel> findAll();
    public Hotel findOne(Long id);
    public void save (Hotel hotel);
    public void delete (Long id);
}
