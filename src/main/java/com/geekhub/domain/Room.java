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
	@ManyToMany(mappedBy="rooms")
    private Set<Booking> bookings = new HashSet<>();
	
	public Set<Booking> getBookings() {
	    return bookings;
	}

	public void setBookings(Set<Booking> bookings) {
		this.bookings = bookings;
	}

	public Room() {
    }
	
	public Room (long id, int floor, String roomNumber, RoomType type, Hotel hotel, int price) {
		this.floor = floor;
		this.id = id;
		this.roomNumber = roomNumber;
		this.type = type;
		this.hotel = hotel;
		this.setPrice(price);
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

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
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

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
	    this.hotel = hotel;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public int compareTo(Object o) {		
		return getRoomNumber().compareTo(((Room) o).getRoomNumber());
	}
}