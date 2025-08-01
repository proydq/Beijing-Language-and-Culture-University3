<template>
  <div class="approval-table">
    <el-table :data="approvalData" :loading="loading" border stripe style="width: 100%">
      <el-table-column prop="name" label="预约/借用名称" min-width="200" />
      <el-table-column prop="cycle" label="预约周期" min-width="200" />
      <el-table-column prop="description" label="描述" min-width="200">
        <template #default="{ row }">
          <span style="white-space: pre-wrap">{{ row.description || '/' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="applicantName" label="预约人" width="120" />
      <el-table-column prop="roomName" label="预约教室" width="150" />

      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleAction(row)">
            {{ viewOnlyMode || row.status !== 'PENDING' ? '查看详情' : '立即审批' }}
          </el-button>
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
export default {
  name: 'ApprovalTable',
  components: {},
  props: {
    approvalData: {
      type: Array,
      default: () => [],
    },
    loading: {
      type: Boolean,
      default: false,
    },
    viewOnlyMode: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['review', 'view'],
  setup(props, { emit }) {
    const handleAction = (row) => {
      if (props.viewOnlyMode || row.status !== 'PENDING') {
        emit('view', row)
      } else {
        emit('review', row)
      }
    }

    return { handleAction }
  },
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
