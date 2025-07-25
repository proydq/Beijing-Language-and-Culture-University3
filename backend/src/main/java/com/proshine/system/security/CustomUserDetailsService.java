package com.proshine.system.security;

import com.proshine.system.entity.SysUser;
import com.proshine.system.repository.SysPermissionRepository;
import com.proshine.system.repository.SysUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情服务
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Autowired
    private SysPermissionRepository permissionRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("加载用户信息：{}", username);
        
        SysUser user = userRepository.findByUsernameAndDeletedFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        
        // 获取用户权限
        List<String> authorities = permissionRepository.findByUserId(user.getId())
                .stream()
                .map(permission -> permission.getCode())
                .collect(Collectors.toList());
        
        log.info("用户 {} 拥有权限：{}", username, authorities);
        
        return new UserPrincipal(user, authorities);
    }
    
    /**
     * 根据用户名和客户域加载用户
     *
     * @param username 用户名
     * @param customerId 客户域ID
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在异常
     */
    public UserDetails loadUserByUsernameAndCustomerId(String username, String customerId) throws UsernameNotFoundException {
        log.info("加载用户信息：{}，客户域：{}", username, customerId);
        
        SysUser user = userRepository.findByUsernameAndCustomerIdAndDeletedFalse(username, customerId)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        
        // 获取用户权限
        List<String> authorities = permissionRepository.findByUserId(user.getId())
                .stream()
                .map(permission -> permission.getCode())
                .collect(Collectors.toList());
        
        log.info("用户 {} 拥有权限：{}", username, authorities);
        
        return new UserPrincipal(user, authorities);
    }
}