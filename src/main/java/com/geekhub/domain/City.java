package com.geekhub.domain;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "city", orphanRemoval = true)
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
