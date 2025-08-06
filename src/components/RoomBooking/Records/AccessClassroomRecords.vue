<template>
  <div class="records-main-content">
    <div class="content-layout">
      <!-- 左侧楼栋树 -->
      <div class="tree-panel">
        <div class="sidebar-header">
          <h3>楼栋架构</h3>
        </div>
        <div class="sidebar-content">
          <el-input
            v-model="sidebarSearch"
            placeholder="搜索楼栋"
            :prefix-icon="Search"
            clearable
            class="search-input"
          />
          <el-tree
            :data="treeData"
            :props="treeProps"
            node-key="id"
            :default-expanded-keys="['all']"
            :highlight-current="true"
            @node-click="handleNodeClick"
            class="structure-tree"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ data.label }}</span>
              </span>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧表格区域 -->
      <div class="table-panel">
        <!-- 筛选区 -->
        <div class="search-area">
          <el-row :gutter="20" class="search-row">
            <el-col :span="6">
              <el-input
                v-model="filterForm.basicInfo"
                placeholder="请输入姓名/工号/联系方式"
                clearable
              />
            </el-col>
            <el-col :span="8">
              <div class="date-range">
                <el-date-picker
                  v-model="filterForm.startTime"
                  type="datetime"
                  placeholder="开始时间"
                  style="width: 100%"
                />
                <span class="separator">~</span>
                <el-date-picker
                  v-model="filterForm.endTime"
                  type="datetime"
                  placeholder="结束时间"
                  style="width: 100%"
                />
              </div>
            </el-col>
            <el-col :span="4">
              <el-select v-model="filterForm.openMethod" placeholder="开门方式" clearable style="width: 100%">
                <el-option label="全部" value="" />
                <el-option label="刷卡" value="刷卡" />
                <el-option label="人脸识别" value="人脸识别" />
                <el-option label="按钮" value="按钮" />
              </el-select>
            </el-col>
            <el-col :span="4">
              <el-select v-model="filterForm.accessType" placeholder="通行类型" clearable style="width: 100%">
                <el-option label="全部" value="" />
                <el-option label="预约权限" value="预约权限" />
                <el-option label="永久权限" value="永久权限" />
              </el-select>
            </el-col>
            <el-col :span="2" class="btn-col">
              <el-button type="primary" @click="search">搜索</el-button>
              <el-button @click="reset">重置</el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 表格 -->
        <div class="content-table">
          <el-table :data="pagedData" v-loading="loading" stripe style="width: 100%">
            <el-table-column prop="roomName" label="预约教室" min-width="120" />
            <el-table-column prop="name" label="姓名" width="100" />
            <el-table-column prop="gender" label="性别" width="80" />
            <el-table-column prop="employeeId" label="工号" width="120" />
            <el-table-column prop="phone" label="联系方式" width="140" />
            <el-table-column prop="openMethod" label="开门方式" min-width="120" />
            <el-table-column prop="accessTime" label="通行时间" min-width="180" />
            <el-table-column prop="accessType" label="通行类型" min-width="120" />
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>

        <!-- 导出按钮区域 -->
        <div class="export-area">
          <el-button type="primary" @click="exportCurrentPage">导出当前页</el-button>
          <el-button type="primary" @click="exportAllPages">导出全部页</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area'
import { accessRecordsApi } from '@/api/accessRecords.js'

// 楼栋结构树数据
const treeData = ref([])
const treeProps = {
  children: 'children',
  label: 'label'
}
const sidebarSearch = ref('')

// 当前选中的楼栋区域信息
const selectedBuildingArea = ref(null)

// 加载楼栋架构数据
const loadBuildingTree = async () => {
  try {
    const response = await getAreaTree()
    if (response.code === 200) {
      // 将后端返回的区域树数据转换为前端需要的格式
      const formatTreeData = (nodes) => {
        return nodes.map(node => ({
          label: node.areaName,
          id: node.id,
          type: node.type,
          children: node.children && node.children.length > 0 ? formatTreeData(node.children) : undefined
        }))
      }
      treeData.value = formatTreeData(response.data || [])
    } else {
      ElMessage.error(response.message || '获取楼栋架构失败')
    }
  } catch (error) {
    console.error('获取楼栋架构失败:', error)
    ElMessage.error('获取楼栋架构失败')
  }
}

const handleNodeClick = (data) => {
  // 保存选中的区域信息
  selectedBuildingArea.value = {
    id: data.id,
    name: data.label
  }
  // 重置分页到第一页
  currentPage.value = 1
}

const filterForm = reactive({
  basicInfo: '',
  startTime: '',
  endTime: '',
  openMethod: '',
  accessType: ''
})

// 出入记录数据
const allRecords = ref([])
const loading = ref(false)
const total = ref(0)

