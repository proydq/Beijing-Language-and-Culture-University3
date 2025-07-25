package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.RoleManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/role-management")
@Slf4j
public class RoleManagementController {

    @Autowired
    private RoleManagementService roleManagementService;

    /**
     * 分页查询角色列表
     */
    @PostMapping("/search")
    public ResponseEntity<ResponsePageDataEntity<RoleVo>> search(@RequestBody SearchRoleCondition condition) {
        try {
            log.info("==========/role-management/search=============");
            condition.setCustomerId(SecurityUtil.getCustomerId());
            ResponsePageDataEntity<RoleVo> result = roleManagementService.searchRoles(condition);
            log.info("查询角色列表成功，共 {} 条记录", result.getTotal());
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("查询角色列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 根据ID查询角色详情
     */
    @GetMapping("/findById")
    public ResponseEntity<RoleDto> findById(@RequestParam String id) {
        try {
            log.info("==========/role-management/findById=============");
            log.info("查询角色详情：{}", id);
            RoleDto dto = roleManagementService.findById(id);
            return ResponseEntity.success(dto);
        } catch (Exception e) {
            log.error("查询角色详情失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 新增或编辑角色
     */
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody RoleDto roleDto) {
        try {
            log.info("==========/role-management/save=============");
            log.info("保存角色：{}", roleDto.getRoleName());
            roleManagementService.save(roleDto);
            log.info("角色保存成功：{}", roleDto.getRoleName());
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("保存角色失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 逻辑删除角色
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String id) {
        try {
            log.info("==========/role-management/delete=============");
            log.info("删除角色：{}", id);
            roleManagementService.delete(id);
            log.info("角色删除成功：{}", id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除角色失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取权限树结构
     */
    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionNode>> permissions() {
        try {
            log.info("==========/role-management/permissions=============");
            List<PermissionNode> tree = roleManagementService.getPermissionTree();
            log.info("获取权限树成功，共 {} 个节点", tree.size());
            return ResponseEntity.success(tree);
        } catch (Exception e) {
            log.error("获取权限树失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 重新分配角色权限
     */
    @PostMapping("/assign-permissions")
    public ResponseEntity<Void> assignPermissions(@RequestBody AssignPermissionRequest request) {
        try {
            log.info("==========/role-management/assign-permissions=============");
            log.info("分配权限：角色ID={}, 权限IDs={}", request.getRoleId(), request.getPermissionIds());
            roleManagementService.assignPermissions(request);
            log.info("角色权限分配成功：{}", request.getRoleId());
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("角色权限分配失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}
