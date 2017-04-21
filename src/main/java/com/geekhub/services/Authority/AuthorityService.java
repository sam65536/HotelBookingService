package com.geekhub.services.Authority;

import com.geekhub.domain.UserRole;
import com.geekhub.domain.entities.Authority;

import java.util.List;

public interface AuthorityService {
    List<Authority> findAll();
    Authority findOne(Long id);
    Authority findByRole(UserRole role);
}
