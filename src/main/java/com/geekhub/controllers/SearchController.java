package com.geekhub.controllers;

import com.geekhub.domain.City;
import com.geekhub.domain.Hotel;
import com.geekhub.domain.Room;
import com.geekhub.repositories.CityRepository;
import com.geekhub.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class SearchController {

    private final CityRepository cities;
    private final SearchService searchService;

    @Autowired
    public SearchController(CityRepository cities, SearchService searchService) {
        this.cities = cities;
        this.searchService = searchService;
    }

    @RequestMapping(value = "/ajax/{cityId}", method = RequestMethod.GET)
    public List<String> getHotelsOfCity(@PathVariable("cityId") long id) {
        City city = cities.findOne(id);
        List<Hotel> hotels = new ArrayList<>(city.getHotels().values());
        List<String> result = new ArrayList<>();
        hotels.stream().map(Hotel::getName).forEach(result::add);
        return result;
    }

    @RequestMapping(value = "/search/rooms", method = RequestMethod.GET)
    public List<Room> searchAvailableRooms (Long hotelId, int persons, Date beginDate, Date endDate) {
        List<Room> roomsList = searchService.searchAvailableRooms(hotelId, persons, beginDate, endDate);
        return roomsList;
    }
}
