package com.proshine.system.repository;

import com.proshine.system.entity.SysOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 组织机构数据访问层
 */
@Repository
public interface SysOrganizationRepository extends JpaRepository<SysOrganization, String> {

    List<SysOrganization> findByParentId(String parentId);

    long countByParentId(String parentId);
}
