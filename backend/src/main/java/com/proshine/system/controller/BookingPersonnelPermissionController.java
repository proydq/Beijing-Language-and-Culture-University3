package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.service.BookingPersonnelPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 预约人员权限配置控制器
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/booking-personnel-permission")
@Slf4j
public class BookingPersonnelPermissionController {

    @Autowired
    private BookingPersonnelPermissionService permissionService;

    /**
     * 获取预约人员权限列表
     */
    @GetMapping("/list")
    public ResponseEntity<ResponsePageDataEntity<BookingPersonnelPermissionDto>> getPermissionList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        try {
            SearchBookingPersonnelPermissionCondition condition = new SearchBookingPersonnelPermissionCondition();
            condition.setPageNum(pageNum);
            condition.setPageSize(pageSize);
            condition.setKeyword(keyword);
            
            ResponsePageDataEntity<BookingPersonnelPermissionDto> result = permissionService.searchPermissions(condition);
            
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取预约人员权限列表失败：", e);
            return ResponseEntity.fail("获取预约人员权限列表失败：" + e.getMessage());
        }
    }

    /**
     * 新增预约人员权限配置
     */
    @PostMapping
    public ResponseEntity<Void> createPermission(
            @Valid @RequestBody BookingPersonnelPermissionSaveRequest request) {
        
        try {
            permissionService.savePermission(request);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("创建预约人员权限配置失败：", e);
            return ResponseEntity.fail("创建预约人员权限配置失败：" + e.getMessage());
        }
    }

    /**
     * 编辑预约人员权限配置
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePermission(
            @PathVariable String id,
            @Valid @RequestBody BookingPersonnelPermissionSaveRequest request) {
        
        try {
            permissionService.updatePermission(id, request);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("更新预约人员权限配置失败：", e);
            return ResponseEntity.fail("更新预约人员权限配置失败：" + e.getMessage());
        }
    }

    /**
     * 删除预约人员权限配置
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(
            @PathVariable String id) {
        
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除预约人员权限配置失败：", e);
            return ResponseEntity.fail("删除预约人员权限配置失败：" + e.getMessage());
        }
    }

    /**
     * 导出预约人员权限列表
     */
    @GetMapping("/export")
    public void exportPermissions(
            @RequestParam(required = false) String keyword,
            HttpServletResponse response) {
        
        try {
            permissionService.exportPermissions(keyword, response);
        } catch (Exception e) {
            log.error("导出预约人员权限列表失败：", e);
            throw new RuntimeException("导出预约人员权限列表失败：" + e.getMessage());
        }
    }





    /**
     * 检查用户是否有指定房间的预约权限
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkBookingPermission(
            @RequestParam String userId,
            @RequestParam String roomId) {
        
        try {
            boolean hasPermission = permissionService.hasBookingPermission(userId, roomId);
            return ResponseEntity.success(hasPermission);
        } catch (Exception e) {
            log.error("检查预约权限失败：", e);
            return ResponseEntity.fail("检查预约权限失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取可预约的房间列表
     */
    @GetMapping("/bookable-rooms/{userId}")
    public ResponseEntity<List<RoomVo>> getBookableRoomsByUserId(
            @PathVariable String userId) {
        
        try {
            List<RoomVo> rooms = permissionService.getBookableRoomsByUserId(userId);
            return ResponseEntity.success(rooms);
        } catch (Exception e) {
            log.error("获取用户可预约房间列表失败：", e);
            return ResponseEntity.fail("获取用户可预约房间列表失败：" + e.getMessage());
        }
    }
}