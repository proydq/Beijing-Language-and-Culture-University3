package com.proshine.system.dto;

import lombok.Data;

/**
 * 用户违规记录DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class UserViolationRecordDto {

    /**
     * 记录ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 预约ID
     */
    private String bookingId;

    /**
     * 超时分钟数
     */
    private Integer overtimeMinutes;

    /**
     * 违规时间
     */
    private String violationTime;

    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 房间编号
     */
    private String roomNo;
}