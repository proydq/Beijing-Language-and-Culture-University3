package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.AreaVo;
import com.proshine.system.dto.SearchAreaCondition;
import com.proshine.system.entity.Area;

import java.util.List;

/**
 * 区域服务接口
 */
public interface AreaService {

    /**
     * 获取区域树形结构
     *
     * @param cstmId 客户域（可选）
     * @param type 区域类型（可选）
     * @return 区域树
     */
    List<Area> getAreaTree(String cstmId, String type);

    /**
     * 根据ID获取区域详情
     *
     * @param id 区域ID
     * @return 区域详情
     */
    Area findById(String id);

    /**
     * 新增区域
     *
     * @param area 区域信息
     * @return 保存后的区域信息
     */
    Area save(Area area);

    /**
     * 更新区域
     *
     * @param id 区域ID
     * @param area 区域信息
     * @return 更新后的区域信息
     */
    Area update(String id, Area area);

    /**
     * 删除区域
     *
     * @param id 区域ID
     */
    void delete(String id);

    /**
     * 级联删除区域及其子节点
     *
     * @param id 区域ID
     */
    void deleteCascade(String id);

    /**
     * 计算路径深度
     *
     * @param parentId 父节点ID
     * @return 路径深度
     */
    Integer calculatePathDeep(String parentId);

    /**
     * 分页查询区域
     *
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<AreaVo> searchArea(SearchAreaCondition condition);

    /**
     * 实体转换为VO
     *
     * @param area 区域实体
     * @return 区域VO
     */
    AreaVo convertToVo(Area area);

    /**
     * 获取指定区域及其所有子区域的ID列表
     *
     * @param areaId 区域ID
     * @return 区域ID列表（包含自身和所有子区域）
     */
    List<String> getAllChildAreaIds(String areaId);
}