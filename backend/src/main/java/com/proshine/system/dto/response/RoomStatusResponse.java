package com.proshine.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教室实时使用状态响应DTO
 * 
 * @author system
 * @date 2024-08-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStatusResponse {

    /**
     * 教室ID
     */
    private String roomId;

    /**
     * 教室名称
     */
    private String roomName;

    /**
     * 楼栋名称
     */
    private String buildingName;

    /**
     * 楼层
     */
    private String floor;

    /**
     * 使用状态：使用中/空闲
     */
    private String status;

    /**
     * 当前使用人数
     */
    private Integer currentUsers;

    /**
     * 最后通行时间
     */
    private LocalDateTime lastAccessTime;

    /**
     * 当前预约信息
     */
    private CurrentBookingInfo currentBooking;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentBookingInfo {
        /**
         * 预约ID
         */
        private String bookingId;

        /**
         * 预约名称
         */
        private String bookingName;

        /**
         * 申请人姓名
         */
        private String applicantName;

        /**
         * 预约时段
         */
        private String bookingPeriod;
    }
}