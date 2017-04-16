package com.geekhub.domain.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hotel {
    private Long id;
    private String address;
    private String name;
    private int rating;
    private boolean status;
    private City city;
    private Category category;
    private User manager;
    private List<Image> images = new ArrayList<>();

    private Map<Long, Room> rooms = new HashMap<>();
    private Map<Long, Comment> comments = new HashMap<>();

    @Override
    public String toString() {
    	return getName();
    }
}