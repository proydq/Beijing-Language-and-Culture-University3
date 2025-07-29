package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 预约时间规则搜索条件
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchBookingTimeRuleCondition extends SearchBaseCondition {

    private String ruleName;
    
    private Integer advanceBookingDays;
    
    private String creatorName;
}