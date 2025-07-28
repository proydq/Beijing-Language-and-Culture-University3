package com.proshine.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 房间保存VO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class RoomSaveVo {

    /**
     * 房间ID（编辑时必填）
     */
    private String id;

    /**
     * 房屋名称（必填）
     */
    @NotBlank(message = "房屋名称不能为空")
    private String roomName;

    /**
     * 房间区域ID
     */
    private String roomAreaId;

    /**
     * 房间区域名称
     */
    private String roomAreaName;

    /**
     * 房间号码（必填）
     */
    @NotBlank(message = "房间号码不能为空")
    private String roomNo;

    /**
     * 房屋类型ID
     */
    private String roomTypeId;

    /**
     * 房屋类型名称（必填）
     */
    @NotBlank(message = "房屋类型不能为空")
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
     * 房间编码（扩展字段1）
     */
    private String roomCode;

    /**
     * 扩展字段2
     */
    private String extend2;

    /**
     * 房间面积（前端传入）
     */
    private String roomArea;

    /**
     * 容纳人数（前端传入，字符串格式）
     */
    private String capacity;
}