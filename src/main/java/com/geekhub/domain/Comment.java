package com.geekhub.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private Date date;
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

    public Comment(String text, Date date, User user, boolean status, Hotel hotel) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isAnswer() {
        return isAnswer;
    }

    public void setAnswer(boolean answer) {
        isAnswer = answer;
    }

    public boolean isStatus() {
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