package com.proshine.controller;

import com.proshine.entity.Room;
import com.proshine.response.ResponseEntity;
import com.proshine.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<Page<Room>> list(Pageable pageable) {
        return ResponseEntity.success(roomService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Room room) {
        return ResponseEntity.success(roomService.save(room));
    }
}
