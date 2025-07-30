package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.ClassroomVo;
import com.proshine.system.dto.SearchClassroomCondition;

import java.util.List;
import java.util.Map;

/**
 * 回收站服务接口
 * 
 * @author system
 * @date 2024-01-01
 */
public interface RecycleBinService {

    /**
     * 搜索已删除的教室
     * 
     * @param condition 搜索条件
     * @return 分页结果
     */
    ResponsePageDataEntity<ClassroomVo> searchDeletedClassrooms(SearchClassroomCondition condition);

    /**
     * 恢复教室
     * 
     * @param id 教室ID
     */
    void restoreClassroom(String id);

    /**
     * 批量恢复教室
     * 
     * @param ids 教室ID列表
     */
    void batchRestoreClassrooms(List<String> ids);

    /**
     * 永久删除教室
     * 
     * @param id 教室ID
     */
    void permanentDeleteClassroom(String id);

    /**
     * 批量永久删除教室
     * 
     * @param ids 教室ID列表
     */
    void batchPermanentDeleteClassrooms(List<String> ids);

    /**
     * 清空回收站
     */
    void clearRecycleBin();

    /**
     * 获取回收站统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getRecycleBinStats();
}