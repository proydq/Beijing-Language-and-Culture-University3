package com.proshine.system.dto;

import lombok.Data;

/**
 * 房间信息VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class RoomVo {

    /**
     * 房间ID
     */
    private String id;

    /**
     * 房屋名称
     */
    private String roomName;

    /**
     * 房间区域名称
     */
    private String roomAreaName;

    /**
     * 房间号码
     */
    private String roomNo;

    /**
     * 房屋类型名称
     */
    private String roomTypeName;

    /**
     * 容纳人数
     */
    private Integer roomVolume;

    /**
     * 备注
     */
    private String remark;

    /**
     * 更新时间（格式化后的字符串）
     */
    private String lastUpdateTime;

    /**
     * 房间编码（扩展字段1）
     */
    private String roomCode;

    /**
     * 扩展字段2
     */
    private String extend2;

    /**
     * 房间面积（前端显示用）
     */
    private String roomArea;

    /**
     * 容纳人数（前端显示用，与roomVolume相同）
     */
    private String capacity;

    /**
     * 更新时间（前端显示用）
     */
    private String updateTime;
}