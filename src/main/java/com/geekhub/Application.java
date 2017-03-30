package com.geekhub;

import com.geekhub.domain.Booking;
import com.geekhub.domain.Room;
import com.geekhub.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.geekhub.repositories.AuthorityRepository;
import com.geekhub.repositories.BookingRepository;
import com.geekhub.repositories.RoomRepository;
import com.geekhub.repositories.UserRepository;

import java.util.*;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	UserRepository users;

	@Autowired
	AuthorityRepository authorities;

	@Autowired
	BookingRepository bookings;

	@Autowired
	RoomRepository rooms;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) {
		users.findAll().forEach(user -> {
			String password = user.getPassword();
			user.setPassword(SecurityConfig.encoder.encode(password));
			users.save(user);
		});

		for (Booking booking : bookings.findAll()) {
			Date begin = booking.getBeginDate();
			Date end = booking.getEndDate();
			List<Date> dates = new ArrayList<Date>();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(begin);
			while (calendar.getTime().getTime() <= end.getTime()) {
				Date result = calendar.getTime();
				dates.add(result);
				calendar.add(Calendar.DATE, 1);
			}
			Map<Date, Long> tmpMap = new HashMap<Date, Long>();
			for (Date date : dates) {
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