package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.OptionVO;
import com.proshine.system.dto.SearchPositionDTO;
import com.proshine.system.entity.SysPosition;
import com.proshine.system.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Position controller.
 */
@RestController
@RequestMapping("/api/position")
@Slf4j
public class PositionController {

    @Autowired
    private PositionService positionService;

    @Autowired
    private com.proshine.system.repository.SysPositionRepository positionRepository;

    /**
     * Search positions with pagination.
     */
    @PostMapping("/search")
    public ResponseEntity<ResponsePageDataEntity<SysPosition>> search(@RequestBody SearchPositionDTO condition) {
        try {
            log.info("==========/api/position/search=============");
            ResponsePageDataEntity<SysPosition> result = positionService.search(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("查询职务列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 职务下拉列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<OptionVO>> all() {
        List<OptionVO> list = positionRepository.findAll().stream()
                .map(p -> new OptionVO(p.getName(), p.getId()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.success(list);
    }

    /**
     * Create new position.
     */
    @PostMapping
    public ResponseEntity<SysPosition> create(@RequestBody SysPosition position) {
        try {
            log.info("==========/api/position [POST]=============");
            SysPosition saved = positionService.create(position);
            return ResponseEntity.success(saved);
        } catch (Exception e) {
            log.error("新增职务失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Update position.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SysPosition> update(@PathVariable String id, @RequestBody SysPosition position) {
        try {
            log.info("==========/api/position/{} [PUT]=============", id);
            SysPosition updated = positionService.update(id, position);
            return ResponseEntity.success(updated);
        } catch (Exception e) {
            log.error("修改职务失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Delete position.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            log.info("==========/api/position/{} [DELETE]=============", id);
            positionService.delete(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除职务失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Export position list as Excel.
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        positionService.exportExcel(response);
    }

    /**
     * Import position data from Excel.
     */
    @PostMapping("/import")
    public ResponseEntity<Void> importData(@RequestParam("file") MultipartFile file) {
        try {
            log.info("==========/api/position/import=============");
            positionService.importExcel(file);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("导入职务数据失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}
