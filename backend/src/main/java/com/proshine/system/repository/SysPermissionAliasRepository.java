package com.proshine.system.repository;

import com.proshine.system.entity.SysPermissionAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 权限别名仓库
 */
@Repository
public interface SysPermissionAliasRepository extends JpaRepository<SysPermissionAlias, String> {
    
    /**
     * 根据旧权限码查找
     */
    Optional<SysPermissionAlias> findByOldCode(String oldCode);
    
    /**
     * 根据新权限码查找
     */
    Optional<SysPermissionAlias> findByNewCode(String newCode);
}