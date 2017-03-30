package com.geekhub.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Booking {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private Date beginDate;
	private Date endDate;
	private boolean state;
	
	@ManyToOne
	private User user;
	
	@JsonManagedReference
	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Room> rooms = new HashSet<Room>();
	
	public Booking(){}
	
	public Booking(long id, Date beginDate, Date endDate, boolean state, User user){
		this.id = id;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.user = user;
		this.state = state;
	}
	
	public Hotel getHotel()
	{
		return rooms.iterator().next().getHotel();
	}
	
	public String getRoomType()
	{
		return rooms.iterator().next().getType().toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Room> getRooms() {
		return rooms;
	}

	public void setRooms(Set<Room> rooms) {
		this.rooms = rooms;
	}
}