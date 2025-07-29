package com.proshine.system.dto;

import com.proshine.system.entity.ContinuousBookingSetting;
import lombok.Data;

/**
 * 连续预约设置与房间信息组合DTO
 */
@Data
public class ContinuousBookingSettingWithRoomDto {
    
    // 房间信息
    private String roomId;
    private String roomName;
    private String roomNo;
    private String roomTypeName;
    private String roomAreaId;
    private String roomAreaName;
    private String areaName;  // 区域名称
    private String buildingName;  // 建筑名称
    private String floorName;  // 楼层名称
    private Integer roomVolume;
    private String roomCode;
    private String remark;
    
    // 连续预约设置信息
    private Integer continuousDays;
    private ContinuousBookingSetting.ContinuousType continuousType;
    private Boolean isUnlimited;
    private Boolean canContinuous;
    private String continuousDaysDisplay;  // 格式化后的连续天数显示
    
    // 时间信息
    private String lastUpdateTime;
    
    /**
     * 默认构造函数
     */
    public ContinuousBookingSettingWithRoomDto() {
        // 设置默认的连续预约设置
        this.continuousDays = 7;
        this.continuousType = ContinuousBookingSetting.ContinuousType.DAYS;
        this.isUnlimited = false;
        this.canContinuous = true;
    }
    
    /**
     * 格式化连续预约天数显示
     */
    public void formatContinuousDaysDisplay() {
        if (this.isUnlimited != null && this.isUnlimited) {
            this.continuousDaysDisplay = "无限制";
        } else if (this.continuousDays != null && this.continuousType != null) {
            String unit = "";
            switch (this.continuousType) {
                case DAYS:
                    unit = "天";
                    break;
                case WEEKS:
                    unit = "周";
                    break;
                case MONTHS:
                    unit = "月";
                    break;
            }
            this.continuousDaysDisplay = this.continuousDays + unit;
        } else {
            this.continuousDaysDisplay = "未设置";
        }
    }
}