package com.geekhub.domain.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class City {
    private long id;
    private String name;
    private List<Hotel> hotels = new ArrayList<>();
}