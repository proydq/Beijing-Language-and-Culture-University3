package com.proshine.system.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 教室预约详情响应数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomBookingDetailsResponse {
    
    /**
     * 教室信息
     */
    private RoomInfo roomInfo;
    
    /**
     * 分页数据
     */
    private PageData pageData;
    
    /**
     * 教室信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomInfo {
        private String roomId;
        private String roomName;
        private String roomCode;
        private String buildingName;
        private String floor;
        private Integer capacity;
        private String equipment;
    }
    
    /**
     * 分页数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageData {
        private Long total;
        private Integer pages;
        private Integer pageNum;
        private Integer pageSize;
        private List<BookingDetail> list;
    }
    
    /**
     * 预约详情内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingDetail {
        private String id;
        private String bookingName;
        private String bookingTime;
        private String description;
        private String applicantName;
        private String applicantPhone;
        private String applicantDepartment;
        private String auditStatus;
        private String auditStatusCode;
        private String usageStatus;
        private String usageStatusCode;
        private String bookingStartTime;
        private String bookingEndTime;
        private Integer peopleCount;
        private String createTime;
        private String approvalTime;
        private String approverName;
        private String approvalComment;
    }
}