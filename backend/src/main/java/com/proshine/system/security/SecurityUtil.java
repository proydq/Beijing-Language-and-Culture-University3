package com.proshine.system.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 */
@Component
@Slf4j
public class SecurityUtil {
    
    /**
     * 获取当前用户名
     *
     * @return 用户名
     */
    public static String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                return userPrincipal.getUsername();
            }
            return null;
        } catch (Exception e) {
            log.error("获取当前用户名失败：", e);
            return null;
        }
    }
    
    /**
     * 获取当前用户ID
     *
     * @return 用户ID
     */
    public static String getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                return userPrincipal.getUserId();
            }
            return null;
        } catch (Exception e) {
            log.error("获取当前用户ID失败：", e);
            return null;
        }
    }
    
    /**
     * 获取当前客户域ID
     *
     * @return 客户域ID
     */
    public static String getCustomerId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                return userPrincipal.getCustomerId();
            }
            return null;
        } catch (Exception e) {
            log.error("获取当前客户域ID失败：", e);
            return null;
        }
    }
    
    /**
     * 获取当前用户主体
     *
     * @return 用户主体
     */
    public static UserPrincipal getCurrentUserPrincipal() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                return (UserPrincipal) authentication.getPrincipal();
            }
            return null;
        } catch (Exception e) {
            log.error("获取当前用户主体失败：", e);
            return null;
        }
    }
    
    /**
     * 检查当前用户是否已认证
     *
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            return authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof UserPrincipal;
        } catch (Exception e) {
            log.error("检查用户认证状态失败：", e);
            return false;
        }
    }

    /**
     * 判断当前用户是否为系统管理员
     *
     * @return 是否为系统管理员
     */
    public static boolean isSystemAdministrator() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                for (GrantedAuthority ga : authentication.getAuthorities()) {
                    if ("ROLE_ADMIN".equals(ga.getAuthority())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.error("判断超级管理员失败：", e);
        }
        return false;
    }
}