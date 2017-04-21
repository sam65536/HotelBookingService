package com.geekhub.services.Category;

import com.geekhub.domain.entities.Category;

import java.util.List;

public interface CategoryService {
   List<Category> findAll();
   Category findOne(Long id);
}
