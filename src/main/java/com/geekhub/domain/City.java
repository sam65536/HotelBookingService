package com.geekhub.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "city", orphanRemoval = true)
    @MapKeyColumn(name = "id")
    private Map<Long, Hotel> hotels = new HashMap<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(Map<Long, Hotel> hotels) {
        this.hotels = hotels;
    }
}
