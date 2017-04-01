package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.User;

public interface UserRepository extends CrudRepository<User,Long> {
    User findByUsername(String username);
}