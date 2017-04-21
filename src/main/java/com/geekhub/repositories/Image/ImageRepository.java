package com.geekhub.repositories.Image;

import com.geekhub.domain.entities.Image;

import java.util.List;

public interface ImageRepository {
    List<Image> findAll();
    Image findOne(Long id);
    void save(Image image);
    void delete(Long id);
}