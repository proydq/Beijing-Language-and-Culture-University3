package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 忘记密码请求DTO
 * 
 * @author system
 * @since 1.0.0
 */
@Data
public class ForgotPasswordRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式不正确，只能包含字母、数字和下划线，长度4-20位")
    private String username;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 客户域ID（可选）
     */
    private String customerId;
}