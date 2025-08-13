package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.AdminManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理员管理控制器
 */
@RestController
@RequestMapping("/admin-management")
@Slf4j
@Validated
public class AdminManagementController {

    @Autowired
    private AdminManagementService adminManagementService;

    /**
     * 分页查询管理员列表
     */
    @PostMapping("/admins/search")
    @PreAuthorize("hasAuthority('system:admin:view') or hasAuthority('system:admin:manage')")
    public ResponseEntity<ResponsePageDataEntity<AdminVo>> searchAdmins(@RequestBody SearchAdminCondition condition) {
        try {
            log.info("==========/admin-management/admins/search=============");
            log.info("分页查询管理员列表，条件：{}", condition);
            
            // 设置客户域
            condition.setCustomerId(SecurityUtil.getCustomerId());
            
            ResponsePageDataEntity<AdminVo> result = adminManagementService.searchAdmins(condition);
            
            log.info("查询管理员列表成功，共 {} 条记录", result.getTotal());
            return ResponseEntity.success(result);
            
        } catch (Exception e) {
            log.error("查询管理员列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 添加管理员
     */
    @PostMapping("/admins/add")
    @PreAuthorize("hasAuthority('system:admin:create') or hasAuthority('system:admin:manage')")
    public ResponseEntity<AddAdminResponse> addAdmins(@Valid @RequestBody AddAdminRequest request) {
        try {
            log.info("==========/admin-management/admins/add=============");
            log.info("添加管理员，用户ID：{}，角色ID：{}", request.getUserIds(), request.getRoleIds());
            
            // 设置客户域
            request.setCustomerId(SecurityUtil.getCustomerId());
            
            AddAdminResponse response = adminManagementService.addAdmins(request);
            
            log.info("添加管理员完成，成功：{}，失败：{}", response.getSuccessCount(), response.getFailCount());
            return ResponseEntity.success(response);
            
        } catch (Exception e) {
            log.error("添加管理员失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 更新管理员角色
     */
    @PutMapping("/admins/{userId}/roles")
    @PreAuthorize("hasAuthority('system:admin:update') or hasAuthority('system:admin:manage')")
    public ResponseEntity<Void> updateAdminRoles(
            @PathVariable String userId,
            @Valid @RequestBody UpdateAdminRolesRequest request) {
        try {
            log.info("==========/admin-management/admins/{}/roles=============", userId);
            log.info("更新管理员角色，用户ID：{}，新角色：{}", userId, request.getRoleIds());
            
            adminManagementService.updateAdminRoles(userId, request);
            
            log.info("用户 {} 角色更新成功", userId);
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("更新管理员角色失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/admins/{userId}")
    @PreAuthorize("hasAuthority('system:admin:delete') or hasAuthority('system:admin:manage')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String userId) {
        try {
            log.info("==========/admin-management/admins/{}=============", userId);
            log.info("删除管理员，用户ID：{}", userId);
            
            adminManagementService.deleteAdmin(userId);
            
            log.info("管理员 {} 删除成功", userId);
            return ResponseEntity.success();
            
        } catch (Exception e) {
            log.error("删除管理员失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 批量删除管理员
     */
    @DeleteMapping("/admins/batch")
    @PreAuthorize("hasAuthority('system:admin:delete') or hasAuthority('system:admin:manage')")
    public ResponseEntity<Void> batchDeleteAdmins(@Valid @RequestBody BatchDeleteAdminRequest request) {
        try {
            log.info("==========/admin-management/admins/batch=============");
            log.info("批量删除管理员，用户ID列表：{}", request.getUserIds());
            
            adminManagementService.batchDeleteAdmins(request);
            
            log.info("批量删除管理员完成");
            return ResponseEntity.success();
            
        } catch (Exception e) {
            log.error("批量删除管理员失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 更新管理员状态
     */
    @PutMapping("/admins/{userId}/status")
    @PreAuthorize("hasAuthority('system:admin:update') or hasAuthority('system:admin:manage')")
    public ResponseEntity<Void> updateAdminStatus(
            @PathVariable String userId,
            @Valid @RequestBody UpdateAdminStatusRequest request) {
        try {
            log.info("==========/admin-management/admins/{}/status=============", userId);
            log.info("更新管理员状态，用户ID：{}，新状态：{}", userId, request.getStatus());
            
            adminManagementService.updateAdminStatus(userId, request);
            
            log.info("用户 {} 状态更新成功，新状态：{}", userId, request.getStatus());
            return ResponseEntity.success();
            
        } catch (Exception e) {
            log.error("更新管理员状态失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取可分配角色列表
     */
    @GetMapping("/roles/available")
    @PreAuthorize("hasAuthority('system:admin:view') or hasAuthority('system:admin:manage')")
    public ResponseEntity<List<AvailableRoleVo>> getAvailableRoles() {
        try {
            log.info("==========/admin-management/roles/available=============");
            log.info("获取可分配角色列表");
            
            List<AvailableRoleVo> roles = adminManagementService.getAvailableRoles();
            
            log.info("获取可分配角色列表成功，共 {} 个角色", roles.size());
            return ResponseEntity.success(roles);
            
        } catch (Exception e) {
            log.error("获取可分配角色列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}