const currentPage = ref(1)
const pageSize = ref(10)

// 加载出入记录数据
const loadAccessRecords = async () => {
  try {
    loading.value = true
    
    // 构建查询参数
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      basicInfo: filterForm.basicInfo || undefined,
      openMethod: filterForm.openMethod || undefined,
      accessType: filterForm.accessType || undefined,
      areaId: selectedBuildingArea.value?.id || undefined
    }
    
    // 处理时间参数
    if (filterForm.startTime) {
      params.startTime = formatDateTime(filterForm.startTime)
    }
    if (filterForm.endTime) {
      params.endTime = formatDateTime(filterForm.endTime)
    }
    
    const response = await accessRecordsApi.getAccessRecords(params)
    
    if (response.code === 200) {
      allRecords.value = response.data?.rows || []
      total.value = response.data?.total || 0
    } else {
      ElMessage.error(response.message || '获取出入记录失败')
    }
  } catch (error) {
    console.error('加载出入记录失败:', error)
    ElMessage.error('加载出入记录失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.getFullYear() + '-' +
    String(d.getMonth() + 1).padStart(2, '0') + '-' +
    String(d.getDate()).padStart(2, '0') + ' ' +
    String(d.getHours()).padStart(2, '0') + ':' +
    String(d.getMinutes()).padStart(2, '0') + ':' +
    String(d.getSeconds()).padStart(2, '0')
}

// 直接使用API返回的数据，不需要前端过滤
const pagedData = computed(() => {
  return allRecords.value
})

function search() {
  currentPage.value = 1
  loadAccessRecords()
}

function reset() {
  filterForm.basicInfo = ''
  filterForm.startTime = ''
  filterForm.endTime = ''
  filterForm.openMethod = ''
  filterForm.accessType = ''
  sidebarSearch.value = ''
  selectedBuildingArea.value = null
  currentPage.value = 1
  loadAccessRecords()
}

// 导出当前页
async function exportCurrentPage() {
  try {
    const params = {
      exportType: 'current',
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      basicInfo: filterForm.basicInfo || undefined,
      openMethod: filterForm.openMethod || undefined,
      accessType: filterForm.accessType || undefined,
      areaId: selectedBuildingArea.value?.id || undefined
    }
    
    // 处理时间参数
    if (filterForm.startTime) {
      params.startTime = formatDateTime(filterForm.startTime)
    }
    if (filterForm.endTime) {
      params.endTime = formatDateTime(filterForm.endTime)
    }
    
    const response = await accessRecordsApi.exportAccessRecords(params)
    
    if (response.code === 200) {
      const { fileUrl, fileName } = response.data
      
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

// 导出全部页
async function exportAllPages() {
  try {
    const params = {
      exportType: 'all',
      basicInfo: filterForm.basicInfo || undefined,
      openMethod: filterForm.openMethod || undefined,
      accessType: filterForm.accessType || undefined,
      areaId: selectedBuildingArea.value?.id || undefined
    }
    
    // 处理时间参数
    if (filterForm.startTime) {
      params.startTime = formatDateTime(filterForm.startTime)
    }
    if (filterForm.endTime) {
      params.endTime = formatDateTime(filterForm.endTime)
    }
    
    const response = await accessRecordsApi.exportAccessRecords(params)
    
    if (response.code === 200) {
      const { fileUrl, fileName } = response.data
      
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

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
  loadAccessRecords()
}

function handleCurrentChange(val) {
  currentPage.value = val
  loadAccessRecords()
}

// 监听选中区域变化，重新加载数据
watch(() => selectedBuildingArea.value, () => {
  if (selectedBuildingArea.value) {
    currentPage.value = 1
    loadAccessRecords()
  }
}, { deep: true })

// 页面初始化
onMounted(() => {
  loadBuildingTree()
  loadAccessRecords()
})
</script>

<style scoped>
.records-main-content {
  flex: 1;
  background: #f9f9f9;
  overflow-y: auto;
  padding: 20px;
}

.content-layout {
  display: flex;
  gap: 20px;
  height: 100%;
}

.tree-panel {
  width: 220px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.sidebar-content {
  padding: 15px;
}

.search-input {
  margin-bottom: 10px;
}

.structure-tree {
  font-size: 14px;
}

.structure-tree :deep(.el-tree-node__content) {
  height: 36px;
  padding: 0 15px;
}

.structure-tree :deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

.structure-tree :deep(.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
  color: #409eff;
}

.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
  font-size: 14px;
}

.table-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.search-area {
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 6px;
}

.separator {
  margin: 0 4px;
}

.btn-col {
  display: flex;
  gap: 8px;
  align-items: center;
}

.content-table {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.export-area {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-bottom: 10px;
}
</style>
