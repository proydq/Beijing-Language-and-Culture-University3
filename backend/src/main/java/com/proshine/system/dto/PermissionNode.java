package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 权限树节点
 */
@Data
public class PermissionNode {
    private String id;
    private String label;
    private String type; // module | action
    private List<PermissionNode> children;
}
