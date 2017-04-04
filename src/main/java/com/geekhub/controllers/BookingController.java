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
import com.geekhub.repositories.RoomTypeRepository;
import com.geekhub.repositories.UserRepository;
import com.geekhub.security.AllowedForApprovingBookings;
import com.geekhub.security.AllowedForHotelManager;
import com.geekhub.security.AllowedForRemovingBookings;
import com.geekhub.security.AllowedForSystemUsers;
import com.geekhub.exceptions.BookingNotFoundException;

@Controller
@RequestMapping(value = "/bookings")
@SessionAttributes({"booking", "numberRooms", "roomType"})
public class BookingController {

    private final BookingRepository bookings;
    private final HotelRepository hotels;
    private final UserRepository users;
    private final RoomTypeRepository roomTypes;

    @Autowired
    public BookingController(BookingRepository bookings, HotelRepository hotels, UserRepository users, RoomTypeRepository roomTypes) {
        this.bookings = bookings;
        this.hotels = hotels;
        this.users = users;
        this.roomTypes = roomTypes;
    }

    @RequestMapping(method = RequestMethod.GET)
    @AllowedForHotelManager
    public String index(Model model) {
        User user = getCurrentUser();
        List<Booking> bookingList = new ArrayList<>();
        for (Booking booking : bookings.findAll()) {
            if (booking.getHotel().getManager().getId() == user.getId()) {
                bookingList.add(booking);
            }
        }
        model.addAttribute("bookings", bookingList);
        return "bookings/index";
    }

