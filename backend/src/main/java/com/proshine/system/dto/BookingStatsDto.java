package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 借用统计数据DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatsDto {

    /**
     * 总借用次数
     */
    private Long totalBookings;

    /**
     * 教师借用次数
     */
    private Long teacherBookings;

    /**
     * 学生借用次数
     */
    private Long studentBookings;

    /**
     * 待审批数量
     */
    private Long pendingApprovals;

    /**
     * 已通过审批数量
     */
    private Long approvedBookings;

    /**
     * 已拒绝数量
     */
    private Long rejectedBookings;

    /**
     * 进行中的预约数量
     */
    private Long ongoingBookings;

    /**
     * 已完成的预约数量
     */
    private Long completedBookings;

    /**
     * 成功率（通过率）
     */
    private Double successRate;
}