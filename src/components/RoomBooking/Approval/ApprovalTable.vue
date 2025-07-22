<template>
  <div class="approval-table">
    <el-table 
      :data="approvalData" 
      :loading="loading"
      border
      stripe
      style="width: 100%"
    >
      <el-table-column prop="bookingName" label="预约名称" min-width="200">
        <template #default="scope">
          <div class="booking-name">
            <span class="name">{{ scope.row.bookingName }}</span>
            <el-tag 
              v-if="scope.row.urgency === '紧急'" 
              type="danger" 
              size="small"
              style="margin-left: 8px;"
            >
              紧急
            </el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="applicant" label="申请人" width="120">
        <template #default="scope">
          <div class="applicant-info">
            <span class="name">{{ scope.row.applicant }}</span>
            <el-tag 
              :type="getApplicantTypeColor(scope.row.applicantType)" 
              size="small"
              style="margin-top: 2px;"
            >
              {{ scope.row.applicantType }}
            </el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="roomName" label="房间名称" width="150" />

      <el-table-column prop="bookingTime" label="预约时间" min-width="180">
        <template #default="scope">
          <div class="time-info">
            <el-icon><clock /></el-icon>
            <span>{{ scope.row.bookingTime }}</span>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="applyTime" label="申请时间" width="160">
        <template #default="scope">
          <span class="apply-time">{{ formatTime(scope.row.applyTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column prop="reason" label="申请理由" min-width="200">
        <template #default="scope">
          <el-tooltip 
            :content="scope.row.reason" 
            placement="top"
            :disabled="scope.row.reason.length <= 30"
          >
            <span class="reason-text">
              {{ scope.row.reason.length > 30 ? scope.row.reason.substring(0, 30) + '...' : scope.row.reason }}
            </span>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag 
            :type="getStatusType(scope.row.status)" 
            size="small"
          >
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              @click="$emit('view-detail', scope.row)"
            >
              <el-icon><view /></el-icon>
              详情
            </el-button>
            
            <template v-if="scope.row.status === '待审批'">
              <el-button 
                type="success" 
                size="small" 
                @click="$emit('approve', scope.row)"
              >
                <el-icon><check /></el-icon>
                通过
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="$emit('reject', scope.row)"
              >
                <el-icon><close /></el-icon>
                拒绝
              </el-button>
            </template>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空数据提示 -->
    <div v-if="!approvalData.length && !loading" class="empty-data">
      <el-empty description="暂无审批数据" />
    </div>
  </div>
</template>

<script>
import { Clock, View, Check, Close } from '@element-plus/icons-vue'

export default {
  name: 'ApprovalTable',
  components: {
    Clock,
    View,
    Check,
    Close
  },
  props: {
    approvalData: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['approve', 'reject', 'view-detail'],
  setup() {
    const getStatusType = (status) => {
      const statusMap = {
        '待审批': 'warning',
        '已通过': 'success',
        '已拒绝': 'danger'
      }
      return statusMap[status] || 'info'
    }

    const getApplicantTypeColor = (type) => {
      const typeMap = {
        '教师': 'success',
        '学生': 'primary',
        '管理员': 'warning'
      }
      return typeMap[type] || 'info'
    }

    const formatTime = (timeStr) => {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    return {
      getStatusType,
      getApplicantTypeColor,
      formatTime
    }
  }
}
</script>

<style scoped>
.approval-table {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.approval-table .el-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.approval-table .el-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.approval-table .el-table td {
  border-bottom: 1px solid #f0f0f0;
}

.approval-table .el-table tr:hover {
  background-color: #f5f7fa;
}

.booking-name {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.booking-name .name {
  font-weight: 500;
}

.applicant-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.applicant-info .name {
  font-weight: 500;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
}

.apply-time {
  font-size: 13px;
  color: #999;
}

.reason-text {
  line-height: 1.4;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  margin: 0;
}

.empty-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons .el-button {
    width: 100%;
    margin: 2px 0;
  }
}
</style>