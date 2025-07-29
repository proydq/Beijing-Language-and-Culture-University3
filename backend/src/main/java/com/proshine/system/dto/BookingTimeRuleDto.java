package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约时间规则DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingTimeRuleDto {

    private String id;
    
    private String ruleName;
    
    private Integer advanceBookingDays;
    
    private List<String> applicableUserIds;
    
    private List<UserInfo> applicableUsers;
    
    private Integer bindingUserCount; // 绑定人数
    
    private String creatorId;
    
    private String creatorName;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String userName;
        private String jobNumber; // 工号
    }
}