package com.geekhub.repositories.RoomType;

import com.geekhub.domain.entities.RoomType;

import java.util.List;

public interface RoomTypeRepository {
    List<RoomType> findAll();
    RoomType findOne(Long id);
}