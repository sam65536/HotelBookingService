package com.geekhub.controllers;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.RoomType;
import com.geekhub.domain.entities.User;
import com.geekhub.exceptions.BookingNotFoundException;
import com.geekhub.security.AllowedForApprovingBookings;
import com.geekhub.security.AllowedForHotelManager;
import com.geekhub.security.AllowedForSystemUsers;
import com.geekhub.services.Booking.BookingService;
import com.geekhub.services.CustomUserDetailsService;
import com.geekhub.services.Hotel.HotelService;
import com.geekhub.services.RoomTypeService.RoomTypeService;
import com.geekhub.services.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping(value = "/bookings")
@SessionAttributes({"booking", "numberRooms", "roomType"})
public class BookingController {

    private final HotelService hotelService;
    private final BookingService bookingService;
    private final UserService userService;
    private final RoomTypeService roomTypeService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public BookingController(BookingService bookingService, HotelService hotelService, UserService userService,
                             RoomTypeService roomTypeService, CustomUserDetailsService customUserDetailsService) {
        this.bookingService = bookingService;
        this.hotelService = hotelService;
        this.roomTypeService = roomTypeService;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @AllowedForHotelManager
    public String index(Model model) {
        User user = customUserDetailsService.getCurrentUser();
        model.addAttribute("bookings", bookingService.getUserBookings(user.getId()));
        return "bookings/index";
    }

    @RequestMapping(value = "/roomTypes", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Iterable<RoomType> getRoomTypes() {
        return roomTypeService.findAll();
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newBooking(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("roomTypes", roomTypeService.findAll());
        return "bookings/create";
    }

    @RequestMapping(value = "/{bookingId}/approve", method = RequestMethod.GET)
    @AllowedForApprovingBookings
    public String approveBooking(@PathVariable("bookingId") long bookingId) {
        Booking booking = bookingService.findOne(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException();
        }
        booking.setState(true);
        bookingService.save(booking);
        return "redirect:/bookings/";
    }
}