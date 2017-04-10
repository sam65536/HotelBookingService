package com.geekhub.repositories.City;

import com.geekhub.domain.City;

import java.util.List;

public interface CityRepository {
    public List<City> findAll();
    public City findOne(Long id);
}
