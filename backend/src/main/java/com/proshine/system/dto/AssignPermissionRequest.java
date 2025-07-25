package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色权限分配请求
 */
@Data
public class AssignPermissionRequest {
    private String roleId;
    private List<String> permissionIds;
}
