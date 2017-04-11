package com.geekhub.repositories.Authority;

import com.geekhub.domain.entities.Authority;

import java.util.List;

public interface AuthorityRepository {
    public List<Authority> findAll();
    public Authority findOne(Long id);
    public Authority findByRole(String role);
    public void save (Authority authority);
    public void delete (Long id);
}