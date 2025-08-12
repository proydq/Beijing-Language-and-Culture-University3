package com.proshine.system.controller;

import com.proshine.common.constant.SystemConstants;
import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.ForgotPasswordRequest;
import com.proshine.system.dto.LoginRequest;
import com.proshine.system.dto.LoginResponse;
import com.proshine.system.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
        log.info("用户登录请求 - username: {}", loginRequest.getUsername());
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        log.info("用户 {} 登录成功", loginRequest.getUsername());
        return ResponseEntity.success(loginResponse);
    }
    
    /**
     * 用户登出
     *
     * @param request HTTP请求
     * @return 响应结果
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (StringUtils.hasText(token)) {
            authenticationService.logout(token);
        }
        log.info("用户登出成功");
        return ResponseEntity.success();
    }
    
    /**
     * 刷新JWT令牌
     *
     * @param request HTTP请求
     * @return 新的JWT令牌
     */
    @PostMapping("/token/refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            return ResponseEntity.fail("令牌不能为空");
        }
        String newToken = authenticationService.refreshToken(token);
        log.info("JWT令牌刷新成功");
        return ResponseEntity.success(newToken);
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
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("忘记密码请求 - username: {}", request.getUsername());
        
        boolean success = authenticationService.forgotPassword(
            request.getUsername(), 
            request.getPhone(), 
            request.getCustomerId()
        );
        
        if (success) {
            log.info("验证码已发送至手机号：{}", maskPhone(request.getPhone()));
            return ResponseEntity.success();
        } else {
            return ResponseEntity.fail("用户名或手机号不正确");
        }
    }
    
    /**
     * 从请求中获取JWT令牌
     *
     * @param request HTTP请求
     * @return JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(SystemConstants.TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(SystemConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(SystemConstants.TOKEN_PREFIX.length());
        }
        return null;
    }
    
    /**
     * 手机号脱敏显示
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}