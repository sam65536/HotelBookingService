package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Image;

public interface ImageRepository extends CrudRepository<Image, Long> {
}