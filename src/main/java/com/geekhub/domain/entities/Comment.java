package com.geekhub.domain.entities;

import java.time.LocalDateTime;

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
    private Hotel hotel;
    private User user;
}