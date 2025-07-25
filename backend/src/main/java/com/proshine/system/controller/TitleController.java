package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.OptionVO;
import com.proshine.system.dto.SearchTitleDTO;
import com.proshine.system.dto.TitleVo;
import com.proshine.system.entity.SysTitle;
import com.proshine.system.service.TitleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Title controller.
 */
@RestController
@RequestMapping("/api/title")
@Slf4j
public class TitleController {

    @Autowired
    private TitleService titleService;

    @Autowired
    private com.proshine.system.repository.SysTitleRepository titleRepository;

    /**
     * Search titles with pagination.
     */
    @PostMapping("/search")
    public ResponseEntity<ResponsePageDataEntity<TitleVo>> search(@RequestBody SearchTitleDTO condition) {
        try {
            log.info("==========/api/title/search=============");
            ResponsePageDataEntity<TitleVo> result = titleService.search(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("查询职称列表失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Create new title.
     */
    @PostMapping
    public ResponseEntity<SysTitle> create(@RequestBody SysTitle title) {
        try {
            log.info("==========/api/title [POST]=============");
            SysTitle saved = titleService.create(title);
            return ResponseEntity.success(saved);
        } catch (Exception e) {
            log.error("新增职称失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Update title.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SysTitle> update(@PathVariable String id, @RequestBody SysTitle title) {
        try {
            log.info("==========/api/title/{} [PUT]=============", id);
            SysTitle updated = titleService.update(id, title);
            return ResponseEntity.success(updated);
        } catch (Exception e) {
            log.error("编辑职称失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Delete title.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            log.info("==========/api/title/{} [DELETE]=============", id);
            titleService.delete(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除职称失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Export excel.
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        titleService.exportExcel(response);
    }

    /**
     * Import excel.
     */
    @PostMapping("/import")
    public ResponseEntity<Void> importData(@RequestParam("file") MultipartFile file) {
        try {
            log.info("==========/api/title/import=============");
            titleService.importExcel(file);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("导入职称数据失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 职称下拉列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<OptionVO>> all() {
        List<OptionVO> list = titleRepository.findAll().stream()
                .map(t -> new OptionVO(t.getName(), t.getId()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.success(list);
    }
}
