package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 远程开门统计响应DTO
 * 
 * @author system
 * @date 2024-08-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoteAccessStatsResponse {

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
    private Long usedRooms;

    /**
     * 活跃操作员数
     */
    private Long activeOperators;

    /**
     * 开门方式统计
     */
    private List<StatItem> openMethodStats;

    /**
     * 操作员类型统计
     */
    private List<StatItem> operatorTypeStats;

    /**
     * 小时统计
     */
    private List<HourlyStatItem> hourlyStats;

    /**
     * 统计项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatItem {
        private String method;
        private Long count;
        private Double percentage;
    }

    /**
     * 小时统计项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyStatItem {
        private String hour;
        private Long count;
    }
}