<template>
  <div class="records-main-content">
    <div class="content-layout">
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
      <div class="table-panel">
        <div class="search-sort-bar">
          <div class="search-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索教室名称"
              clearable
              style="width: 220px;"
            >
              <template #prefix>
                <el-icon><search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="sort-section">
            <el-select v-model="sortOrder" style="width: 170px;">
              <el-option label="预约次数降序" value="times_desc" />
              <el-option label="预约累计时长升序" value="duration_asc" />
            </el-select>
            <el-button type="primary" @click="exportCurrent">导出当前页</el-button>
            <el-button type="primary" @click="exportAll">导出全部页</el-button>
          </div>
        </div>

        <div class="content-table">
          <el-table
            v-loading="loading"
            :data="pagedData"
            style="width: 100%"
            stripe
          >
            <el-table-column prop="roomName" label="预约教室" min-width="120" />
            <el-table-column prop="bookingCount" label="预约次数（次）" width="120" />
            <el-table-column prop="totalDuration" label="预约累计时长（分）" width="160" />
            <el-table-column prop="totalPeople" label="累计预约人数（人）" width="150" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="viewDetails(row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

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
      </div>
    </div>
  </div>

  <BorrowDetailDialog
    v-model:visible="detailDialogVisible"
    :room-id="currentRoom.id"
    :room-name="currentRoom.name"
    :room-code="currentRoom.code"
  />
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area'
import { getRoomBookingStats, exportRoomBookingStats } from '@/api/roomBookingManagement'
import BorrowDetailDialog from './BorrowDetailDialog.vue'

// 楼栋结构树数据
const treeData = ref([])
const treeProps = {
  children: 'children',
  label: 'label'
}
const sidebarSearch = ref('')

// 当前选中的楼栋区域信息
const selectedBuildingArea = ref(null)

const searchKeyword = ref('')
const sortOrder = ref('times_desc')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 教室预约统计数据
const statsRecords = ref([])

// 加载教室预约统计数据
const loadRoomBookingStats = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      areaId: selectedBuildingArea.value?.id || undefined,
      roomName: searchKeyword.value || undefined,
      sortBy: getSortField(),
      sortOrder: getSortOrder()
    }
    
    const response = await getRoomBookingStats(params)
    if (response.code === 200) {
      statsRecords.value = response.data.rows || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取教室预约统计失败')
    }
  } catch (error) {
    console.error('获取教室预约统计失败:', error)
    ElMessage.error('获取教室预约统计失败')
  } finally {
    loading.value = false
  }
}

// 获取排序字段
const getSortField = () => {
  switch (sortOrder.value) {
    case 'times_desc':
      return 'bookingCount'
    case 'duration_asc':
      return 'totalDuration'
    default:
      return 'bookingCount'
  }
}

// 获取排序方向
const getSortOrder = () => {
  switch (sortOrder.value) {
    case 'times_desc':
      return 'desc'
    case 'duration_asc':
      return 'asc'
    default:
      return 'desc'
  }
}

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

const pagedData = computed(() => statsRecords.value)

const handleNodeClick = (data) => {
  // 保存选中的区域信息
  selectedBuildingArea.value = {
    id: data.id,
    name: data.label
  }
  // 重置分页到第一页
  currentPage.value = 1
  loadRoomBookingStats()
}

// 导出当前页
async function exportCurrent() {
  try {
    const params = {
      exportType: 'current',
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      areaId: selectedBuildingArea.value?.id || undefined,
      roomName: searchKeyword.value || undefined,
      sortBy: getSortField(),
      sortOrder: getSortOrder()
    }
    
    const response = await exportRoomBookingStats(params)
    if (response.code === 200) {
      ElMessage.success(`导出成功，共${response.data.recordCount}条记录`)
      // 这里可以添加下载文件的逻辑
      // window.open(response.data.fileUrl)
    } else {
      ElMessage.error(response.message || '导出失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导出全部页
async function exportAll() {
  try {
    const params = {
      exportType: 'all',
      areaId: selectedBuildingArea.value?.id || undefined,
      roomName: searchKeyword.value || undefined,
      sortBy: getSortField(),
      sortOrder: getSortOrder()
    }
    
    const response = await exportRoomBookingStats(params)
    if (response.code === 200) {
      ElMessage.success(`导出成功，共${response.data.recordCount}条记录`)
      // 这里可以添加下载文件的逻辑
      // window.open(response.data.fileUrl)
    } else {
      ElMessage.error(response.message || '导出失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

function viewDetails(row) {
  currentRoom.value = { 
    id: row.roomId,
    name: row.roomName, 
    code: row.roomCode || row.roomId // 如果没有roomCode，使用roomId作为fallback
  }
  detailDialogVisible.value = true
}

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
  loadRoomBookingStats()
}

function handleCurrentChange(val) {
  currentPage.value = val
  loadRoomBookingStats()
}

const detailDialogVisible = ref(false)
const currentRoom = ref({})

// 监听搜索关键字变化
watch(searchKeyword, () => {
  currentPage.value = 1
  loadRoomBookingStats()
})

// 监听排序变化
watch(sortOrder, () => {
  currentPage.value = 1
  loadRoomBookingStats()
})

// 页面初始化
onMounted(() => {
  loadBuildingTree()
  loadRoomBookingStats()
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

.search-sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  gap: 15px;
}
.sort-section {
  display: flex;
  align-items: center;
  gap: 10px;
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
</style>
