package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.AreaVo;
import com.proshine.system.dto.SearchAreaCondition;
import com.proshine.system.entity.Area;
import com.proshine.system.repository.AreaRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 区域服务实现类
 */
@Service
@Slf4j
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Override
    public List<Area> getAreaTree(String cstmId, String type) {
        // 如果没有指定客户域，使用当前用户的客户域
        if (!StringUtils.hasText(cstmId)) {
            cstmId = SecurityUtil.getCustomerId();
        }
        
        List<Area> all;
        if (StringUtils.hasText(type)) {
            all = areaRepository.findByCstmIdAndType(cstmId, type);
        } else {
            all = areaRepository.findByCstmId(cstmId);
        }
        
        return buildTree(all, null);
    }

    /**
     * 构建树形结构
     *
     * @param all 所有节点
     * @param parentId 父节点ID
     * @return 树形结构
     */
    private List<Area> buildTree(List<Area> all, String parentId) {
        List<Area> list = new ArrayList<>();
        for (Area area : all) {
            if (parentId == null) {
                if (!StringUtils.hasText(area.getParentId())) {
                    area.setChildren(buildTree(all, area.getId()));
                    list.add(area);
                }
            } else if (parentId.equals(area.getParentId())) {
                area.setChildren(buildTree(all, area.getId()));
                list.add(area);
            }
        }
        return list;
    }

    @Override
    public Area findById(String id) {
        Optional<Area> optional = areaRepository.findById(id);
        if (!optional.isPresent()) {
            throw new RuntimeException("区域不存在");
        }
        return optional.get();
    }

    @Override
    @Transactional
    public Area save(Area area) {
        // 设置客户域
        area.setCstmId(SecurityUtil.getCustomerId());
        
        // 计算路径深度
        Integer pathDeep = calculatePathDeep(area.getParentId());
        area.setPathDeep(pathDeep);
        
        // 清空ID，确保新增
        area.setId(null);
        
        return areaRepository.save(area);
    }

    @Override
    @Transactional
    public Area update(String id, Area area) {
        Optional<Area> optional = areaRepository.findById(id);
        if (!optional.isPresent()) {
            throw new RuntimeException("区域不存在");
        }
        
        Area exist = optional.get();
        
        // 更新字段
        if (StringUtils.hasText(area.getAreaName())) {
            exist.setAreaName(area.getAreaName());
        }
        if (StringUtils.hasText(area.getType())) {
            exist.setType(area.getType());
        }
        if (StringUtils.hasText(area.getExtend1())) {
            exist.setExtend1(area.getExtend1());
        }
        if (StringUtils.hasText(area.getExtend2())) {
            exist.setExtend2(area.getExtend2());
        }
        
        return areaRepository.save(exist);
    }

    @Override
    @Transactional
    public void delete(String id) {
        // 检查是否有子节点
        long count = areaRepository.countByParentId(id);
        if (count > 0) {
            throw new RuntimeException("存在子节点，无法删除");
        }
        areaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteCascade(String id) {
        // 级联删除节点及其所有子节点
        areaRepository.deleteByIdCascade(id);
    }

    @Override
    public Integer calculatePathDeep(String parentId) {
        if (!StringUtils.hasText(parentId)) {
            return 1; // 根节点深度为1
        }
        
        Optional<Area> parentOptional = areaRepository.findById(parentId);
        if (!parentOptional.isPresent()) {
            throw new RuntimeException("父节点不存在");
        }
        
        Area parent = parentOptional.get();
        return parent.getPathDeep() + 1;
    }

    @Override
    public ResponsePageDataEntity<AreaVo> searchArea(SearchAreaCondition condition) {
        // 设置客户域
        condition.setCustomerId(SecurityUtil.getCustomerId());
        
        // 构建查询条件
        Specification<Area> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 客户域条件
            if (StringUtils.hasText(condition.getCustomerId())) {
                predicates.add(criteriaBuilder.equal(root.get("cstmId"), condition.getCustomerId()));
            }
            
            // 区域名称模糊查询
            if (StringUtils.hasText(condition.getAreaName())) {
                predicates.add(criteriaBuilder.like(root.get("areaName"), "%" + condition.getAreaName() + "%"));
            }
            
            // 父节点ID
            if (StringUtils.hasText(condition.getParentId())) {
                predicates.add(criteriaBuilder.equal(root.get("parentId"), condition.getParentId()));
            }
            
            // 类型
            if (StringUtils.hasText(condition.getType())) {
                predicates.add(criteriaBuilder.equal(root.get("type"), condition.getType()));
            }
            
            // 路径深度
            if (condition.getPathDeep() != null) {
                predicates.add(criteriaBuilder.equal(root.get("pathDeep"), condition.getPathDeep()));
            }
            
            // 扩展字段1
            if (StringUtils.hasText(condition.getExtend1())) {
                predicates.add(criteriaBuilder.like(root.get("extend1"), "%" + condition.getExtend1() + "%"));
            }
            
            // 扩展字段2
            if (StringUtils.hasText(condition.getExtend2())) {
                predicates.add(criteriaBuilder.like(root.get("extend2"), "%" + condition.getExtend2() + "%"));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // 分页查询
        Pageable pageable = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize());
        Page<Area> page = areaRepository.findAll(spec, pageable);
        
        // 转换为VO
        List<AreaVo> voList = page.getContent().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
        
        // 构建返回结果
        ResponsePageDataEntity<AreaVo> result = new ResponsePageDataEntity<>();
        result.setTotal(page.getTotalElements());
        result.setRows(voList);
        
        return result;
    }

    @Override
    public AreaVo convertToVo(Area area) {
        if (area == null) {
            return null;
        }
        
        AreaVo vo = new AreaVo();
        vo.setId(area.getId());
        vo.setAreaName(area.getAreaName());
        vo.setParentId(area.getParentId());
        vo.setPathDeep(area.getPathDeep());
        vo.setType(area.getType());
        vo.setCstmId(area.getCstmId());
        vo.setCreateTime(area.getCreateTime());
        vo.setLastUpdateTime(area.getLastUpdateTime());
        vo.setExtend1(area.getExtend1());
        vo.setExtend2(area.getExtend2());
        
        // 转换子节点
        if (area.getChildren() != null && !area.getChildren().isEmpty()) {
            List<AreaVo> childrenVo = area.getChildren().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
            vo.setChildren(childrenVo);
        }
        
        // 设置子节点数量
        if (area.getChildren() != null) {
            vo.setCount(area.getChildren().size());
        } else {
            // 查询子节点数量
            long count = areaRepository.countByParentId(area.getId());
            vo.setCount((int) count);
        }
        
        return vo;
    }

    @Override
    public List<String> getAllChildAreaIds(String areaId) {
        List<String> result = new ArrayList<>();
        if (StringUtils.hasText(areaId)) {
            // 添加当前区域ID
            result.add(areaId);
            // 递归获取所有子区域ID
            collectChildAreaIds(areaId, result);
        }
        return result;
    }

    /**
     * 递归收集子区域ID
     *
     * @param parentId 父区域ID
     * @param result 结果列表
     */
    private void collectChildAreaIds(String parentId, List<String> result) {
        List<Area> children = areaRepository.findByParentId(parentId);
        for (Area child : children) {
            result.add(child.getId());
            // 递归获取子区域的子区域
            collectChildAreaIds(child.getId(), result);
        }
    }
}