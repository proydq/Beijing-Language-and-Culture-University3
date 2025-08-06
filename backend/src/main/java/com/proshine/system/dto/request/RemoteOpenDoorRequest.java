package com.proshine.system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 远程开门操作请求DTO
 * 
 * @author system
 * @date 2024-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteOpenDoorRequest {

    /**
     * 教室ID，必填
     */
    @NotBlank(message = "教室ID不能为空")
    private String roomId;

    /**
     * 设备ID，必填
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 开门原因，必填
     */
    @NotBlank(message = "开门原因不能为空")
    private String reason;

    /**
     * 开门方式（管理员授权/应急开门），必填
     */
    @NotBlank(message = "开门方式不能为空")
    private String openMethod;

    /**
     * 关联预约ID，可选
     */
    private String bookingId;

    /**
     * 目标用户ID（为谁开门），可选
     */
    private String targetUserId;
}