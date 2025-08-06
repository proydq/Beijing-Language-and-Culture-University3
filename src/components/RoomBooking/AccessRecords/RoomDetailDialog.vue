<template>
  <el-dialog
    v-model="dialogVisible"
    title="教室详情"
    width="700px"
    :before-close="handleClose"
  >
    <div v-if="room" class="room-detail">
      <!-- 教室基本信息 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><House /></el-icon>
          教室信息
        </h4>
        <div class="room-basic-info">
          <div class="room-header">
            <div class="room-name">{{ room.roomName }}</div>
            <el-tag
              :type="room.status === '使用中' ? 'danger' : 'success'"
              size="large"
            >
              {{ room.status }}
            </el-tag>
          </div>
          <div class="room-details">
            <div class="detail-item">
              <label>楼栋:</label>
              <span>{{ room.buildingName }}</span>
            </div>
            <div class="detail-item">
              <label>楼层:</label>
              <span>{{ room.floor }}</span>
            </div>
            <div class="detail-item">
              <label>当前人数:</label>
              <span class="current-users">{{ room.currentUsers }} 人</span>
            </div>
            <div class="detail-item">
              <label>最后通行:</label>
              <span>{{ formatLastAccessTime(room.lastAccessTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 当前预约信息 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><Calendar /></el-icon>
          当前预约
        </h4>
        <div v-if="room.currentBooking" class="booking-info">
          <div class="booking-card">
            <div class="booking-header">
              <div class="booking-name">{{ room.currentBooking.bookingName }}</div>
              <div class="booking-period">{{ room.currentBooking.bookingPeriod }}</div>
            </div>
            <div class="booking-details">
              <div class="detail-item">
                <label>申请人:</label>
                <span>{{ room.currentBooking.applicantName }}</span>
              </div>
              <div class="detail-item">
                <label>预约ID:</label>
                <span class="booking-id">{{ room.currentBooking.bookingId }}</span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-booking">
          <el-icon><Warning /></el-icon>
          <span>当前无预约信息</span>
        </div>
      </div>

      <!-- 今日通行统计 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><DataLine /></el-icon>
          今日统计
        </h4>
        <div class="today-stats">
          <div class="stat-card">
            <div class="stat-value">{{ todayStats.totalAccess }}</div>
            <div class="stat-label">总通行次数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ todayStats.uniqueUsers }}</div>
            <div class="stat-label">通行人数</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ todayStats.peakHour }}</div>
            <div class="stat-label">高峰时段</div>
          </div>
        </div>
      </div>

      <!-- 最近通行记录 -->
      <div class="detail-section">
        <h4 class="section-title">
          <el-icon><List /></el-icon>
          最近通行记录
          <el-button 
            type="text" 
            size="small" 
            @click="loadRecentRecords"
            :loading="recordsLoading"
          >
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </h4>
        <div class="recent-records">
          <div v-if="recordsLoading" class="loading-records">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="recentRecords.length > 0" class="records-list">
            <div
              v-for="record in recentRecords"
              :key="record.id"
              class="record-item"
            >
              <div class="record-user">
                <div class="user-name">{{ record.name }}</div>
                <el-tag
                  :type="getUserTypeTagType(record.userType)"
                  size="small"
                >
                  {{ record.userType }}
                </el-tag>
              </div>
              <div class="record-method">
                <el-tag
                  :type="getOpenMethodTagType(record.openMethod)"
                  size="small"
                >
                  {{ record.openMethod }}
                </el-tag>
              </div>
              <div class="record-time">
                {{ formatRecordTime(record.accessTime) }}
              </div>
            </div>
          </div>
          <div v-else class="no-records">
            <el-icon><DocumentRemove /></el-icon>
            <span>暂无通行记录</span>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="viewAllRecords">
          <el-icon><View /></el-icon>
          查看全部记录
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  House, Calendar, Warning, DataLine, List, Refresh, 
  DocumentRemove, View
} from '@element-plus/icons-vue'
import { accessRecordsApi } from '@/api/accessRecords.js'

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  room: {
    type: Object,
    default: null
  }
})

// Emits
const emit = defineEmits(['update:visible', 'view-all-records'])

// 响应式数据
const recordsLoading = ref(false)
const recentRecords = ref([])

