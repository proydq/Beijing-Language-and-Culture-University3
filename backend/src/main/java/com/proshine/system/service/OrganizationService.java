package com.proshine.system.service;

import com.proshine.system.entity.SysOrganization;

import java.util.List;

/**
 * 组织机构服务接口
 */
public interface OrganizationService {

    /**
     * 获取组织树结构
     *
     * @return 组织树
     */
    List<SysOrganization> getOrganizationTree();

    /**
     * 根据ID获取组织详情
     */
    SysOrganization findById(String id);

    /**
     * 新增组织
     */
    SysOrganization save(SysOrganization organization);

    /**
     * 更新组织
     */
    SysOrganization update(String id, SysOrganization organization);

    /**
     * 删除组织
     */
    void delete(String id);
}
