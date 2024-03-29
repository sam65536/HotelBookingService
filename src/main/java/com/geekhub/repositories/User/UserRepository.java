package com.geekhub.repositories.User;

import com.geekhub.domain.entities.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();
    User findOne(Long id);
    User findByUsername(String username);
    void save(User user);
    void delete(Long id);
}
