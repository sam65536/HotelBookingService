package com.geekhub.repositories.RoomType;

import com.geekhub.domain.RoomType;

import java.util.List;

public interface RoomTypeRepository {
    public List<RoomType> findAll();
    public RoomType findOne(Long id);
}
