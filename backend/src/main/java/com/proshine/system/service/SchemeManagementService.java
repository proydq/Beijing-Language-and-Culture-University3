package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 方案管理服务接口
 * 专门处理SchemeManagement.vue页面的教室管理相关业务
 * 
 * @author system
 * @date 2024-01-01
 */
public interface SchemeManagementService {

    /**
     * 分页查询教室列表
     * 仅查询roomTypeName为"教室"的数据
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<ClassroomVo> searchClassrooms(SearchClassroomCondition condition);

    /**
     * 添加教室
     * 同时在Room表中新增记录，roomTypeName设置为"教室"
     * 
     * @param classroomAddVo 教室添加信息
     * @return 添加结果
     */
    ClassroomAddResult addClassroom(ClassroomAddVo classroomAddVo);

    /**
     * 根据ID获取教室详情
     * 包括完整的审批人信息
     * 
     * @param id 教室ID
     * @return 教室详情
     */
    ClassroomVo getClassroomDetail(String id);

    /**
     * 更新教室信息
     * 包括基本信息和审批设置
     * 
     * @param classroomUpdateVo 教室更新信息
     */
    void updateClassroom(ClassroomUpdateVo classroomUpdateVo);

    /**
     * 批量删除教室
     * 
     * @param ids 教室ID列表
     */
    void batchDeleteClassrooms(List<String> ids);

    /**
     * 批量设置审批权限
     * 
     * @param batchApprovalVo 批量审批设置信息
     */
    void batchSetApproval(BatchApprovalVo batchApprovalVo);

    /**
     * 从Room表同步教室数据
     * 同步所有roomTypeName为"教室"的数据
     * 
     * @return 同步结果
     */
    SyncResult syncClassroomsFromRoom();

    /**
     * 分页查询人员列表
     * 用于审批人选择
     * 
     * @param condition 查询条件
     * @return 分页结果
     */
    ResponsePageDataEntity<PersonnelVo> searchPersonnel(SearchPersonnelCondition condition);

    /**
     * 导出教室数据
     * 
     * @param roomIds 教室ID列表
     * @param response HTTP响应
     */
    void exportClassrooms(List<String> roomIds, HttpServletResponse response);

    /**
     * 导入教室数据
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResult importClassrooms(MultipartFile file);
}