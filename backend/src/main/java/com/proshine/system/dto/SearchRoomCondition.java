package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 房间查询条件DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRoomCondition extends SearchBaseCondition {

    /**
     * 房屋名称（模糊查询）
     */
    private String roomName;

    /**
     * 房间号码（模糊查询）
     */
    private String roomNo;

    /**
     * 房屋类型名称
     */
    private String roomTypeName;

    /**
     * 房间区域ID
     */
    private String roomAreaId;

    /**
     * 房间区域名称
     */
    private String roomAreaName;

    /**
     * 房间编码（扩展字段1）
     */
    private String roomCode;
}