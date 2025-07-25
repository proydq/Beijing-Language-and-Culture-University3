package com.proshine.system.service.impl;

import com.proshine.system.entity.SysOrganization;
import com.proshine.system.repository.SysOrganizationRepository;
import com.proshine.system.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 组织机构服务实现类
 */
@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private SysOrganizationRepository organizationRepository;

    @Override
    public List<SysOrganization> getOrganizationTree() {
        List<SysOrganization> all = organizationRepository.findAll();
        return buildTree(all, null);
    }

    private List<SysOrganization> buildTree(List<SysOrganization> all, String parentId) {
        List<SysOrganization> list = new ArrayList<>();
        for (SysOrganization org : all) {
            if (parentId == null) {
                if (!StringUtils.hasText(org.getParentId())) {
                    org.setChildren(buildTree(all, org.getId()));
                    list.add(org);
                }
            } else if (parentId.equals(org.getParentId())) {
                org.setChildren(buildTree(all, org.getId()));
                list.add(org);
            }
        }
        return list;
    }

    @Override
    public SysOrganization findById(String id) {
        Optional<SysOrganization> optional = organizationRepository.findById(id);
        if (!optional.isPresent()) {
            throw new RuntimeException("组织不存在");
        }
        return optional.get();
    }

    @Override
    @Transactional
    public SysOrganization save(SysOrganization organization) {
        organization.setId(null);
        return organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public SysOrganization update(String id, SysOrganization organization) {
        Optional<SysOrganization> optional = organizationRepository.findById(id);
        if (!optional.isPresent()) {
            throw new RuntimeException("组织不存在");
        }
        SysOrganization exist = optional.get();
        if (StringUtils.hasText(organization.getName())) {
            exist.setName(organization.getName());
        }
        if (StringUtils.hasText(organization.getCode())) {
            exist.setCode(organization.getCode());
        }
        if (StringUtils.hasText(organization.getDescription())) {
            exist.setDescription(organization.getDescription());
        }
        exist.setParentId(organization.getParentId());
        return organizationRepository.save(exist);
    }

    @Override
    @Transactional
    public void delete(String id) {
        long count = organizationRepository.countByParentId(id);
        if (count > 0) {
            throw new RuntimeException("存在子节点，无法删除");
        }
        organizationRepository.deleteById(id);
    }
}
