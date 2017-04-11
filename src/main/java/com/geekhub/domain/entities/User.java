package com.geekhub.domain.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String email;
    private String name;
    private String password;
    private String username;
    private Authority authority;

    @JsonManagedReference
    private Map<Long, Comment> comments = new HashMap<>();
	
    @JsonManagedReference
    private Set<Hotel> hotels = new HashSet<>();

    public User() {
    }

    public User(String email, String name, String password, String username) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.username = username;
    }

    public Iterable<Comment> getComments() {
        return comments.values();
    }
}