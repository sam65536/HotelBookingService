package com.geekhub.common;

import com.geekhub.domain.entities.Booking;
import com.geekhub.domain.entities.Room;
import com.geekhub.domain.entities.User;
import com.geekhub.repositories.Booking.BookingRepository;
import com.geekhub.repositories.Room.RoomRepository;
import com.geekhub.repositories.User.UserRepository;
import com.geekhub.security.SecurityConfig;
import com.geekhub.services.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MvcLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MvcLoader.class);

    @Autowired
    Environment environment;

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

    @Override
    public void run(String... args) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String option : args) {
            sb.append(" ").append(option);
        }

        sb = sb.length() == 0 ? sb.append("No Options Specified") : sb;
        logger.info(String.format("App launched with following arguments: %s", sb.toString()));

        PropertySource<?> ps = new SimpleCommandLinePropertySource(args);
        String appUrl = (String) ps.getProperty("appurl");

        logger.info(String.format("Command-line appurl is %s", appUrl));

        String applicationPropertyUrl = environment.getProperty("spring.application.url");
        logger.info(String.format("Current Spring Social ApplicationUrl is %s", applicationPropertyUrl));

        String applicationVersion = environment.getProperty("web.site.version");
        logger.info(String.format("MVC Application Version: %s", applicationVersion));

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