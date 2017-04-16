package com.geekhub.domain.entities;

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
}