package com.geekhub.controllers;

import java.util.List;

import com.geekhub.repositories.Booking.BookingRepository;
import com.geekhub.repositories.Hotel.HotelRepository;
import com.geekhub.repositories.RoomType.RoomTypeRepository;
import com.geekhub.repositories.User.UserRepository;
import com.geekhub.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.RoomType;
import com.geekhub.domain.entities.User;
import com.geekhub.security.AllowedForApprovingBookings;
import com.geekhub.security.AllowedForHotelManager;
import com.geekhub.exceptions.BookingNotFoundException;

@Controller
@RequestMapping(value = "/bookings")
@SessionAttributes({"booking", "numberRooms", "roomType"})
public class BookingController {

    private final BookingRepository bookings;
    private final HotelRepository hotels;
    private final UserRepository users;
    private final UserService userService;
    private final RoomTypeRepository roomTypes;
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingRepository bookings, HotelRepository hotels, UserRepository users, UserService userService, RoomTypeRepository roomTypes, BookingService bookingService) {
        this.bookings = bookings;
        this.hotels = hotels;
        this.users = users;
        this.userService = userService;
        this.roomTypes = roomTypes;
        this.bookingService = bookingService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @AllowedForHotelManager
    public String index(Model model) {
        User user = bookingService.getCurrentUser();
        List<Booking> bookingList = userService.getUserBookings(user.getId());
        model.addAttribute("bookings", bookingList);
        return "bookings/index";
    }

    @RequestMapping(value = "/roomTypes", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
    public @ResponseBody Iterable<RoomType> getRoomTypes() {
        return roomTypes.findAll();
    }

//    @RequestMapping(value = "/new/{hotelId}", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
//    public @ResponseBody Booking bookRoomJSON(@PathVariable("hotelId") long hotelId) {
//        long firstRoomType = 1;
//        int numberRooms = 2;
//        Booking booking = new Booking();
//        booking.setBeginDate(LocalDate.parse("2016-02-28"));
//        booking.setEndDate(LocalDate.parse("2016-03-02"));
//        RoomType currentRoomType = roomTypes.findOne(firstRoomType);
//        List<LocalDate> dates = bookingService.getBookingDays(booking);
//        booking.setUser(users.findOne((long) 1));
//        Hotel hotel = hotels.findOne(hotelId);
//        List<Room> availableRooms = bookingService.getAvailableRooms(numberRooms, hotel, currentRoomType, booking, dates);
//        Set<Room> roomsBooking = new HashSet<>(availableRooms);
//        booking.setRooms(roomsBooking);
//        bookings.save(booking);
//        return booking;
//    }
//
//    @RequestMapping(value = "/new/{hotelId}", method = RequestMethod.GET)
//    @AllowedForSystemUsers
//    public String bookRoom(Model model, @PathVariable("hotelId") long hotelId,
//                           @ModelAttribute("booking") Booking booking,
//                           @ModelAttribute("numberRooms") int numberRooms,
//                           @ModelAttribute("roomType") long roomType, Authentication authentication) {
//        RoomType currentRoomType = roomTypes.findOne(roomType);
//        List<LocalDate> dates = bookingService.getBookingDays(booking);
//        booking.setUser(bookingService.getCurrentUser());
//        Hotel hotel = hotels.findOne(hotelId);
//        List<Room> availableRooms = bookingService.getAvailableRooms(numberRooms, hotel, currentRoomType, booking, dates);
//        Set<Room> bookedRooms = new HashSet<>(availableRooms);
//        booking.setRooms(bookedRooms);
//        bookings.save(booking);
//        model.addAttribute("bookings", bookings.findAll());
//        CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;
//        if (principal != null) {
//            String authority = (principal.getAuthorities().iterator().next()).getAuthority();
//            if (authority.equals("ROLE_USER")
//                    || authority.equals("ROLE_COMMENT_MODERATOR")
//                    || authority.equals("ROLE_ADMIN")) {
//                return "redirect:/users/me";
//            }
//        }
//        return "redirect:/bookings";
//    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newBooking(Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("roomTypes", roomTypes.findAll());
        return "bookings/create";
    }

//    @RequestMapping(value = "/search", method = RequestMethod.POST)
//    @AllowedForSystemUsers
//    public String searchRooms(@ModelAttribute Booking booking, Model model,
//                              @RequestParam("roomType") long roomType,
//                              @RequestParam("numberRooms") int numberRooms) {
//        RoomType currentRoomType = roomTypes.findOne(roomType);
//        List<LocalDate> dates = bookingService.getBookingDays(booking);
//        List<Room> availableRooms = bookingService.getAvailableRooms(numberRooms, currentRoomType, dates);
//        model.addAttribute("rooms", availableRooms);
//        model.addAttribute("booking", booking);
//        model.addAttribute("roomType", currentRoomType);
//        model.addAttribute("numberRooms", numberRooms);
//        return "bookings/results";
//    }
//
//    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = {"text/plain", "application/json"})
//    public @ResponseBody Iterable<Room> searchRoomsJSON(LocalDate checkin, LocalDate checkout, String rooms, long roomType) {
//        int numberRooms = Integer.parseInt(rooms);
//        Booking booking = new Booking();
//        booking.setBeginDate(checkin);
//        booking.setEndDate(checkout);
//        RoomType currentRoomType = roomTypes.findOne(roomType);
//        List<LocalDate> dates = bookingService.getBookingDays(booking);
//        List<Room> availableRooms = bookingService.getAvailableRooms(numberRooms, currentRoomType, dates);
//        return availableRooms;
//    }

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

//    @RequestMapping(value = "/{bookingId}/remove", method = RequestMethod.GET)
//    @AllowedForRemovingBookings
//    public String removeBooking(Model model, @PathVariable("bookingId") long bookingId, Authentication authentication) {
//        Booking booking = bookings.findOne(bookingId);
//        if (booking == null) {
//            throw new BookingNotFoundException();
//        }
//        Set<Room> rooms = booking.getRooms();
//        Iterator<Room> iterator = rooms.iterator();
//        while (iterator.hasNext()) {
//            Room room = iterator.next();
//            Map<LocalDate, Long> daysReserved = room.getReservedDays();
//            List<LocalDate> dates = bookingService.getBookingDays(booking);
//            for (LocalDate date : dates) {
//                daysReserved.remove(date);
//            }
//            room.setReservedDays(daysReserved);
//        }
//        bookings.delete(booking.getId());
//        CustomUserDetail principal = (authentication != null) ? (CustomUserDetail) authentication.getPrincipal() : null;
//        if (principal != null) {
//            String authority = (principal.getAuthorities().iterator().next()).getAuthority();
//            if (authority.equals(("ROLE_USER"))) {
//                return "redirect:/users/me";
//            }
//        }
//        return "redirect:/bookings";
//    }
}