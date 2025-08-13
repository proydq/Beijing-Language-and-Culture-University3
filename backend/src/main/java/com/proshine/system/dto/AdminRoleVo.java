package com.proshine.system.dto;

import lombok.Data;

/**
 * 管理员角色信息视图对象
 */
@Data
public class AdminRoleVo {
    
    /**
     * 角色ID
     */
    private String roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 角色描述
     */
    private String roleDescription;
    
    /**
     * 角色类型
     */
    private String roleType;
}