package com.geekhub.domain.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Booking {
    private Long id;
    private LocalDate beginDate;
    private LocalDate endDate;
    private boolean state;
    private User user;
    private Room room;
}