package com.geekhub.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Room implements Comparable<Object> {

    private Long id;
    private int floor;
    private int price;
    private String roomNumber;
    private RoomType type;
    private long hotelId;

    private Map<LocalDate, Long> reservedDays = new HashMap<>();
    private Set<Booking> bookings = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public Map<LocalDate, Long> getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(Map<LocalDate, Long> reservedDays) {
        this.reservedDays = reservedDays;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

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