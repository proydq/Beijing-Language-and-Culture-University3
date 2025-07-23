package com.proshine.service;

import com.proshine.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomService {
    Page<Room> findAll(Pageable pageable);
    Room save(Room room);
}
