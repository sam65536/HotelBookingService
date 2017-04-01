package com.geekhub.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.geekhub.domain.Booking;
import com.geekhub.domain.CustomUserDetail;
import com.geekhub.domain.Hotel;
import com.geekhub.domain.Room;
import com.geekhub.domain.RoomType;
import com.geekhub.domain.User;
import com.geekhub.repositories.BookingRepository;
import com.geekhub.repositories.HotelRepository;
import com.geekhub.repositories.RoomRepository;
import com.geekhub.repositories.RoomTypeRepository;
import com.geekhub.repositories.UserRepository;
import com.geekhub.security.AllowedForApprovingBookings;
import com.geekhub.security.AllowedForHotelManager;
import com.geekhub.security.AllowedForRemovingBookings;
import com.geekhub.security.AllowedForSystemUsers;
import com.geekhub.exceptions.BookingNotFoundException;

@Controller
@RequestMapping(value="/bookings")
@SessionAttributes({"booking", "numberRooms","roomType"})
public class BookingController {

	@Autowired
	BookingRepository bookings;

	@Autowired
	HotelRepository hotels;

	@Autowired
	RoomRepository rooms;

	@Autowired
	UserRepository users;

	@Autowired
	RoomTypeRepository roomTypes;

	@RequestMapping(method=RequestMethod.GET)
	@AllowedForHotelManager
	public String index(Model model) {
		User user = getCurrentUser();
		List<Booking> books = new ArrayList<Booking>();
		Iterator<Booking> it = bookings.findAll().iterator();
		while(it.hasNext()){
			Booking book = it.next();
			if(book.getHotel().getManager().getId() == user.getId()){
				books.add(book);
			}
		}
		model.addAttribute("bookings", books);
		return "bookings/index";
	}

	@RequestMapping(value="/roomTypes", method=RequestMethod.GET, produces={"text/plain","application/json"})
	public @ResponseBody Iterable<RoomType> getRoomTypes(){
		return roomTypes.findAll();
	}

	@RequestMapping(value="/new/{hotel_id}", method=RequestMethod.GET, produces={"text/plain","application/json"})
	public @ResponseBody Booking bookRoomJSON(@PathVariable("hotel_id") long hotel_id){

		int numberRooms = 2;
		long roomType = 1;
		Booking booking = new Booking();
		booking.setBeginDate(new Date(1448713320000L));
		booking.setEndDate(new Date(1449145320000L));

		RoomType rt = roomTypes.findOne(roomType);
		List<Date> dates = getDates(booking);

		booking.setUser(users.findOne((long)1));
		Hotel hotel = hotels.findOne(hotel_id);		
		Map<Long,Room> roomsFromHotel = hotel.getRooms();
		List<Room> rooms_available = new ArrayList<Room>();
		int counter = 1;
		for(Long entry : roomsFromHotel.keySet())
		{
			Room r = roomsFromHotel.get(entry);
			Map<Date, Long> room_bookings = r.getReservedDays();
			boolean found = false;
			Iterator<Date> itDates = dates.iterator();

			while(itDates.hasNext()){
				Date day = itDates.next();
				if(room_bookings.get(day) != null){
					found = true;
					break;
				}
			}	
			if(!found && r.getType() == rt && counter <= numberRooms)
			{						
				rooms_available.add(r);
				for(Date date: dates)
					room_bookings.put(date, booking.getId());
				counter++;
			}
			else if(counter > numberRooms)
				break;
		}
		Set<Room> roomsBooking = new HashSet<Room>(rooms_available);
		booking.setRooms(roomsBooking);
		bookings.save(booking);

		return booking;
	}

	@RequestMapping(value="/new/{hotel_id}", method=RequestMethod.GET)
	@AllowedForSystemUsers
	public String bookRoom(Model model, @PathVariable("hotel_id") long hotel_id, @ModelAttribute("booking") Booking booking, @ModelAttribute("numberRooms") int numberRooms,
						   @ModelAttribute("roomType") long roomType, Authentication authentication){

		RoomType rt = roomTypes.findOne(roomType);
		List<Date> dates = getDates(booking);

		booking.setUser(getCurrentUser());
		Hotel hotel = hotels.findOne(hotel_id);		
		Map<Long,Room> roomsFromHotel = hotel.getRooms();
		List<Room> rooms_available = new ArrayList<Room>();
		int counter = 1;
		for(Long entry : roomsFromHotel.keySet())
		{
			Room r = roomsFromHotel.get(entry);
			Map<Date, Long> room_bookings = r.getReservedDays();
			boolean found = false;
			Iterator<Date> itDates = dates.iterator();

			while(itDates.hasNext()){
				Date day = itDates.next();
				if(room_bookings.get(day) != null){
					found = true;
					break;
				}
			}	
			if(!found && r.getType() == rt && counter <= numberRooms)
			{						
				rooms_available.add(r);
				for(Date date: dates)
					room_bookings.put(date, booking.getId());
				counter++;
			}
			else if(counter > numberRooms)
				break;
		}
		Set<Room> roomsBooking = new HashSet<Room>(rooms_available);
		booking.setRooms(roomsBooking);
		bookings.save(booking);
		model.addAttribute("bookings", bookings.findAll());

		CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;
		if(principal != null)
		{
			String a = ((SimpleGrantedAuthority) principal.getAuthorities().iterator().next()).getAuthority();

			if (a.equals("ROLE_USER") || a.equals("ROLE_COMMENT_MODERATOR") || a.equals("ROLE_ADMIN"))
				return "redirect:/users/me";
		}

		return "redirect:/bookings";
	}

