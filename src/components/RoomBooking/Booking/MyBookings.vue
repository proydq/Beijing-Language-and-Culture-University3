<template>
  <div class="my-bookings">
    <!-- 搜索区域 -->
    <div class="search-filters">
      <div class="filter-row">
        <div class="filter-item">
          <label>预约名称</label>
          <el-input v-model="searchForm.reservationName" placeholder="请输入预约名称" clearable />
        </div>
        <div class="filter-item">
          <label>审核状态</label>
          <el-select v-model="searchForm.approvalStatus" placeholder="请选择审核状态" clearable>
            <el-option label="审核中" value="审核中" />
            <el-option label="通过" value="通过" />
            <el-option label="拒绝" value="拒绝" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>使用状态</label>
          <el-select v-model="searchForm.usageStatus" placeholder="请选择使用状态" clearable>
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>预约时间</label>
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleDateChange"
          />
        </div>
        <div class="filter-actions">
          <el-button type="primary" @click="handleSearch">
            <el-icon><search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 预约表格 -->
    <div class="booking-table">
      <el-table :data="filteredData" border>
        <el-table-column prop="reservationName" label="预约名称" min-width="200" align="center" />
        <el-table-column prop="reservationPeriod" label="预约周期" min-width="250" align="center" />
        <el-table-column prop="description" label="描述" min-width="200" align="center">
          <template #default="{ row }">
            <el-tooltip placement="top" :content="row.description">
              <span class="ellipsis">{{ row.description }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="预约人" width="120" align="center" />
        <el-table-column prop="roomName" label="预约教室" width="150" align="center" />
        <el-table-column prop="approvalStatus" label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
              :type="getApprovalStatusType(row.approvalStatus)"
              size="small"
              class="status-tag"
              @click="handleViewAuditDetail(row)"
            >
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="usageStatus" label="使用状态" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.usageStatus === '/'">{{ row.usageStatus }}</span>
            <el-tag v-else :type="getUsageStatusType(row.usageStatus)" size="small">
              {{ row.usageStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">查看详情</el-button>
            <el-popconfirm
              v-if="
                (row.approvalStatus === '审核中' || row.approvalStatus === '通过') &&
                row.usageStatus === '未开始'
              "
              title="确定要取消该预约吗？取消后将无法恢复。"
              confirm-button-text="确认"
              cancel-button-text="取消"
              confirm-button-type="danger"
              @confirm="handleCancel(row)"
            >
              <template #reference>
                <el-button type="text" size="small" style="color: #f56c6c"> 取消预约 </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    <AuditDetailDialog v-model="auditDialogVisible" :record="currentRecord" />
    <ReservationDetailDialog
      v-model="detailDialogVisible"
      :detail="reservationDetail"
      @cancel="handleCancelReservation"
    />
  </div>
</template>

<script setup>
import { reactive, computed, watch, ref } from 'vue'
import { Search } from '@element-plus/icons-vue'
import AuditDetailDialog from './AuditDetailDialog.vue'
import ReservationDetailDialog from './ReservationDetailDialog.vue'

const props = defineProps({
  bookingData: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['cancel'])

const searchForm = reactive({
  reservationName: '',
  approvalStatus: '',
  usageStatus: '',
  dateRange: [],
  startDate: '',
  endDate: '',
})

// mock 预约基础信息数据
const bookingBaseInfo = {
  1: {
    reservationName: '【活动1】的教室借用',
    applicant: '王鹏',
    reservationPeriod: '2025.08.24 星期四 第三节次',
    description: '班级活动使用，需使用投影设备',
    participants: '张三, 李四',
    remark: '需要提前布置',
    approvalStatus: '审批中',
  },
  2: {
    reservationName: '【学生会】定期会议',
    applicant: '李明',
    reservationPeriod: '2025.08.25 星期五 第五节次',
    description: '学生组织定期内部会议',
    participants: '刘强, 陈伟',
    remark: '无',
    approvalStatus: '已通过',
  },
  3: {
    reservationName: '【外聘讲座】演讲厅借用',
    applicant: '赵敏',
    reservationPeriod: '2025.08.20 星期一 第九节次',
    description: '外聘教授举办讲座，要求提前布场',
    participants: '张三, 李四, 王五',
    remark: '讲座需准备扩音设备',
    approvalStatus: '已通过',
  },
  4: {
    reservationName: '【活动八定名】的教室借用',
    applicant: '王鹏',
    reservationPeriod: '2025.04.24 第四节次',
    description: '实验班借用智慧教室用于演示活动',
    participants: '李四, 王五',
    remark: '活动已取消',
    approvalStatus: '已拒绝',
  },
}

// mock 审核详情数据
const auditDetailData = {
  1: [
    {
      levelName: '自动审批',
      approvers: ['系统'],
      confirmedApprover: '系统',
      approvalTime: '2025-07-21 10:12:33',
      comment: '系统自动通过',
    },
    {
      levelName: '一级审批',
      approvers: ['王五', '李四'],
      confirmedApprover: '王五',
      approvalTime: '2025-07-21 11:20:00',
      comment: '同意：排课正常，无异议',
    },
    {
      levelName: '二级审批',
      approvers: ['刘晓旭'],
      confirmedApprover: '',
      approvalTime: null,
      comment: '',
    },
  ],
  2: [
    {
      levelName: '自动审批',
      approvers: ['系统'],
      confirmedApprover: '系统',
      approvalTime: '2025-08-21 09:00',
      comment: '系统自动通过',
    },
    {
      levelName: '一级审批',
      approvers: ['赵主管', '钱经理'],
      confirmedApprover: '钱经理',
      approvalTime: '2025-08-22 12:00',
      comment: '同意：排课正常，无异议',
    },
    {
      levelName: '二级审批',
      approvers: ['孙校长'],
      confirmedApprover: '孙校长',
      approvalTime: '2025-08-23 15:00',
      comment: '同意：排课正常，无异议',
    },
  ],
  3: [
    {
      levelName: '自动审批',
      approvers: ['系统'],
      confirmedApprover: '系统',
      approvalTime: '2025-08-15 08:00',
      comment: '系统自动通过',
    },
    {
      levelName: '一级审批',
      approvers: ['赵主管'],
      confirmedApprover: '赵主管',
      approvalTime: '2025-08-16 09:30',
      comment: '同意：排课正常，无异议',
    },
  ],
  4: [
    {
      levelName: '自动审批',
      approvers: ['系统'],
      confirmedApprover: '系统',
      approvalTime: '2025-04-20 09:00',
      comment: '系统自动通过',
    },
    {
      levelName: '一级审批',
      approvers: ['赵主管'],
      confirmedApprover: '赵主管',
      approvalTime: '2025-04-21 11:00',
      comment: '拒绝：活动已取消',
    },
  ],
}

const auditDialogVisible = ref(false)
// 当前弹窗展示的预约记录
const currentRecord = ref({})
const detailDialogVisible = ref(false)
const reservationDetail = ref({
  userName: '张三',
  reservationTitle: '张三的教室借用',
  borrowTime: '2025.04.24 第1节次、第2节次',
  borrowDesc: '我是描述内容...',
  participants: ['张三', '李四', '王五'],
  remark: '备注信息...',
  approvalSteps: [
    {
      levelName: '自动审批',
      approvers: ['系统'],
      confirmedApprover: '系统',
      approvalTime: '2022.07.14 14:23:56',
      comment: '系统自动通过',
    },
  ],
})

watch(
  () => searchForm.dateRange,
  (val) => {
    if (Array.isArray(val) && val.length === 2) {
      searchForm.startDate = val[0]
      searchForm.endDate = val[1]
    } else {
      searchForm.startDate = ''
      searchForm.endDate = ''
    }
  },
)

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
})

const mappedData = computed(() =>
  props.bookingData.map((item) => ({
    reservationName: item.reservationName || item.bookingName,
    reservationPeriod: item.reservationPeriod || item.bookingTime,
    description: item.description,
    applicantName: item.applicantName || item.applicant,
    roomName: item.roomName,
    approvalStatus: item.approvalStatus || item.auditStatus,
    usageStatus: item.usageStatus || item.useStatus,
  })),
)

const filteredData = computed(() => {
  let data = mappedData.value

  if (searchForm.reservationName) {
    data = data.filter((item) => item.reservationName?.includes(searchForm.reservationName))
  }

  if (searchForm.approvalStatus) {
    data = data.filter((item) => item.approvalStatus === searchForm.approvalStatus)
  }

  if (searchForm.usageStatus) {
    data = data.filter((item) => item.usageStatus === searchForm.usageStatus)
  }

  if (searchForm.startDate && searchForm.endDate) {
    const start = new Date(searchForm.startDate)
    const end = new Date(searchForm.endDate)
    data = data.filter((item) => {
      const dateStr = item.reservationPeriod?.split(' ')[0]
      if (!dateStr) return false
      const d = new Date(dateStr.replace(/\./g, '-'))
      return d >= start && d <= end
    })
  }

  pagination.total = data.length
  const startIndex = (pagination.currentPage - 1) * pagination.pageSize
  return data.slice(startIndex, startIndex + pagination.pageSize)
})

function getApprovalStatusType(status) {
  const map = {
    审核中: 'warning',
    通过: 'success',
    拒绝: 'danger',
    已取消: 'info',
  }
  return map[status] || 'info'
}

function getUsageStatusType(status) {
  const map = {
    未开始: 'info',
    进行中: 'warning',
    已结束: 'info',
  }
  return map[status] || 'info'
}

function handleSearch() {
  pagination.currentPage = 1
}

function handleReset() {
  Object.keys(searchForm).forEach((key) => {
    if (Array.isArray(searchForm[key])) {
      searchForm[key] = []
    } else {
      searchForm[key] = ''
    }
  })
  pagination.currentPage = 1
}

function handleSizeChange(val) {
  pagination.pageSize = val
  pagination.currentPage = 1
}

function handleCurrentChange(val) {
  pagination.currentPage = val
}

function handleView(row) {
  reservationDetail.value = {
    userName: row.applicantName,
    reservationTitle: row.reservationName,
    borrowTime: row.reservationPeriod,
    borrowDesc: row.description,
    participants: (bookingBaseInfo[row.id]?.participants || '张三, 李四, 王五').split(', '),
    remark: bookingBaseInfo[row.id]?.remark || '',
    approvalSteps: auditDetailData[row.id] || [],
  }
  detailDialogVisible.value = true
}

function handleCancel(row) {
  emit('cancel', row)
}

function handleCancelReservation() {
  emit('cancel', reservationDetail.value)
  detailDialogVisible.value = false
}

function handleViewAuditDetail(row) {
  const base = bookingBaseInfo[row.id] || {}
  currentRecord.value = {
    reservationTitle: base.reservationName || row.reservationName,
    userName: base.applicant || row.applicantName,
    borrowPeriodText: base.reservationPeriod || row.reservationPeriod,
    borrowDesc: base.description || row.description,
    participants: base.participants ? base.participants.split(', ') : [],
    remark: base.remark || '',
    approvalStatus: base.approvalStatus || row.approvalStatus,
    approvalSteps: auditDetailData[row.id] || [],
  }
  auditDialogVisible.value = true
}

function handleDateChange(val) {
  if (Array.isArray(val) && val.length === 2) {
    searchForm.startDate = val[0]
    searchForm.endDate = val[1]
  } else {
    searchForm.startDate = ''
    searchForm.endDate = ''
  }
}
</script>

<style scoped>
.my-bookings {
  flex: 1;
  padding: 20px;
  background: #f9f9f9;
}

.search-filters {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 200px;
}

.filter-item label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.booking-table {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.booking-table .el-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.booking-table .el-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.booking-table .el-table td {
  border-bottom: 1px solid #f0f0f0;
}

.booking-table .el-table tr:hover {
  background-color: #f5f7fa;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.status-tag {
  cursor: pointer;
}

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  width: 100%;
}
:deep(.el-table .cell) {
  padding: 0 !important;
}
</style>
