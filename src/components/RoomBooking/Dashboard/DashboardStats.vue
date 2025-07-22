<template>
  <div class="dashboard-container">
    <!-- 总借用次数统计 -->
    <div class="stats-overview">
      <div class="stats-content">
        <!-- 左侧统计数据 -->
        <div class="stats-card">
          <div class="stats-item">
            <span class="label">借用总次数：</span>
            <span class="value">{{ stats.totalBookings }}次</span>
          </div>
          <div class="stats-item">
            <span class="label">教师借用次数：</span>
            <span class="value">{{ stats.teacherBookings }}次</span>
          </div>
          <div class="stats-item">
            <span class="label">学生借用次数：</span>
            <span class="value">{{ stats.studentBookings }}次</span>
          </div>
        </div>

        <!-- 右侧图表区域 -->
        <div class="chart-section">
          <div class="chart-container">
            <div class="chart-item">
              <div class="pie-chart" ref="studentChart"></div>
              <div class="chart-title">学生借用数据</div>
            </div>
            <div class="chart-item">
              <div class="pie-chart" ref="teacherChart"></div>
              <div class="chart-title">教师借用数据</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 借用趋势图 -->
    <div class="trend-section">
      <div class="trend-header">
        <div class="trend-title">
          <h3>借用趋势图</h3>
          <p class="trend-desc">*时间选择跨度不能超过180天</p>
        </div>
        <div class="time-filter">
          <el-button-group>
            <el-button
              v-for="timeRange in timeRanges"
              :key="timeRange"
              :class="{ 'is-active': activeTimeRange === timeRange }"
              @click="setActiveTimeRange(timeRange)"
            >
              {{ timeRange }}
            </el-button>
          </el-button-group>
          <div class="date-range">
            <el-date-picker
              v-model="startDate"
              type="date"
              placeholder="请选择日期"
              size="small"
              style="width: 140px;"
            />
            <span class="date-separator">~</span>
            <el-date-picker
              v-model="endDate"
              type="date"
              placeholder="请选择日期"
              size="small"
              style="width: 140px;"
            />
            <el-button type="primary" size="small">筛选</el-button>
          </div>
        </div>
      </div>
      <div class="trend-chart">
        <div class="line-chart" ref="trendChart"></div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'

