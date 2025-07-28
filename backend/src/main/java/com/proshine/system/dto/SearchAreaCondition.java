package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 区域查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchAreaCondition extends SearchBaseCondition {

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 区域类型
     */
    private String type;

    /**
     * 路径深度
     */
    private Integer pathDeep;

    /**
     * 扩展字段1
     */
    private String extend1;

    /**
     * 扩展字段2
     */
    private String extend2;
}