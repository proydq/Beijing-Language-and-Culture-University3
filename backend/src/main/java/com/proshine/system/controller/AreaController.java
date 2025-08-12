package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.AreaVo;
import com.proshine.system.dto.SearchAreaCondition;
import com.proshine.system.entity.Area;
import com.proshine.system.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区域控制器
 */
@RestController
@RequestMapping("/area")
@Slf4j
public class AreaController {

    @Autowired
    private AreaService areaService;

    /**
     * 获取区域树形结构
     *
     * @param cstmId 客户域（可选）
     * @param type 区域类型（可选）
     * @return 区域树形结构
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('AREA_VIEW') or hasAuthority('AREA_MANAGE')")
    public ResponseEntity<List<Area>> getAreaTree(
            @RequestParam(required = false) String cstmId,
            @RequestParam(required = false) String type) {
        try {
            log.info("==========/area/tree=============cstmId:{}, type:{}", cstmId, type);
            List<Area> tree = areaService.getAreaTree(cstmId, type);
            return ResponseEntity.success(tree);
        } catch (Exception e) {
            log.error("获取区域树形结构失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 根据ID获取区域详情
     *
     * @param id 区域ID
     * @return 区域详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('AREA_VIEW') or hasAuthority('AREA_MANAGE')")
    public ResponseEntity<Area> findById(@PathVariable String id) {
        try {
            log.info("==========/area/{} [GET]=============id:{}", id, id);
            Area area = areaService.findById(id);
            return ResponseEntity.success(area);
        } catch (Exception e) {
            log.error("查询区域详情失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 新增区域
     *
     * @param area 区域信息
     * @return 保存后的区域信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('AREA_ADD') or hasAuthority('AREA_MANAGE')")
    public ResponseEntity<Area> save(@RequestBody Area area) {
        try {
            log.info("==========/area [POST]=============areaName:{}, type:{}", 
                    area.getAreaName(), area.getType());
            Area saved = areaService.save(area);
            return ResponseEntity.success(saved);
        } catch (Exception e) {
            log.error("新增区域失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 更新区域
     *
     * @param id 区域ID
     * @param area 区域信息
     * @return 更新后的区域信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('AREA_EDIT') or hasAuthority('AREA_MANAGE')")
    public ResponseEntity<Area> update(@PathVariable String id, @RequestBody Area area) {
        try {
            log.info("==========/area/{} [PUT]=============id:{}, areaName:{}", 
                    id, id, area.getAreaName());
            Area updated = areaService.update(id, area);
            return ResponseEntity.success(updated);
        } catch (Exception e) {
            log.error("更新区域失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 删除区域（仅删除当前节点，如果有子节点则不允许删除）
     *
     * @param id 区域ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('AREA_DELETE') or hasAuthority('AREA_MANAGE')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            log.info("==========/area/{} [DELETE]=============id:{}", id, id);
            areaService.delete(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除区域失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 级联删除区域（删除当前节点及其所有子节点）
     *
     * @param id 区域ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}/cascade")
    @PreAuthorize("hasAuthority('AREA_DELETE') or hasAuthority('AREA_MANAGE')")
    public ResponseEntity<Void> deleteCascade(@PathVariable String id) {
        try {
            log.info("==========/area/{}/cascade [DELETE]=============id:{}", id, id);
            areaService.deleteCascade(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("级联删除区域失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * 分页查询区域
     *
     * @param condition 查询条件
     * @return 分页结果
     */
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('AREA_VIEW') or hasAuthority('AREA_MANAGE')")
    public ResponseEntity<ResponsePageDataEntity<AreaVo>> searchArea(@RequestBody SearchAreaCondition condition) {
        try {
            log.info("==========/area/search [POST]=============areaName:{}, type:{}, pageNumber:{}, pageSize:{}", 
                    condition.getAreaName(), condition.getType(), condition.getPageNumber(), condition.getPageSize());
            ResponsePageDataEntity<AreaVo> result = areaService.searchArea(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("分页查询区域失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}