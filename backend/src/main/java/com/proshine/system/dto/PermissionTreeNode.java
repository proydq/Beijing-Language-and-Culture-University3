package com.proshine.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 权限树节点DTO
 */
@Data
public class PermissionTreeNode {
    /**
     * 权限ID
     */
    private String id;
    
    /**
     * 权限编码
     */
    private String code;
    
    /**
     * 权限名称
     */
    private String name;
    
    /**
     * 权限类型 MENU/BUTTON
     */
    private String type;
    
    /**
     * 父级ID
     */
    private String parentId;
    
    /**
     * 前端路由路径
     */
    private String path;
    
    /**
     * 前端组件名称
     */
    private String component;
    
    /**
     * 菜单图标
     */
    private String icon;
    
    /**
     * 是否在菜单中显示
     */
    private Boolean visible;
    
    /**
     * 是否缓存页面
     */
    private Boolean keepAlive;
    
    /**
     * 重定向路径
     */
    private String redirect;
    
    /**
     * 排序值
     */
    private Integer sort;
    
    /**
     * 子权限列表
     */
    private List<PermissionTreeNode> children;
}