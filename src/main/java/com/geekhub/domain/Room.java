package com.geekhub.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Room implements Comparable<Object> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
	
    private int floor;
    private int price;
    private String roomNumber;

    @JsonManagedReference
    @ManyToOne
    private Hotel hotel;

    @JsonManagedReference
    @ManyToOne
    private RoomType type;
	
    @ElementCollection
    private Map<Date, Long> reservedDays = new HashMap<>();

    @JsonBackReference
    @ManyToMany(mappedBy = "rooms")
    private Set<Booking> bookings = new HashSet<>();

    public Room() {
    }

    public Room(int floor, int price, String roomNumber, Hotel hotel, RoomType type) {
        this.floor = floor;
        this.price = price;
        this.roomNumber = roomNumber;
        this.hotel = hotel;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public Map<Date, Long> getReservedDays() {
        return reservedDays;
    }

    public void setReservedDays(Map<Date, Long> reservedDays) {
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

    public boolean isAvailable(Date beginDate, Date endDate) {
       for (Date date : getReservedDays().keySet()) {
           if (date.equals(beginDate) || (date.equals(endDate))) {
               return false;
           }
       }
       return true;
    }
    @Override
    public int compareTo(Object o) {
        return getRoomNumber().compareTo( ((Room) o).getRoomNumber() );
    }
}