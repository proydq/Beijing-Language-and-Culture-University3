<template>
  <Layout>
    <div class="access-control-dashboard">
      <!-- 顶部导航标签 -->
      <div class="top-tabs">
        <div class="tab-item active" @click="navigateTo('dashboard')">首页</div>
        <div class="tab-item" @click="navigateTo('records')">通行记录</div>
        <div class="tab-item" @click="navigateTo('permissions')">权限分配</div>
        <div class="tab-item" @click="navigateTo('device-management')">门禁控制管理</div>
      </div>

      <!-- 统计卡片区域 -->
      <div class="stats-section">
        <div class="stats-grid">
          <!-- 人脸识别记录 -->
          <div class="stats-card">
            <div class="card-header">
              <div class="card-icon face-icon">
                <el-icon size="40"><user /></el-icon>
              </div>
              <h3>人脸识别记录</h3>
            </div>
            <div class="stats-data">
              <div class="stat-item">
                <span class="label">昨日 (人次)</span>
                <span class="value">32</span>
              </div>
              <div class="stat-item">
                <span class="label">本周 (人次)</span>
                <span class="value">1332</span>
              </div>
              <div class="stat-item">
                <span class="label">本月 (人次)</span>
                <span class="value">13320</span>
              </div>
            </div>
          </div>

          <!-- 刷卡识别记录 -->
          <div class="stats-card">
            <div class="card-header">
              <div class="card-icon card-icon-style">
                <el-icon size="40"><credit-card /></el-icon>
              </div>
              <h3>刷卡识别记录</h3>
            </div>
            <div class="stats-data">
              <div class="stat-item">
                <span class="label">昨日 (人次)</span>
                <span class="value">32</span>
              </div>
              <div class="stat-item">
                <span class="label">本周 (人次)</span>
                <span class="value">1332</span>
              </div>
              <div class="stat-item">
                <span class="label">本月 (人次)</span>
                <span class="value">13320</span>
              </div>
            </div>
          </div>

          <!-- 开门按钮记录 -->
          <div class="stats-card">
            <div class="card-header">
              <div class="card-icon door-icon">
                <el-icon size="40"><key /></el-icon>
              </div>
              <h3>开门按钮记录</h3>
            </div>
            <div class="stats-data">
              <div class="stat-item">
                <span class="label">昨日 (人次)</span>
                <span class="value">32</span>
              </div>
              <div class="stat-item">
                <span class="label">本周 (人次)</span>
                <span class="value">1332</span>
              </div>
              <div class="stat-item">
                <span class="label">本月 (人次)</span>
                <span class="value">13320</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="charts-section">
        <div class="charts-grid">
          <!-- 通行趋势统计 -->
          <div class="chart-card large">
            <div class="chart-header">
              <h3>通行趋势统计</h3>
              <div class="date-range">
                <el-date-picker
                  v-model="startDate"
                  type="date"
                  placeholder="请选择开始时间"
                  size="small"
                />
                <span class="separator">~</span>
                <el-date-picker
                  v-model="endDate"
                  type="date"
                  placeholder="请选择结束时间"
                  size="small"
                />
                <el-button type="primary" size="small" @click="queryTrend">查询</el-button>
                <el-button size="small" @click="resetTrend">重置</el-button>
              </div>
            </div>
            <div class="chart-content">
              <div ref="trendChart" class="trend-chart"></div>
            </div>
          </div>

          <!-- 通行方式占比 -->
          <div class="chart-card">
            <div class="chart-header">
              <h3>通行方式占比</h3>
              <el-date-picker
                v-model="methodDate"
                type="month"
                placeholder="2024.06"
                size="small"
              />
            </div>
            <div class="chart-content">
              <div ref="methodChart" class="pie-chart"></div>
            </div>
          </div>

          <!-- 通行类型占比 -->
          <div class="chart-card">
            <div class="chart-header">
              <h3>通行类型占比</h3>
              <el-date-picker
                v-model="typeDate"
                type="month"
                placeholder="2024.06"
                size="small"
              />
            </div>
            <div class="chart-content">
              <div ref="typeChart" class="pie-chart"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import * as echarts from 'echarts'

