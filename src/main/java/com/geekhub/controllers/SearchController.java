package com.geekhub.controllers;

import com.geekhub.domain.Hotel;
import com.geekhub.repositories.CityRepository;
import com.geekhub.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final HotelRepository hotels;
    private final CityRepository cities;

    @Autowired
    public SearchController(HotelRepository hotels, CityRepository cities) {
        this.hotels = hotels;
        this.cities = cities;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchHotel(Model model, @RequestParam("cityId") Long cityId) {
        Iterable<Hotel> hotelsList = (cityId == null) ? hotels.findAll() : cities.findOne(cityId).getHotels().values();
        model.addAttribute("hotels", hotelsList);
        return "hotels/index";
    }
}
