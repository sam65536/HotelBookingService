package com.geekhub.domain.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private long id;
    private LocalDateTime date;
    private boolean isAnswer;
    private boolean status;
    private String text;
    private Comment reply;
		
    @JsonBackReference
    private Hotel hotel;
	
    @JsonBackReference
    private User user;

    public Comment() {
    }

    public Comment(String text, LocalDateTime date, User user, boolean status, Hotel hotel) {
        this.text = text;
        this.date = date;
        this.user = user;
        this.status = status;
        this.hotel = hotel;
        this.isAnswer = false;
    }
}