<template>
  <div class="access-stats-cards">
    <div class="stats-grid">
      <!-- 总记录数 -->
      <div class="stat-card total-records">
        <div class="stat-icon">
          <el-icon size="32">
            <Document />
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ loading ? '-' : formatNumber(stats.totalRecords || 0) }}
          </div>
          <div class="stat-label">总记录数</div>
        </div>
      </div>

      <!-- 今日记录数 -->
      <div class="stat-card today-records">
        <div class="stat-icon">
          <el-icon size="32">
            <Calendar />
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ loading ? '-' : formatNumber(stats.todayRecords || 0) }}
          </div>
          <div class="stat-label">今日记录数</div>
        </div>
      </div>

      <!-- 使用教室数 -->
      <div class="stat-card total-rooms">
        <div class="stat-icon">
          <el-icon size="32">
            <House />
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ loading ? '-' : formatNumber(stats.totalRooms || 0) }}
          </div>
          <div class="stat-label">使用教室数</div>
        </div>
      </div>

      <!-- 使用人数 -->
      <div class="stat-card total-users">
        <div class="stat-icon">
          <el-icon size="32">
            <User />
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">
            {{ loading ? '-' : formatNumber(stats.totalUsers || 0) }}
          </div>
          <div class="stat-label">使用人数</div>
        </div>
      </div>
    </div>

    <!-- 详细统计 -->
    <div class="detailed-stats">
      <div class="stats-row">
        <!-- 通行类型统计 -->
        <div class="stat-chart-card">
          <div class="card-header">
            <h4>通行类型统计</h4>
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
          <div class="chart-content">
            <div v-if="loading" class="loading-placeholder">
              <el-skeleton :rows="3" animated />
            </div>
            <div v-else class="stats-list">
              <div 
                v-for="(value, key) in stats.accessTypeStats" 
                :key="key"
                class="stats-item"
              >
                <span class="item-label">{{ key }}</span>
                <span class="item-value">{{ formatNumber(value) }}</span>
              </div>
              <div v-if="!stats.accessTypeStats || Object.keys(stats.accessTypeStats).length === 0" class="no-data">
                暂无数据
              </div>
            </div>
          </div>
        </div>

        <!-- 开门方式统计 -->
        <div class="stat-chart-card">
          <div class="card-header">
            <h4>开门方式统计</h4>
          </div>
          <div class="chart-content">
            <div v-if="loading" class="loading-placeholder">
              <el-skeleton :rows="3" animated />
            </div>
            <div v-else class="stats-list">
              <div 
                v-for="(value, key) in stats.openMethodStats" 
                :key="key"
                class="stats-item"
              >
                <span class="item-label">{{ key }}</span>
                <span class="item-value">{{ formatNumber(value) }}</span>
              </div>
              <div v-if="!stats.openMethodStats || Object.keys(stats.openMethodStats).length === 0" class="no-data">
                暂无数据
              </div>
            </div>
          </div>
        </div>

        <!-- 用户类型统计 -->
        <div class="stat-chart-card">
          <div class="card-header">
            <h4>用户类型统计</h4>
          </div>
          <div class="chart-content">
            <div v-if="loading" class="loading-placeholder">
              <el-skeleton :rows="3" animated />
            </div>
            <div v-else class="stats-list">
              <div 
                v-for="(value, key) in stats.userTypeStats" 
                :key="key"
                class="stats-item"
              >
                <span class="item-label">{{ key }}</span>
                <span class="item-value">{{ formatNumber(value) }}</span>
              </div>
              <div v-if="!stats.userTypeStats || Object.keys(stats.userTypeStats).length === 0" class="no-data">
                暂无数据
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Document, Calendar, House, User, Refresh } from '@element-plus/icons-vue'

// Props
defineProps({
  stats: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits
defineEmits(['refresh'])

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toLocaleString()
}
</script>

<style scoped>
.access-stats-cards {
  margin-bottom: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  margin-right: 16px;
  padding: 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.total-records .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.today-records .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.total-rooms .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.total-users .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.detailed-stats {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 0;
}

.stat-chart-card {
  padding: 20px;
  border-right: 1px solid #eee;
}

.stat-chart-card:last-child {
  border-right: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-content {
  min-height: 120px;
}

.loading-placeholder {
  padding: 20px 0;
}

.stats-list {
  space-y: 8px;
}

.stats-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.stats-item:last-child {
  border-bottom: none;
}

.item-label {
  font-size: 14px;
  color: #666;
}

.item-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.no-data {
  text-align: center;
  color: #999;
  font-size: 14px;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stats-row {
    grid-template-columns: 1fr;
  }
  
  .stat-chart-card {
    border-right: none;
    border-bottom: 1px solid #eee;
  }
  
  .stat-chart-card:last-child {
    border-bottom: none;
  }
}
</style>