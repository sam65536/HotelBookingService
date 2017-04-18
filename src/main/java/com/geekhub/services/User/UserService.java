package com.geekhub.services.User;

import com.geekhub.domain.entities.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findOne(Long id);
    User findByUsername(String username);
    void save(User user);
    void delete(Long id);
}
