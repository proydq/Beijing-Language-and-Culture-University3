package com.proshine.system.dto;

import lombok.Data;

/**
 * 管理员搜索条件
 */
@Data
public class SearchAdminCondition {
    
    /**
     * 管理员姓名(模糊查询)
     */
    private String adminName;
    
    /**
     * 登录账号(模糊查询)
     */
    private String loginAccount;
    
    /**
     * 账号状态: ACTIVE, INACTIVE
     */
    private String accountStatus;
    
    /**
     * 角色ID
     */
    private String roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 页码(从1开始)
     */
    private Integer pageNumber = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    
    /**
     * 客户域ID(自动设置)
     */
    private String customerId;
}