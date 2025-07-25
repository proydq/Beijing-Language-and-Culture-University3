package com.proshine.system.repository;

import com.proshine.system.entity.SysTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for SysTitle.
 */
@Repository
public interface SysTitleRepository extends JpaRepository<SysTitle, String>,
        JpaSpecificationExecutor<SysTitle> {
}
