package com.geekhub.controllers;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.Room;
import com.geekhub.domain.entities.RoomType;
import com.geekhub.domain.entities.User;
import com.geekhub.exceptions.BookingNotFoundException;
import com.geekhub.security.annotations.AllowedForApprovingBookings;
import com.geekhub.security.annotations.AllowedForHotelManager;
import com.geekhub.security.annotations.AllowedForSystemUsers;
import com.geekhub.services.Booking.BookingService;
import com.geekhub.services.CustomUserDetailsService;
import com.geekhub.services.Room.RoomService;
import com.geekhub.services.RoomTypeService.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/bookings")
@SessionAttributes({"booking"})
public class BookingController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final RoomTypeService roomTypeService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public BookingController(RoomService roomService,BookingService bookingService,RoomTypeService roomTypeService,CustomUserDetailsService customUserDetailsService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
        this.roomTypeService = roomTypeService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @AllowedForHotelManager
    public String index(Model model) {
        User user = customUserDetailsService.getCurrentUser();
        model.addAttribute("bookings", bookingService.getUserBookings(user.getId()));
        return "bookings/index";
    }

    @AllowedForSystemUsers
    @RequestMapping(value = "/new/{roomId}", method = RequestMethod.GET)
    public String bookTheRoom(@ModelAttribute Booking booking, @PathVariable long roomId) {
        Room room = roomService.findOne(roomId);
        User user = customUserDetailsService.getCurrentUser();
        booking.setRoom(room);
        booking.setUser(user);
        return "bookings/create";
    }


    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    public String approveBooking(@ModelAttribute Booking booking) {
        booking.setState(false);
        bookingService.save(booking);
        return "redirect:/";
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