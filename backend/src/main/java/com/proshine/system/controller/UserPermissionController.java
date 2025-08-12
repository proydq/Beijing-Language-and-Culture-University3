package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.PermissionDTO;
import com.proshine.system.entity.SysPermission;
import com.proshine.system.entity.SysRole;
import com.proshine.system.repository.SysPermissionRepository;
import com.proshine.system.repository.SysRoleRepository;
import com.proshine.system.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户权限控制器
 * 提供获取当前用户权限、角色等信息的接口
 */
@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserPermissionController {
    
    @Autowired
    private SysPermissionRepository permissionRepository;
    
    @Autowired
    private SysRoleRepository roleRepository;
    
    /**
     * 获取当前用户的权限列表
     *
     * @return 权限列表
     */
    @GetMapping("/current/permissions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PermissionDTO>> getCurrentUserPermissions() {
        try {
            String userId = SecurityUtil.getCurrentUserId();
            log.info("获取用户权限信息，用户ID：{}", userId);
            
            // 获取用户权限
            List<SysPermission> permissions = permissionRepository.findByUserId(userId);
            
            // 转换为DTO
            List<PermissionDTO> permissionDTOs = permissions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            log.info("用户 {} 拥有 {} 个权限", userId, permissionDTOs.size());
            return ResponseEntity.success(permissionDTOs);
            
        } catch (Exception e) {
            log.error("获取用户权限失败：", e);
            return ResponseEntity.fail("获取用户权限失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户的权限编码列表（仅返回code）
     *
     * @return 权限编码列表
     */
    @GetMapping("/current/permission-codes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getCurrentUserPermissionCodes() {
        try {
            String userId = SecurityUtil.getCurrentUserId();
            
            // 获取用户权限
            List<SysPermission> permissions = permissionRepository.findByUserId(userId);
            
            // 提取权限编码
            List<String> permissionCodes = permissions.stream()
                    .map(SysPermission::getCode)
                    .collect(Collectors.toList());
            
            return ResponseEntity.success(permissionCodes);
            
        } catch (Exception e) {
            log.error("获取用户权限编码失败：", e);
            return ResponseEntity.fail("获取用户权限编码失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户的角色列表
     *
     * @return 角色列表
     */
    @GetMapping("/current/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getCurrentUserRoles() {
        try {
            String userId = SecurityUtil.getCurrentUserId();
            
            // 获取用户角色
            List<SysRole> roles = roleRepository.findByUserId(userId);
            
            // 提取角色编码
            List<String> roleCodes = roles.stream()
                    .map(SysRole::getCode)
                    .collect(Collectors.toList());
            
            return ResponseEntity.success(roleCodes);
            
        } catch (Exception e) {
            log.error("获取用户角色失败：", e);
            return ResponseEntity.fail("获取用户角色失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户的完整权限信息（包括权限和角色）
     *
     * @return 权限和角色信息
     */
    @GetMapping("/current/auth-info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserAuthInfo() {
        try {
            String userId = SecurityUtil.getCurrentUserId();
            
            // 获取权限
            List<SysPermission> permissions = permissionRepository.findByUserId(userId);
            List<PermissionDTO> permissionDTOs = permissions.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            List<String> permissionCodes = permissions.stream()
                    .map(SysPermission::getCode)
                    .collect(Collectors.toList());
            
            // 获取角色
            List<SysRole> roles = roleRepository.findByUserId(userId);
            List<String> roleCodes = roles.stream()
                    .map(SysRole::getCode)
                    .collect(Collectors.toList());
            
            // 构建响应
            Map<String, Object> authInfo = new HashMap<>();
            authInfo.put("permissions", permissionCodes);
            authInfo.put("permissionDetails", permissionDTOs);
            authInfo.put("roles", roleCodes);
            authInfo.put("userId", userId);
            authInfo.put("username", SecurityUtil.getCurrentUsername());
            
            return ResponseEntity.success(authInfo);
            
        } catch (Exception e) {
            log.error("获取用户权限信息失败：", e);
            return ResponseEntity.fail("获取用户权限信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 刷新当前用户的权限信息（权限变更后调用）
     *
     * @return 刷新后的权限信息
     */
    @PostMapping("/current/refresh-permissions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> refreshPermissions() {
        try {
            // 重新获取权限信息
            return getCurrentUserAuthInfo();
            
        } catch (Exception e) {
            log.error("刷新权限失败：", e);
            return ResponseEntity.fail("刷新权限失败：" + e.getMessage());
        }
    }
    
    /**
     * 将权限实体转换为DTO
     */
    private PermissionDTO convertToDTO(SysPermission permission) {
        PermissionDTO dto = new PermissionDTO();
        dto.setCode(permission.getCode());
        dto.setName(permission.getName());
        dto.setType(permission.getType() != null ? permission.getType().name() : null);
        dto.setParentId(permission.getParentId());
        dto.setUrl(permission.getUrl());
        dto.setSort(permission.getSort());
        return dto;
    }
}