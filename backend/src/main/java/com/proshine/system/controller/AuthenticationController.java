package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.LoginRequest;
import com.proshine.system.dto.LoginResponse;
import com.proshine.system.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/authentication")
@Slf4j
public class AuthenticationController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("==========/authentication/login=============");
            log.info("用户登录请求：{}", loginRequest.getUsername());
            
            LoginResponse loginResponse = authenticationService.login(loginRequest);
            
            log.info("用户 {} 登录成功", loginRequest.getUsername());
            return ResponseEntity.success(loginResponse);
            
        } catch (Exception e) {
            log.error("用户登录失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 用户登出
     *
     * @param request HTTP请求
     * @return 响应结果
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        try {
            log.info("==========/authentication/logout=============");
            
            String token = getTokenFromRequest(request);
            if (StringUtils.hasText(token)) {
                authenticationService.logout(token);
            }
            
            log.info("用户登出成功");
            return ResponseEntity.success(null);
            
        } catch (Exception e) {
            log.error("用户登出失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 刷新JWT令牌
     *
     * @param request HTTP请求
     * @return 新的JWT令牌
     */
    @GetMapping("/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {
        try {
            log.info("==========/authentication/refresh=============");
            
            String token = getTokenFromRequest(request);
            if (!StringUtils.hasText(token)) {
                return ResponseEntity.fail("令牌不能为空");
            }
            
            String newToken = authenticationService.refreshToken(token);
            
            log.info("JWT令牌刷新成功");
            return ResponseEntity.success(newToken);
            
        } catch (Exception e) {
            log.error("刷新JWT令牌失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 忘记密码
     *
     * @param username 用户名
     * @param phone 手机号
     * @param customerId 客户域ID
     * @return 处理结果
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestParam String username,
            @RequestParam String phone,
            @RequestParam(required = false) String customerId) {
        try {
            log.info("==========/authentication/forgot-password=============");
            log.info("忘记密码请求：用户名={}, 手机号={}", username, phone);
            
            boolean success = authenticationService.forgotPassword(username, phone, customerId);
            
            if (success) {
                log.info("忘记密码处理成功，用户名：{}", username);
                return ResponseEntity.success(null);
            } else {
                return ResponseEntity.fail("用户名或手机号不正确");
            }
            
        } catch (Exception e) {
            log.error("忘记密码处理失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
    
    /**
     * 从请求中获取JWT令牌
     *
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}