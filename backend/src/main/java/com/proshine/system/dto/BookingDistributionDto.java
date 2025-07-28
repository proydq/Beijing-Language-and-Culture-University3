package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 借用数据分布DTO
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDistributionDto {

    /**
     * 申请人类型（教师/学生）
     */
    private String type;

    /**
     * 借用次数
     */
    private Long count;

    /**
     * 占比百分比
     */
    private Double percentage;

    /**
     * 构造函数（不包含百分比，后续计算）
     * 
     * @param type 申请人类型
     * @param count 借用次数
     */
    public BookingDistributionDto(String type, Long count) {
        this.type = type;
        this.count = count;
    }
}