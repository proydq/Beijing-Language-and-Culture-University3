<template>
  <div class="stats-card">
    <div class="stats-header">
      <h3>教室借用统计</h3>
      <div class="time-filter">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="~"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
        />
      </div>
    </div>

    <div class="stats-content" v-loading="loading">
      <!-- 总览数据 -->
      <div class="overview-cards">
        <div class="overview-card">
          <div class="card-icon total-records">
            <el-icon><Document /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statsData.totalRecords || 0 }}</div>
            <div class="card-label">总记录数</div>
          </div>
        </div>

        <div class="overview-card">
          <div class="card-icon today-records">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statsData.todayRecords || 0 }}</div>
            <div class="card-label">今日记录数</div>
          </div>
        </div>

        <div class="overview-card">
          <div class="card-icon total-rooms">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statsData.totalRooms || 0 }}</div>
            <div class="card-label">使用教室数</div>
          </div>
        </div>

        <div class="overview-card">
          <div class="card-icon total-users">
            <el-icon><User /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ statsData.totalUsers || 0 }}</div>
            <div class="card-label">使用人数</div>
          </div>
        </div>
      </div>

      <!-- 统计图表 -->
      <div class="charts-container">
        <div class="chart-item">
          <h4>通行类型分布</h4>
          <div class="chart-content">
            <div class="chart-legend">
              <div 
                v-for="(value, key) in statsData.accessTypeStats" 
                :key="key"
                class="legend-item"
              >
                <span class="legend-color access-type"></span>
                <span class="legend-label">{{ key }}: {{ value }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-item">
          <h4>开门方式分布</h4>
          <div class="chart-content">
            <div class="chart-legend">
              <div 
                v-for="(value, key) in statsData.openMethodStats" 
                :key="key"
                class="legend-item"
              >
                <span class="legend-color open-method"></span>
                <span class="legend-label">{{ key }}: {{ value }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="chart-item">
          <h4>用户类型分布</h4>
          <div class="chart-content">
            <div class="chart-legend">
              <div 
                v-for="(value, key) in statsData.userTypeStats" 
                :key="key"
                class="legend-item"
              >
                <span class="legend-color user-type"></span>
                <span class="legend-label">{{ key }}: {{ value }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Calendar, OfficeBuilding, User } from '@element-plus/icons-vue'
import { getAccessStats } from '@/api/roomBookingManagement'

const loading = ref(false)
const dateRange = ref([])
const statsData = reactive({
  totalRecords: 0,
  todayRecords: 0,
  totalRooms: 0,
  totalUsers: 0,
  accessTypeStats: {},
  openMethodStats: {},
  userTypeStats: {}
})

// 加载统计数据
const loadStatsData = async () => {
  try {
    loading.value = true
    const params = {}
    
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0] + ' 00:00:00'
      params.endTime = dateRange.value[1] + ' 23:59:59'
    }
    
    const response = await getAccessStats(params)
    if (response.code === 200) {
      Object.assign(statsData, response.data)
    } else {
      ElMessage.error(response.message || '获取统计信息失败')
    }
  } catch (error) {
    console.error('获取统计信息失败:', error)
    ElMessage.error('获取统计信息失败')
  } finally {
    loading.value = false
  }
}

// 日期范围变化处理
const handleDateChange = () => {
  loadStatsData()
}

// 页面初始化
onMounted(() => {
  loadStatsData()
})
</script>

<style scoped>
.stats-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
}

.stats-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.stats-content {
  padding: 20px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: #fff;
}

.card-icon.total-records {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon.today-records {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon.total-rooms {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-icon.total-users {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  flex: 1;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  line-height: 1;
  margin-bottom: 5px;
}

.card-label {
  font-size: 14px;
  color: #666;
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.chart-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.chart-item h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.chart-content {
  min-height: 120px;
}

.chart-legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.access-type {
  background: #409eff;
}

.legend-color.open-method {
  background: #67c23a;
}

.legend-color.user-type {
  background: #e6a23c;
}

.legend-label {
  font-size: 14px;
  color: #666;
}
</style>