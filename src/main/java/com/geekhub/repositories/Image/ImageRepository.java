package com.geekhub.repositories.Image;

import com.geekhub.domain.entities.Image;

import java.util.List;

public interface ImageRepository {
    public List<Image> findAll();
    public Image findOne(long id);
    public void save (Image image);
    public void delete (long id);
}