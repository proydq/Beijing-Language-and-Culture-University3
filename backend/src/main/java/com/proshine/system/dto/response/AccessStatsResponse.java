package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 教室借用统计响应DTO
 * 
 * @author system
 * @date 2024-08-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessStatsResponse {

    /**
     * 总记录数
     */
    private Long totalRecords;

    /**
     * 今日记录数
     */
    private Long todayRecords;

    /**
     * 使用教室数
     */
    private Long totalRooms;

    /**
     * 使用人数
     */
    private Long totalUsers;

    /**
     * 通行类型统计
     */
    private Map<String, Long> accessTypeStats;

    /**
     * 开门方式统计
     */
    private Map<String, Long> openMethodStats;

    /**
     * 用户类型统计
     */
    private Map<String, Long> userTypeStats;
}