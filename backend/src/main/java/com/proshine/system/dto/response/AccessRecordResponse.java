package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教室借用记录响应DTO
 * 
 * @author system
 * @date 2024-08-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessRecordResponse {

    /**
     * 记录ID
     */
    private String id;

    /**
     * 教室ID
     */
    private String roomId;

    /**
     * 教室名称
     */
    private String roomName;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 工号
     */
    private String employeeId;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 部门
     */
    private String department;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 开门方式
     */
    private String openMethod;

    /**
     * 通行时间
     */
    private LocalDateTime accessTime;

    /**
     * 通行类型
     */
    private String accessType;

    /**
     * 预约ID
     */
    private String bookingId;

    /**
     * 预约名称
     */
    private String bookingName;

    /**
     * 预约时段
     */
    private String bookingPeriod;

    /**
     * 门禁设备ID
     */
    private String deviceId;

    /**
     * 门禁设备名称
     */
    private String deviceName;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;
}