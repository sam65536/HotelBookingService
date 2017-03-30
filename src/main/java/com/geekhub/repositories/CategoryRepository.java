package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {

	Category findByName(String name);
}