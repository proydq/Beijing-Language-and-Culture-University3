package com.proshine.system.repository;

import com.proshine.system.entity.ClassroomApprovalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教室审批配置数据访问层
 * 
 * @author system
 * @date 2024-01-01
 */
@Repository
public interface ClassroomApprovalConfigRepository extends JpaRepository<ClassroomApprovalConfig, String>, JpaSpecificationExecutor<ClassroomApprovalConfig> {

    /**
     * 根据房间ID和客户域ID查询审批配置
     * 
     * @param roomId 房间ID
     * @param cstmId 客户域ID
     * @return 审批配置
     */
    Optional<ClassroomApprovalConfig> findByRoomIdAndCstmId(String roomId, String cstmId);

    /**
     * 根据客户域ID查询所有审批配置
     * 
     * @param cstmId 客户域ID
     * @return 审批配置列表
     */
    List<ClassroomApprovalConfig> findByCstmId(String cstmId);

    /**
     * 根据房间ID列表和客户域ID查询审批配置
     * 
     * @param roomIds 房间ID列表
     * @param cstmId 客户域ID
     * @return 审批配置列表
     */
    List<ClassroomApprovalConfig> findByRoomIdInAndCstmId(List<String> roomIds, String cstmId);

    /**
     * 根据房间ID和客户域ID删除审批配置
     * 
     * @param roomId 房间ID
     * @param cstmId 客户域ID
     */
    void deleteByRoomIdAndCstmId(String roomId, String cstmId);

    /**
     * 根据房间ID列表和客户域ID批量删除审批配置
     * 
     * @param roomIds 房间ID列表
     * @param cstmId 客户域ID
     */
    @Modifying
    @Query("DELETE FROM ClassroomApprovalConfig c WHERE c.roomId IN :roomIds AND c.cstmId = :cstmId")
    void deleteByRoomIdInAndCstmId(@Param("roomIds") List<String> roomIds, @Param("cstmId") String cstmId);

    /**
     * 检查房间是否已存在审批配置
     * 
     * @param roomId 房间ID
     * @param cstmId 客户域ID
     * @return 是否存在
     */
    boolean existsByRoomIdAndCstmId(String roomId, String cstmId);
}