package com.proshine.system.repository;

import com.proshine.system.entity.SysOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 组织机构数据访问层
 */
@Repository
public interface SysOrganizationRepository extends JpaRepository<SysOrganization, String> {

    List<SysOrganization> findByParentId(String parentId);

    long countByParentId(String parentId);

    /**
     * 根据ID和客户域ID查询组织机构
     *
     * @param id 组织机构ID
     * @param cstmId 客户域ID
     * @return 组织机构信息
     */
    Optional<SysOrganization> findByIdAndCstmId(String id, String cstmId);
}
