package com.proshine.system.dto;

import com.proshine.system.entity.ContinuousBookingSetting;
import lombok.Data;

import java.util.List;

/**
 * 连续预约设置DTO
 */
@Data
public class ContinuousBookingSettingDto {

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 可连续预约天数
     */
    private Integer continuousDays;

    /**
     * 连续类型
     */
    private ContinuousBookingSetting.ContinuousType continuousType;

    /**
     * 是否无限制
     */
    private Boolean isUnlimited;

    /**
     * 是否允许连续预约
     */
    private Boolean canContinuous;
}