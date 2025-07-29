package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 预约人员权限配置保存请求
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class BookingPersonnelPermissionSaveRequest {

    private String subject;
    
    private List<String> userIds;
    
    private List<String> roomIds;
}