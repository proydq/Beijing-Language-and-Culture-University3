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
            <el-col :span="8">
              <el-input
                v-model="filterForm.basicInfo"
                placeholder="请输入姓名/工号/联系方式"
                clearable
              />
            </el-col>
            <el-col :span="10">
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
            <el-col :span="6" class="btn-col">
              <el-button type="primary" @click="search">搜索</el-button>
              <el-button @click="reset">重置</el-button>
            </el-col>
          </el-row>
        </div>

        <!-- 表格 -->
        <div class="content-table">
          <el-table :data="pagedData" stripe style="width: 100%">
            <el-table-column prop="roomName" label="预约教室" min-width="120" />
            <el-table-column prop="username" label="姓名" width="100" />
            <el-table-column prop="gender" label="性别" width="80" />
            <el-table-column prop="jobNumber" label="工号" width="120" />
            <el-table-column prop="contact" label="联系方式" width="140" />
            <el-table-column prop="openType" label="开门方式" min-width="120" />
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
            :total="filteredData.length"
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
  endTime: ''
})

const allRecords = ref([
  {
    id: 1,
    roomName: 'A-101',
    username: '张三',
    gender: '男',
    jobNumber: 'T001',
    contact: '13800138000',
    openType: '刷卡',
    accessTime: '2024-07-01 08:00:00',
    accessType: '远程开门',
    floor: '1F'
  },
  {
    id: 2,
    roomName: 'A-102',
    username: '李四',
    gender: '女',
    jobNumber: 'T002',
    contact: '13800138001',
    openType: '人脸识别',
    accessTime: '2024-07-01 09:30:00',
    accessType: '远程开门',
    floor: '1F'
  },
  {
    id: 3,
    roomName: 'A-201',
    username: '王五',
    gender: '男',
    jobNumber: 'T003',
    contact: '13800138002',
    openType: '按钮',
    accessTime: '2024-07-01 10:00:00',
    accessType: '远程开门',
    floor: '2F'
  },
  {
    id: 4,
    roomName: 'B-101',
    username: '赵六',
    gender: '女',
    jobNumber: 'T004',
    contact: '13800138003',
    openType: '刷卡',
    accessTime: '2024-07-02 11:20:00',
    accessType: '远程开门',
    floor: '1F'
  },
  {
    id: 5,
    roomName: 'B-202',
    username: '孙七',
    gender: '男',
    jobNumber: 'T005',
    contact: '13800138004',
    openType: '人脸识别',
    accessTime: '2024-07-02 13:40:00',
    accessType: '远程开门',
    floor: '2F'
  },
  {
    id: 6,
    roomName: 'B-203',
    username: '周八',
    gender: '女',
    jobNumber: 'T006',
    contact: '13800138005',
    openType: '按钮',
    accessTime: '2024-07-03 08:10:00',
    accessType: '远程开门',
    floor: '2F'
  },
  {
    id: 7,
    roomName: 'A-103',
    username: '吴九',
    gender: '男',
    jobNumber: 'T007',
    contact: '13800138006',
    openType: '刷卡',
    accessTime: '2024-07-03 09:50:00',
    accessType: '远程开门',
    floor: '1F'
  },
  {
    id: 8,
    roomName: 'A-202',
    username: '郑十',
    gender: '女',
    jobNumber: 'T008',
    contact: '13800138007',
    openType: '人脸识别',
    accessTime: '2024-07-03 11:00:00',
    accessType: '远程开门',
    floor: '2F'
  }
])

const currentPage = ref(1)
const pageSize = ref(10)

const filteredData = computed(() => {
  let data = allRecords.value
  if (selectedBuildingArea.value) {
    // 这里可以根据选中的楼栋区域过滤数据
    // 暂时保留原有的floor过滤逻辑，后续可以根据实际需求调整
    data = data.filter(item => item.floor === selectedBuildingArea.value.name)
  }
  if (filterForm.basicInfo) {
    data = data.filter(item =>
      [item.username, item.jobNumber, item.contact].some(v => v.includes(filterForm.basicInfo))
    )
  }
  if (filterForm.startTime) {
    data = data.filter(item => new Date(item.accessTime) >= new Date(filterForm.startTime))
  }
  if (filterForm.endTime) {
    data = data.filter(item => new Date(item.accessTime) <= new Date(filterForm.endTime))
  }
  return data
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

function search() {
  currentPage.value = 1
}

function reset() {
  filterForm.basicInfo = ''
  filterForm.startTime = ''
  filterForm.endTime = ''
  sidebarSearch.value = ''
  selectedBuildingArea.value = null
  currentPage.value = 1
}

function exportCurrentPage() {
  ElMessage.success('导出当前页')
}

function exportAllPages() {
  ElMessage.success('导出全部页')
}

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
}

function handleCurrentChange(val) {
  currentPage.value = val
}

// 页面初始化
onMounted(() => {
  loadBuildingTree()
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
