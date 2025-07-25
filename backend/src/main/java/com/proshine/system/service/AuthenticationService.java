package com.proshine.system.service;

import com.proshine.system.dto.LoginRequest;
import com.proshine.system.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthenticationService {
    
    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 用户登出
     *
     * @param token JWT令牌
     */
    void logout(String token);
    
    /**
     * 刷新JWT令牌
     *
     * @param token 原JWT令牌
     * @return 新JWT令牌
     */
    String refreshToken(String token);
    
    /**
     * 忘记密码
     *
     * @param username 用户名
     * @param phone 手机号
     * @param customerId 客户域ID
     * @return 是否成功
     */
    boolean forgotPassword(String username, String phone, String customerId);
}