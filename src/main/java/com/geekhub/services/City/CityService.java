package com.geekhub.services.City;

import com.geekhub.domain.entities.City;

import java.util.List;

public interface CityService  {
    List<City> findAll();
    City findOne(Long id);
}
