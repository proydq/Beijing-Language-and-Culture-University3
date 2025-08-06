<template>
  <el-dialog
    v-model="dialogVisible"
    title="出入记录详情"
    width="600px"
    :before-close="handleClose"
  >
    <div v-if="record" class="record-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><InfoFilled /></el-icon>
          基本信息
        </h4>
        <div class="detail-grid">
          <div class="detail-item">
            <label>记录ID:</label>
            <span>{{ record.id }}</span>
          </div>
          <div class="detail-item">
            <label>通行时间:</label>
            <span class="highlight-time">{{ formatDateTime(record.accessTime) }}</span>
          </div>
          <div class="detail-item">
            <label>创建时间:</label>
            <span>{{ formatDateTime(record.createTime) }}</span>
          </div>
        </div>
      </div>

      <!-- 教室信息 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><House /></el-icon>
          教室信息
        </h4>
        <div class="detail-grid">
          <div class="detail-item">
            <label>教室名称:</label>
            <span class="room-name">{{ record.roomName }}</span>
          </div>
          <div class="detail-item">
            <label>楼栋:</label>
            <span>{{ record.buildingName }}</span>
          </div>
          <div class="detail-item">
            <label>楼层:</label>
            <span>{{ record.floor }}</span>
          </div>
        </div>
      </div>

      <!-- 用户信息 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><User /></el-icon>
          用户信息
        </h4>
        <div class="detail-grid">
          <div class="detail-item">
            <label>姓名:</label>
            <span class="user-name">{{ record.name }}</span>
          </div>
          <div class="detail-item">
            <label>性别:</label>
            <span>{{ record.gender || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>用户类型:</label>
            <el-tag
              :type="getUserTypeTagType(record.userType)"
              size="small"
            >
              {{ record.userType }}
            </el-tag>
          </div>
          <div class="detail-item">
            <label>工号:</label>
            <span>{{ record.employeeId || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>联系方式:</label>
            <span>{{ record.phone || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>部门:</label>
            <span>{{ record.department || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 通行信息 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><Key /></el-icon>
          通行信息
        </h4>
        <div class="detail-grid">
          <div class="detail-item">
            <label>开门方式:</label>
            <el-tag
              :type="getOpenMethodTagType(record.openMethod)"
              size="small"
            >
              <el-icon style="margin-right: 4px;">
                <component :is="getOpenMethodIcon(record.openMethod)" />
              </el-icon>
              {{ record.openMethod }}
            </el-tag>
          </div>
          <div class="detail-item">
            <label>通行类型:</label>
            <el-tag
              :type="getAccessTypeTagType(record.accessType)"
              size="small"
            >
              {{ record.accessType }}
            </el-tag>
          </div>
          <div class="detail-item">
            <label>门禁设备:</label>
            <span>{{ record.deviceName || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>设备ID:</label>
            <span class="device-id">{{ record.deviceId || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 预约信息 -->
      <div v-if="record.bookingId" class="detail-section">
        <h4 class="section-title">
          <el-icon><Calendar /></el-icon>
          关联预约
        </h4>
        <div class="detail-grid">
          <div class="detail-item">
            <label>预约ID:</label>
            <span class="booking-id">{{ record.bookingId }}</span>
          </div>
          <div class="detail-item">
            <label>预约名称:</label>
            <span class="booking-name">{{ record.bookingName }}</span>
          </div>
          <div class="detail-item">
            <label>预约时段:</label>
            <span class="booking-period">{{ record.bookingPeriod }}</span>
          </div>
        </div>
      </div>
      <div v-else class="detail-section">
        <h4 class="section-title">
          <el-icon><Calendar /></el-icon>
          关联预约
        </h4>
        <div class="no-booking-info">
          <el-icon><Warning /></el-icon>
          <span>此次通行未关联预约信息</span>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handlePrint">
          <el-icon><Printer /></el-icon>
          打印记录
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  InfoFilled, House, User, Key, Calendar, Warning, Printer,
  CreditCard, Camera, Button as ButtonIcon
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  record: {
    type: Object,
    default: null
  }
})

// Emits
const emit = defineEmits(['update:visible'])

// 计算属性
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false
}

// 打印记录
const handlePrint = () => {
  // 这里可以实现打印功能
  ElMessage.info('打印功能开发中...')
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
.record-detail {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
}

.detail-item span {
  color: #303133;
  flex: 1;
}

/* 特殊样式 */
.highlight-time {
  color: #409eff !important;
  font-weight: 600;
}

.room-name {
  color: #67c23a !important;
  font-weight: 600;
}

.user-name {
  color: #e6a23c !important;
  font-weight: 600;
}

.booking-id,
.device-id {
  font-family: 'Courier New', monospace;
  background: #e7f4ff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.booking-name {
  color: #409eff !important;
  font-weight: 500;
}

.booking-period {
  background: #f0f9ff;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #409eff;
}

.no-booking-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-style: italic;
  padding: 20px;
  text-align: center;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  :deep(.el-dialog) {
    width: 95% !important;
    margin-top: 5vh !important;
  }

  .detail-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .detail-item label {
    min-width: auto;
  }
}

/* 滚动条样式 */
.record-detail::-webkit-scrollbar {
  width: 6px;
}

.record-detail::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.record-detail::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.record-detail::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>