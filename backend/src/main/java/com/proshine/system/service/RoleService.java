package com.proshine.system.service;

import com.proshine.system.entity.Permission;

import java.util.List;

public interface RoleService {
    List<Permission> allPermissions();
    void assignPermissions(String roleId, List<String> permissionIds);
}
