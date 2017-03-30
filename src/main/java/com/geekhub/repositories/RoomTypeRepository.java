package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.RoomType;

public interface RoomTypeRepository extends CrudRepository<RoomType, Long> {
}