package com.proshine.system.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 更新管理员状态请求
 */
@Data
public class UpdateAdminStatusRequest {
    
    /**
     * 状态: ACTIVE, INACTIVE
     */
    @NotBlank(message = "状态不能为空")
    private String status;
}