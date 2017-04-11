package com.geekhub.repositories.Room;

import com.geekhub.domain.entities.Room;

import java.util.List;

public interface RoomRepository {
    public List<Room> findAll();
    public Room findOne(long id);
    public void save (Room room);
    public void delete (long id);
}