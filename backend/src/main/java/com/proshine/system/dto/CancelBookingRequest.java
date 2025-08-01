package com.proshine.system.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 取消预约请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class CancelBookingRequest {
    
    /**
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    private String reason;
}