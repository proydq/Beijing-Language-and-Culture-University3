package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.entity.SysUser;
import com.proshine.system.entity.SysUserRole;
import com.proshine.system.entity.SysRole;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.repository.SysUserRoleRepository;
import com.proshine.system.repository.SysRoleRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.AdminManagementService;
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
 * 管理员管理服务实现
 */
@Service
@Slf4j
public class AdminManagementServiceImpl implements AdminManagementService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private SysUserRoleRepository sysUserRoleRepository;

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Override
    public ResponsePageDataEntity<AdminVo> searchAdmins(SearchAdminCondition condition) {
        log.info("分页查询管理员列表，条件：{}", condition);

        // 构建查询条件
        Specification<SysUser> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 客户域过滤
            predicates.add(criteriaBuilder.equal(root.get("customerId"), condition.getCustomerId()));

            // 管理员姓名模糊查询
            if (StringUtils.hasText(condition.getAdminName())) {
                predicates.add(criteriaBuilder.like(root.get("realName"), "%" + condition.getAdminName() + "%"));
            }

            // 登录账号模糊查询
            if (StringUtils.hasText(condition.getLoginAccount())) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + condition.getLoginAccount() + "%"));
            }

            // 账号状态过滤
            if (StringUtils.hasText(condition.getAccountStatus())) {
                try {
                    SysUser.Status statusEnum = SysUser.Status.valueOf(condition.getAccountStatus());
                    predicates.add(criteriaBuilder.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 如果状态值无效，忽略此过滤条件
                    log.warn("无效的账号状态值: {}", condition.getAccountStatus());
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 分页查询
        Pageable pageable = PageRequest.of(
                condition.getPageNumber() - 1,
                condition.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createTime")
        );

        Page<SysUser> userPage = sysUserRepository.findAll(spec, pageable);

        // 转换为VO
        List<AdminVo> adminVos = userPage.getContent().stream()
                .map(this::convertToAdminVo)
                .collect(Collectors.toList());

        // 批量查询用户角色
        List<String> userIds = adminVos.stream().map(AdminVo::getId).collect(Collectors.toList());
        Map<String, List<AdminRoleVo>> userRolesMap = getUserRolesMap(userIds, condition.getCustomerId());

        // 设置角色信息并过滤
        List<AdminVo> filteredAdmins = new ArrayList<>();
        for (AdminVo admin : adminVos) {
            List<AdminRoleVo> roles = userRolesMap.getOrDefault(admin.getId(), new ArrayList<>());
            admin.setRoles(roles);
            
            // 如果有角色过滤条件，检查是否匹配
            boolean shouldInclude = true;
            if (StringUtils.hasText(condition.getRoleId()) || StringUtils.hasText(condition.getRoleName())) {
                shouldInclude = roles.stream().anyMatch(role -> {
                    boolean matchId = !StringUtils.hasText(condition.getRoleId()) || 
                                    condition.getRoleId().equals(role.getRoleId());
                    boolean matchName = !StringUtils.hasText(condition.getRoleName()) || 
                                      role.getRoleName().contains(condition.getRoleName());
                    return matchId && matchName;
                });
            }
            
            if (shouldInclude) {
                filteredAdmins.add(admin);
            }
        }
        
        adminVos = filteredAdmins;

        ResponsePageDataEntity<AdminVo> result = new ResponsePageDataEntity<>();
        result.setRows(adminVos);
        result.setTotal(userPage.getTotalElements());

        log.info("查询管理员列表成功，共 {} 条记录", result.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddAdminResponse addAdmins(AddAdminRequest request) {
        log.info("添加管理员，用户ID：{}，角色ID：{}", request.getUserIds(), request.getRoleIds());

        String customerId = SecurityUtil.getCustomerId();
        int successCount = 0;
        List<String> failUserIds = new ArrayList<>();
        List<String> failReasons = new ArrayList<>();

        for (String userId : request.getUserIds()) {
            try {
                // 检查用户是否存在
                Optional<SysUser> userOpt = sysUserRepository.findByIdAndCustomerId(userId, customerId);
                if (!userOpt.isPresent()) {
                    failUserIds.add(userId);
                    failReasons.add("用户不存在");
                    continue;
                }

                // 检查用户是否已经是管理员
                List<SysUserRole> existingRoles = sysUserRoleRepository.findByUserIdAndCustomerId(userId, customerId);
                if (!existingRoles.isEmpty()) {
                    // 删除现有角色，重新分配
                    sysUserRoleRepository.deleteAll(existingRoles);
                }

                // 分配新角色
                boolean roleAssignmentFailed = false;
                for (String roleId : request.getRoleIds()) {
                    // 验证角色是否存在
                    Optional<SysRole> roleOpt = sysRoleRepository.findByIdAndCustomerId(roleId, customerId);
                    if (!roleOpt.isPresent()) {
                        failUserIds.add(userId);
                        failReasons.add("角色不存在：" + roleId);
                        roleAssignmentFailed = true;
                        break;
                    }

                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRole.setCustomerId(customerId);
                    userRole.setCreateTime(LocalDateTime.now());
                    sysUserRoleRepository.save(userRole);
                }
                
                if (!roleAssignmentFailed) {
                    successCount++;
                    log.info("用户 {} 添加为管理员成功", userId);
                }

            } catch (Exception e) {
                log.error("添加管理员失败，用户ID：{}", userId, e);
                failUserIds.add(userId);
                failReasons.add("系统错误：" + e.getMessage());
            }
        }

        AddAdminResponse response = AddAdminResponse.builder()
                .successCount(successCount)
                .failCount(failUserIds.size())
                .failUserIds(failUserIds)
                .failReasons(failReasons)
                .build();

        log.info("添加管理员完成，成功：{}，失败：{}", successCount, failUserIds.size());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdminRoles(String userId, UpdateAdminRolesRequest request) {
        log.info("更新管理员角色，用户ID：{}，新角色：{}", userId, request.getRoleIds());

        String customerId = SecurityUtil.getCustomerId();

        // 检查用户是否存在
        Optional<SysUser> userOpt = sysUserRepository.findByIdAndCustomerId(userId, customerId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 删除现有角色
        List<SysUserRole> existingRoles = sysUserRoleRepository.findByUserIdAndCustomerId(userId, customerId);
        sysUserRoleRepository.deleteAll(existingRoles);

        // 分配新角色
        for (String roleId : request.getRoleIds()) {
            // 验证角色是否存在
            Optional<SysRole> roleOpt = sysRoleRepository.findByIdAndCustomerId(roleId, customerId);
            if (!roleOpt.isPresent()) {
                throw new IllegalArgumentException("角色不存在：" + roleId);
            }

            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCustomerId(customerId);
            userRole.setCreateTime(LocalDateTime.now());
            sysUserRoleRepository.save(userRole);
        }

        log.info("用户 {} 角色更新成功", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdmin(String userId) {
        log.info("删除管理员，用户ID：{}", userId);

        String customerId = SecurityUtil.getCustomerId();

        // 检查用户是否存在
        Optional<SysUser> userOpt = sysUserRepository.findByIdAndCustomerId(userId, customerId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 删除用户角色关联
        List<SysUserRole> userRoles = sysUserRoleRepository.findByUserIdAndCustomerId(userId, customerId);
        sysUserRoleRepository.deleteAll(userRoles);

        log.info("管理员 {} 删除成功", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteAdmins(BatchDeleteAdminRequest request) {
        log.info("批量删除管理员，用户ID列表：{}", request.getUserIds());

        for (String userId : request.getUserIds()) {
            try {
                deleteAdmin(userId);
            } catch (Exception e) {
                log.error("删除管理员失败，用户ID：{}", userId, e);
                // 继续处理其他用户，不中断整个批量操作
            }
        }

        log.info("批量删除管理员完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdminStatus(String userId, UpdateAdminStatusRequest request) {
        log.info("更新管理员状态，用户ID：{}，新状态：{}", userId, request.getStatus());

        String customerId = SecurityUtil.getCustomerId();

        // 检查用户是否存在
        Optional<SysUser> userOpt = sysUserRepository.findByIdAndCustomerId(userId, customerId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("用户不存在");
        }

        SysUser user = userOpt.get();
        try {
            SysUser.Status newStatus = SysUser.Status.valueOf(request.getStatus());
            user.setStatus(newStatus);
            user.setUpdateTime(LocalDateTime.now());
            sysUserRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的状态值: " + request.getStatus());
        }

        log.info("用户 {} 状态更新成功，新状态：{}", userId, request.getStatus());
    }

    @Override
    public List<AvailableRoleVo> getAvailableRoles() {
        log.info("获取可分配角色列表");

        String customerId = SecurityUtil.getCustomerId();
        List<SysRole> roles = sysRoleRepository.findByCustomerIdAndStatus(customerId, "ACTIVE");

        List<AvailableRoleVo> availableRoles = roles.stream()
                .map(this::convertToAvailableRoleVo)
                .collect(Collectors.toList());

        log.info("获取可分配角色列表成功，共 {} 个角色", availableRoles.size());
        return availableRoles;
    }

    /**
     * 转换用户实体为管理员VO
     */
    private AdminVo convertToAdminVo(SysUser user) {
        AdminVo adminVo = new AdminVo();
        adminVo.setId(user.getId());
        adminVo.setUsername(user.getUsername());
        adminVo.setRealName(user.getRealName());
        adminVo.setGender(user.getGender());
        adminVo.setPhone(user.getPhone());
        adminVo.setEmail(user.getEmail());
        adminVo.setDepartmentName(user.getDepartmentName());
        adminVo.setJobNumber(user.getJobNumber());
        adminVo.setAccountStatus(user.getStatus() != null ? user.getStatus().name() : null);
        adminVo.setCreateTime(user.getCreateTime());
        adminVo.setLastLoginTime(user.getLastLoginTime());
        adminVo.setAvatarUrl(user.getAvatarUrl());

        // 微信相关信息（如果有的话）
        adminVo.setWechatNickname(user.getWechatNickname() != null ? user.getWechatNickname() : "/");
        adminVo.setWechatPhone(user.getWechatPhone() != null ? user.getWechatPhone() : "/");
        adminVo.setWechatBindTime(user.getWechatBindTime());

        return adminVo;
    }

    /**
     * 批量获取用户角色映射
     */
    private Map<String, List<AdminRoleVo>> getUserRolesMap(List<String> userIds, String customerId) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<Object[]> userRoleData = sysUserRoleRepository.findUserRolesWithDetails(userIds, customerId);

            Map<String, List<AdminRoleVo>> userRolesMap = new HashMap<>();
            for (Object[] data : userRoleData) {
                if (data.length >= 5) {
                    String userId = (String) data[0];
                    String roleId = (String) data[1];
                    String roleName = (String) data[2];
                    String roleDescription = (String) data[3];
                    String roleType = (String) data[4];

                    AdminRoleVo roleVo = new AdminRoleVo();
                    roleVo.setRoleId(roleId);
                    roleVo.setRoleName(roleName);
                    roleVo.setRoleDescription(roleDescription);
                    roleVo.setRoleType(roleType);

                    userRolesMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(roleVo);
                }
            }

            return userRolesMap;
        } catch (Exception e) {
            log.error("获取用户角色映射失败:", e);
            return new HashMap<>();
        }
    }

    /**
     * 转换角色实体为可分配角色VO
     */
    private AvailableRoleVo convertToAvailableRoleVo(SysRole role) {
        AvailableRoleVo roleVo = new AvailableRoleVo();
        roleVo.setRoleId(role.getId());
        roleVo.setRoleName(role.getRoleName());
        roleVo.setRoleDescription(role.getRoleDescription());
        roleVo.setRoleType(role.getRoleType());
        roleVo.setIsDefault(role.getIsDefault());

        // TODO: 获取角色权限列表
        // roleVo.setPermissions(getRolePermissions(role.getId()));

        return roleVo;
    }
}