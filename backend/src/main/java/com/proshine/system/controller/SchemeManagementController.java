package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.service.SchemeManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 方案管理控制器
 * 专门处理SchemeManagement.vue页面的教室管理相关接口
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/scheme-management")
@Slf4j
public class SchemeManagementController {

    @Autowired
    private SchemeManagementService schemeManagementService;

    /**
     * 获取教室列表
     */
    @PostMapping("/classrooms/list")
    @PreAuthorize("hasAuthority('SCHEME_VIEW') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<ResponsePageDataEntity<ClassroomVo>> getClassroomList(
            @RequestBody @Valid SearchClassroomCondition condition) {
        try {
            ResponsePageDataEntity<ClassroomVo> result = schemeManagementService.searchClassrooms(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取教室列表失败", e);
            return ResponseEntity.fail("获取教室列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加教室
     */
    @PostMapping("/classrooms/add")
    @PreAuthorize("hasAuthority('SCHEME_ADD') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<ClassroomAddResult> addClassroom(
            @RequestBody @Valid ClassroomAddVo classroomAddVo) {
        try {
            ClassroomAddResult result = schemeManagementService.addClassroom(classroomAddVo);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("添加教室失败", e);
            return ResponseEntity.fail("添加教室失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取教室详情
     */
    @GetMapping("/classrooms/{id}")
    @PreAuthorize("hasAuthority('SCHEME_VIEW') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<ClassroomVo> getClassroomDetail(@PathVariable String id) {
        try {
            ClassroomVo result = schemeManagementService.getClassroomDetail(id);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取教室详情失败", e);
            return ResponseEntity.fail("获取教室详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新教室信息
     */
    @PutMapping("/classrooms/update")
    @PreAuthorize("hasAuthority('SCHEME_EDIT') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<Void> updateClassroom(
            @RequestBody @Valid ClassroomUpdateVo classroomUpdateVo) {
        try {
            schemeManagementService.updateClassroom(classroomUpdateVo);
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("更新教室信息失败", e);
            return ResponseEntity.fail("更新教室信息失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除教室（逻辑删除）
     */
    @PutMapping("/classrooms/batch-delete")
    @PreAuthorize("hasAuthority('SCHEME_DELETE') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<Void> batchDeleteClassrooms(
            @RequestBody @Valid BatchDeleteClassroomVo batchDeleteVo) {
        try {
            schemeManagementService.batchDeleteClassrooms(batchDeleteVo.getIds());
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("批量删除教室失败", e);
            return ResponseEntity.fail("批量删除教室失败: " + e.getMessage());
        }
    }

    /**
     * 批量设置审批权限
     */
    @PutMapping("/classrooms/batch-approval")
    @PreAuthorize("hasAuthority('SCHEME_EDIT') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<Void> batchSetApproval(
            @RequestBody @Valid BatchApprovalVo batchApprovalVo) {
        try {
            schemeManagementService.batchSetApproval(batchApprovalVo);
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("批量设置审批权限失败", e);
            return ResponseEntity.fail("批量设置审批权限失败: " + e.getMessage());
        }
    }

    /**
     * 手动同步教室数据
     */
    @PostMapping("/classrooms/sync")
    @PreAuthorize("hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<SyncResult> syncClassrooms() {
        try {
            SyncResult result = schemeManagementService.syncClassroomsFromRoom();
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("同步教室数据失败", e);
            return ResponseEntity.fail("同步教室数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取人员列表
     */
    @PostMapping("/personnel/list")
    @PreAuthorize("hasAuthority('SCHEME_VIEW') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<ResponsePageDataEntity<PersonnelVo>> getPersonnelList(
            @RequestBody @Valid SearchPersonnelCondition condition) {
        try {
            ResponsePageDataEntity<PersonnelVo> result = schemeManagementService.searchPersonnel(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取人员列表失败", e);
            return ResponseEntity.fail("获取人员列表失败: " + e.getMessage());
        }
    }

    /**
     * 导出教室数据
     */
    @PostMapping("/classrooms/export")
    @PreAuthorize("hasAuthority('SCHEME_VIEW') or hasAuthority('SCHEME_MANAGE')")
    public void exportClassrooms(
            @RequestBody @Valid ExportClassroomVo exportVo,
            HttpServletResponse response) {
        try {
            schemeManagementService.exportClassrooms(exportVo.getRoomIds(), response);
        } catch (Exception e) {
            log.error("导出教室数据失败", e);
            throw new RuntimeException("导出教室数据失败: " + e.getMessage());
        }
    }

    /**
     * 导入教室数据
     */
    @PostMapping("/classrooms/import")
    @PreAuthorize("hasAuthority('SCHEME_ADD') or hasAuthority('SCHEME_MANAGE')")
    public ResponseEntity<ImportResult> importClassrooms(
            @RequestParam("file") MultipartFile file) {
        try {
            ImportResult result = schemeManagementService.importClassrooms(file);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("导入教室数据失败", e);
            return ResponseEntity.fail("导入教室数据失败: " + e.getMessage());
        }
    }
}