package com.proshine.controller;

import com.proshine.entity.Admin;
import com.proshine.response.ResponseEntity;
import com.proshine.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<Page<Admin>> list(Pageable pageable) {
        return ResponseEntity.success(adminService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<Admin> create(@RequestBody Admin admin) {
        return ResponseEntity.success(adminService.save(admin));
    }
}
