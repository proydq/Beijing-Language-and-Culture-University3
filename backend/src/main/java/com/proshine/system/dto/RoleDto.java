package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色详情DTO
 */
@Data
public class RoleDto {
    private String id;
    private String roleName;
    private String roleDesc;
    private List<String> permissionIds;
}
