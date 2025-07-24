package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.AssignPermissionsRequest;
import com.proshine.system.entity.Permission;
import com.proshine.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role-management")
@RequiredArgsConstructor
public class RoleManagementController {

    private final RoleService roleService;

    @GetMapping("/permissions")
    public ResponseEntity<List<Permission>> permissions() {
        List<Permission> list = roleService.allPermissions();
        return ResponseEntity.success(list);
    }

    @PostMapping("/assign-permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<Void> assignPermissions(@RequestBody AssignPermissionsRequest req) {
        roleService.assignPermissions(req.getRoleId(), req.getPermissionIds());
        return ResponseEntity.success(null);
    }
}
