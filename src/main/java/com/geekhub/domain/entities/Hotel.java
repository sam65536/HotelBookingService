package com.geekhub.domain.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hotel {
    private long id;
    private String address;
    private String name;
    private int rating;
    private boolean status;
    private City city;
    private Category category;
    private List<Image> images = new ArrayList<>();

    @JsonBackReference
    private User manager;
    
    @JsonBackReference
    private Map<Long, Room> rooms = new HashMap<>();

    @JsonManagedReference
    private Map<Long, Comment> comments = new HashMap<>();

    @Override
    public String toString() {
    	return getName();
    }
}