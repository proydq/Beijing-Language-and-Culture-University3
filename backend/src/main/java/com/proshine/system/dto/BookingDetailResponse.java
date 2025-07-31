package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约详情响应DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BookingDetailResponse {
    
    /**
     * 预约ID
     */
    private String id;
    
    /**
     * 预约名称
     */
    private String bookingName;
    
    /**
     * 申请人姓名
     */
    private String applicantName;
    
    /**
     * 申请人类型
     */
    private String applicantType;
    
    /**
     * 申请人电话
     */
    private String applicantPhone;
    
    /**
     * 申请人部门
     */
    private String applicantDepartment;
    
    /**
     * 房间ID
     */
    private String roomId;
    
    /**
     * 房间名称
     */
    private String roomName;
    
    /**
     * 预约开始时间
     */
    private LocalDateTime bookingStartTime;
    
    /**
     * 预约结束时间
     */
    private LocalDateTime bookingEndTime;
    
    /**
     * 预约时段描述
     */
    private String bookingPeriod;
    
    /**
     * 预约描述
     */
    private String description;
    
    /**
     * 申请理由
     */
    private String reason;
    
    /**
     * 审批状态
     */
    private String approvalStatus;
    
    /**
     * 使用状态
     */
    private String usageStatus;
    
    /**
     * 紧急程度
     */
    private String urgency;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 参与人列表
     */
    private List<String> participants;
    
    /**
     * 审批人列表
     */
    private List<String> approvers;
    
    /**
     * 参与人详细信息
     */
    private List<PersonDetails> participantDetails;
    
    /**
     * 审批人详细信息
     */
    private List<PersonDetails> approverDetails;
    
    /**
     * 人员详细信息内部类
     */
    @Data
    public static class PersonDetails {
        private String id;
        private String name;
        private String department;
        private String position;
        private String phone;
        private String email;
    }
}