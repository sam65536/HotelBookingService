package com.geekhub.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.geekhub.utils.LocalDateConverter;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate beginDate;

    @Convert(converter = LocalDateConverter.class)
    @JsonFormat(pattern = "YYYY-MM-DD")
    private LocalDate endDate;

    private boolean state;

    @ManyToOne
    private User user;

    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Room> rooms = new HashSet<>();

    public Booking() {
    }

    public Booking(long id, LocalDate beginDate, LocalDate endDate, boolean state, User user) {
        this.id = id;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.user = user;
        this.state = state;
    }

    public Hotel getHotel() {
        return rooms.stream().findAny().get().getHotel();
    }

    public RoomType getRoomType() {
        return rooms.stream().findAny().get().getType();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean getState() {
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