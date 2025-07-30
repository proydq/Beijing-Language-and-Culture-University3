package com.proshine.system.repository;

import com.proshine.system.entity.ClassroomApprovalLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 审批级别配置数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface ClassroomApprovalLevelRepository extends JpaRepository<ClassroomApprovalLevel, String>, JpaSpecificationExecutor<ClassroomApprovalLevel> {

    /**
     * 根据配置ID和客户域ID查询审批级别
     * 
     * @param configId 配置ID
     * @param cstmId 客户域ID
     * @return 审批级别列表
     */
    List<ClassroomApprovalLevel> findByConfigIdAndCstmIdOrderByLevelNumber(String configId, String cstmId);

    /**
     * 根据配置ID列表和客户域ID查询审批级别
     * 
     * @param configIds 配置ID列表
     * @param cstmId 客户域ID
     * @return 审批级别列表
     */
    List<ClassroomApprovalLevel> findByConfigIdInAndCstmIdOrderByLevelNumber(List<String> configIds, String cstmId);

    /**
     * 根据配置ID和客户域ID删除审批级别
     * 
     * @param configId 配置ID
     * @param cstmId 客户域ID
     */
    void deleteByConfigIdAndCstmId(String configId, String cstmId);

    /**
     * 根据配置ID列表和客户域ID批量删除审批级别
     * 
     * @param configIds 配置ID列表
     * @param cstmId 客户域ID
     */
    @Modifying
    @Query("DELETE FROM ClassroomApprovalLevel c WHERE c.configId IN :configIds AND c.cstmId = :cstmId")
    void deleteByConfigIdInAndCstmId(@Param("configIds") List<String> configIds, @Param("cstmId") String cstmId);

    /**
     * 根据客户域ID查询所有审批级别
     * 
     * @param cstmId 客户域ID
     * @return 审批级别列表
     */
    List<ClassroomApprovalLevel> findByCstmId(String cstmId);
}