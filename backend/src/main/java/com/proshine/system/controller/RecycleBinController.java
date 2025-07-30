package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import com.proshine.system.service.RecycleBinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 回收站控制器
 * 处理RecycleBin.vue页面的回收站管理相关接口
 * 
 * @author system
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/recycle-bin")
@Slf4j
public class RecycleBinController {

    @Autowired
    private RecycleBinService recycleBinService;

    /**
     * 搜索已删除的教室列表
     */
    @PostMapping("/classrooms/search")
    public ResponseEntity<ResponsePageDataEntity<ClassroomVo>> searchDeletedClassrooms(
            @RequestBody @Valid SearchClassroomCondition condition) {
        try {
            ResponsePageDataEntity<ClassroomVo> result = recycleBinService.searchDeletedClassrooms(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("搜索已删除教室失败", e);
            return ResponseEntity.fail("搜索已删除教室失败: " + e.getMessage());
        }
    }

    /**
     * 恢复单个教室
     */
    @PutMapping("/classrooms/{id}/restore")
    public ResponseEntity<Void> restoreClassroom(@PathVariable String id) {
        try {
            recycleBinService.restoreClassroom(id);
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("恢复教室失败", e);
            return ResponseEntity.fail("恢复教室失败: " + e.getMessage());
        }
    }

    /**
     * 批量恢复教室
     */
    @PutMapping("/classrooms/batch-restore")
    public ResponseEntity<Void> batchRestoreClassrooms(
            @RequestBody @Valid BatchDeleteClassroomVo batchRestoreVo) {
        try {
            recycleBinService.batchRestoreClassrooms(batchRestoreVo.getIds());
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("批量恢复教室失败", e);
            return ResponseEntity.fail("批量恢复教室失败: " + e.getMessage());
        }
    }

    /**
     * 永久删除单个教室
     */
    @DeleteMapping("/classrooms/{id}")
    public ResponseEntity<Void> permanentDeleteClassroom(@PathVariable String id) {
        try {
            recycleBinService.permanentDeleteClassroom(id);
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("永久删除教室失败", e);
            return ResponseEntity.fail("永久删除教室失败: " + e.getMessage());
        }
    }

    /**
     * 批量永久删除教室
     */
    @DeleteMapping("/classrooms/batch-delete")
    public ResponseEntity<Void> batchPermanentDeleteClassrooms(
            @RequestBody @Valid BatchDeleteClassroomVo batchDeleteVo) {
        try {
            recycleBinService.batchPermanentDeleteClassrooms(batchDeleteVo.getIds());
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("批量永久删除教室失败", e);
            return ResponseEntity.fail("批量永久删除教室失败: " + e.getMessage());
        }
    }

    /**
     * 清空回收站
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearRecycleBin() {
        try {
            recycleBinService.clearRecycleBin();
            return ResponseEntity.success();
        } catch (Exception e) {
            log.error("清空回收站失败", e);
            return ResponseEntity.fail("清空回收站失败: " + e.getMessage());
        }
    }

    /**
     * 获取回收站统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRecycleBinStats() {
        try {
            Map<String, Object> stats = recycleBinService.getRecycleBinStats();
            return ResponseEntity.success(stats);
        } catch (Exception e) {
            log.error("获取回收站统计信息失败", e);
            return ResponseEntity.fail("获取回收站统计信息失败: " + e.getMessage());
        }
    }
}