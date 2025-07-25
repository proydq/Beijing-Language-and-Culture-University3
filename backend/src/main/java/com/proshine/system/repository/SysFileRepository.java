package com.proshine.system.repository;

import com.proshine.system.entity.SysFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for SysFile
 */
@Repository
public interface SysFileRepository extends JpaRepository<SysFile, String> {
}