export default {
  name: 'DashboardStats',
  props: {
    stats: {
      type: Object,
      required: true,
      default: () => ({
        totalBookings: 0,
        teacherBookings: 0,
        studentBookings: 0
      })
    }
  },
  emits: ['time-range-change'],
  setup(props, { emit }) {
    const studentChart = ref(null)
    const teacherChart = ref(null)
    const trendChart = ref(null)
    
    const timeRanges = ['近7天', '近15天', '近30天', '近90天']
    const activeTimeRange = ref('近15天')
    const startDate = ref('')
    const endDate = ref('')

    const setActiveTimeRange = (timeRange) => {
      activeTimeRange.value = timeRange
      emit('time-range-change', timeRange)
    }

    // 模拟图表数据
    const createPieChart = (container, title, data) => {
      if (!container) return
      
      // 模拟饼图的创建（实际项目中可以使用 ECharts 或其他图表库）
      container.innerHTML = `
        <svg width="200" height="200" viewBox="0 0 200 200">
          <circle cx="100" cy="100" r="80" fill="#52c41a" stroke="#fff" stroke-width="2"/>
          <path d="M 100 20 A 80 80 0 0 1 180 100 L 100 100 Z" fill="#ff4d4f" stroke="#fff" stroke-width="2"/>
          <path d="M 180 100 A 80 80 0 0 1 100 180 L 100 100 Z" fill="#faad14" stroke="#fff" stroke-width="2"/>
        </svg>
        <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); text-align: center;">
          <div style="font-size: 16px; font-weight: bold;">${title}</div>
        </div>
      `
    }

    const createTrendChart = (container) => {
      if (!container) return
      
      // 模拟趋势图的创建
      container.innerHTML = `
        <svg width="100%" height="300" viewBox="0 0 800 300" style="background: #f9f9f9;">
          <!-- 背景网格 -->
          <defs>
            <pattern id="grid" width="40" height="30" patternUnits="userSpaceOnUse">
              <path d="M 40 0 L 0 0 0 30" fill="none" stroke="#e0e0e0" stroke-width="1"/>
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid)" />
          
          <!-- 学生数据线 -->
          <polyline 
            points="50,250 100,200 150,180 200,160 250,140 300,120 350,100 400,90 450,85 500,95 550,110 600,130 650,150 700,140 750,120"
            fill="none" 
            stroke="#52c41a" 
            stroke-width="3"
          />
          
          <!-- 教师数据线 -->
          <polyline 
            points="50,280 100,270 150,260 200,250 250,240 300,230 350,220 400,210 450,205 500,215 550,225 600,235 650,245 700,240 750,230"
            fill="none" 
            stroke="#ff7875" 
            stroke-width="3"
          />
          
          <!-- 数据点 -->
          <circle cx="100" cy="200" r="4" fill="#52c41a"/>
          <circle cx="200" cy="160" r="4" fill="#52c41a"/>
          <circle cx="300" cy="120" r="4" fill="#52c41a"/>
          <circle cx="400" cy="90" r="4" fill="#52c41a"/>
          <circle cx="500" cy="95" r="4" fill="#52c41a"/>
          <circle cx="600" cy="130" r="4" fill="#52c41a"/>
          <circle cx="700" cy="140" r="4" fill="#52c41a"/>
          
          <circle cx="100" cy="270" r="4" fill="#ff7875"/>
          <circle cx="200" cy="250" r="4" fill="#ff7875"/>
          <circle cx="300" cy="230" r="4" fill="#ff7875"/>
          <circle cx="400" cy="210" r="4" fill="#ff7875"/>
          <circle cx="500" cy="215" r="4" fill="#ff7875"/>
          <circle cx="600" cy="235" r="4" fill="#ff7875"/>
          <circle cx="700" cy="240" r="4" fill="#ff7875"/>
          
          <!-- Y轴标签 -->
          <text x="30" y="50" font-size="12" fill="#666">100%</text>
          <text x="30" y="100" font-size="12" fill="#666">80%</text>
          <text x="30" y="150" font-size="12" fill="#666">60%</text>
          <text x="30" y="200" font-size="12" fill="#666">40%</text>
          <text x="30" y="250" font-size="12" fill="#666">20%</text>
          <text x="30" y="290" font-size="12" fill="#666">0%</text>
          
          <!-- X轴标签 -->
          <text x="50" y="320" font-size="12" fill="#666">04.07</text>
          <text x="150" y="320" font-size="12" fill="#666">04.09</text>
          <text x="250" y="320" font-size="12" fill="#666">04.11</text>
          <text x="350" y="320" font-size="12" fill="#666">04.13</text>
          <text x="450" y="320" font-size="12" fill="#666">04.15</text>
          <text x="550" y="320" font-size="12" fill="#666">04.17</text>
          <text x="650" y="320" font-size="12" fill="#666">04.19</text>
          <text x="750" y="320" font-size="12" fill="#666">04.21</text>
          
          <!-- 图例 -->
          <circle cx="650" cy="40" r="6" fill="#52c41a"/>
          <text x="665" y="45" font-size="12" fill="#666">学生</text>
          <circle cx="720" cy="40" r="6" fill="#ff7875"/>
          <text x="735" y="45" font-size="12" fill="#666">教师</text>
        </svg>
      `
    }

    onMounted(() => {
      nextTick(() => {
        createPieChart(studentChart.value, '学生', [])
        createPieChart(teacherChart.value, '教师', [])
        createTrendChart(trendChart.value)
      })
    })

    return {
      studentChart,
      teacherChart,
      trendChart,
      timeRanges,
      activeTimeRange,
      startDate,
      endDate,
      setActiveTimeRange
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-overview {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stats-content {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.stats-card {
  flex: 1;
  min-width: 300px;
}

.stats-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 16px;
}

.stats-item .label {
  color: #666;
  margin-right: 10px;
  min-width: 120px;
}

.stats-item .value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.chart-section {
  flex: 2;
}

.chart-container {
  display: flex;
  gap: 40px;
  justify-content: center;
}

.chart-item {
  text-align: center;
}

.pie-chart {
  width: 200px;
  height: 200px;
  margin-bottom: 15px;
  position: relative;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-title {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.trend-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.trend-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 20px;
}

.trend-title h3 {
  font-size: 18px;
  color: #333;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.trend-desc {
  color: #ff4d4f;
  font-size: 14px;
  margin: 0;
}

.time-filter {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

:deep(.el-button-group .el-button) {
  background: #f0f0f0;
  border: 1px solid #d9d9d9;
  color: #666;
}

:deep(.el-button-group .el-button.is-active) {
  background: #4A90E2;
  border-color: #4A90E2;
  color: white;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-separator {
  color: #666;
  font-size: 14px;
}

.trend-chart {
  margin-top: 20px;
  overflow-x: auto;
}

.line-chart {
  width: 100%;
  min-height: 300px;
  border-radius: 8px;
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .chart-container {
    gap: 20px;
  }
  
  .trend-header {
    flex-direction: column;
    align-items: stretch;
  }
}

@media (max-width: 768px) {
  .chart-container {
    flex-direction: column;
    align-items: center;
  }
  
  .pie-chart {
    width: 150px;
    height: 150px;
  }
  
  .time-filter {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .date-range {
    justify-content: center;
  }
}
</style>