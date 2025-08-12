package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限信息DTO
 * 用于返回用户的权限信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {
    
    /**
     * 权限编码，如：USER_ADD, USER_EDIT
     */
    private String code;
    
    /**
     * 权限名称，如：用户新增，用户编辑
     */
    private String name;
    
    /**
     * 权限类型：MENU（菜单）或 BUTTON（按钮）
     */
    private String type;
    
    /**
     * 父级权限ID（用于构建权限树）
     */
    private String parentId;
    
    /**
     * 权限路径或标识
     */
    private String url;
    
    /**
     * 排序值
     */
    private Integer sort;
}