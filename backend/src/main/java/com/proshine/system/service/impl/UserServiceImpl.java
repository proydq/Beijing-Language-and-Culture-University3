package com.proshine.system.service.impl;

import com.proshine.system.entity.Permission;
import com.proshine.system.entity.RolePermission;
import com.proshine.system.entity.User;
import com.proshine.system.entity.UserRole;
import com.proshine.system.repository.PermissionRepository;
import com.proshine.system.repository.RolePermissionRepository;
import com.proshine.system.repository.UserRepository;
import com.proshine.system.repository.UserRoleRepository;
import com.proshine.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<String> getPermissionCodes(String userId) {
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<String> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<RolePermission> rp = rolePermissionRepository.findAll().stream()
                .filter(r -> roleIds.contains(r.getRoleId()))
                .collect(Collectors.toList());
        List<String> permIds = rp.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        if (permIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return permissionRepository.findAllById(permIds).stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRole(String userId, List<String> roleIds) {
        userRoleRepository.deleteByUserId(userId);
        for (String roleId : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleRepository.save(ur);
        }
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
