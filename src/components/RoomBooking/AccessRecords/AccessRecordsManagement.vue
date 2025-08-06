<template>
  <div class="access-records-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>教室出入记录管理</h2>
      <p class="page-description">查看和管理教室的出入记录，支持多条件筛选和数据导出</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <AccessStatsCards :stats="accessStats" :loading="statsLoading" @refresh="loadAccessStats" />
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <AccessRecordsFilter 
        v-model="filterParams" 
        @search="handleSearch" 
        @reset="handleReset"
        @export="handleExport"
      />
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <AccessRecordsTable 
        :records="accessRecords"
        :loading="tableLoading"
        :total="total"
        :current-page="currentPage"
        :page-size="pageSize"
        @page-change="handlePageChange"
        @size-change="handleSizeChange"
        @view-detail="handleViewDetail"
      />
    </div>

    <!-- 详情弹窗 -->
    <AccessRecordDetailDialog 
      v-model:visible="detailDialogVisible"
      :record="selectedRecord"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import AccessStatsCards from './AccessStatsCards.vue'
import AccessRecordsFilter from './AccessRecordsFilter.vue'
import AccessRecordsTable from './AccessRecordsTable.vue'
import AccessRecordDetailDialog from './AccessRecordDetailDialog.vue'
import { accessRecordsApi } from '@/api/accessRecords.js'

// 响应式数据
const accessRecords = ref([])
const accessStats = ref({})
const tableLoading = ref(false)
const statsLoading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 筛选参数
const filterParams = reactive({
  areaId: '',
  roomId: '',
  basicInfo: '',
  startTime: '',
  endTime: '',
  openMethod: '',
  accessType: ''
})

// 详情弹窗
const detailDialogVisible = ref(false)
const selectedRecord = ref(null)

// 加载出入记录列表
const loadAccessRecords = async () => {
  try {
    tableLoading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...filterParams
    }
    
    const response = await accessRecordsApi.getAccessRecords(params)
    
    if (response.success) {
      accessRecords.value = response.result?.rows || []
      total.value = response.result?.total || 0
    } else {
      ElMessage.error(response.message || '获取出入记录失败')
    }
  } catch (error) {
    console.error('加载出入记录失败:', error)
    ElMessage.error('加载出入记录失败')
  } finally {
    tableLoading.value = false
  }
}

// 加载统计数据
const loadAccessStats = async () => {
  try {
    statsLoading.value = true
    const params = {}
    
    // 如果有时间筛选条件，加入统计查询
    if (filterParams.startTime) {
      params.startTime = filterParams.startTime
    }
    if (filterParams.endTime) {
      params.endTime = filterParams.endTime
    }
    if (filterParams.areaId) {
      params.areaId = filterParams.areaId
    }
    
    const response = await accessRecordsApi.getAccessStats(params)
    
    if (response.success) {
      accessStats.value = response.result || {}
    } else {
      ElMessage.error(response.message || '获取统计数据失败')
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    statsLoading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadAccessRecords()
  loadAccessStats()
}

// 重置处理
const handleReset = () => {
  Object.keys(filterParams).forEach(key => {
    filterParams[key] = ''
  })
  currentPage.value = 1
  loadAccessRecords()
  loadAccessStats()
}

// 导出处理
const handleExport = async (exportType = 'current') => {
  try {
    const params = {
      exportType,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...filterParams
    }
    
    const response = await accessRecordsApi.exportAccessRecords(params)
    
    if (response.success) {
      const { fileUrl, fileName } = response.result
      
      // 创建下载链接
      const link = document.createElement('a')
      link.href = fileUrl
      link.download = fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      ElMessage.success('导出成功')
    } else {
      ElMessage.error(response.message || '导出失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  loadAccessRecords()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadAccessRecords()
}

// 查看详情
const handleViewDetail = (record) => {
  selectedRecord.value = record
  detailDialogVisible.value = true
}

// 初始化数据
onMounted(() => {
  loadAccessRecords()
  loadAccessStats()
})
</script>

<style scoped>
.access-records-management {
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

.stats-section {
  margin-bottom: 20px;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.table-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>