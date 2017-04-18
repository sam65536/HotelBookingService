package com.geekhub.services.RoomTypeService;

import com.geekhub.domain.entities.RoomType;

import java.util.List;

public interface RoomTypeService {
    List<RoomType> findAll();
    RoomType findOne(Long id);
}
