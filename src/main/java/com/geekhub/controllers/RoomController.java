package com.geekhub.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.geekhub.domain.Booking;
import com.geekhub.domain.Hotel;
import com.geekhub.domain.Room;
import com.geekhub.repositories.BookingRepository;
import com.geekhub.repositories.HotelRepository;
import com.geekhub.repositories.RoomRepository;
import com.geekhub.repositories.RoomTypeRepository;
import com.geekhub.security.AllowedForManageHotel;

@Controller
@RequestMapping(value = "/hotels")
public class RoomController {

	private final HotelRepository hotels;
	private final RoomTypeRepository roomTypes;
	private final RoomRepository rooms;
	private final BookingRepository bookings;

	@Autowired
	public RoomController(HotelRepository hotels, RoomTypeRepository roomTypes, RoomRepository rooms, BookingRepository bookings) {
        this.hotels = hotels;
        this.roomTypes = roomTypes;
        this.rooms = rooms;
        this.bookings = bookings;
	}

    @RequestMapping(value = "{id}/rooms/new", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String newRoom(@PathVariable("id") long id, Model model) {
    	Room rоом = new Room();
    	model.addAttribute("hotel", hotels.findOne(id));
    	model.addAttribute("room", rоом);
    	model.addAttribute("roomTypes", roomTypes.findAll());
    	return "rooms/create";
    }

    @RequestMapping(value = "{id}/rooms", method = RequestMethod.POST)
    @AllowedForManageHotel
    public String saveRoom(@PathVariable("id") long id, @ModelAttribute Room room, Model model) {  
    	Hotel hotel = hotels.findOne(id);    	
    	room.setHotel(hotel);    	
    	rooms.save(room);
    	return "redirect:/hotels/" + id + "/rooms";
    }

    @RequestMapping(value = "{id}/rooms", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String showRooms(@PathVariable("id") long id, Model model) {
    	Hotel hotel = hotels.findOne(id);
    	Map<Long, Room> hotelRooms = hotel.getRooms();
    	Map<Integer, Room> rooms = new HashMap<>();
    	for (Long roomsId : hotelRooms.keySet()) {
    	    Room room = hotelRooms.get(roomsId);
            rooms.put(Integer.parseInt(room.getRoomNumber()), room);
    	}
    	List<Room> orderedRooms = new ArrayList<>();
    	SortedSet<Integer> orderedSet = new TreeSet<>(rooms.keySet());
    	for (Integer key : orderedSet) {
    	    orderedRooms.add(rooms.get(key));
        }
    	model.addAttribute("hotel", hotel);
    	model.addAttribute("orderedRooms",orderedRooms);
    	return "rooms/hotel-rooms";
    }

    @RequestMapping(value = "{id}/rooms/{roomId}/edit", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String editRoom(@PathVariable("id") long id, @PathVariable("roomId") long roomId, Model model) {
    	Hotel hotel = hotels.findOne(id);
    	model.addAttribute("hotel", hotel);
    	model.addAttribute("room", hotel.getRooms().get(roomId));
    	model.addAttribute("roomTypes", roomTypes.findAll()); 
    	return "rooms/edit";
    }
    
    @RequestMapping(value = "{id}/rooms/{id_room}/remove", method = RequestMethod.GET)
    @AllowedForManageHotel
    public String removeRoom(@PathVariable("id") long id, @PathVariable("roomId") long roomId, Model model) {
    	Hotel hotel = hotels.findOne(id);
    	for(Booking booking : rooms.findOne(roomId).getBookings()) {
    	    bookings.delete(booking);
    	}
    	rooms.delete(roomId);
    	model.addAttribute("hotel", hotel);
    	return "redirect:/hotels/{id}/rooms";
    }
}