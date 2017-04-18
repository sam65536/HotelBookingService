package com.geekhub.services.RoomTypeService;

import com.geekhub.domain.entities.RoomType;
import com.geekhub.repositories.RoomType.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    private RoomTypeRepository roomTypeRepository;

    @Autowired
    public void setRoomRepository(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
    }

    @Override
    public List<RoomType> findAll() {
        return roomTypeRepository.findAll();
    }

    @Override
    public RoomType findOne(Long id) {
        return roomTypeRepository.findOne(id);
    }
}