	@RequestMapping(value="/new", method=RequestMethod.GET)
	public String newBooking(Model model)
	{
		model.addAttribute("booking", new Booking());
		model.addAttribute("roomTypes", roomTypes.findAll());
		return "bookings/create";
	}

	@RequestMapping(value="/search", method=RequestMethod.POST)
	@AllowedForSystemUsers
	public String searchRooms(@ModelAttribute Booking booking, Model model, @RequestParam("roomType") long roomType, @RequestParam("numberRooms") int numberRooms) {

		RoomType rt = roomTypes.findOne(roomType);
		List<Room> rooms_available = new ArrayList<Room>();
		List<Date> dates = getDates(booking);
		Iterator<Hotel> ithotels = hotels.findAll().iterator();

		while(ithotels.hasNext()){
			Hotel hotel = ithotels.next();

			if(hotel.getStatus())
			{
				Map<Long, Room> rooms = hotel.getRooms();
				int counter = 0;
				Room currentRoom = null;
				for(Entry<Long, Room> room : rooms.entrySet()){
					Room r = room.getValue();
					Map<Date, Long> room_bookings = r.getReservedDays();
					boolean found = false;
					Iterator<Date> itDates = dates.iterator();

					while(itDates.hasNext()){
						Date day = itDates.next();
						if(room_bookings.get(day) != null){
							found = true;
							break;
						}
					}	

					if(!found && r.getType().getDescription().equals(rt.getDescription()))
					{						
						counter++;
						currentRoom = r;
					}
				}
				if(counter >= numberRooms)
					rooms_available.add(currentRoom);
			}
		}

		model.addAttribute("rooms", rooms_available);
		model.addAttribute("booking", booking);
		model.addAttribute("roomType", rt);
		model.addAttribute("numberRooms", numberRooms);
		return "bookings/results";
	}

	@RequestMapping(value="/search", method=RequestMethod.GET, produces={"text/plain","application/json"})
	public @ResponseBody Iterable<Room> searchRoomsJSON(Date checkin, Date checkout, String rooms, long roomType)
	{		
		int numberRooms = Integer.parseInt(rooms);
		Booking booking = new Booking();
		booking.setBeginDate(checkin);
		booking.setEndDate(checkout);

		RoomType rt = roomTypes.findOne(roomType);
		List<Room> rooms_available = new ArrayList<Room>();
		List<Date> dates = getDates(booking);
		Iterator<Hotel> ithotels = hotels.findAll().iterator();

		while(ithotels.hasNext()){
			Hotel hotel = ithotels.next();
			Map<Long, Room> rooms_map = hotel.getRooms();
			int counter = 0;
			Room currentRoom = null;
			for(Entry<Long, Room> room : rooms_map.entrySet()){
				Room r = room.getValue();
				Map<Date, Long> room_bookings = r.getReservedDays();
				boolean found = false;
				Iterator<Date> itDates = dates.iterator();

				while(itDates.hasNext()){
					Date day = itDates.next();
					if(room_bookings.get(day) != null){
						found = true;
						break;
					}
				}	

				if(!found && r.getType().getDescription().equals(rt.getDescription()))
				{						
					counter++;
					currentRoom = r;
				}
			}
			if(counter >= numberRooms)
				rooms_available.add(currentRoom);
		}
		return rooms_available;
	}

	private List<Date> getDates(Booking booking){

		List<Date> dates = new ArrayList<Date>();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(booking.getBeginDate());

		while (calendar.getTime().getTime() <= booking.getEndDate().getTime()){
			Date result = calendar.getTime();
			dates.add(result);
			calendar.add(Calendar.DATE, 1);       
		}      
		return dates;
	}

	@RequestMapping(value="/{booking_id}/approve", method=RequestMethod.GET)
	@AllowedForApprovingBookings
	public String approveBooking(Model model, @PathVariable("booking_id") long booking_id){

		Booking booking = bookings.findOne(booking_id);

		if(booking == null)
			throw new BookingNotFoundException();

		booking.setState(true);
		bookings.save(booking);
		return "redirect:/bookings/";
	}

	@RequestMapping(value="/{booking_id}/remove", method=RequestMethod.GET)
	@AllowedForRemovingBookings
	public String removeBooking(Model model, @PathVariable("booking_id") long booking_id, Authentication authentication){
		Booking booking = bookings.findOne(booking_id);

		if(booking == null)
			throw new BookingNotFoundException();

		Set<Room> rooms = booking.getRooms();
		Iterator<Room> it = rooms.iterator();

		while(it.hasNext()){
			Room room = it.next();
			Map<Date, Long> daysReserved = room.getReservedDays();

			List<Date> dates = getDates(booking);

			for (Date d : dates)
				daysReserved.remove(d);

			room.setReservedDays(daysReserved);
		}

		bookings.delete(booking);

		CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;

		if(principal != null)
		{
			String a = ((SimpleGrantedAuthority) principal.getAuthorities().iterator().next()).getAuthority();

			if (a.equals(("ROLE_USER")))
				return "redirect:/users/me";
		}

		return "redirect:/bookings";
	}

	private User getCurrentUser(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();    	    
		CustomUserDetail myUser= (CustomUserDetail) auth.getPrincipal(); 
		return myUser.getUser();
	}
}