package com.proshine.system.dto;

import lombok.Data;

/**
 * 违规设置DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ViolationSettingDto {

    /**
     * 设置ID
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
     * 房间号
     */
    private String roomNo;

    /**
     * 所属楼
     */
    private String roomAreaName;

    /**
     * 超时分钟数
     */
    private Integer startTime;

    /**
     * 违规次数阈值
     */
    private Integer violationCount;

    /**
     * 最后更新时间
     */
    private String lastUpdateTime;
}