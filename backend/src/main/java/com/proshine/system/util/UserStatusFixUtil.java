package com.proshine.system.util;

import com.proshine.system.entity.SysUser;
import com.proshine.system.repository.SysUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 用户状态修复工具类
 * 用于修复状态枚举变更导致的登录问题
 */
@Component
@Slf4j
public class UserStatusFixUtil {

    @Autowired
    private SysUserRepository userRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 检查并修复用户状态
     */
    @Transactional
    public void checkAndFixUserStatus() {
        log.info("========== 开始检查和修复用户状态 ==========");
        
        try {
            // 先执行原始SQL修复（避免Hibernate枚举问题）
            executeRawStatusFix();
            
            // 然后查询所有未删除的用户进行二次检查
            List<SysUser> allUsers;
            try {
                allUsers = userRepository.findAll()
                        .stream()
                        .filter(user -> user.getDeleted() != null && !user.getDeleted())
                        .collect(java.util.stream.Collectors.toList());
            } catch (Exception e) {
                log.error("查询用户列表时发生枚举错误，先执行SQL修复: {}", e.getMessage());
                executeRawStatusFix();
                // 重新查询
                allUsers = userRepository.findAll()
                        .stream()
                        .filter(user -> user.getDeleted() != null && !user.getDeleted())
                        .collect(java.util.stream.Collectors.toList());
            }
            
            log.info("找到 {} 个未删除的用户", allUsers.size());
            
            int fixedCount = 0;
            
            for (SysUser user : allUsers) {
                SysUser.Status originalStatus = user.getStatus();
                boolean needsFix = false;
                
                // 检查状态是否需要修复
                if (originalStatus == null) {
                    log.warn("用户 {} 的状态为null，设置为ACTIVE", user.getUsername());
                    user.setStatus(SysUser.Status.ACTIVE);
                    needsFix = true;
                } else if (originalStatus != SysUser.Status.ACTIVE && originalStatus != SysUser.Status.INACTIVE) {
                    log.warn("用户 {} 的状态值无效: {}，设置为ACTIVE", user.getUsername(), originalStatus);
                    user.setStatus(SysUser.Status.ACTIVE);
                    needsFix = true;
                }
                
                if (needsFix) {
                    try {
                        userRepository.save(user);
                        fixedCount++;
                        log.info("修复用户 {} 的状态：{} -> {}", 
                                user.getUsername(), originalStatus, user.getStatus());
                    } catch (Exception e) {
                        log.error("修复用户 {} 状态失败", user.getUsername(), e);
                    }
                }
            }
            
            log.info("用户状态修复完成，共修复 {} 个用户", fixedCount);
            
            // 统计最终状态分布
            logStatusDistribution();
            
        } catch (Exception e) {
            log.error("检查和修复用户状态失败", e);
        }
        
        log.info("========== 用户状态检查和修复完成 ==========");
    }
    
    /**
     * 执行原始SQL修复状态值
     */
    private void executeRawStatusFix() {
        try {
            log.info("执行原始SQL修复用户状态...");
            
            // 修复 NORMAL -> ACTIVE
            int normalCount = entityManager.createNativeQuery(
                    "UPDATE sys_user SET status = 'ACTIVE', update_time = NOW() WHERE status = 'NORMAL' AND (deleted = 0 OR deleted IS NULL)")
                    .executeUpdate();
            log.info("修复 NORMAL -> ACTIVE，影响行数: {}", normalCount);
            
            // 修复 DISABLED -> INACTIVE
            int disabledCount = entityManager.createNativeQuery(
                    "UPDATE sys_user SET status = 'INACTIVE', update_time = NOW() WHERE status = 'DISABLED' AND (deleted = 0 OR deleted IS NULL)")
                    .executeUpdate();
            log.info("修复 DISABLED -> INACTIVE，影响行数: {}", disabledCount);
            
            // 修复 null 状态
            int nullCount = entityManager.createNativeQuery(
                    "UPDATE sys_user SET status = 'ACTIVE', update_time = NOW() WHERE (status IS NULL OR status = '' OR status = ' ') AND (deleted = 0 OR deleted IS NULL)")
                    .executeUpdate();
            log.info("修复 NULL 状态 -> ACTIVE，影响行数: {}", nullCount);
            
            // 刷新持久化上下文
            entityManager.flush();
            entityManager.clear();
            
            log.info("原始SQL状态修复完成");
            
        } catch (Exception e) {
            log.error("执行原始SQL修复失败: {}", e.getMessage());
        }
    }
    
    /**
     * 记录状态分布统计
     */
    private void logStatusDistribution() {
        try {
            long activeCount = userRepository.countByStatus(SysUser.Status.ACTIVE);
            long inactiveCount = userRepository.countByStatus(SysUser.Status.INACTIVE);
            
            log.info("用户状态分布统计：");
            log.info("- ACTIVE: {} 个用户", activeCount);
            log.info("- INACTIVE: {} 个用户", inactiveCount);
            log.info("- 总计: {} 个用户", activeCount + inactiveCount);
            
        } catch (Exception e) {
            log.error("统计用户状态分布失败", e);
        }
    }
    
    /**
     * 检查特定用户的状态
     */
    public void checkUserStatus(String username) {
        log.info("检查用户状态：{}", username);
        
        try {
            java.util.Optional<SysUser> userOpt = userRepository.findByUsernameAndDeletedFalse(username);
            
            if (userOpt.isPresent()) {
                SysUser user = userOpt.get();
                log.info("用户 {} 详细信息：", username);
                log.info("- ID: {}", user.getId());
                log.info("- 真实姓名: {}", user.getRealName());
                log.info("- 状态: {}", user.getStatus());
                log.info("- 客户域ID: {}", user.getCustomerId());
                log.info("- 是否删除: {}", user.getDeleted());
                log.info("- 创建时间: {}", user.getCreateTime());
                log.info("- 更新时间: {}", user.getUpdateTime());
            } else {
                log.warn("未找到用户：{}", username);
            }
            
        } catch (Exception e) {
            log.error("检查用户状态失败：{}", username, e);
        }
    }
}