package com.proshine.system.config;

import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.repository.SysRoleRepository;
import com.proshine.system.repository.SysUserRoleRepository;
import com.proshine.system.util.UserStatusFixUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 管理员管理模块健康检查
 * 在应用启动时验证必要的数据库表和基础数据是否存在
 */
@Component
@Slf4j
public class AdminManagementHealthCheck implements ApplicationRunner {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;
    
    @Autowired
    private UserStatusFixUtil userStatusFixUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========== 管理员管理模块健康检查开始 ==========");
        
        try {
            // 检查数据库连接和表结构
            checkDatabaseTables();
            
            // 检查基础数据
            checkBasicData();
            
            // 检查和修复用户状态
            checkAndFixUserStatus();
            
            log.info("========== 管理员管理模块健康检查完成 ==========");
            
        } catch (Exception e) {
            log.error("========== 管理员管理模块健康检查失败 ==========", e);
            throw e;
        }
    }

    /**
     * 检查数据库表结构
     */
    private void checkDatabaseTables() {
        try {
            // 尝试查询各个表，验证表结构是否正确
            long userCount = sysUserRepository.count();
            long roleCount = sysRoleRepository.count();
            long userRoleCount = sysUserRoleRepository.count();
            
            log.info("数据库表检查通过:");
            log.info("- sys_user 表存在，当前记录数: {}", userCount);
            log.info("- sys_role 表存在，当前记录数: {}", roleCount);
            log.info("- sys_user_role 表存在，当前记录数: {}", userRoleCount);
            
        } catch (Exception e) {
            log.error("数据库表结构检查失败，请确保已正确执行数据库初始化脚本", e);
            throw new RuntimeException("数据库表结构检查失败", e);
        }
    }

    /**
     * 检查基础数据
     */
    private void checkBasicData() {
        try {
            // 检查是否有管理员角色数据
            long activeRoleCount = sysRoleRepository.countByStatus("ACTIVE");
            if (activeRoleCount == 0) {
                log.warn("未找到任何活跃的角色数据，请检查初始化脚本是否正确执行");
            } else {
                log.info("角色数据检查通过，活跃角色数量: {}", activeRoleCount);
            }
            
            // 检查是否有用户数据
            long activeUserCount = sysUserRepository.countByStatus(com.proshine.system.entity.SysUser.Status.ACTIVE);
            if (activeUserCount == 0) {
                log.warn("未找到任何活跃的用户数据，建议创建初始管理员用户");
            } else {
                log.info("用户数据检查通过，活跃用户数量: {}", activeUserCount);
            }
            
        } catch (Exception e) {
            log.error("基础数据检查失败", e);
            // 基础数据检查失败不阻止应用启动，只记录警告
        }
    }
    
    /**
     * 检查和修复用户状态
     */
    private void checkAndFixUserStatus() {
        try {
            log.info("开始检查用户状态...");
            userStatusFixUtil.checkAndFixUserStatus();
            log.info("用户状态检查完成");
        } catch (Exception e) {
            log.error("用户状态检查失败", e);
            // 不阻止应用启动，只记录警告
        }
    }
}