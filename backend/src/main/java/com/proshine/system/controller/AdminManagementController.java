package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.AssignRoleRequest;
import com.proshine.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin-management")
@RequiredArgsConstructor
public class AdminManagementController {

    private final UserService userService;

    @PostMapping("/assign-role")
    @PreAuthorize("hasAuthority('user:assign')")
    public ResponseEntity<Void> assignRole(@RequestBody AssignRoleRequest req) {
        userService.assignRole(req.getUserId(), req.getRoleIds());
        return ResponseEntity.success(null);
    }
}
