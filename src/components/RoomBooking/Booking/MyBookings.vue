<template>
  <div class="my-bookings">
    <!-- 搜索区域 -->
    <div class="search-filters">
      <div class="filter-row">
        <div class="filter-item">
          <label>预约名称</label>
          <el-input
            v-model="searchForm.reservationName"
            placeholder="请输入预约名称"
            clearable
          />
        </div>
        <div class="filter-item">
          <label>审核状态</label>
          <el-select
            v-model="searchForm.approvalStatus"
            placeholder="请选择审核状态"
            clearable
          >
            <el-option label="审核中" value="审核中" />
            <el-option label="通过" value="通过" />
            <el-option label="拒绝" value="拒绝" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>使用状态</label>
          <el-select
            v-model="searchForm.usageStatus"
            placeholder="请选择使用状态"
            clearable
          >
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
        <el-table-column prop="reservationName" label="预约名称" min-width="200" />
        <el-table-column prop="reservationPeriod" label="预约周期" min-width="250" />
        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="{ row }">
            <el-tooltip placement="top" :content="row.description">
              <span class="ellipsis">{{ row.description }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="预约人" width="120" />
        <el-table-column prop="roomName" label="预约教室" width="150" />
        <el-table-column prop="approvalStatus" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getApprovalStatusType(row.approvalStatus)" size="small">
              {{ row.approvalStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="usageStatus" label="使用状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getUsageStatusType(row.usageStatus)" size="small">
              {{ row.usageStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">查看详情</el-button>
            <el-button
              v-if="(row.approvalStatus === '审核中' || row.approvalStatus === '通过') && row.usageStatus === '未开始'"
              type="text"
              size="small"
              style="color: #f56c6c"
              @click="handleCancel(row)"
            >
              取消预约
            </el-button>
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
  </div>
</template>

<script setup>
import { reactive, computed, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  bookingData: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['view', 'cancel'])

const searchForm = reactive({
  reservationName: '',
  approvalStatus: '',
  usageStatus: '',
  dateRange: [],
  startDate: '',
  endDate: ''
})

watch(
  () => searchForm.dateRange,
  val => {
    if (Array.isArray(val) && val.length === 2) {
      searchForm.startDate = val[0]
      searchForm.endDate = val[1]
    } else {
      searchForm.startDate = ''
      searchForm.endDate = ''
    }
  }
)

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const mappedData = computed(() =>
  props.bookingData.map(item => ({
    reservationName: item.reservationName || item.bookingName,
    reservationPeriod: item.reservationPeriod || item.bookingTime,
    description: item.description,
    applicantName: item.applicantName || item.applicant,
    roomName: item.roomName,
    approvalStatus: item.approvalStatus || item.auditStatus,
    usageStatus: item.usageStatus || item.useStatus
  }))
)

const filteredData = computed(() => {
  let data = mappedData.value

  if (searchForm.reservationName) {
    data = data.filter(item =>
      item.reservationName?.includes(searchForm.reservationName)
    )
  }

  if (searchForm.approvalStatus) {
    data = data.filter(item => item.approvalStatus === searchForm.approvalStatus)
  }

  if (searchForm.usageStatus) {
    data = data.filter(item => item.usageStatus === searchForm.usageStatus)
  }

  if (searchForm.startDate && searchForm.endDate) {
    const start = new Date(searchForm.startDate)
    const end = new Date(searchForm.endDate)
    data = data.filter(item => {
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
    拒绝: 'danger'
  }
  return map[status] || 'info'
}

function getUsageStatusType(status) {
  const map = {
    未开始: 'info',
    进行中: 'warning',
    已结束: 'success'
  }
  return map[status] || 'info'
}

function handleSearch() {
  pagination.currentPage = 1
}

function handleReset() {
  Object.keys(searchForm).forEach(key => {
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
  emit('view', row)
}

function handleCancel(row) {
  emit('cancel', row)
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

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  width: 100%;
}
</style>