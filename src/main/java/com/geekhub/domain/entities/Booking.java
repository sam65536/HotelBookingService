package com.geekhub.domain.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Booking {
    private Long id;
    private LocalDate beginDate;
    private LocalDate endDate;
    private boolean state;
    private User user;
    private Set<Room> rooms = new HashSet<>();

    public Hotel getHotel() {
        return new Hotel();
    }
}