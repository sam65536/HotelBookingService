package com.geekhub.domain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomType {
    private long id;
    private String description;
    private String occupancy;

    @Override
    public String toString() {
        return description;
    }
}