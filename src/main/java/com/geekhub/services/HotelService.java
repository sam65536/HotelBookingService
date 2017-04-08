package com.geekhub.services;

import com.geekhub.domain.CustomUserDetail;
import com.geekhub.domain.Hotel;
import com.geekhub.domain.Room;
import com.geekhub.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

@Service
public class HotelService {

    public Map<Room, Map<LocalDate, Boolean>> getOccupancy(Hotel hotel, LocalDate start, LocalDate end) {
        List<LocalDate> dates = new LinkedList<>();

        Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .forEach(dates::add);

        Map<Room, Map<LocalDate, Boolean>> result = new TreeMap<>();
        for (Room room : hotel.getRooms().values()) {
            Map<LocalDate, Long> reservedDays = room.getReservedDays();
            Map<LocalDate, Boolean> roomOccupied = new TreeMap<>();
            for (LocalDate date : dates) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formatDate = simpleDateFormat.format(date);
                for (LocalDate reservedDate : reservedDays.keySet()) {
                    String day = simpleDateFormat.format(reservedDate);
                    if (formatDate.equals(day)) {
                        roomOccupied.put(date, true);
                    }
                    else {
                        if (!(roomOccupied.containsKey(date) && roomOccupied.get(date))) {
                            roomOccupied.put(date, false);
                        }
                    }
                }
                if (reservedDays.isEmpty()) {
                    roomOccupied.put(date, false);
                }
            }
            result.put(room, roomOccupied);
        }
        return result;
    }

   public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail myUser = (CustomUserDetail) authentication.getPrincipal();
        return myUser.getUser();
    }
}
