package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.entity.SysOrganization;
import com.proshine.system.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组织机构控制器
 */
@RestController
@RequestMapping("/api/organization")
@Slf4j
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * 获取组织树结构
     */
    @GetMapping("/tree")
    public ResponseEntity<List<SysOrganization>> tree() {
        try {
            log.info("==========/api/organization/tree=============");
            List<SysOrganization> list = organizationService.getOrganizationTree();
            return ResponseEntity.success(list);
        } catch (Exception e) {
            log.error("获取组织树失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 获取单个组织详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<SysOrganization> findById(@PathVariable String id) {
        try {
            log.info("==========/api/organization/{} [GET]=============", id);
            SysOrganization org = organizationService.findById(id);
            return ResponseEntity.success(org);
        } catch (Exception e) {
            log.error("查询组织详情失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 新增组织
     */
    @PostMapping
    public ResponseEntity<SysOrganization> create(@RequestBody SysOrganization organization) {
        try {
            log.info("==========/api/organization [POST]=============");
            SysOrganization saved = organizationService.save(organization);
            return ResponseEntity.success(saved);
        } catch (Exception e) {
            log.error("新增组织失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 编辑组织
     */
    @PutMapping("/{id}")
    public ResponseEntity<SysOrganization> update(@PathVariable String id, @RequestBody SysOrganization organization) {
        try {
            log.info("==========/api/organization/{} [PUT]=============", id);
            SysOrganization updated = organizationService.update(id, organization);
            return ResponseEntity.success(updated);
        } catch (Exception e) {
            log.error("编辑组织失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 删除组织
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            log.info("==========/api/organization/{} [DELETE]=============", id);
            organizationService.delete(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除组织失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}
