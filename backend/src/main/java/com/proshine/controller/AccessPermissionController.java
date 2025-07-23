package com.proshine.controller;

import com.proshine.entity.AccessPermission;
import com.proshine.response.ResponseEntity;
import com.proshine.service.AccessPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access-permission")
public class AccessPermissionController {

    private final AccessPermissionService service;

    @Autowired
    public AccessPermissionController(AccessPermissionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<AccessPermission>> list(Pageable pageable) {
        return ResponseEntity.success(service.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<AccessPermission> create(@RequestBody AccessPermission permission) {
        return ResponseEntity.success(service.save(permission));
    }
}
