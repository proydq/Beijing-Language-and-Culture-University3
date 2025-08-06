<template>
  <div class="access-records-table">
    <!-- 表格工具栏 -->
    <div class="table-toolbar">
      <div class="toolbar-left">
        <span class="total-count">共 {{ total }} 条记录</span>
      </div>
      <div class="toolbar-right">
        <el-button
          type="text"
          size="small"
          @click="$emit('refresh')"
          :loading="loading"
        >
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-table
      :data="records"
      :loading="loading"
      stripe
      border
      style="width: 100%"
      empty-text="暂无数据"
      :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
    >
      <el-table-column prop="accessTime" label="通行时间" width="160" sortable>
        <template #default="{ row }">
          <div class="time-cell">
            <el-icon class="time-icon"><Clock /></el-icon>
            {{ formatDateTime(row.accessTime) }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="roomName" label="教室信息" width="150">
        <template #default="{ row }">
          <div class="room-cell">
            <div class="room-name">{{ row.roomName }}</div>
            <div class="room-location">{{ row.buildingName }} {{ row.floor }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="name" label="用户信息" width="200">
        <template #default="{ row }">
          <div class="user-cell">
            <div class="user-main">
              <span class="user-name">{{ row.name }}</span>
              <el-tag
                :type="getUserTypeTagType(row.userType)"
                size="small"
                class="user-type-tag"
              >
                {{ row.userType }}
              </el-tag>
            </div>
            <div class="user-info">
              <span v-if="row.employeeId" class="employee-id">工号: {{ row.employeeId }}</span>
              <span v-if="row.department" class="department">{{ row.department }}</span>
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="openMethod" label="开门方式" width="120" align="center">
        <template #default="{ row }">
          <el-tag
            :type="getOpenMethodTagType(row.openMethod)"
            size="small"
          >
            <el-icon style="margin-right: 4px;">
              <component :is="getOpenMethodIcon(row.openMethod)" />
            </el-icon>
            {{ row.openMethod }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="accessType" label="通行类型" width="120" align="center">
        <template #default="{ row }">
          <el-tag
            :type="getAccessTypeTagType(row.accessType)"
            size="small"
          >
            {{ row.accessType }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="bookingName" label="关联预约" width="180">
        <template #default="{ row }">
          <div v-if="row.bookingName" class="booking-cell">
            <div class="booking-name">{{ row.bookingName }}</div>
            <div class="booking-period">{{ row.bookingPeriod }}</div>
          </div>
          <span v-else class="no-booking">-</span>
        </template>
      </el-table-column>

      <el-table-column prop="deviceName" label="门禁设备" width="150">
        <template #default="{ row }">
          <div class="device-cell">
            <el-icon class="device-icon"><Monitor /></el-icon>
            {{ row.deviceName || '-' }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="phone" label="联系方式" width="130">
        <template #default="{ row }">
          {{ row.phone || '-' }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="100" fixed="right" align="center">
        <template #default="{ row }">
          <el-button
            type="text"
            size="small"
            @click="$emit('view-detail', row)"
          >
            <el-icon><View /></el-icon>
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { 
  Clock, Refresh, Monitor, View,
  CreditCard, Camera, Button as ButtonIcon
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  records: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

// Emits
const emit = defineEmits(['page-change', 'size-change', 'view-detail', 'refresh'])

// 响应式数据
const currentPage = computed({
  get: () => props.currentPage,
  set: (value) => emit('page-change', value)
})

const pageSize = computed({
  get: () => props.pageSize,
  set: (value) => emit('size-change', value)
})

// 分页处理
const handlePageChange = (page) => {
  emit('page-change', page)
}

const handleSizeChange = (size) => {
  emit('size-change', size)
}

// 格式化时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取用户类型标签样式
const getUserTypeTagType = (userType) => {
  const typeMap = {
    '教师': 'primary',
    '学生': 'success',
    '管理员': 'warning'
  }
  return typeMap[userType] || 'info'
}

// 获取开门方式标签样式
const getOpenMethodTagType = (openMethod) => {
  const typeMap = {
    '刷卡': 'primary',
    '人脸识别': 'success',
    '按钮': 'warning'
  }
  return typeMap[openMethod] || 'info'
}

// 获取开门方式图标
const getOpenMethodIcon = (openMethod) => {
  const iconMap = {
    '刷卡': CreditCard,
    '人脸识别': Camera,
    '按钮': ButtonIcon
  }
  return iconMap[openMethod] || CreditCard
}

// 获取通行类型标签样式
const getAccessTypeTagType = (accessType) => {
  const typeMap = {
    '预约权限': 'primary',
    '永久权限': 'success'
  }
  return typeMap[accessType] || 'info'
}
</script>

<style scoped>
.access-records-table {
  background: white;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.total-count {
  font-size: 14px;
  color: #606266;
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

/* 表格单元格样式 */
.time-cell {
  display: flex;
  align-items: center;
}

.time-icon {
  margin-right: 6px;
  color: #909399;
}

.room-cell {
  line-height: 1.4;
}

.room-name {
  font-weight: 500;
  color: #303133;
}

.room-location {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.user-cell {
  line-height: 1.4;
}

.user-main {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.user-type-tag {
  font-size: 11px;
}

.user-info {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 8px;
}

.employee-id,
.department {
  white-space: nowrap;
}

.booking-cell {
  line-height: 1.4;
}

.booking-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.booking-period {
  font-size: 12px;
  color: #909399;
}

.no-booking {
  color: #c0c4cc;
}

.device-cell {
  display: flex;
  align-items: center;
}

.device-icon {
  margin-right: 6px;
  color: #909399;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  :deep(.el-table__header-wrapper),
  :deep(.el-table__body-wrapper) {
    overflow-x: auto;
  }
}

@media (max-width: 768px) {
  .table-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .toolbar-right {
    width: 100%;
    justify-content: flex-end;
  }

  .pagination-wrapper {
    overflow-x: auto;
  }

  :deep(.el-pagination) {
    justify-content: center;
    min-width: 600px;
  }
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table .el-table__cell) {
  padding: 12px 8px;
}

:deep(.el-table__row:hover > .el-table__cell) {
  background-color: #f5f7fa;
}

:deep(.el-table__empty-block) {
  padding: 60px 0;
}

:deep(.el-table__empty-text) {
  color: #909399;
}
</style>