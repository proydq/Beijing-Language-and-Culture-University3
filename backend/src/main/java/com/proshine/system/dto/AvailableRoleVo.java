package com.proshine.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 可分配角色视图对象
 */
@Data
public class AvailableRoleVo {
    
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
    
    /**
     * 是否默认角色
     */
    private Boolean isDefault;
    
    /**
     * 权限列表
     */
    private List<String> permissions;
}