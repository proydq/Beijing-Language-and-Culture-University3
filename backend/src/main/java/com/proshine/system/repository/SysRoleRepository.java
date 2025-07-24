package com.proshine.system.repository;

import com.proshine.system.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, String> {
}
