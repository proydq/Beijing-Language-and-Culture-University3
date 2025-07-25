package com.proshine.system.service.impl;

import com.proshine.system.dto.LoginRequest;
import com.proshine.system.dto.LoginResponse;
import com.proshine.system.entity.SysUser;
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
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 认证服务实现类
 */
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
                throw new RuntimeException("用户不存在");
            }
            
            SysUser user = userOptional.get();
            
            // 检查用户状态
            if (user.getStatus() == SysUser.Status.DISABLED) {
                throw new RuntimeException("用户已被禁用");
            }
            
            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getCustomerId());
            
            log.info("用户 {} 登录成功，生成JWT令牌", user.getUsername());
            
            return new LoginResponse(token, user.getId(), user.getUsername(), user.getRealName(), user.getCustomerId());
            
        } catch (AuthenticationException e) {
            log.error("用户登录失败：{}", e.getMessage());
            throw new RuntimeException("用户名或密码错误");
        } catch (Exception e) {
            log.error("用户登录异常：", e);
            throw new RuntimeException("登录失败：" + e.getMessage());
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
                throw new RuntimeException("令牌刷新失败");
            }
        } catch (Exception e) {
            log.error("刷新JWT令牌异常：", e);
            throw new RuntimeException("令牌刷新失败：" + e.getMessage());
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
            
            // 生成新密码（这里简单设置为123456，实际应该发送短信验证码）
            String newPassword = "123456";
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            log.info("用户 {} 密码重置成功", username);
            return true;
            
        } catch (Exception e) {
            log.error("忘记密码处理异常：", e);
            return false;
        }
    }
}