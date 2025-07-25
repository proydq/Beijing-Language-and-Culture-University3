package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchUserCondition;
import com.proshine.system.dto.UserVo;
import com.proshine.system.entity.SysRole;
import com.proshine.system.entity.SysUser;
import com.proshine.system.entity.SysUserRole;
import com.proshine.system.repository.*;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现类
 */
@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {
    
    @Autowired
    private SysUserRepository userRepository;
    
    @Autowired
    private SysRoleRepository roleRepository;
    
    @Autowired
    private SysUserRoleRepository userRoleRepository;
    
    @Autowired
    private SysPermissionRepository permissionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public ResponsePageDataEntity<UserVo> searchUsers(SearchUserCondition condition) {
        try {
            log.info("分页查询用户信息，条件：{}", condition);
            
            // 设置客户域
            condition.setCustomerId(SecurityUtil.getCustomerId());
            
            // 构建查询条件
            Specification<SysUser> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                
                // 客户域过滤
                if (StringUtils.hasText(condition.getCustomerId())) {
                    predicates.add(criteriaBuilder.equal(root.get("customerId"), condition.getCustomerId()));
                }
                
                // 逻辑删除过滤
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
                
                // 用户名模糊查询
                if (StringUtils.hasText(condition.getUsername())) {
                    predicates.add(criteriaBuilder.like(root.get("username"), "%" + condition.getUsername() + "%"));
                }
                
                // 姓名模糊查询
                if (StringUtils.hasText(condition.getRealName())) {
                    predicates.add(criteriaBuilder.like(root.get("realName"), "%" + condition.getRealName() + "%"));
                }
                
                // 手机号查询
                if (StringUtils.hasText(condition.getPhone())) {
                    predicates.add(criteriaBuilder.like(root.get("phone"), "%" + condition.getPhone() + "%"));
                }
                
                // 工号查询
                if (StringUtils.hasText(condition.getJobNumber())) {
                    predicates.add(criteriaBuilder.like(root.get("jobNumber"), "%" + condition.getJobNumber() + "%"));
                }
                
                // 部门查询
                if (StringUtils.hasText(condition.getDepartmentId())) {
                    predicates.add(criteriaBuilder.equal(root.get("departmentId"), condition.getDepartmentId()));
                }
                
                // 状态查询
                if (condition.getStatus() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), condition.getStatus()));
                }
                
                // 性别查询
                if (StringUtils.hasText(condition.getGender())) {
                    predicates.add(criteriaBuilder.equal(root.get("gender"), condition.getGender()));
                }
                
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            
            // 分页查询
            Pageable pageable = PageRequest.of(
                    condition.getPageNumber() - 1,
                    condition.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createTime")
            );
            
            Page<SysUser> page = userRepository.findAll(spec, pageable);
            
            // 转换为VO
            List<UserVo> userVos = page.getContent().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
            
            ResponsePageDataEntity<UserVo> result = new ResponsePageDataEntity<>();
            result.setTotal(page.getTotalElements());
            result.setRows(userVos);
            
            log.info("查询到 {} 条用户记录", result.getTotal());
            return result;
            
        } catch (Exception e) {
            log.error("分页查询用户信息失败：", e);
            throw new RuntimeException("查询用户信息失败：" + e.getMessage());
        }
    }
    
    @Override
    public UserVo findById(String id) {
        try {
            log.info("根据ID查询用户信息：{}", id);
            
            Optional<SysUser> userOptional = userRepository.findById(id);
            if (!userOptional.isPresent()) {
                throw new RuntimeException("用户不存在");
            }
            
            SysUser user = userOptional.get();
            
            // 检查客户域权限
            String currentCustomerId = SecurityUtil.getCustomerId();
            if (!user.getCustomerId().equals(currentCustomerId)) {
                throw new RuntimeException("无权限访问该用户信息");
            }
            
            return convertToVo(user);
            
        } catch (Exception e) {
            log.error("根据ID查询用户信息失败：", e);
            throw new RuntimeException("查询用户信息失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public SysUser save(SysUser user) {
        try {
            log.info("保存用户信息：{}", user.getUsername());
            
            // 设置客户域
            user.setCustomerId(SecurityUtil.getCustomerId());
            
            // 检查用户名是否存在
            if (existsByUsername(user.getUsername(), user.getCustomerId())) {
                throw new RuntimeException("用户名已存在");
            }
            
            // 检查工号是否存在
            if (StringUtils.hasText(user.getJobNumber()) && 
                existsByJobNumber(user.getJobNumber(), user.getCustomerId())) {
                throw new RuntimeException("工号已存在");
            }
            
            // 加密密码
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // 默认密码
                user.setPassword(passwordEncoder.encode("123456"));
            }
            
            // 设置默认状态
            if (user.getStatus() == null) {
                user.setStatus(SysUser.Status.NORMAL);
            }
            
            user.setDeleted(false);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            
            SysUser savedUser = userRepository.save(user);
            log.info("用户 {} 保存成功", savedUser.getUsername());
            
            return savedUser;
            
        } catch (Exception e) {
            log.error("保存用户信息失败：", e);
            throw new RuntimeException("保存用户信息失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public SysUser update(SysUser user) {
        try {
            log.info("更新用户信息：{}", user.getId());
            
            Optional<SysUser> existingUserOptional = userRepository.findById(user.getId());
            if (!existingUserOptional.isPresent()) {
                throw new RuntimeException("用户不存在");
            }
            
            SysUser existingUser = existingUserOptional.get();
            
            // 检查客户域权限
            String currentCustomerId = SecurityUtil.getCustomerId();
            if (!existingUser.getCustomerId().equals(currentCustomerId)) {
                throw new RuntimeException("无权限修改该用户信息");
            }
            
            // 更新字段
            if (StringUtils.hasText(user.getRealName())) {
                existingUser.setRealName(user.getRealName());
            }
            if (StringUtils.hasText(user.getGender())) {
                existingUser.setGender(user.getGender());
            }
            if (StringUtils.hasText(user.getPhone())) {
                existingUser.setPhone(user.getPhone());
            }
            if (StringUtils.hasText(user.getJobNumber())) {
                existingUser.setJobNumber(user.getJobNumber());
            }
            if (StringUtils.hasText(user.getDepartmentId())) {
                existingUser.setDepartmentId(user.getDepartmentId());
                existingUser.setDepartmentName(user.getDepartmentName());
            }
            if (StringUtils.hasText(user.getPositionId())) {
                existingUser.setPositionId(user.getPositionId());
                existingUser.setPositionName(user.getPositionName());
            }
            if (StringUtils.hasText(user.getTitleId())) {
                existingUser.setTitleId(user.getTitleId());
                existingUser.setTitleName(user.getTitleName());
            }
            if (StringUtils.hasText(user.getAvatarUrl())) {
                existingUser.setAvatarUrl(user.getAvatarUrl());
            }
            if (StringUtils.hasText(user.getFaceImageUrl())) {
                existingUser.setFaceImageUrl(user.getFaceImageUrl());
            }
            if (StringUtils.hasText(user.getCardNumber())) {
                existingUser.setCardNumber(user.getCardNumber());
            }
            if (StringUtils.hasText(user.getAttendanceNumber())) {
                existingUser.setAttendanceNumber(user.getAttendanceNumber());
            }
            if (user.getStatus() != null) {
                existingUser.setStatus(user.getStatus());
            }
            
            existingUser.setUpdateTime(LocalDateTime.now());
            
            SysUser updatedUser = userRepository.save(existingUser);
            log.info("用户 {} 更新成功", updatedUser.getUsername());
            
            return updatedUser;
            
        } catch (Exception e) {
            log.error("更新用户信息失败：", e);
            throw new RuntimeException("更新用户信息失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void delete(List<String> ids) {
        try {
            log.info("删除用户，IDs：{}", ids);
            
            String currentCustomerId = SecurityUtil.getCustomerId();
            
            for (String id : ids) {
                Optional<SysUser> userOptional = userRepository.findById(id);
                if (userOptional.isPresent()) {
                    SysUser user = userOptional.get();
                    
                    // 检查客户域权限
                    if (!user.getCustomerId().equals(currentCustomerId)) {
                        log.warn("无权限删除用户：{}", id);
                        continue;
                    }
                    
                    user.setDeleted(true);
                    user.setUpdateTime(LocalDateTime.now());
                    userRepository.save(user);
                    
                    log.info("用户 {} 删除成功", user.getUsername());
                }
            }
            
        } catch (Exception e) {
            log.error("删除用户失败：", e);
            throw new RuntimeException("删除用户失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void moveToRecycleBin(List<String> ids) {
        // 逻辑删除，与delete方法相同
        delete(ids);
    }
    
    @Override
    @Transactional
    public void changeStatus(String userId, SysUser.Status status) {
        try {
            log.info("修改用户状态：{}, 状态：{}", userId, status);
            
            Optional<SysUser> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                throw new RuntimeException("用户不存在");
            }
            
            SysUser user = userOptional.get();
            
            // 检查客户域权限
            String currentCustomerId = SecurityUtil.getCustomerId();
            if (!user.getCustomerId().equals(currentCustomerId)) {
                throw new RuntimeException("无权限修改该用户状态");
            }
            
            user.setStatus(status);
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
            
            log.info("用户 {} 状态修改成功", user.getUsername());
            
        } catch (Exception e) {
            log.error("修改用户状态失败：", e);
            throw new RuntimeException("修改用户状态失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void resetPassword(String userId, String newPassword) {
        try {
            log.info("重置用户密码：{}", userId);
            
            Optional<SysUser> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                throw new RuntimeException("用户不存在");
            }
            
            SysUser user = userOptional.get();
            
            // 检查客户域权限
            String currentCustomerId = SecurityUtil.getCustomerId();
            if (!user.getCustomerId().equals(currentCustomerId)) {
                throw new RuntimeException("无权限重置该用户密码");
            }
            
            // 默认密码
            if (!StringUtils.hasText(newPassword)) {
                newPassword = "123456";
            }
            
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdateTime(LocalDateTime.now());
            userRepository.save(user);
            
            log.info("用户 {} 密码重置成功", user.getUsername());
            
        } catch (Exception e) {
            log.error("重置用户密码失败：", e);
            throw new RuntimeException("重置用户密码失败：" + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public void assignRoles(String userId, List<String> roleIds) {
        try {
            log.info("分配用户角色：{}, 角色IDs：{}", userId, roleIds);
            
            // 删除原有角色关联
            userRoleRepository.deleteByUserId(userId);
            
            // 添加新的角色关联
            if (roleIds != null && !roleIds.isEmpty()) {
                for (String roleId : roleIds) {
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(roleId);
                    userRole.setCreateTime(LocalDateTime.now());
                    userRoleRepository.save(userRole);
                }
            }
            
            log.info("用户 {} 角色分配成功", userId);
            
        } catch (Exception e) {
            log.error("分配用户角色失败：", e);
            throw new RuntimeException("分配用户角色失败：" + e.getMessage());
        }
    }
    
    @Override
    public boolean existsByUsername(String username, String customerId) {
        return userRepository.existsByUsernameAndCustomerIdAndDeletedFalse(username, customerId);
    }
    
    @Override
    public boolean existsByJobNumber(String jobNumber, String customerId) {
        return userRepository.existsByJobNumberAndCustomerIdAndDeletedFalse(jobNumber, customerId);
    }
    
    /**
     * 转换实体为VO
     *
     * @param user 用户实体
     * @return 用户VO
     */
    private UserVo convertToVo(SysUser user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        
        // 获取用户角色
        List<SysRole> roles = roleRepository.findByUserId(user.getId());
        userVo.setRoleIds(roles.stream().map(SysRole::getId).collect(Collectors.toList()));
        userVo.setRoleNames(roles.stream().map(SysRole::getName).collect(Collectors.toList()));
        
        // 获取用户权限
        List<String> permissions = permissionRepository.findByUserId(user.getId())
                .stream()
                .map(permission -> permission.getCode())
                .collect(Collectors.toList());
        userVo.setPermissions(permissions);
        
        return userVo;
    }
}