package com.proshine.system.security;

import com.proshine.system.entity.SysPermissionAlias;
import com.proshine.system.repository.SysPermissionAliasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

/**
 * 自定义权限评估器，支持新旧权限码
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomPermissionEvaluator.class);
    
    @Autowired
    private SysPermissionAliasRepository permissionAliasRepository;
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null) {
            return false;
        }
        
        String permissionCode = permission.toString();
        
        // 1. 直接检查用户是否有该权限
        boolean hasDirectPermission = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(permissionCode));
        
        if (hasDirectPermission) {
            logger.debug("用户直接拥有权限: {}", permissionCode);
            return true;
        }
        
        // 2. 检查是否是旧权限码，如果是，查找对应的新权限码
        Optional<SysPermissionAlias> aliasOpt = permissionAliasRepository.findByOldCode(permissionCode);
        if (aliasOpt.isPresent()) {
            String newCode = aliasOpt.get().getNewCode();
            boolean hasNewPermission = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(auth -> auth.equals(newCode));
            
            if (hasNewPermission) {
                logger.debug("通过别名映射找到权限: {} -> {}", permissionCode, newCode);
                return true;
            }
        }
        
        // 3. 检查是否有管理权限（如果请求的是具体操作权限）
        if (permissionCode.contains(":") && !permissionCode.endsWith(":manage")) {
            String[] parts = permissionCode.split(":");
            if (parts.length == 3) {
                String managePermission = parts[0] + ":" + parts[1] + ":manage";
                boolean hasManagePermission = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(auth -> auth.equals(managePermission));
                
                if (hasManagePermission) {
                    logger.debug("通过管理权限获得操作权限: {} -> {}", managePermission, permissionCode);
                    return true;
                }
            }
        }
        
        logger.debug("用户没有权限: {}", permissionCode);
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return hasPermission(authentication, targetType, permission);
    }
}