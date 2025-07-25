package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private String userId;
    private String username;
    private String realName;
    private String customerId;
    
    public LoginResponse(String token, String userId, String username, String realName, String customerId) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.customerId = customerId;
    }
}