export default {
  name: 'AccessControlDashboard',
  components: {
    Layout
  },
  setup() {
    const router = useRouter()
    
    // 日期相关
    const startDate = ref('')
    const endDate = ref('')
    const methodDate = ref('2024-06')
    const typeDate = ref('2024-06')

    // 图表引用
    const trendChart = ref(null)
    const methodChart = ref(null)
    const typeChart = ref(null)

    // 图表实例
    let trendChartInstance = null
    let methodChartInstance = null
    let typeChartInstance = null

    // 导航方法
    const navigateTo = (type) => {
      switch(type) {
        case 'dashboard':
          // 当前页面，不需要跳转
          break
        case 'records':
          router.push('/access-control/records')
          break
        case 'permissions':
          router.push('/access-control/permissions')
          break
        case 'device-management':
          router.push('/access-control/device-management')
          break
      }
    }

    // 初始化趋势图表
    const initTrendChart = () => {
      if (trendChart.value) {
        trendChartInstance = echarts.init(trendChart.value)
        
        const option = {
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            }
          },
          legend: {
            data: ['人脸识别', '刷卡识别'],
            top: 10
          },
          grid: {
            top: 50,
            left: 50,
            right: 30,
            bottom: 50
          },
          xAxis: {
            type: 'category',
            data: ['2024/6/1', '2024/6/2', '2024/6/3', '2024/6/4', '2024/6/5', '2024/6/6', '2024/6/7', '2024/6/8', '2024/6/9', '2024/6/10', '2024/6/11', '2024/6/12', '2024/6/13', '2024/6/14', '2024/6/15']
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: '人脸识别',
              type: 'line',
              data: [65, 70, 45, 35, 125, 30, 60, 55, 100, 45, 35, 62, 58, 98, 100],
              smooth: true,
              areaStyle: {
                opacity: 0.3,
                color: '#ff4757'
              },
              itemStyle: {
                color: '#ff4757'
              },
              lineStyle: {
                color: '#ff4757'
              }
            },
            {
              name: '刷卡识别',
              type: 'line',
              data: [25, 30, 15, 20, 45, 10, 25, 20, 40, 15, 12, 22, 18, 38, 40],
              smooth: true,
              areaStyle: {
                opacity: 0.3,
                color: '#3742fa'
              },
              itemStyle: {
                color: '#3742fa'
              },
              lineStyle: {
                color: '#3742fa'
              }
            }
          ]
        }
        
        trendChartInstance.setOption(option)
      }
    }

    // 初始化通行方式占比图表
    const initMethodChart = () => {
      if (methodChart.value) {
        methodChartInstance = echarts.init(methodChart.value)
        
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            bottom: 10,
            data: ['刷卡', '人脸识别']
          },
          series: [
            {
              name: '通行方式',
              type: 'pie',
              radius: ['40%', '70%'],
              center: ['50%', '45%'],
              data: [
                { value: 32, name: '刷卡', itemStyle: { color: '#ffa726' } },
                { value: 68, name: '人脸识别', itemStyle: { color: '#ef5350' } }
              ],
              label: {
                show: true,
                formatter: '{d}%'
              }
            }
          ]
        }
        
        methodChartInstance.setOption(option)
      }
    }

    // 初始化通行类型占比图表
    const initTypeChart = () => {
      if (typeChart.value) {
        typeChartInstance = echarts.init(typeChart.value)
        
        const option = {
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            bottom: 10,
            data: ['会议', '权限', '临时']
          },
          series: [
            {
              name: '通行类型',
              type: 'pie',
              radius: ['40%', '70%'],
              center: ['50%', '45%'],
              data: [
                { value: 32, name: '会议', itemStyle: { color: '#42a5f5' } },
                { value: 58, name: '权限', itemStyle: { color: '#66bb6a' } },
                { value: 10, name: '临时', itemStyle: { color: '#ffa726' } }
              ],
              label: {
                show: true,
                formatter: '{d}%'
              }
            }
          ]
        }
        
        typeChartInstance.setOption(option)
      }
    }

    // 查询趋势
    const queryTrend = () => {
      console.log('查询趋势:', startDate.value, endDate.value)
      // 这里可以根据日期范围重新加载图表数据
    }

    // 重置趋势
    const resetTrend = () => {
      startDate.value = ''
      endDate.value = ''
      // 重置图表数据
      initTrendChart()
    }

    // 窗口大小改变时重新调整图表
    const handleResize = () => {
      if (trendChartInstance) trendChartInstance.resize()
      if (methodChartInstance) methodChartInstance.resize()
      if (typeChartInstance) typeChartInstance.resize()
    }

    onMounted(() => {
      nextTick(() => {
        initTrendChart()
        initMethodChart()
        initTypeChart()
        
        // 监听窗口大小变化
        window.addEventListener('resize', handleResize)
      })
    })

    return {
      startDate,
      endDate,
      methodDate,
      typeDate,
      trendChart,
      methodChart,
      typeChart,
      navigateTo,
      queryTrend,
      resetTrend
    }
  }
}
</script>

<style scoped>
.access-control-dashboard {
  background-color: #f5f5f5;
  min-height: calc(100vh - 110px);
}

.top-tabs {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  display: flex;
  padding: 0;
  border-radius: 0;
}

.tab-item {
  padding: 15px 30px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
  border-right: 1px solid rgba(255, 255, 255, 0.2);
}

.tab-item:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.tab-item.active {
  color: white;
  background-color: rgba(255, 255, 255, 0.2);
  position: relative;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: #fff;
}

.stats-section {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stats-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.stats-card:hover {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.face-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.card-icon-style {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.door-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.card-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.stats-data {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  text-align: center;
}

.stat-item .label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-item .value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.charts-section {
  padding: 0 20px 20px;
}

.charts-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.chart-card.large {
  grid-row: span 1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-wrap: wrap;
  gap: 10px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.separator {
  color: #666;
  margin: 0 5px;
}

.chart-content {
  height: 300px;
}

.trend-chart {
  width: 100%;
  height: 100%;
}

.pie-chart {
  width: 100%;
  height: 100%;
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-card.large {
    grid-row: span 1;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .top-tabs {
    flex-wrap: wrap;
  }
  
  .tab-item {
    flex: 1;
    text-align: center;
    min-width: 120px;
  }
  
  .date-range {
    justify-content: center;
  }
  
  .chart-header {
    flex-direction: column;
    align-items: stretch;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-date-editor) {
  width: auto;
}

:deep(.el-button--small) {
  padding: 7px 15px;
}
</style>