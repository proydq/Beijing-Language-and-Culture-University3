package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.entity.SysUser;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.util.UserStatusFixUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户状态管理控制器
 * 用于测试和验证用户状态相关功能
 */
@RestController
@RequestMapping("/debug/user-status")
@Slf4j
public class UserStatusController {
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Autowired
    private UserStatusFixUtil userStatusFixUtil;
    
    /**
     * 检查用户状态分布
     */
    @GetMapping("/distribution")
    @PreAuthorize("hasAuthority('system:system:manage')")
    public ResponseEntity<Map<String, Object>> getStatusDistribution() {
        try {
            log.info("========== /debug/user-status/distribution ==========");
            
            Map<String, Object> result = new HashMap<>();
            
            // 统计各状态用户数量
            long activeCount = userRepository.countByStatus(SysUser.Status.ACTIVE);
            long inactiveCount = userRepository.countByStatus(SysUser.Status.INACTIVE);
            long totalCount = userRepository.count();
            
            Map<String, Long> statusCounts = new HashMap<>();
            statusCounts.put("ACTIVE", activeCount);
            statusCounts.put("INACTIVE", inactiveCount);
            statusCounts.put("TOTAL", totalCount);
            
            result.put("statusCounts", statusCounts);
            
            // 检查是否有异常状态的用户
            List<SysUser> allUsers = userRepository.findAll();
            long nullStatusCount = allUsers.stream()
                    .filter(user -> user.getStatus() == null)
                    .count();
            
            result.put("nullStatusCount", nullStatusCount);
            result.put("summary", String.format("活跃用户: %d, 禁用用户: %d, 总用户: %d, 状态为null: %d", 
                    activeCount, inactiveCount, totalCount, nullStatusCount));
            
            log.info("用户状态分布：{}", result);
            return ResponseEntity.success(result);
            
        } catch (Exception e) {
            log.error("获取用户状态分布失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 修复用户状态
     */
    @PostMapping("/fix")
    @PreAuthorize("hasAuthority('system:system:manage')")
    public ResponseEntity<String> fixUserStatus() {
        try {
            log.info("========== /debug/user-status/fix ==========");
            
            userStatusFixUtil.checkAndFixUserStatus();
            
            return ResponseEntity.success("用户状态修复完成");
            
        } catch (Exception e) {
            log.error("修复用户状态失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 检查特定用户状态
     */
    @GetMapping("/check/{username}")
    @PreAuthorize("hasAuthority('system:system:manage')")
    public ResponseEntity<Map<String, Object>> checkUserStatus(@PathVariable String username) {
        try {
            log.info("========== /debug/user-status/check/{} ==========", username);
            
            java.util.Optional<SysUser> userOpt = userRepository.findByUsernameAndDeletedFalse(username);
            
            Map<String, Object> result = new HashMap<>();
            
            if (userOpt.isPresent()) {
                SysUser user = userOpt.get();
                result.put("found", true);
                result.put("id", user.getId());
                result.put("username", user.getUsername());
                result.put("realName", user.getRealName());
                result.put("status", user.getStatus() != null ? user.getStatus().name() : null);
                result.put("customerId", user.getCustomerId());
                result.put("deleted", user.getDeleted());
                result.put("createTime", user.getCreateTime());
                result.put("updateTime", user.getUpdateTime());
            } else {
                result.put("found", false);
                result.put("message", "用户不存在：" + username);
            }
            
            log.info("用户状态检查结果：{}", result);
            return ResponseEntity.success(result);
            
        } catch (Exception e) {
            log.error("检查用户状态失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 测试状态枚举转换
     */
    @PostMapping("/test-enum")
    @PreAuthorize("hasAuthority('system:system:manage')")
    public ResponseEntity<Map<String, Object>> testStatusEnum(@RequestBody Map<String, String> request) {
        try {
            log.info("========== /debug/user-status/test-enum ==========");
            
            String statusValue = request.get("status");
            Map<String, Object> result = new HashMap<>();
            
            try {
                SysUser.Status status = SysUser.Status.valueOf(statusValue);
                result.put("valid", true);
                result.put("status", status.name());
                result.put("ordinal", status.ordinal());
            } catch (IllegalArgumentException e) {
                result.put("valid", false);
                result.put("error", "无效的状态值: " + statusValue);
                result.put("validValues", new String[]{"ACTIVE", "INACTIVE"});
            }
            
            log.info("状态枚举测试结果：{}", result);
            return ResponseEntity.success(result);
            
        } catch (Exception e) {
            log.error("测试状态枚举失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}