    @RequestMapping(value = "/roomTypes", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Iterable<RoomType> getRoomTypes() {
        return roomTypes.findAll();
    }

    @RequestMapping(value = "/new/{hotelId}", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Booking bookRoomJSON(@PathVariable("hotelId") long hotelId) {
        long firstRoomType = 1;
        int numberRooms = 2;
        Booking booking = new Booking();
        booking.setBeginDate(new Date(1448713320000L));
        booking.setEndDate(new Date(1449145320000L));
        RoomType currentRoomType = roomTypes.findOne(firstRoomType);
        List<Date> dates = getDates(booking);
        booking.setUser(users.findOne((long) 1));
        Hotel hotel = hotels.findOne(hotelId);
        List<Room> availableRooms = getAvailableRooms(numberRooms, hotel, currentRoomType, booking, dates);
        Set<Room> roomsBooking = new HashSet<>(availableRooms);
        booking.setRooms(roomsBooking);
        bookings.save(booking);
        return booking;
    }

    @RequestMapping(value = "/new/{hotelId}", method = RequestMethod.GET)
    @AllowedForSystemUsers
    public String bookRoom(Model model, @PathVariable("hotelId") long hotelId,
                           @ModelAttribute("booking") Booking booking,
                           @ModelAttribute("numberRooms") int numberRooms,
                           @ModelAttribute("roomType") long roomType, Authentication authentication) {
        RoomType currentRoomType = roomTypes.findOne(roomType);
        List<Date> dates = getDates(booking);
        booking.setUser(getCurrentUser());
        Hotel hotel = hotels.findOne(hotelId);
        List<Room> availableRooms = getAvailableRooms(numberRooms, hotel, currentRoomType, booking, dates);
        Set<Room> bookedRooms = new HashSet<>(availableRooms);
        booking.setRooms(bookedRooms);
        bookings.save(booking);
        model.addAttribute("bookings", bookings.findAll());
        CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;
        if (principal != null) {
            String authority = (principal.getAuthorities().iterator().next()).getAuthority();
            if (authority.equals("ROLE_USER") || authority.equals("ROLE_COMMENT_MODERATOR")
                    || authority.equals("ROLE_ADMIN")) {
                return "redirect:/users/me";
            }
        }
        return "redirect:/bookings";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newBooking(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("roomTypes", roomTypes.findAll());
        return "bookings/create";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @AllowedForSystemUsers
    public String searchRooms(@ModelAttribute Booking booking, Model model,
                              @RequestParam("roomType") long roomType,
                              @RequestParam("numberRooms") int numberRooms) {
        RoomType currentRoomType = roomTypes.findOne(roomType);
        List<Date> dates = getDates(booking);
        List<Room> availableRooms = getAvailableRooms(numberRooms, currentRoomType, dates);
        model.addAttribute("rooms", availableRooms);
        model.addAttribute("booking", booking);
        model.addAttribute("roomType", currentRoomType);
        model.addAttribute("numberRooms", numberRooms);
        return "bookings/results";
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Iterable<Room> searchRoomsJSON(Date checkin, Date checkout, String rooms, long roomType) {
        int numberRooms = Integer.parseInt(rooms);
        Booking booking = new Booking();
        booking.setBeginDate(checkin);
        booking.setEndDate(checkout);
        RoomType currentRoomType = roomTypes.findOne(roomType);
        List<Date> dates = getDates(booking);
        List<Room> availableRooms = getAvailableRooms(numberRooms, currentRoomType, dates);
        return availableRooms;
    }

    @RequestMapping(value = "/{bookingId}/approve", method = RequestMethod.GET)
    @AllowedForApprovingBookings
    public String approveBooking(Model model, @PathVariable("bookingId") long bookingId) {
        Booking booking = bookings.findOne(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException();
        }
        booking.setState(true);
        bookings.save(booking);
        return "redirect:/bookings/";
    }

    @RequestMapping(value = "/{bookingId}/remove", method = RequestMethod.GET)
    @AllowedForRemovingBookings
    public String removeBooking(Model model, @PathVariable("bookingId") long bookingId,
                                Authentication authentication) {

        Booking booking = bookings.findOne(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException();
        }
        Set<Room> rooms = booking.getRooms();
        Iterator<Room> iterator = rooms.iterator();
        while (iterator.hasNext()) {
            Room room = iterator.next();
            Map<Date, Long> daysReserved = room.getReservedDays();
            List<Date> dates = getDates(booking);
            for (Date date : dates) {
                daysReserved.remove(date);
            }
            room.setReservedDays(daysReserved);
        }
        bookings.delete(booking);
        CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;
        if (principal != null) {
            String authority = (principal.getAuthorities().iterator().next()).getAuthority();

            if (authority.equals(("ROLE_USER"))) {
                return "redirect:/users/me";
            }
        }
        return "redirect:/bookings";
    }

    private List<Date> getDates(Booking booking) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(booking.getBeginDate());
        while (calendar.getTime().getTime() <= booking.getEndDate().getTime()) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail myUser = (CustomUserDetail) authentication.getPrincipal();
        return myUser.getUser();
    }

    private List<Room> getAvailableRooms(int numberRooms, Hotel hotel, RoomType roomType, Booking booking, List<Date> dates) {
        Map<Long, Room> roomsFromHotel = hotel.getRooms();
        List<Room> availableRooms = new ArrayList<>();
        int counter = 1;
        for (Long hotelRoomsId : roomsFromHotel.keySet()) {
            Room room = roomsFromHotel.get(hotelRoomsId);
            Map<Date, Long> roomBookings = room.getReservedDays();
            boolean found = false;
            for (Date day : dates) {
                if (roomBookings.get(day) != null) {
                    found = true;
                    break;
                }
            }
            if ((!found) && (room.getType() == roomType) && (counter <= numberRooms)) {
                availableRooms.add(room);
                for (Date date : dates) {
                    roomBookings.put(date, booking.getId());
                }
                counter++;
            } else if (counter > numberRooms) {
                break;
            }
        }
        return availableRooms;
    }

    private List<Room> getAvailableRooms(int numberRooms, RoomType roomType, List<Date> dates) {
        List<Room> availableRooms = new ArrayList<>();
        for (Hotel hotel : hotels.findAll()) {
            if (hotel.getStatus()) {
                Map<Long, Room> rooms = hotel.getRooms();
                int counter = 0;
                Room currentRoom = null;
                for (Entry<Long, Room> roomsEntry : rooms.entrySet()) {
                    Room room = roomsEntry.getValue();
                    Map<Date, Long> roomBookings = room.getReservedDays();
                    boolean found = false;
                    for (Date day : dates) {
                        if (roomBookings.get(day) != null) {
                            found = true;
                            break;
                        }
                    }
                    if (!found && room.getType().getDescription().equals(roomType.getDescription())) {
                        counter++;
                        currentRoom = room;
                    }
                }
                if (counter >= numberRooms) {
                    availableRooms.add(currentRoom);
                }
            }
        }
        return availableRooms;
    }
}