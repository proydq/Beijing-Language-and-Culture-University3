package com.proshine.system.repository;

import com.proshine.system.entity.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, String> {
    List<SysUserRole> findByUserId(String userId);
}
