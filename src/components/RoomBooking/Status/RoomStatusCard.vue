<template>
  <div class="room-status-card">
    <div class="status-header">
      <h3>教室实时使用状态</h3>
      <div class="header-actions">
        <el-select 
          v-model="selectedAreaId" 
          placeholder="选择区域" 
          clearable 
          style="width: 200px; margin-right: 10px;"
          @change="handleAreaChange"
        >
          <el-option label="全部区域" value="" />
          <el-option 
            v-for="area in areaOptions" 
            :key="area.id" 
            :label="area.label" 
            :value="area.id" 
          />
        </el-select>
        <el-button type="primary" :icon="Refresh" @click="refreshStatus">刷新</el-button>
      </div>
    </div>

    <div class="status-content" v-loading="loading">
      <div class="status-summary">
        <div class="summary-item">
          <span class="summary-label">使用中:</span>
          <span class="summary-value in-use">{{ inUseCount }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">空闲:</span>
          <span class="summary-value idle">{{ idleCount }}</span>
        </div>
        <div class="summary-item">
          <span class="summary-label">总计:</span>
          <span class="summary-value total">{{ roomStatusList.length }}</span>
        </div>
      </div>

      <div class="room-grid">
        <div 
          v-for="room in roomStatusList" 
          :key="room.roomId"
          class="room-card"
          :class="{ 'in-use': room.status === '使用中', 'idle': room.status === '空闲' }"
        >
          <div class="room-header">
            <div class="room-name">{{ room.roomName }}</div>
            <div class="room-status" :class="room.status === '使用中' ? 'status-busy' : 'status-free'">
              {{ room.status }}
            </div>
          </div>
          
          <div class="room-info">
            <div class="info-item">
              <span class="info-label">楼栋:</span>
              <span class="info-value">{{ room.buildingName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">楼层:</span>
              <span class="info-value">{{ room.floor }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">当前人数:</span>
              <span class="info-value">{{ room.currentUsers }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">最后通行:</span>
              <span class="info-value">{{ formatTime(room.lastAccessTime) }}</span>
            </div>
          </div>

          <div v-if="room.currentBooking" class="current-booking">
            <div class="booking-title">当前预约</div>
            <div class="booking-info">
              <div class="booking-name">{{ room.currentBooking.bookingName }}</div>
              <div class="booking-details">
                <span>{{ room.currentBooking.applicantName }}</span>
                <span class="booking-period">{{ room.currentBooking.bookingPeriod }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!roomStatusList.length && !loading" class="empty-state">
        <el-empty description="暂无教室状态数据" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getRoomStatus } from '@/api/roomBookingManagement'
import { getAreaTree } from '@/api/area'

const loading = ref(false)
const selectedAreaId = ref('')
const roomStatusList = ref([])
const areaOptions = ref([])
let refreshTimer = null

// 计算统计数据
const inUseCount = computed(() => roomStatusList.value.filter(room => room.status === '使用中').length)
const idleCount = computed(() => roomStatusList.value.filter(room => room.status === '空闲').length)

// 加载区域选项
const loadAreaOptions = async () => {
  try {
    const response = await getAreaTree()
    if (response.code === 200) {
      const formatAreaOptions = (nodes) => {
        let options = []
        nodes.forEach(node => {
          options.push({
            id: node.id,
            label: node.areaName
          })
          if (node.children && node.children.length > 0) {
            options = options.concat(formatAreaOptions(node.children))
          }
        })
        return options
      }
      areaOptions.value = formatAreaOptions(response.data || [])
    }
  } catch (error) {
    console.error('获取区域选项失败:', error)
  }
}

// 加载教室状态数据
const loadRoomStatus = async () => {
  try {
    loading.value = true
    const response = await getRoomStatus(selectedAreaId.value)
    if (response.code === 200) {
      roomStatusList.value = response.data || []
    } else {
      ElMessage.error(response.message || '获取教室状态失败')
    }
  } catch (error) {
    console.error('获取教室状态失败:', error)
    ElMessage.error('获取教室状态失败')
  } finally {
    loading.value = false
  }
}

// 区域变化处理
const handleAreaChange = () => {
  loadRoomStatus()
}

// 刷新状态
const refreshStatus = () => {
  loadRoomStatus()
}

// 格式化时间
const formatTime = (dateTime) => {
  if (!dateTime) return '无'
  const date = new Date(dateTime)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000)
  
  if (diff < 60) {
    return '刚刚'
  } else if (diff < 3600) {
    return `${Math.floor(diff / 60)}分钟前`
  } else if (diff < 86400) {
    return `${Math.floor(diff / 3600)}小时前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

// 启动自动刷新
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    loadRoomStatus()
  }, 30000) // 30秒刷新一次
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 组件挂载时初始化
onMounted(() => {
  loadAreaOptions()
  loadRoomStatus()
  startAutoRefresh()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.room-status-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.status-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
}

.status-content {
  padding: 20px;
}

.status-summary {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-label {
  font-size: 14px;
  color: #666;
}

.summary-value {
  font-size: 18px;
  font-weight: bold;
}

.summary-value.in-use {
  color: #f56c6c;
}

.summary-value.idle {
  color: #67c23a;
}

.summary-value.total {
  color: #909399;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.room-card {
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
}

.room-card.in-use {
  border-color: #f56c6c;
  background: #fef0f0;
}

.room-card.idle {
  border-color: #67c23a;
  background: #f0f9ff;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.room-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.room-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.room-status.status-busy {
  background: #f56c6c;
  color: #fff;
}

.room-status.status-free {
  background: #67c23a;
  color: #fff;
}

.room-info {
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.info-label {
  font-size: 13px;
  color: #606266;
}

.info-value {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
}

.current-booking {
  border-top: 1px solid #e4e7ed;
  padding-top: 12px;
}

.booking-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.booking-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.booking-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #606266;
}

.booking-period {
  background: #e1f3ff;
  padding: 2px 6px;
  border-radius: 4px;
  color: #409eff;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}
</style>