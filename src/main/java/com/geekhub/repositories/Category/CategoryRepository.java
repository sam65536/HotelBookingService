package com.geekhub.repositories.Category;

import com.geekhub.domain.entities.Category;

import java.util.List;

public interface CategoryRepository {
    List<Category> findAll();
    Category findOne(Long id);
}
