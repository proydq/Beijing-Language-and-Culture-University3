package com.proshine.system.repository;

import com.proshine.system.entity.SysPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository for SysPosition entity.
 */
@Repository
public interface SysPositionRepository extends JpaRepository<SysPosition, String>,
        JpaSpecificationExecutor<SysPosition> {
}
