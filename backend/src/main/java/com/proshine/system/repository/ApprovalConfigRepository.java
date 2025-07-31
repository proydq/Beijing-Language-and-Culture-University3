package com.proshine.system.repository;

import com.proshine.system.entity.ApprovalConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 房间审批配置Repository
 */
@Repository
public interface ApprovalConfigRepository extends JpaRepository<ApprovalConfig, String>, JpaSpecificationExecutor<ApprovalConfig> {
    List<ApprovalConfig> findByRoomIdAndCstmIdOrderByApprovalLevel(String roomId, String cstmId);
}
