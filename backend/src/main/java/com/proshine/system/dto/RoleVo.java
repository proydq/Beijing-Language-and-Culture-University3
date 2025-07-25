package com.proshine.system.dto;

import lombok.Data;

/**
 * 角色列表展示对象
 */
@Data
public class RoleVo {
    private String id;
    private String roleName;
    private String roleDesc;
    private String moduleNames;
    private Integer userCount;
}
