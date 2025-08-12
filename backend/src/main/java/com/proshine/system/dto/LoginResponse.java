package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private String userId;
    private String username;
    private String realName;
    private String customerId;
    
    /**
     * 用户权限编码列表（如：USER_ADD, USER_EDIT）
     */
    private List<String> permissions;
    
    /**
     * 用户权限详情列表（包含权限名称、类型等信息）
     */
    private List<PermissionDTO> permissionDetails;
    
    /**
     * 用户角色列表
     */
    private List<String> roles;
    
    /**
     * 权限树结构（用于前端动态生成菜单和路由）
     */
    private List<PermissionTreeNode> permissionTree;
    
    public LoginResponse(String token, String userId, String username, String realName, String customerId) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.customerId = customerId;
    }
}