package com.geekhub.repositories.User;

import com.geekhub.domain.User;

import java.util.List;

public interface UserRepository {
    public List<User> findAll();
    public User findOne(Long id);
    public User findByUsername(String username);
    public void save (User user);
    public void delete (Long id);
}
