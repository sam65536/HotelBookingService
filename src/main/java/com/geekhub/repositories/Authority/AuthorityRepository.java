package com.geekhub.repositories.Authority;

import com.geekhub.domain.UserRole;
import com.geekhub.domain.entities.Authority;

import java.util.List;

public interface AuthorityRepository {
    List<Authority> findAll();
    Authority findOne(Long id);
    Authority findByRole(UserRole role);
    void save(Authority authority);
    void delete(Long id);
}