package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.entity.SysUser;
import com.proshine.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<SysUser>> list() {
        List<SysUser> list = userService.getAll();
        return ResponseEntity.success(list);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SysUser> save(@RequestBody SysUser user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return ResponseEntity.success(userService.save(user));
    }
}
