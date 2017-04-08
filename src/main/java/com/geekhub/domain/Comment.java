package com.geekhub.domain;

import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.geekhub.utils.LocalDateTimeConverter;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime date;

    private boolean isAnswer;
    private boolean status;
    private String text;

    @OneToOne
    private Comment reply;
		
    @JsonBackReference
    @ManyToOne
    private Hotel hotel;
	
    @JsonBackReference
    @ManyToOne
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean getIsAnswer() {
        return isAnswer;
    }

    public void setAnswer(boolean answer) {
        isAnswer = answer;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Comment getReply() {
        return reply;
    }

    public void setReply(Comment reply) {
        this.reply = reply;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}