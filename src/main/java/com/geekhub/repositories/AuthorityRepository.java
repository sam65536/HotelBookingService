package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Authority;

public interface AuthorityRepository extends CrudRepository<Authority, Long> {
    Authority findByRole(String role);
}