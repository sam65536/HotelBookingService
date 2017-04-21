package com.geekhub.domain.entities;

import com.geekhub.domain.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Authority {
    private Long id;
    private UserRole role;
}