package com.geekhub;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.Room;
import com.geekhub.domain.entities.User;
import com.geekhub.repositories.Booking.BookingRepository;
import com.geekhub.repositories.Room.RoomRepository;
import com.geekhub.repositories.User.UserRepository;
import com.geekhub.security.SecurityConfig;
import com.geekhub.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.*;

@SpringBootApplication
public class Application implements CommandLineRunner {

   private UserRepository users;
   private BookingRepository bookings;
   private RoomRepository rooms;
   private BookingService bookingService;

   @Autowired
   public void setUsers(UserRepository users) {
       this.users = users;
   }

   @Autowired
   public void setBookings(BookingRepository bookings) {
       this.bookings = bookings;
   }

   @Autowired
   public void setRooms(RoomRepository rooms) {
       this.rooms = rooms;
   }

   @Autowired
   public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
   }

   public static void main(String[] args) {
       SpringApplication.run(Application.class, args);
   }

   @Override
   public void run(String... strings) {

       for (User user : users.findAll()) {
           String password = user.getPassword();
           user.setPassword(SecurityConfig.encoder.encode(password));
           users.save(user);
       }

       for (Booking booking : bookings.findAll()) {
           List<LocalDate> bookingDays = bookingService.getBookingDays(booking);
           Map<LocalDate, Long> tmpMap = new HashMap<>();
           for (LocalDate date : bookingDays) {
               tmpMap.put(date, booking.getId());
	       }
	       for (Room room : booking.getRooms()) {
               room.setReservedDays(tmpMap);
               rooms.save(room);
           }
           bookings.save(booking);
       }
   }
}