package com.proshine.system.service.impl;

import com.proshine.system.entity.Permission;
import com.proshine.system.entity.RolePermission;
import com.proshine.system.repository.PermissionRepository;
import com.proshine.system.repository.RolePermissionRepository;
import com.proshine.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public List<Permission> allPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    @Transactional
    public void assignPermissions(String roleId, List<String> permissionIds) {
        rolePermissionRepository.deleteByRoleId(roleId);
        for (String pid : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(pid);
            rolePermissionRepository.save(rp);
        }
    }
}
