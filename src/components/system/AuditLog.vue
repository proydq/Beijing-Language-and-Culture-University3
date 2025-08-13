<template>
  <div class="audit-log-container">
    <el-card shadow="never">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="模块">
            <el-select v-model="searchForm.module" placeholder="请选择模块" clearable style="width: 120px">
              <el-option label="用户管理" value="USER" />
              <el-option label="角色管理" value="ROLE" />
              <el-option label="房间管理" value="ROOM" />
              <el-option label="预订管理" value="BOOKING" />
              <el-option label="系统管理" value="SYSTEM" />
            </el-select>
          </el-form-item>
          <el-form-item label="操作">
            <el-select v-model="searchForm.action" placeholder="请选择操作" clearable style="width: 120px">
              <el-option label="查看" value="VIEW" />
              <el-option label="创建" value="CREATE" />
              <el-option label="更新" value="UPDATE" />
              <el-option label="删除" value="DELETE" />
              <el-option label="登录" value="LOGIN" />
              <el-option label="登出" value="LOGOUT" />
            </el-select>
          </el-form-item>
          <el-form-item label="结果">
            <el-select v-model="searchForm.success" placeholder="请选择结果" clearable style="width: 100px">
              <el-option label="成功" :value="1" />
              <el-option label="失败" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 350px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button type="success" @click="handleExport" v-permission="'system:log:export'">
              导出日志
            </el-button>
            <el-button type="warning" @click="handleCleanLogs" v-permission="'system:log:clean'">
              清理日志
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card">
            <div class="stats-content">
              <div class="stats-value">{{ statistics.totalActions }}</div>
              <div class="stats-label">总操作数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card success">
            <div class="stats-content">
              <div class="stats-value">{{ statistics.successCount }}</div>
              <div class="stats-label">成功操作</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card danger">
            <div class="stats-content">
              <div class="stats-value">{{ statistics.failedCount }}</div>
              <div class="stats-label">失败操作</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stats-card info">
            <div class="stats-content">
              <div class="stats-value">{{ statistics.avgDuration }}ms</div>
              <div class="stats-label">平均耗时</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="module" label="模块" width="80">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作" width="80">
          <template #default="scope">
            <el-tag :type="getActionTagType(scope.row.action)" size="small">
              {{ scope.row.action }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permCode" label="权限码" width="150" show-overflow-tooltip />
        <el-table-column prop="requestUrl" label="请求URL" width="150" show-overflow-tooltip />
        <el-table-column prop="requestMethod" label="方法" width="80">
          <template #default="scope">
            <el-tag :type="getMethodTagType(scope.row.requestMethod)" size="small">
              {{ scope.row.requestMethod }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="120" />
        <el-table-column prop="success" label="结果" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.success === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.success === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="耗时(ms)" width="100" />
        <el-table-column prop="requestTime" label="请求时间" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleViewDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pageInfo.currentPage"
          v-model:page-size="pageInfo.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pageInfo.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="操作详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="追踪ID">{{ currentDetail.traceId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentDetail.username }}</el-descriptions-item>
        <el-descriptions-item label="模块">{{ currentDetail.module }}</el-descriptions-item>
        <el-descriptions-item label="操作">{{ currentDetail.action }}</el-descriptions-item>
        <el-descriptions-item label="权限码">{{ currentDetail.permCode }}</el-descriptions-item>
        <el-descriptions-item label="资源类型">{{ currentDetail.resourceType }}</el-descriptions-item>
        <el-descriptions-item label="请求URL">{{ currentDetail.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentDetail.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentDetail.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="操作结果">
          <el-tag :type="currentDetail.success === 1 ? 'success' : 'danger'">
            {{ currentDetail.success === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentDetail.duration }}ms</el-descriptions-item>
        <el-descriptions-item label="请求时间">{{ currentDetail.requestTime }}</el-descriptions-item>
        
        <el-descriptions-item label="请求参数" :span="2">
          <el-input
            type="textarea"
            :rows="4"
            :value="formatJson(currentDetail.requestParams)"
            readonly
          />
        </el-descriptions-item>
        
        <el-descriptions-item label="响应数据" :span="2" v-if="currentDetail.responseData">
          <el-input
            type="textarea"
            :rows="4"
            :value="formatJson(currentDetail.responseData)"
            readonly
          />
        </el-descriptions-item>
        
        <el-descriptions-item label="错误信息" :span="2" v-if="currentDetail.errorMsg">
          <div class="error-msg">{{ currentDetail.errorMsg }}</div>
        </el-descriptions-item>
        
        <el-descriptions-item label="User Agent" :span="2">
          <div class="user-agent">{{ currentDetail.userAgent }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 模拟数据
const mockData = [
  {
    id: '1',
    traceId: 'trace-001',
    username: 'admin',
    module: 'USER',
    action: 'CREATE',
    permCode: 'system:user:create',
    resourceType: 'SysUser',
    requestUrl: '/api/sys-user',
    requestMethod: 'POST',
    ipAddress: '192.168.1.100',
    success: 1,
    duration: 156,
    requestTime: '2024-01-15 10:30:00',
    requestParams: '{"username":"test","email":"test@example.com"}',
    responseData: '{"code":200,"message":"success","data":{"id":123}}',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
  },
  {
    id: '2',
    traceId: 'trace-002',
    username: 'operator',
    module: 'ROOM',
    action: 'VIEW',
    permCode: 'building:room:view',
    resourceType: 'Room',
    requestUrl: '/api/room/list',
    requestMethod: 'GET',
    ipAddress: '192.168.1.101',
    success: 1,
    duration: 89,
    requestTime: '2024-01-15 10:31:00',
    requestParams: '{"page":1,"size":20}',
    responseData: '{"code":200,"data":{"total":50,"content":[...]}}',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
  },
  {
    id: '3',
    traceId: 'trace-003',
    username: 'user1',
    module: 'BOOKING',
    action: 'DELETE',
    permCode: 'booking:booking:delete',
    resourceType: 'Booking',
    requestUrl: '/api/room-booking/123',
    requestMethod: 'DELETE',
    ipAddress: '192.168.1.102',
    success: 0,
    duration: 45,
    requestTime: '2024-01-15 10:32:00',
    requestParams: '{}',
    errorMsg: '权限不足：用户无删除权限',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
  }
]

// 搜索表单
const searchForm = reactive({
  username: '',
  module: '',
  action: '',
  success: null
})

const dateRange = ref([])

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页信息
const pageInfo = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 统计数据
const statistics = reactive({
  totalActions: 0,
  successCount: 0,
  failedCount: 0,
  avgDuration: 0
})

// 详情对话框
const detailDialogVisible = ref(false)
const currentDetail = ref({})

// 获取操作标签类型
const getActionTagType = (action) => {
  const typeMap = {
    'CREATE': 'success',
    'UPDATE': 'warning',
    'DELETE': 'danger',
    'VIEW': 'primary',
    'LOGIN': 'info',
    'LOGOUT': 'info'
  }
  return typeMap[action] || ''
}

// 获取HTTP方法标签类型
const getMethodTagType = (method) => {
  const typeMap = {
    'GET': 'primary',
    'POST': 'success',
    'PUT': 'warning',
    'DELETE': 'danger',
    'PATCH': 'info'
  }
  return typeMap[method] || ''
}

// 格式化JSON
const formatJson = (jsonStr) => {
  if (!jsonStr) return ''
  try {
    return JSON.stringify(JSON.parse(jsonStr), null, 2)
  } catch {
    return jsonStr
  }
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    let filteredData = [...mockData]
    
    // 模拟搜索过滤
    if (searchForm.username) {
      filteredData = filteredData.filter(item => 
        item.username.toLowerCase().includes(searchForm.username.toLowerCase())
      )
    }
    if (searchForm.module) {
      filteredData = filteredData.filter(item => item.module === searchForm.module)
    }
    if (searchForm.action) {
      filteredData = filteredData.filter(item => item.action === searchForm.action)
    }
    if (searchForm.success !== null) {
      filteredData = filteredData.filter(item => item.success === searchForm.success)
    }
    
    tableData.value = filteredData
    pageInfo.total = filteredData.length
    
    // 更新统计数据
    updateStatistics(filteredData)
  } catch (error) {
    ElMessage.error('获取数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStatistics = (data) => {
  statistics.totalActions = data.length
  statistics.successCount = data.filter(item => item.success === 1).length
  statistics.failedCount = data.filter(item => item.success === 0).length
  
  const totalDuration = data.reduce((sum, item) => sum + (item.duration || 0), 0)
  statistics.avgDuration = data.length > 0 ? Math.round(totalDuration / data.length) : 0
}

// 查询
const handleSearch = () => {
  pageInfo.currentPage = 1
  getList()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.module = ''
  searchForm.action = ''
  searchForm.success = null
  dateRange.value = []
  handleSearch()
}

// 查看详情
const handleViewDetail = (row) => {
  currentDetail.value = { ...row }
  detailDialogVisible.value = true
}

// 导出日志
const handleExport = async () => {
  try {
    ElMessage.success('导出功能开发中...')
  } catch (error) {
    ElMessage.error('导出失败：' + (error.message || '未知错误'))
  }
}

// 清理日志
const handleCleanLogs = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清理过期日志吗？此操作将删除30天前的日志记录。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('日志清理成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清理失败：' + (error.message || '未知错误'))
    }
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageInfo.pageSize = val
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pageInfo.currentPage = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.audit-log-container {
  .search-area {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 4px;
  }
  
  .stats-row {
    margin-bottom: 20px;
  }
  
  .stats-card {
    .stats-content {
      text-align: center;
      
      .stats-value {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 8px;
      }
      
      .stats-label {
        font-size: 14px;
        color: #909399;
      }
    }
    
    &.success .stats-value {
      color: #67c23a;
    }
    
    &.danger .stats-value {
      color: #f56c6c;
    }
    
    &.info .stats-value {
      color: #909399;
    }
  }
  
  .pagination-area {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .error-msg {
    color: #f56c6c;
    background-color: #fef0f0;
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #fbc4c4;
  }
  
  .user-agent {
    word-break: break-all;
    font-size: 12px;
    color: #666;
  }
}
</style>