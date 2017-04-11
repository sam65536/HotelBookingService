package com.geekhub.domain.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@Setter
public class Room implements Comparable<Object> {
    private Long id;
    private int floor;
    private int price;
    private String roomNumber;
    private RoomType type;
    private Hotel hotel;

    private Map<LocalDate, Long> reservedDays = new HashMap<>();
    private Set<Booking> bookings = new HashSet<>();

    public int getCapacity() {
        return Integer.parseInt(type.getOccupancy());
    }

    public boolean isAvailable(LocalDate start, LocalDate end) {
        long reservedDays = Stream.iterate(start, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                .filter(localDate -> getReservedDays().keySet().contains(localDate))
                .count();
       return (reservedDays == 0);
    }

    @Override
    public int compareTo(Object o) {
        return getRoomNumber().compareTo( ((Room) o).getRoomNumber() );
    }
}