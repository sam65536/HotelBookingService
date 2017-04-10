package com.geekhub.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class Hotel {

    private Long id;
    private String address;
    private String name;
    private int rating;
    private boolean status;
    private City city;
    private Category category;
    
    @JsonBackReference
    private User manager;
    
    @JsonBackReference
    private Map<Long, Room> rooms = new HashMap<>();

    @JsonManagedReference
    private Map<Long, Comment> comments = new HashMap<>();

    @JsonManagedReference
    private Map<Long, Image> images = new HashMap<>();
 
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Map<Long, Room> getRooms() {
        return rooms;
    }

    public void setRooms(Map<Long, Room> rooms) {
        this.rooms = rooms;
    }

    public Map<Long, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<Long, Comment> comments) {
        this.comments = comments;
    }

    public Map<Long, Image> getImages() {
        return images;
    }

    public void setImages(Map<Long, Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
    	return getName();
    }
}