<template>
  <div class="room-status-monitor">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>教室状态监控</h2>
      <p class="page-description">实时监控教室使用状态和通行情况</p>
      <div class="header-actions">
        <el-button 
          type="primary" 
          @click="refreshData"
          :loading="loading"
        >
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-switch
          v-model="autoRefresh"
          active-text="自动刷新"
          @change="handleAutoRefreshChange"
        />
      </div>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-form :inline="true">
        <el-form-item label="区域筛选:">
          <el-select
            v-model="selectedAreaId"
            placeholder="请选择区域"
            clearable
            style="width: 200px"
            @change="handleAreaChange"
          >
            <el-option
              v-for="area in areaOptions"
              :key="area.value"
              :label="area.label"
              :value="area.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态筛选:">
          <el-select
            v-model="statusFilter"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
            @change="filterRooms"
          >
            <el-option label="使用中" value="使用中" />
            <el-option label="空闲" value="空闲" />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 状态统计 -->
    <div class="status-stats">
      <div class="stat-item">
        <div class="stat-number">{{ roomStats.total }}</div>
        <div class="stat-label">总教室数</div>
      </div>
      <div class="stat-item in-use">
        <div class="stat-number">{{ roomStats.inUse }}</div>
        <div class="stat-label">使用中</div>
      </div>
      <div class="stat-item available">
        <div class="stat-number">{{ roomStats.available }}</div>
        <div class="stat-label">空闲</div>
      </div>
      <div class="stat-item usage-rate">
        <div class="stat-number">{{ roomStats.usageRate }}%</div>
        <div class="stat-label">使用率</div>
      </div>
    </div>

    <!-- 教室状态卡片 -->
    <div class="room-grid">
      <div
        v-for="room in filteredRooms"
        :key="room.roomId"
        :class="['room-card', room.status === '使用中' ? 'in-use' : 'available']"
        @click="showRoomDetail(room)"
      >
        <div class="room-header">
          <div class="room-name">{{ room.roomName }}</div>
          <div class="room-status">
            <el-tag
              :type="room.status === '使用中' ? 'danger' : 'success'"
              size="small"
            >
              {{ room.status }}
            </el-tag>
          </div>
        </div>

        <div class="room-info">
          <div class="info-item">
            <el-icon><House /></el-icon>
            <span>{{ room.buildingName }} {{ room.floor }}</span>
          </div>
          <div class="info-item">
            <el-icon><User /></el-icon>
            <span>当前人数: {{ room.currentUsers }}</span>
          </div>
          <div class="info-item">
            <el-icon><Clock /></el-icon>
            <span>最后通行: {{ formatLastAccessTime(room.lastAccessTime) }}</span>
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
        <div v-else class="no-booking">
          <el-icon><Warning /></el-icon>
          <span>无预约信息</span>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading && roomList.length === 0" class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && filteredRooms.length === 0" class="empty-state">
      <el-empty
        description="暂无教室数据"
        :image-size="100"
      >
        <el-button type="primary" @click="refreshData">重新加载</el-button>
      </el-empty>
    </div>

    <!-- 教室详情弹窗 -->
    <RoomDetailDialog
      v-model:visible="detailDialogVisible"
      :room="selectedRoom"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, House, User, Clock, Warning } from '@element-plus/icons-vue'
import RoomDetailDialog from './RoomDetailDialog.vue'
import { accessRecordsApi } from '@/api/accessRecords.js'

// 响应式数据
const loading = ref(false)
const autoRefresh = ref(false)
const selectedAreaId = ref('')
const statusFilter = ref('')
const roomList = ref([])
const selectedRoom = ref(null)
const detailDialogVisible = ref(false)

let refreshTimer = null

// 区域选项
const areaOptions = ref([
  { label: '教学楼A', value: 'area001' },
  { label: '教学楼B', value: 'area002' },
  { label: '实验楼', value: 'area003' }
])

// 教室统计
const roomStats = computed(() => {
  const total = roomList.value.length
  const inUse = roomList.value.filter(room => room.status === '使用中').length
  const available = total - inUse
  const usageRate = total > 0 ? Math.round((inUse / total) * 100) : 0

  return {
    total,
    inUse,
    available,
    usageRate
  }
})

// 筛选后的教室列表
const filteredRooms = computed(() => {
  let filtered = roomList.value

  // 按状态筛选
  if (statusFilter.value) {
    filtered = filtered.filter(room => room.status === statusFilter.value)
  }

  return filtered
})

// 加载教室状态数据
const loadRoomStatus = async () => {
  try {
    loading.value = true
    const params = {}
    
    if (selectedAreaId.value) {
      params.areaId = selectedAreaId.value
    }
    
    const response = await accessRecordsApi.getRoomStatus(params)
    
    if (response.success) {
      roomList.value = response.result || []
    } else {
      ElMessage.error(response.message || '获取教室状态失败')
    }
  } catch (error) {
    console.error('加载教室状态失败:', error)
    ElMessage.error('加载教室状态失败')
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = () => {
  loadRoomStatus()
}

// 处理区域变化
const handleAreaChange = () => {
  loadRoomStatus()
}

// 筛选教室
const filterRooms = () => {
  // 筛选逻辑在计算属性中处理
}

// 显示教室详情
const showRoomDetail = (room) => {
  selectedRoom.value = room
  detailDialogVisible.value = true
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
    return accessTime.toLocaleDateString('zh-CN')
  }
}

// 处理自动刷新变化
const handleAutoRefreshChange = (value) => {
  if (value) {
    // 开启自动刷新，每30秒刷新一次
    refreshTimer = setInterval(() => {
      loadRoomStatus()
    }, 30000)
    ElMessage.success('已开启自动刷新（30秒间隔）')
  } else {
    // 关闭自动刷新
    if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
    ElMessage.info('已关闭自动刷新')
  }
}

// 生命周期
onMounted(() => {
  loadRoomStatus()
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.room-status-monitor {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.page-description {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 16px;
  align-items: center;
}

.filter-section {
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.status-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
  border-left: 4px solid #409eff;
}

.stat-item.in-use {
  border-left-color: #f56c6c;
}

.stat-item.available {
  border-left-color: #67c23a;
}

.stat-item.usage-rate {
  border-left-color: #e6a23c;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.room-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border-left: 4px solid #e4e7ed;
}

.room-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.room-card.in-use {
  border-left-color: #f56c6c;
  background: linear-gradient(135deg, #fff 0%, #fef0f0 100%);
}

.room-card.available {
  border-left-color: #67c23a;
  background: linear-gradient(135deg, #fff 0%, #f0f9ff 100%);
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.room-name {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.room-info {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.info-item:last-child {
  margin-bottom: 0;
}

.current-booking {
  background: #f0f9ff;
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid #409eff;
}

.booking-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.booking-name {
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
  background: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
}

.no-booking {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  color: #909399;
  font-size: 14px;
  justify-content: center;
}

.loading-state,
.empty-state {
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .room-status-monitor {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .status-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .room-grid {
    grid-template-columns: 1fr;
  }
}
</style>