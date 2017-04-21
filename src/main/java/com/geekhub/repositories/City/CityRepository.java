package com.geekhub.repositories.City;

import com.geekhub.domain.entities.City;

import java.util.List;

public interface CityRepository {
    List<City> findAll();
    City findOne(Long id);
}
