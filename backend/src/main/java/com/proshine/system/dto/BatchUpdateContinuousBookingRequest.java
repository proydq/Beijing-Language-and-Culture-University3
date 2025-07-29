package com.proshine.system.dto;

import com.proshine.system.entity.ContinuousBookingSetting;
import lombok.Data;

import java.util.List;

/**
 * 批量更新连续预约设置请求DTO
 */
@Data
public class BatchUpdateContinuousBookingRequest {

    /**
     * 房间ID列表
     */
    private List<String> roomIds;

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