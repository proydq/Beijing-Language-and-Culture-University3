package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.entity.SysPermission;
import com.proshine.system.repository.SysPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 调试控制器 - 用于检查权限数据
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private SysPermissionRepository permissionRepository;

    /**
     * 查看所有权限数据
     */
    @GetMapping("/permissions")
    public ResponseEntity<List<SysPermission>> getAllPermissions() {
        List<SysPermission> permissions = permissionRepository.findAll();
        return ResponseEntity.success(permissions);
    }

    /**
     * 查看MENU类型的权限
     */
    @GetMapping("/menu-permissions")
    public ResponseEntity<List<SysPermission>> getMenuPermissions() {
        List<SysPermission> permissions = permissionRepository.findAll()
                .stream()
                .filter(p -> p.getType() == SysPermission.Type.MENU && !p.getDeleted())
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.success(permissions);
    }

    /**
     * 查看特定权限代码的详细信息
     */
    @GetMapping("/permission-details")
    public ResponseEntity<?> getPermissionDetails() {
        // 查找几个关键的权限
        String[] codes = {"USER_MANAGE", "ROLE_MANAGE", "ROOM_MANAGE", "BOOKING_MANAGE"};
        
        for (String code : codes) {
            List<SysPermission> allPermissions = permissionRepository.findAll();
            SysPermission permission = allPermissions.stream()
                    .filter(p -> code.equals(p.getCode()) && !p.getDeleted())
                    .findFirst()
                    .orElse(null);
            if (permission != null) {
                System.out.println("=== 权限详情: " + code + " ===");
                System.out.println("ID: " + permission.getId());
                System.out.println("名称: " + permission.getName());
                System.out.println("类型: " + permission.getType());
                System.out.println("路径: " + permission.getPath());
                System.out.println("组件: " + permission.getComponent());
                System.out.println("图标: " + permission.getIcon());
                System.out.println("可见: " + permission.getVisible());
                System.out.println("父级ID: " + permission.getParentId());
                System.out.println("排序: " + permission.getSort());
                System.out.println("---");
            }
        }
        
        return ResponseEntity.success("权限详情已打印到控制台");
    }
}