package com.proshine.system.repository;

import com.proshine.system.entity.SysRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRolePermissionRepository extends JpaRepository<SysRolePermission, String> {
    List<SysRolePermission> findByRoleId(String roleId);
}
