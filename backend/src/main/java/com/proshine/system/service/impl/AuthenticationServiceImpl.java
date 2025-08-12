package com.proshine.system.service.impl;

import com.proshine.common.constant.SystemConstants;
import com.proshine.common.enums.ErrorCode;
import com.proshine.common.exception.BusinessException;
import com.proshine.system.dto.LoginRequest;
import com.proshine.system.dto.LoginResponse;
import com.proshine.system.dto.PermissionDTO;
import com.proshine.system.dto.PermissionTreeNode;
import com.proshine.system.entity.SysPermission;
import com.proshine.system.entity.SysRole;
import com.proshine.system.entity.SysUser;
import com.proshine.system.repository.SysPermissionRepository;
import com.proshine.system.repository.SysRoleRepository;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.security.JwtUtil;
import com.proshine.system.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 认证服务实现类
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AuthenticationServiceImpl implements AuthenticationService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private SysPermissionRepository permissionRepository;
    
    @Autowired
    private SysRoleRepository roleRepository;
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            log.info("==========/authentication/login=============");
            log.info("用户登录请求：{}", loginRequest.getUsername());
            
            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            
            // 查询用户信息
            Optional<SysUser> userOptional;
            if (StringUtils.hasText(loginRequest.getCustomerId())) {
                userOptional = userRepository.findByUsernameAndCustomerIdAndDeletedFalse(
                        loginRequest.getUsername(), loginRequest.getCustomerId());
            } else {
                userOptional = userRepository.findByUsernameAndDeletedFalse(loginRequest.getUsername());
            }
            
            if (!userOptional.isPresent()) {
                throw new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND);
            }
            
            SysUser user = userOptional.get();
            
            // 检查用户状态
            if (user.getStatus() == SysUser.Status.DISABLED) {
                throw new BusinessException(ErrorCode.AUTH_USER_DISABLED);
            }
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getCustomerId());
            
            // 获取用户权限信息
            List<SysPermission> permissions = permissionRepository.findByUserId(user.getId());
            
            // 调试：打印权限数据
            log.info("=== 用户权限调试 ===");
            log.info("用户ID: {}, 权限数量: {}", user.getId(), permissions.size());
            
            for (SysPermission perm : permissions) {
                if (perm.getType() == SysPermission.Type.MENU) {
                    log.info("菜单权限: {} | path: {} | component: {} | icon: {}", 
                        perm.getCode(), perm.getPath(), perm.getComponent(), perm.getIcon());
                }
            }
            List<String> permissionCodes = permissions.stream()
                    .map(SysPermission::getCode)
                    .collect(Collectors.toList());
            
            // 构建权限详情列表
            List<PermissionDTO> permissionDetails = permissions.stream()
                    .map(permission -> {
                        PermissionDTO dto = new PermissionDTO();
                        dto.setCode(permission.getCode());
                        dto.setName(permission.getName());
                        dto.setType(permission.getType() != null ? permission.getType().name() : null);
                        dto.setParentId(permission.getParentId());
                        dto.setUrl(permission.getUrl());
                        dto.setSort(permission.getSort());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            // 获取用户角色信息
            List<SysRole> roles = roleRepository.findByUserId(user.getId());
            List<String> roleCodes = roles.stream()
                    .map(SysRole::getCode)
                    .collect(Collectors.toList());
            
            log.info("用户 {} 登录成功，生成JWT令牌，拥有 {} 个权限，{} 个角色", 
                    user.getUsername(), permissionCodes.size(), roleCodes.size());
            
            // 构建权限树
            log.info("开始构建权限树...");
            List<PermissionTreeNode> permissionTree = buildPermissionTree(permissions);
            log.info("权限树构建完成，节点数: {}", permissionTree.size());
            
            // 构建完整的登录响应
            LoginResponse response = new LoginResponse(token, user.getId(), user.getUsername(), 
                    user.getRealName(), user.getCustomerId());
            response.setPermissions(permissionCodes);
            response.setPermissionDetails(permissionDetails);
            response.setRoles(roleCodes);
            response.setPermissionTree(permissionTree);
            
            return response;
            
        } catch (AuthenticationException e) {
            log.error("用户登录失败：{}", e.getMessage());
            throw new BusinessException(ErrorCode.AUTH_USERNAME_PASSWORD_ERROR);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("用户登录异常：", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
    
    @Override
    public void logout(String token) {
        try {
            log.info("用户登出，令牌：{}", token.substring(0, Math.min(token.length(), 20)) + "...");
            // 这里可以实现令牌黑名单机制
            // 目前简单记录日志
        } catch (Exception e) {
            log.error("用户登出异常：", e);
        }
    }
    
    @Override
    public String refreshToken(String token) {
        try {
            log.info("刷新JWT令牌");
            String newToken = jwtUtil.refreshToken(token);
            if (newToken != null) {
                log.info("JWT令牌刷新成功");
                return newToken;
            } else {
                throw new BusinessException(ErrorCode.AUTH_TOKEN_INVALID);
            }
        } catch (Exception e) {
            log.error("刷新JWT令牌异常：", e);
            throw new BusinessException(ErrorCode.AUTH_TOKEN_INVALID);
        }
    }
    
    /**
     * 构建权限树
     * @param permissions 权限列表
     * @return 权限树
     */
    private List<PermissionTreeNode> buildPermissionTree(List<SysPermission> permissions) {
        // 创建权限映射
        Map<String, PermissionTreeNode> nodeMap = new HashMap<>();
        
        // 先将所有权限转换为树节点
        for (SysPermission permission : permissions) {
            // 调试信息：打印关键权限的字段值
            if (permission.getType() == SysPermission.Type.MENU) {
                log.info("权限调试: {} - path: {}, component: {}, icon: {}", 
                    permission.getCode(), permission.getPath(), permission.getComponent(), permission.getIcon());
            }
            
            PermissionTreeNode node = new PermissionTreeNode();
            node.setId(permission.getId());
            node.setCode(permission.getCode());
            node.setName(permission.getName());
            node.setType(permission.getType() != null ? permission.getType().name() : null);
            node.setParentId(permission.getParentId());
            node.setPath(permission.getPath());
            node.setComponent(permission.getComponent());
            node.setIcon(permission.getIcon());
            node.setVisible(permission.getVisible());
            node.setKeepAlive(permission.getKeepAlive());
            node.setRedirect(permission.getRedirect());
            node.setSort(permission.getSort());
            node.setChildren(new ArrayList<>());
            
            nodeMap.put(permission.getId(), node);
        }
        
        // 构建树结构
        List<PermissionTreeNode> tree = new ArrayList<>();
        for (PermissionTreeNode node : nodeMap.values()) {
            if (node.getParentId() == null || node.getParentId().isEmpty()) {
                // 根节点
                tree.add(node);
            } else {
                // 子节点
                PermissionTreeNode parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }
        
        // 按照sort排序
        sortTree(tree);
        
        return tree;
    }
    
    /**
     * 递归排序权限树
     */
    private void sortTree(List<PermissionTreeNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        
        nodes.sort((a, b) -> {
            if (a.getSort() == null && b.getSort() == null) return 0;
            if (a.getSort() == null) return 1;
            if (b.getSort() == null) return -1;
            return a.getSort().compareTo(b.getSort());
        });
        
        for (PermissionTreeNode node : nodes) {
            sortTree(node.getChildren());
        }
    }
    
    @Override
    public boolean forgotPassword(String username, String phone, String customerId) {
        try {
            log.info("忘记密码请求：用户名={}, 手机号={}", username, phone);
            
            // 查询用户
            Optional<SysUser> userOptional;
            if (StringUtils.hasText(customerId)) {
                userOptional = userRepository.findByUsernameAndCustomerIdAndDeletedFalse(username, customerId);
            } else {
                userOptional = userRepository.findByUsernameAndDeletedFalse(username);
            }
            
            if (!userOptional.isPresent()) {
                log.warn("忘记密码：用户不存在，用户名={}", username);
                return false;
            }
            
            SysUser user = userOptional.get();
            
            // 验证手机号
            if (!phone.equals(user.getPhone())) {
                log.warn("忘记密码：手机号不匹配，用户名={}", username);
                return false;
            }
            
            // 生成6位数字验证码
            String verifyCode = generateVerifyCode();
            
            // TODO: 实际项目中应该调用短信服务发送验证码
            // smsService.sendVerifyCode(phone, verifyCode);
            
            // 临时存储验证码（实际项目中应该使用Redis等缓存）
            // 这里为了保持功能可用，暂时将验证码记录到日志中
            log.info("【开发环境】用户 {} 的密码重置验证码：{}", username, verifyCode);
            
            // 在实际项目中，应该将验证码存储到缓存中
            // cacheService.set(SystemConstants.CacheKey.PASSWORD_RESET_PREFIX + username, 
            //     verifyCode, SystemConstants.VERIFY_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            
            return true;
            
        } catch (Exception e) {
            log.error("忘记密码处理异常：", e);
            return false;
        }
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateVerifyCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < SystemConstants.VERIFY_CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}