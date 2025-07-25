package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchUserCondition;
import com.proshine.system.dto.UserVo;
import com.proshine.system.entity.SysUser;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户控制器
 */
@RestController
@RequestMapping("/system/user")
@Slf4j
public class SysUserController {
    
    @Autowired
    private SysUserService userService;
    
    /**
     * 分页查询用户信息
     *
     * @param condition 查询条件
     * @return 分页用户信息
     */
    @PostMapping("/search")
    public ResponseEntity<ResponsePageDataEntity<UserVo>> searchUsers(@RequestBody SearchUserCondition condition) {
        try {
            log.info("==========/system/user/search=============");
            
            // 设置客户域
            condition.setCustomerId(SecurityUtil.getCustomerId());
            
            ResponsePageDataEntity<UserVo> result = userService.searchUsers(condition);
            
            log.info("查询用户信息成功，共 {} 条记录", result.getTotal());
            return ResponseEntity.success(result);
            
        } catch (Exception e) {
            log.error("查询用户信息失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/findById")
    public ResponseEntity<UserVo> findById(@RequestParam String id) {
        try {
            log.info("==========/system/user/findById=============");
            log.info("根据ID查询用户信息：{}", id);
            
            UserVo userVo = userService.findById(id);
            
            log.info("查询用户信息成功：{}", userVo.getUsername());
            return ResponseEntity.success(userVo);
            
        } catch (Exception e) {
            log.error("根据ID查询用户信息失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 保存用户信息
     *
     * @param user 用户实体
     * @return 保存结果
     */
    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody SysUser user) {
        try {
            log.info("==========/system/user/save=============");
            log.info("保存用户信息：{}", user.getUsername());
            
            // 设置客户域
            user.setCustomerId(SecurityUtil.getCustomerId());
            
            userService.save(user);
            
            log.info("用户 {} 保存成功", user.getUsername());
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("保存用户信息失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     *
     * @param user 用户实体
     * @return 更新结果
     */
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody SysUser user) {
        try {
            log.info("==========/system/user/update=============");
            log.info("更新用户信息：{}", user.getId());
            
            userService.update(user);
            
            log.info("用户 {} 更新成功", user.getId());
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("更新用户信息失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 删除用户（逻辑删除）
     *
     * @param ids 用户ID列表
     * @return 删除结果
     */
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody List<String> ids) {
        try {
            log.info("==========/system/user/delete=============");
            log.info("删除用户，IDs：{}", ids);
            
            userService.delete(ids);
            
            log.info("用户删除成功，共 {} 个", ids.size());
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("删除用户失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 批量移动到回收站
     *
     * @param ids 用户ID列表
     * @return 处理结果
     */
    @PostMapping("/moveToRecycleBin")
    public ResponseEntity<Void> moveToRecycleBin(@RequestBody List<String> ids) {
        try {
            log.info("==========/system/user/moveToRecycleBin=============");
            log.info("移动用户到回收站，IDs：{}", ids);
            
            userService.moveToRecycleBin(ids);
            
            log.info("用户移动到回收站成功，共 {} 个", ids.size());
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("移动用户到回收站失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 处理结果
     */
    @GetMapping("/changeStatus")
    public ResponseEntity<Void> changeStatus(
            @RequestParam String userId,
            @RequestParam String status) {
        try {
            log.info("==========/system/user/changeStatus=============");
            log.info("修改用户状态：{}, 状态：{}", userId, status);
            
            SysUser.Status userStatus = SysUser.Status.valueOf(status.toUpperCase());
            userService.changeStatus(userId, userStatus);
            
            log.info("用户 {} 状态修改成功", userId);
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("修改用户状态失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码（可选）
     * @return 处理结果
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(
            @RequestParam String userId,
            @RequestParam(required = false) String newPassword) {
        try {
            log.info("==========/system/user/resetPassword=============");
            log.info("重置用户密码：{}", userId);
            
            userService.resetPassword(userId, newPassword);
            
            log.info("用户 {} 密码重置成功", userId);
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("重置用户密码失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 分配用户角色
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 处理结果
     */
    @PostMapping("/assignRoles")
    public ResponseEntity<Void> assignRoles(
            @RequestParam String userId,
            @RequestBody List<String> roleIds) {
        try {
            log.info("==========/system/user/assignRoles=============");
            log.info("分配用户角色：{}, 角色IDs：{}", userId, roleIds);
            
            userService.assignRoles(userId, roleIds);
            
            log.info("用户 {} 角色分配成功", userId);
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("分配用户角色失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    @GetMapping("/checkUsername")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        try {
            log.info("==========/system/user/checkUsername=============");
            log.info("检查用户名是否存在：{}", username);
            
            String customerId = SecurityUtil.getCustomerId();
            boolean exists = userService.existsByUsername(username, customerId);
            
            log.info("用户名 {} 存在性检查结果：{}", username, exists);
            return ResponseEntity.success(exists);
            
        } catch (Exception e) {
            log.error("检查用户名失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 检查工号是否存在
     *
     * @param jobNumber 工号
     * @return 是否存在
     */
    @GetMapping("/checkJobNumber")
    public ResponseEntity<Boolean> checkJobNumber(@RequestParam String jobNumber) {
        try {
            log.info("==========/system/user/checkJobNumber=============");
            log.info("检查工号是否存在：{}", jobNumber);
            
            String customerId = SecurityUtil.getCustomerId();
            boolean exists = userService.existsByJobNumber(jobNumber, customerId);
            
            log.info("工号 {} 存在性检查结果：{}", jobNumber, exists);
            return ResponseEntity.success(exists);
            
        } catch (Exception e) {
            log.error("检查工号失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}