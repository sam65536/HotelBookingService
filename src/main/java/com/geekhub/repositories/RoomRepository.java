package com.geekhub.repositories;

import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Room;

public interface RoomRepository extends CrudRepository<Room, Long> {
}