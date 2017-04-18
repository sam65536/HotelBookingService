package com.geekhub.controllers;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.Hotel;
import com.geekhub.domain.entities.Room;
import com.geekhub.repositories.RoomType.RoomTypeRepository;
import com.geekhub.security.AllowedForManageHotel;
import com.geekhub.services.Booking.BookingService;
import com.geekhub.services.Hotel.HotelService;
import com.geekhub.services.Room.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping(value = "/hotels")
public class RoomController {

    private final HotelService hotelService;
    private final RoomTypeRepository roomTypes;
    private final RoomService roomService;
    private final BookingService bookingService;

    @Autowired
    public RoomController(HotelService hotelService, RoomTypeRepository roomTypes, RoomService roomService, BookingService bookingService) {
		this.hotelService = hotelService;
		this.roomTypes = roomTypes;
		this.roomService = roomService;
		this.bookingService = bookingService;
    }

    @RequestMapping(value = "{id}/rooms/new", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String newRoom(@PathVariable("id") long id, Model model) {
    	Room room = new Room();
    	model.addAttribute("hotel", hotelService.findOne(id));
    	model.addAttribute("room", room);
    	model.addAttribute("roomTypes", roomTypes.findAll());
    	return "rooms/create";
    }

    @RequestMapping(value = "{id}/rooms", method = RequestMethod.POST)
    @AllowedForManageHotel
    public String saveRoom(@PathVariable("id") long id, @ModelAttribute Room room) {
    	Hotel hotel = hotelService.findOne(id);
    	room.setHotel(hotel);
    	roomService.save(room);
    	return "redirect:/hotels/" + id + "/rooms";
    }

    @RequestMapping(value = "{id}/rooms", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String showRooms(@PathVariable("id") long id, Model model) {
    	Hotel hotel = hotelService.findOne(id);
    	List<Room> orderedRooms = roomService.getHotelRooms(id);
    	model.addAttribute("hotel", hotel);
    	model.addAttribute("orderedRooms", orderedRooms);
    	return "rooms/hotel-rooms";
    }

    @RequestMapping(value = "{id}/rooms/{roomId}/edit", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String editRoom(@PathVariable("id") long id, @PathVariable("roomId") long roomId, Model model) {
    	Hotel hotel = hotelService.findOne(id);
    	model.addAttribute("hotel", hotel);
    	model.addAttribute("room", roomService.getHotelRooms(id).get((int) roomId));
    	model.addAttribute("roomTypes", roomTypes.findAll()); 
    	return "rooms/edit";
    }
    
    @RequestMapping(value = "{id}/rooms/{id_room}/remove", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String removeRoom(@PathVariable("id") long id, @PathVariable("roomId") long roomId, Model model) {
    	Hotel hotel = hotelService.findOne(id);
    	for (Booking booking : roomService.findOne(roomId).getBookings()) {
    	    bookingService.delete(booking.getId());
    	}
    	roomService.delete(roomId);
    	model.addAttribute("hotel", hotel);
    	return "redirect:/hotels/{id}/rooms";
    }
}