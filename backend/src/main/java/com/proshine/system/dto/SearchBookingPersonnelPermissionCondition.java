package com.proshine.system.dto;

import lombok.Data;

/**
 * 预约人员权限配置查询条件
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class SearchBookingPersonnelPermissionCondition {

    private String customerId;
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private String keyword;
}