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
            <el-button type="primary" size="small" @click="handleCustomFilter">筛选</el-button>
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
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'

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
    },
    distributionData: {
      type: Array,
      default: () => []
    },
    trendData: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['time-range-change', 'custom-range-change'],
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

    const handleCustomFilter = () => {
      if (!startDate.value || !endDate.value) {
        ElMessage.warning('请选择开始和结束日期')
        return
      }
      
      const start = new Date(startDate.value)
      const end = new Date(endDate.value)
      
      if (start >= end) {
        ElMessage.warning('开始日期必须小于结束日期')
        return
      }
      
      const diffTime = Math.abs(end - start)
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
      
      if (diffDays > 180) {
        ElMessage.warning('时间跨度不能超过180天')
        return
      }
      
      // 清除快捷选择的激活状态
      activeTimeRange.value = ''
      emit('custom-range-change', startDate.value, endDate.value)
    }

    // 创建饼图
    const createPieChart = (container, title, data) => {
      if (!container || !data || data.length === 0) {
        container.innerHTML = `
          <div style="display: flex; align-items: center; justify-content: center; height: 200px; color: #999;">
            <div style="text-align: center;">
              <div style="font-size: 48px; margin-bottom: 10px;">📊</div>
              <div>暂无${title}数据</div>
            </div>
          </div>
        `
        return
      }

      // 计算角度
      let currentAngle = 0
      const colors = ['#52c41a', '#ff4d4f', '#faad14', '#1890ff', '#722ed1']
      
      let pathElements = ''
      data.forEach((item, index) => {
        const percentage = item.percentage || 0
        const angle = (percentage / 100) * 360
        const largeArcFlag = angle > 180 ? 1 : 0
        
        const startAngle = (currentAngle * Math.PI) / 180
        const endAngle = ((currentAngle + angle) * Math.PI) / 180
        
        const x1 = 100 + 80 * Math.cos(startAngle)
        const y1 = 100 + 80 * Math.sin(startAngle)
        const x2 = 100 + 80 * Math.cos(endAngle)
        const y2 = 100 + 80 * Math.sin(endAngle)
        
        if (percentage > 0) {
          pathElements += `
            <path d="M 100 100 L ${x1} ${y1} A 80 80 0 ${largeArcFlag} 1 ${x2} ${y2} Z" 
                  fill="${colors[index % colors.length]}" 
                  stroke="#fff" 
                  stroke-width="2"/>
          `
        }
        
        currentAngle += angle
      })

      container.innerHTML = `
        <svg width="200" height="200" viewBox="0 0 200 200">
          ${pathElements}
        </svg>
        <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); text-align: center;">
          <div style="font-size: 16px; font-weight: bold;">${title}</div>
        </div>
      `
    }

    const createTrendChart = (container, data) => {
      if (!container) return

      if (!data || data.length === 0) {
        container.innerHTML = `
          <div style="display: flex; align-items: center; justify-content: center; height: 300px; color: #999; background: #f9f9f9; border-radius: 8px;">
            <div style="text-align: center;">
              <div style="font-size: 48px; margin-bottom: 10px;">📈</div>
              <div>暂无趋势数据</div>
            </div>
          </div>
        `
        return
      }

      const chartWidth = 800
      const chartHeight = 300
      const padding = { left: 60, right: 60, top: 60, bottom: 60 }
      const dataWidth = chartWidth - padding.left - padding.right
      const dataHeight = chartHeight - padding.top - padding.bottom
      
      // 计算数据点位置
      const stepX = dataWidth / (data.length - 1 || 1)
      let studentPoints = ''
      let teacherPoints = ''
      let studentCircles = ''
      let teacherCircles = ''
      let xLabels = ''
      
      data.forEach((item, index) => {
        const x = padding.left + index * stepX
        const studentY = padding.top + dataHeight - (item.studentPercentage || 0) / 100 * dataHeight
        const teacherY = padding.top + dataHeight - (item.teacherPercentage || 0) / 100 * dataHeight
        
        studentPoints += `${x},${studentY} `
        teacherPoints += `${x},${teacherY} `
        
        studentCircles += `<circle cx="${x}" cy="${studentY}" r="4" fill="#52c41a"/>`
        teacherCircles += `<circle cx="${x}" cy="${teacherY}" r="4" fill="#ff7875"/>`
        
        // 格式化日期标签
        const date = new Date(item.date)
        const dateStr = `${(date.getMonth() + 1).toString().padStart(2, '0')}.${date.getDate().toString().padStart(2, '0')}`
        xLabels += `<text x="${x}" y="${chartHeight - 20}" font-size="12" fill="#666" text-anchor="middle">${dateStr}</text>`
      })
      
      // Y轴标签
      const yLabels = [100, 80, 60, 40, 20, 0].map(value => {
        const y = padding.top + (100 - value) / 100 * dataHeight
        return `<text x="${padding.left - 10}" y="${y + 5}" font-size="12" fill="#666" text-anchor="end">${value}%</text>`
      }).join('')
      
      // 网格线
      const gridLines = [100, 80, 60, 40, 20, 0].map(value => {
        const y = padding.top + (100 - value) / 100 * dataHeight
        return `<line x1="${padding.left}" y1="${y}" x2="${chartWidth - padding.right}" y2="${y}" stroke="#e0e0e0" stroke-width="1"/>`
      }).join('')

      container.innerHTML = `
        <svg width="100%" height="300" viewBox="0 0 ${chartWidth} ${chartHeight}" style="background: #f9f9f9;">
          <!-- 网格线 -->
          ${gridLines}
          
          <!-- 数据线 -->
          <polyline points="${studentPoints.trim()}" fill="none" stroke="#52c41a" stroke-width="3"/>
          <polyline points="${teacherPoints.trim()}" fill="none" stroke="#ff7875" stroke-width="3"/>
          
          <!-- 数据点 -->
          ${studentCircles}
          ${teacherCircles}
          
          <!-- 坐标轴标签 -->
          ${yLabels}
          ${xLabels}
          
          <!-- 图例 -->
          <circle cx="${chartWidth - 150}" cy="40" r="6" fill="#52c41a"/>
          <text x="${chartWidth - 135}" y="45" font-size="12" fill="#666">学生</text>
          <circle cx="${chartWidth - 80}" cy="40" r="6" fill="#ff7875"/>
          <text x="${chartWidth - 65}" y="45" font-size="12" fill="#666">教师</text>
        </svg>
      `
    }

    // 监听数据变化，更新图表
    watch(
      () => props.distributionData,
      (newData) => {
        if (newData && newData.length > 0) {
          nextTick(() => {
            const studentData = newData.filter(item => item.type === '学生')
            const teacherData = newData.filter(item => item.type === '教师')
            createPieChart(studentChart.value, '学生', studentData)
            createPieChart(teacherChart.value, '教师', teacherData)
          })
        }
      },
      { immediate: true }
    )

    watch(
      () => props.trendData,
      (newData) => {
        if (newData) {
          nextTick(() => {
            createTrendChart(trendChart.value, newData)
          })
        }
      },
      { immediate: true }
    )

    onMounted(() => {
      nextTick(() => {
        // 初始化图表
        createPieChart(studentChart.value, '学生', [])
        createPieChart(teacherChart.value, '教师', [])
        createTrendChart(trendChart.value, [])
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
      setActiveTimeRange,
      handleCustomFilter
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
