package com.geekhub.controllers;

import com.geekhub.domain.City;
import com.geekhub.domain.Room;
import com.geekhub.repositories.City.CityRepository;
import com.geekhub.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(value = "/search")
public class SearchController {

    private final CityRepository cities;
    private final SearchService searchService;

    @Autowired
    public SearchController(CityRepository cities, SearchService searchService) {
        this.cities = cities;
        this.searchService = searchService;
    }

    @RequestMapping(value = "/ajax/{cityId}", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody Map<Long, String> getHotelsOfCity(@PathVariable("cityId") long id) {
        City city = cities.findOne(id);
        Map<Long, String> result = new HashMap<>();
        city.getHotels().values().forEach(hotel -> result.put(hotel.getId(), hotel.getName()));
        return result;
    }

    @RequestMapping(value = "/json", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody List<Room> searchAvailableRooms (
                                            @RequestParam("hotelId") Long hotelId,
                                            @RequestParam("persons") Integer persons,
                                            @RequestParam("beginDate") LocalDate beginDate,
                                            @RequestParam("endDate") LocalDate endDate) {

        List<Room> roomsList = searchService.searchAvailableRooms(hotelId, persons, beginDate, endDate);
        return roomsList;
    }

    @RequestMapping(value = "/rooms", method = RequestMethod.GET)
    public String searchAvailableRooms (Model model,
                                        @RequestParam("cityId") Long cityId,
                                        @RequestParam("hotelId") Long hotelId,
                                        @RequestParam("persons") Integer persons,
                                        @RequestParam("beginDate")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate beginDate,
                                        @RequestParam("endDate")
                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Room> roomsList = searchService.searchAvailableRooms(hotelId, persons, beginDate, endDate);
        model.addAttribute("cities", cities.findAll());
        model.addAttribute("hotels", cities.findOne(cityId).getHotels().values());
        model.addAttribute("rooms", roomsList);
        return "rooms/available-rooms";
    }
}
