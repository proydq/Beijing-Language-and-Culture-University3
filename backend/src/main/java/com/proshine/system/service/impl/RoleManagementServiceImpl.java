package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.entity.SysPermission;
import com.proshine.system.entity.SysRole;
import com.proshine.system.entity.SysRolePermission;
import com.proshine.system.repository.SysPermissionRepository;
import com.proshine.system.repository.SysRolePermissionRepository;
import com.proshine.system.repository.SysRoleRepository;
import com.proshine.system.repository.SysUserRoleRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.RoleManagementService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色管理服务实现类
 */
@Service
@Slf4j
public class RoleManagementServiceImpl implements RoleManagementService {

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private SysPermissionRepository permissionRepository;

    @Autowired
    private SysRolePermissionRepository rolePermissionRepository;

    @Autowired
    private SysUserRoleRepository userRoleRepository;



    @Override
    public ResponsePageDataEntity<RoleVo> searchRoles(SearchRoleCondition condition) {
        try {
            log.info("分页查询角色列表，条件：{}", condition);

            Specification<SysRole> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                // 客户域过滤
                if (StringUtils.hasText(condition.getCustomerId())) {
                    predicates.add(cb.equal(root.get("customerId"), condition.getCustomerId()));
                } else if (StringUtils.hasText(SecurityUtil.getCustomerId())) {
                    predicates.add(cb.equal(root.get("customerId"), SecurityUtil.getCustomerId()));
                }

                // 逻辑删除过滤
                predicates.add(cb.equal(root.get("deleted"), false));

                // 角色名模糊查询
                if (StringUtils.hasText(condition.getRoleName())) {
                    predicates.add(cb.like(root.get("name"), "%" + condition.getRoleName() + "%"));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            };

            Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize());

            Page<SysRole> page = roleRepository.findAll(spec, pageable);

            List<RoleVo> roleVos = page.getContent().stream().map(role -> {
                RoleVo vo = new RoleVo();
                vo.setId(role.getId());
                vo.setRoleName(role.getName());
                vo.setRoleDesc(role.getRemark());

                List<SysPermission> permissions = permissionRepository.findByRoleId(role.getId());
                String moduleNames = permissions.stream()
                        .filter(p -> p.getType() == SysPermission.Type.MENU)
                        .map(SysPermission::getName)
                        .collect(Collectors.joining(","));
                vo.setModuleNames(moduleNames);

                int count = userRoleRepository.findByRoleId(role.getId()).size();
                vo.setUserCount(count);
                return vo;
            }).collect(Collectors.toList());

            ResponsePageDataEntity<RoleVo> result = new ResponsePageDataEntity<>();
            result.setTotal(page.getTotalElements());
            result.setRows(roleVos);
            return result;
        } catch (Exception e) {
            log.error("查询角色列表失败：", e);
            throw new RuntimeException("查询角色列表失败：" + e.getMessage());
        }
    }

    @Override
    public RoleDto findById(String id) {
        try {
            Optional<SysRole> optional = roleRepository.findById(id);
            if (!optional.isPresent() || Boolean.TRUE.equals(optional.get().getDeleted())) {
                throw new RuntimeException("角色不存在");
            }

            SysRole role = optional.get();
            if (!Objects.equals(role.getCustomerId(), SecurityUtil.getCustomerId())) {
                throw new RuntimeException("无权限访问该角色信息");
            }

            RoleDto dto = new RoleDto();
            dto.setId(role.getId());
            dto.setRoleName(role.getName());
            dto.setRoleDesc(role.getRemark());

            List<SysRolePermission> rolePermissions = rolePermissionRepository.findByRoleId(id);
            List<String> permissionIds = rolePermissions.stream()
                    .map(SysRolePermission::getPermissionId)
                    .collect(Collectors.toList());
            dto.setPermissionIds(permissionIds);
            return dto;
        } catch (Exception e) {
            log.error("查询角色详情失败：", e);
            throw new RuntimeException("查询角色详情失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void save(RoleDto roleDto) {
        try {
            String customerId = SecurityUtil.getCustomerId();
            SysRole role;
            if (!StringUtils.hasText(roleDto.getId())) {
                role = new SysRole();
                role.setId(UUID.randomUUID().toString().replace("-", ""));
                role.setCreateTime(LocalDateTime.now());
                role.setCustomerId(customerId);
                role.setDeleted(false);
            } else {
                Optional<SysRole> optional = roleRepository.findById(roleDto.getId());
                if (!optional.isPresent()) {
                    throw new RuntimeException("角色不存在");
                }
                role = optional.get();
                if (!Objects.equals(role.getCustomerId(), customerId)) {
                    throw new RuntimeException("无权限修改该角色");
                }
            }

            role.setName(roleDto.getRoleName());
            role.setRemark(roleDto.getRoleDesc());
            role.setUpdateTime(LocalDateTime.now());
            roleRepository.save(role);

            rolePermissionRepository.deleteByRoleId(role.getId());
            if (roleDto.getPermissionIds() != null && !roleDto.getPermissionIds().isEmpty()) {
                for (String pid : roleDto.getPermissionIds()) {
                    rolePermissionRepository.batchInsertRolePermissions(role.getId(), pid);
                }
            }
        } catch (Exception e) {
            log.error("保存角色失败：", e);
            throw new RuntimeException("保存角色失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        try {
            Optional<SysRole> optional = roleRepository.findById(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("角色不存在");
            }
            SysRole role = optional.get();
            if (!Objects.equals(role.getCustomerId(), SecurityUtil.getCustomerId())) {
                throw new RuntimeException("无权限删除该角色");
            }
            role.setDeleted(true);
            role.setUpdateTime(LocalDateTime.now());
            roleRepository.save(role);
        } catch (Exception e) {
            log.error("删除角色失败：", e);
            throw new RuntimeException("删除角色失败：" + e.getMessage());
        }
    }

    @Override
    public List<PermissionNode> getPermissionTree() {
        try {
            String customerId = SecurityUtil.getCustomerId();
            Specification<SysPermission> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("customerId"), customerId));
                predicates.add(cb.equal(root.get("deleted"), false));
                return cb.and(predicates.toArray(new Predicate[0]));
            };
            List<SysPermission> allPermissions = permissionRepository.findAll(spec, Sort.by("sort"));
            return buildPermissionTree(allPermissions, null);
        } catch (Exception e) {
            log.error("获取权限树失败：", e);
            throw new RuntimeException("获取权限树失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void assignPermissions(AssignPermissionRequest request) {
        try {
            rolePermissionRepository.deleteByRoleId(request.getRoleId());
            if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
                for (String pid : request.getPermissionIds()) {
                    rolePermissionRepository.batchInsertRolePermissions(request.getRoleId(), pid);
                }
            }
        } catch (Exception e) {
            log.error("分配角色权限失败：", e);
            throw new RuntimeException("分配角色权限失败：" + e.getMessage());
        }
    }

    /**
     * 构建权限树
     */
    private List<PermissionNode> buildPermissionTree(List<SysPermission> allPermissions, String parentId) {
        List<PermissionNode> nodes = new ArrayList<>();
        for (SysPermission permission : allPermissions) {
            if (parentId == null) {
                if (!StringUtils.hasText(permission.getParentId())) {
                    PermissionNode node = convertPermissionToNode(permission, allPermissions);
                    nodes.add(node);
                }
            } else if (parentId.equals(permission.getParentId())) {
                PermissionNode node = convertPermissionToNode(permission, allPermissions);
                nodes.add(node);
            }
        }
        return nodes;
    }

    private PermissionNode convertPermissionToNode(SysPermission permission, List<SysPermission> allPermissions) {
        PermissionNode node = new PermissionNode();
        node.setId(permission.getId());
        node.setLabel(permission.getName());
        node.setType(permission.getType() == null ? null : permission.getType().name().toLowerCase());
        List<PermissionNode> children = buildPermissionTree(allPermissions, permission.getId());
        if (!children.isEmpty()) {
            node.setChildren(children);
        }
        return node;
    }
}

