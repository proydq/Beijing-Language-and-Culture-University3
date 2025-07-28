package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 区域视图对象
 */
@Data
public class AreaVo {

    /**
     * 主键UUID
     */
    private String id;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 路径深度
     */
    private Integer pathDeep;

    /**
     * 类型（building/floor/room）
     */
    private String type;

    /**
     * 客户域
     */
    private String cstmId;

    /**
     * 创建时间（时间戳）
     */
    private Long createTime;

    /**
     * 最后更新时间（时间戳）
     */
    private Long lastUpdateTime;

    /**
     * 扩展字段1
     */
    private String extend1;

    /**
     * 扩展字段2
     */
    private String extend2;

    /**
     * 子节点列表
     */
    private List<AreaVo> children;

    /**
     * 节点统计数量（可用于显示子节点数量等）
     */
    private Integer count;
}