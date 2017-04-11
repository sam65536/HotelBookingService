package com.geekhub.domain.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {
    private long id;
    private LocalDateTime insertionDate;
    private String path;

    @JsonBackReference
    private Hotel hotel;
}