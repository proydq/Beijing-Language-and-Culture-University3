package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 添加黑名单请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class AddBlacklistRequest {

    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 加入黑名单原因
     */
    @NotBlank(message = "原因不能为空")
    private String reason;
}