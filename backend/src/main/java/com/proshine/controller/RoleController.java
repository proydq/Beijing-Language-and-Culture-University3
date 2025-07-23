package com.proshine.controller;

import com.proshine.entity.Role;
import com.proshine.response.ResponseEntity;
import com.proshine.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<Page<Role>> list(Pageable pageable) {
        return ResponseEntity.success(roleService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role role) {
        return ResponseEntity.success(roleService.save(role));
    }
}
