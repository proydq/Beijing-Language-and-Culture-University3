package com.proshine.system.user.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.entity.SysOrganization;
import com.proshine.system.entity.SysUser;
import com.proshine.system.repository.SysOrganizationRepository;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.user.dto.SearchUserCondition;
import com.proshine.system.user.dto.UserSaveRequest;
import com.proshine.system.user.dto.UserVO;
import com.proshine.system.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private SysOrganizationRepository organizationRepository;

    @Override
    public ResponsePageDataEntity<UserVO> search(SearchUserCondition condition) {
        Specification<SysUser> spec = buildSpec(condition);
        Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        Page<SysUser> page = userRepository.findAll(spec, pageable);
        List<UserVO> rows = page.getContent().stream().map(this::toVo).collect(Collectors.toList());
        ResponsePageDataEntity<UserVO> result = new ResponsePageDataEntity<>();
        result.setTotal(page.getTotalElements());
        result.setRows(rows);
        return result;
    }

    private Specification<SysUser> buildSpec(SearchUserCondition condition) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("deleted"), false));
            if (StringUtils.hasText(condition.getRealName())) {
                predicates.add(cb.like(root.get("realName"), "%" + condition.getRealName() + "%"));
            }
            if (StringUtils.hasText(condition.getPhone())) {
                predicates.add(cb.like(root.get("phone"), "%" + condition.getPhone() + "%"));
            }
            if (StringUtils.hasText(condition.getJobNumber())) {
                predicates.add(cb.like(root.get("jobNumber"), "%" + condition.getJobNumber() + "%"));
            }
            if (StringUtils.hasText(condition.getStatus())) {
                predicates.add(cb.equal(root.get("status"), SysUser.Status.valueOf(condition.getStatus())));
            }
            if (StringUtils.hasText(condition.getOrganizationId())) {
                List<String> ids = collectOrgIds(condition.getOrganizationId());
                if (!ids.isEmpty()) {
                    predicates.add(root.get("departmentId").in(ids));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<String> collectOrgIds(String orgId) {
        List<SysOrganization> all = organizationRepository.findAll();
        Set<String> ids = new HashSet<>();
        collect(ids, all, orgId);
        return new ArrayList<>(ids);
    }

    private void collect(Set<String> ids, List<SysOrganization> all, String id) {
        ids.add(id);
        for (SysOrganization org : all) {
            if (id.equals(org.getParentId())) {
                collect(ids, all, org.getId());
            }
        }
    }

    @Override
    public UserVO findById(String id) {
        return userRepository.findById(id)
                .filter(u -> !Boolean.TRUE.equals(u.getDeleted()))
                .map(this::toVo)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    @Transactional
    public UserVO create(UserSaveRequest request) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(request, user);
        user.setStatus(SysUser.Status.NORMAL);
        user.setDeleted(false);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        SysUser saved = userRepository.save(user);
        return toVo(saved);
    }

    @Override
    @Transactional
    public UserVO update(String id, UserSaveRequest request) {
        SysUser user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getGender())) {
            user.setGender(request.getGender());
        }
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StringUtils.hasText(request.getJobNumber())) {
            user.setJobNumber(request.getJobNumber());
        }
        if (StringUtils.hasText(request.getDepartmentId())) {
            user.setDepartmentId(request.getDepartmentId());
        }
        if (StringUtils.hasText(request.getPositionId())) {
            user.setPositionId(request.getPositionId());
        }
        if (StringUtils.hasText(request.getTitleId())) {
            user.setTitleId(request.getTitleId());
        }
        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (StringUtils.hasText(request.getFaceImageUrl())) {
            user.setFaceImageUrl(request.getFaceImageUrl());
        }
        if (StringUtils.hasText(request.getCardNumber())) {
            user.setCardNumber(request.getCardNumber());
        }
        if (StringUtils.hasText(request.getAttendanceNumber())) {
            user.setAttendanceNumber(request.getAttendanceNumber());
        }
        user.setUpdateTime(LocalDateTime.now());
        SysUser updated = userRepository.save(user);
        return toVo(updated);
    }

    @Override
    @Transactional
    public void delete(String id) {
        userRepository.findById(id).ifPresent(u -> {
            u.setDeleted(true);
            u.setUpdateTime(LocalDateTime.now());
            userRepository.save(u);
        });
    }

    @Override
    public void exportExcel(SearchUserCondition condition, HttpServletResponse response) {
        Specification<SysUser> spec = buildSpec(condition);
        List<SysUser> list = userRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createTime"));
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("users");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("姓名");
            header.createCell(1).setCellValue("手机号");
            header.createCell(2).setCellValue("工号");
            header.createCell(3).setCellValue("性别");
            header.createCell(4).setCellValue("部门");
            header.createCell(5).setCellValue("职务");
            header.createCell(6).setCellValue("职称");
            header.createCell(7).setCellValue("状态");
            header.createCell(8).setCellValue("创建时间");
            int rowIdx = 1;
            for (SysUser u : list) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(Optional.ofNullable(u.getRealName()).orElse(""));
                row.createCell(1).setCellValue(Optional.ofNullable(u.getPhone()).orElse(""));
                row.createCell(2).setCellValue(Optional.ofNullable(u.getJobNumber()).orElse(""));
                row.createCell(3).setCellValue(Optional.ofNullable(u.getGender()).orElse(""));
                row.createCell(4).setCellValue(Optional.ofNullable(u.getDepartmentName()).orElse(""));
                row.createCell(5).setCellValue(Optional.ofNullable(u.getPositionName()).orElse(""));
                row.createCell(6).setCellValue(Optional.ofNullable(u.getTitleName()).orElse(""));
                row.createCell(7).setCellValue(u.getStatus() != null ? u.getStatus().name() : "");
                row.createCell(8).setCellValue(u.getCreateTime() != null ? u.getCreateTime().toString() : "");
            }
            String fileName = URLEncoder.encode("users.xlsx", "UTF-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            log.error("导出用户列表失败", e);
            throw new RuntimeException("导出用户列表失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void importExcel(MultipartFile file) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i < rows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                SysUser user = new SysUser();
                user.setRealName(row.getCell(0).getStringCellValue());
                user.setPhone(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null);
                user.setJobNumber(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null);
                user.setGender(row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null);
                user.setDepartmentName(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null);
                user.setPositionName(row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null);
                user.setTitleName(row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null);
                user.setStatus(SysUser.Status.NORMAL);
                user.setDeleted(false);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(LocalDateTime.now());
                userRepository.save(user);
            }
        } catch (IOException e) {
            log.error("导入用户数据失败", e);
            throw new RuntimeException("导入用户数据失败：" + e.getMessage());
        }
    }

    private UserVO toVo(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setJobNumber(user.getJobNumber());
        vo.setGender(user.getGender());
        vo.setDepartmentName(user.getDepartmentName());
        vo.setPositionName(user.getPositionName());
        vo.setTitleName(user.getTitleName());
        vo.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
