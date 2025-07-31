package com.proshine.system.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建房间预约请求DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class CreateBookingRequest {
    
    /**
     * 房间ID
     */
    private String roomId;
    
    /**
     * 房间名称
     */
    private String roomName;
    
    /**
     * 申请人姓名
     */
    private String applicant;
    
    /**
     * 申请人ID（从当前用户获取）
     */
    private String applicantId;
    
    /**
     * 申请人类型（教师/学生/管理员）
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
     * 预约名称
     */
    private String bookingName;
    
    /**
     * 借用时间（格式描述）
     */
    private String borrowTime;
    
    /**
     * 预约开始时间
     */
    private LocalDateTime bookingStartTime;
    
    /**
     * 预约结束时间
     */
    private LocalDateTime bookingEndTime;
    
    /**
     * 借用描述
     */
    private String description;
    
    /**
     * 申请理由
     */
    private String reason;
    
    /**
     * 参与人姓名列表
     */
    private List<String> participants;
    
    /**
     * 备注详情
     */
    private String remark;
    
    /**
     * 审批人姓名列表
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
     * 紧急程度
     */
    private String urgency = "NORMAL";
    
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