// 计算属性
const dialogVisible = computed({
  get: () => props.visible,
  set: (value) => emit('update:visible', value)
})

// 模拟今日统计数据
const todayStats = computed(() => {
  if (!props.room) return { totalAccess: 0, uniqueUsers: 0, peakHour: '-' }
  
  // 这里可以从API获取真实数据
  return {
    totalAccess: Math.floor(Math.random() * 50) + 10,
    uniqueUsers: Math.floor(Math.random() * 20) + 5,
    peakHour: '14:00-15:00'
  }
})

// 监听弹窗显示状态
watch(() => props.visible, (visible) => {
  if (visible && props.room) {
    loadRecentRecords()
  }
})

// 加载最近通行记录
const loadRecentRecords = async () => {
  if (!props.room) return
  
  try {
    recordsLoading.value = true
    
    // 模拟API调用获取最近记录
    const mockRecords = [
      {
        id: '1',
        name: '张三',
        userType: '教师',
        openMethod: '刷卡',
        accessTime: new Date(Date.now() - 10 * 60 * 1000).toISOString()
      },
      {
        id: '2',
        name: '李四',
        userType: '学生',
        openMethod: '人脸识别',
        accessTime: new Date(Date.now() - 30 * 60 * 1000).toISOString()
      },
      {
        id: '3',
        name: '王五',
        userType: '教师',
        openMethod: '按钮',
        accessTime: new Date(Date.now() - 60 * 60 * 1000).toISOString()
      }
    ]
    
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    recentRecords.value = mockRecords
  } catch (error) {
    console.error('加载最近通行记录失败:', error)
    ElMessage.error('加载最近通行记录失败')
  } finally {
    recordsLoading.value = false
  }
}

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false
}

// 查看全部记录
const viewAllRecords = () => {
  emit('view-all-records', props.room)
  handleClose()
}

// 格式化最后通行时间
const formatLastAccessTime = (time) => {
  if (!time) return '暂无记录'
  
  const now = new Date()
  const accessTime = new Date(time)
  const diffMinutes = Math.floor((now - accessTime) / (1000 * 60))
  
  if (diffMinutes < 1) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffMinutes < 24 * 60) {
    const hours = Math.floor(diffMinutes / 60)
    return `${hours}小时前`
  } else {
    return accessTime.toLocaleDateString('zh-CN') + ' ' + accessTime.toLocaleTimeString('zh-CN')
  }
}

// 格式化记录时间
const formatRecordTime = (time) => {
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
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
</script>

<style scoped>
.room-detail {
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
  justify-content: space-between;
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.section-title > div:first-child {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 教室基本信息 */
.room-basic-info {
  background: white;
  padding: 16px;
  border-radius: 8px;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.room-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.room-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item label {
  font-weight: 500;
  color: #606266;
  min-width: 70px;
}

.current-users {
  color: #409eff;
  font-weight: 600;
}

/* 预约信息 */
.booking-info {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.booking-card {
  padding: 16px;
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.booking-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.booking-period {
  background: #409eff;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.booking-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.booking-id {
  font-family: 'Courier New', monospace;
  background: #e7f4ff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.no-booking {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  color: #909399;
  background: white;
  border-radius: 8px;
}

/* 今日统计 */
.today-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-card {
  background: white;
  padding: 16px;
  border-radius: 8px;
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

/* 最近记录 */
.recent-records {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.loading-records {
  padding: 16px;
}

.records-list {
  max-height: 200px;
  overflow-y: auto;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.record-item:last-child {
  border-bottom: none;
}

.record-user {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.record-method {
  margin: 0 16px;
}

.record-time {
  font-size: 12px;
  color: #909399;
  min-width: 60px;
  text-align: right;
}

.no-records {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px;
  color: #909399;
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

  .room-details,
  .booking-details {
    grid-template-columns: 1fr;
  }

  .today-stats {
    grid-template-columns: 1fr;
  }

  .record-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .record-method,
  .record-time {
    margin: 0;
  }
}

/* 滚动条样式 */
.room-detail::-webkit-scrollbar,
.records-list::-webkit-scrollbar {
  width: 6px;
}

.room-detail::-webkit-scrollbar-track,
.records-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.room-detail::-webkit-scrollbar-thumb,
.records-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.room-detail::-webkit-scrollbar-thumb:hover,
.records-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>