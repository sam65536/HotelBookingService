package com.geekhub.domain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Category {
    private long id;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}