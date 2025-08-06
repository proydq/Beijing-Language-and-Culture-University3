package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 远程开门记录响应DTO
 * 
 * @author system
 * @date 2024-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteAccessRecordResponse {

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
     * 操作用户ID
     */
    private String userId;

    /**
     * 操作用户姓名
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
     * 关联预约ID
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
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 实际操作员ID
     */
    private String operatorId;

    /**
     * 实际操作员姓名
     */
    private String operatorName;

    /**
     * 操作员类型
     */
    private String operatorType;

    /**
     * 开门原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}