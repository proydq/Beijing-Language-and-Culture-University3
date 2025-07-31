package com.proshine.system.service;

import com.proshine.system.entity.ApprovalConfig;

import java.util.List;

/**
 * 房间审批配置服务接口
 */
public interface ApprovalConfigService {
    List<ApprovalConfig> getByRoomId(String roomId);

    void setRoomApprovers(String roomId, List<ApprovalLevel> levels);

    class ApprovalLevel {
        private Integer level;
        private String approverId;
        private String approverName;
        private String approverType;

        public ApprovalLevel(Integer level, String approverId, String approverName, String approverType) {
            this.level = level;
            this.approverId = approverId;
            this.approverName = approverName;
            this.approverType = approverType;
        }

        public Integer getLevel() { return level; }
        public String getApproverId() { return approverId; }
        public String getApproverName() { return approverName; }
        public String getApproverType() { return approverType; }
    }
}
