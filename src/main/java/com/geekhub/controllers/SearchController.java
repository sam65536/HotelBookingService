package com.geekhub.controllers;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.City;
import com.geekhub.domain.entities.Room;
import com.geekhub.services.City.CityService;
import com.geekhub.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/search")
@SessionAttributes({"booking"})
public class SearchController {

    private final CityService cityService;
    private final SearchService searchService;

    @Autowired
    public SearchController(CityService cityService, SearchService searchService) {
        this.cityService = cityService;
        this.searchService = searchService;
    }

    @RequestMapping(value = "/ajax/{cityId}", method = RequestMethod.GET, produces = {"application/json"})
    public @ResponseBody Map<Long, String> getHotelsOfCity(@PathVariable("cityId") long id) {
        City city = cityService.findOne(id);
        Map<Long, String> result = new HashMap<>();
        city.getHotels().forEach(hotel -> result.put(hotel.getId(), hotel.getName()));
        return result;
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
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("hotels", cityService.findOne(cityId).getHotels());
        model.addAttribute("rooms", roomsList);

        Booking booking = new Booking();
        booking.setBeginDate(beginDate);
        booking.setEndDate(endDate);

        model.addAttribute("booking", booking);

        return "rooms/available-rooms";
    }
}