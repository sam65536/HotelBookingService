package com.geekhub.repositories.Category;

import com.geekhub.domain.Category;

import java.util.List;

public interface CategoryRepository {
    public List<Category> findAll();
    public Category findOne(long id);
}
