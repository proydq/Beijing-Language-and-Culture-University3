package com.proshine.system.service.impl;

import com.proshine.system.entity.ApprovalConfig;
import com.proshine.system.repository.ApprovalConfigRepository;
import com.proshine.system.security.SecurityUtil;
import com.proshine.system.service.ApprovalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 房间审批配置服务实现
 */
@Service
public class ApprovalConfigServiceImpl implements ApprovalConfigService {

    @Autowired
    private ApprovalConfigRepository approvalConfigRepository;

    @Override
    public List<ApprovalConfig> getByRoomId(String roomId) {
        String cstmId = SecurityUtil.getCustomerId();
        return approvalConfigRepository.findByRoomIdAndCstmIdOrderByApprovalLevel(roomId, cstmId);
    }

    @Override
    @Transactional
    public void setRoomApprovers(String roomId, List<ApprovalLevel> levels) {
        String cstmId = SecurityUtil.getCustomerId();
        approvalConfigRepository.deleteAll(approvalConfigRepository.findByRoomIdAndCstmIdOrderByApprovalLevel(roomId, cstmId));
        List<ApprovalConfig> configs = new ArrayList<>();
        if (levels != null) {
            for (ApprovalLevel level : levels) {
                ApprovalConfig config = new ApprovalConfig();
                config.setCstmId(cstmId);
                config.setRoomId(roomId);
                config.setApprovalLevel(level.getLevel());
                config.setApproverId(level.getApproverId());
                config.setApproverName(level.getApproverName());
                config.setApproverType(level.getApproverType());
                configs.add(config);
            }
            approvalConfigRepository.saveAll(configs);
        }
    }
}
