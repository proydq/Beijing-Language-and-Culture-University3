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
            <el-table-column prop="times" label="预约次数（次）" width="120" />
            <el-table-column prop="duration" label="预约累计时长（分）" width="160" />
            <el-table-column prop="people" label="累计预约人数（人）" width="150" />
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
    :record-list="currentRecords"
    :room-name="currentRoom.name"
    :room-code="currentRoom.code"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area'
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

const allRecords = ref([
  {
    id: 1,
    roomName: 'A-101',
    roomCode: 'A101',
    times: 5,
    duration: 300,
    people: 40,
    floor: '1F',
    records: [
      {
        id: 1,
        name: '《借阅人生百味》的教室借用',
        time: '2025.04.24 第一节次 第二节次',
        date: '2025-04-24',
        description: '本次借用用于课程研讨与拍摄活动，需使用多媒体设备...',
        applicant: '王强',
        auditStatus: '审核中',
        usageStatus: '未开始'
      },
      {
        id: 2,
        name: '摄影交流会',
        time: '2025.04.26 第三节次',
        date: '2025-04-26',
        description: '摄影社团活动。',
        applicant: '李四',
        auditStatus: '通过',
        usageStatus: '已结束'
      }
    ]
  },
  {
    id: 2,
    roomName: 'A-201',
    roomCode: 'A201',
    times: 3,
    duration: 180,
    people: 30,
    floor: '2F',
    records: [
      {
        id: 1,
        name: '《现代教育技术》期末讨论',
        time: '2025.04.28 第一节次 第二节次',
        date: '2025-04-28',
        description: '讨论课程项目并进行汇报演练。',
        applicant: '李四',
        auditStatus: '通过',
        usageStatus: '进行中'
      }
    ]
  },
  {
    id: 3,
    roomName: 'B-101',
    roomCode: 'B101',
    times: 8,
    duration: 400,
    people: 100,
    floor: '1F',
    records: [
      {
        id: 1,
        name: '摄影培训交流',
        time: '2025.04.15 第五节次 第七节次',
        date: '2025-04-15',
        description: '校内摄影社团培训活动，已完成拍摄练习。',
        applicant: '张华',
        auditStatus: '通过',
        usageStatus: '已结束'
      }
    ]
  },
  {
    id: 4,
    roomName: 'B-202',
    roomCode: 'B202',
    times: 2,
    duration: 90,
    people: 20,
    floor: '2F',
    records: [
      {
        id: 1,
        name: '舞蹈排练活动',
        time: '2025.05.02 第二节次 第四节次',
        date: '2025-05-02',
        description: '因场地冲突，本次借用申请已被拒绝。',
        applicant: '赵梅',
        auditStatus: '拒绝',
        usageStatus: '未开始'
      }
    ]
  }
])

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

const filteredData = computed(() => {
  let data = allRecords.value
  if (selectedBuildingArea.value) {
    // 这里可以根据选中的楼栋区域过滤数据
    // 暂时保留原有的floor过滤逻辑，后续可以根据实际需求调整
    data = data.filter(item => item.floor === selectedBuildingArea.value.name)
  }
  if (searchKeyword.value) {
    data = data.filter(item => item.roomName.includes(searchKeyword.value))
  }
  data = [...data].sort((a, b) => {
    if (sortOrder.value === 'times_desc') return b.times - a.times
    if (sortOrder.value === 'duration_asc') return a.duration - b.duration
    return 0
  })
  return data
})

const total = computed(() => filteredData.value.length)

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredData.value.slice(start, start + pageSize.value)
})

const handleNodeClick = (data) => {
  // 保存选中的区域信息
  selectedBuildingArea.value = {
    id: data.id,
    name: data.label
  }
  // 重置分页到第一页
  currentPage.value = 1
}

function exportCurrent() {
  ElMessage.success('导出当前页')
}

function exportAll() {
  ElMessage.success('导出全部页')
}

function viewDetails(row) {
  currentRecords.value = row.records || []
  currentRoom.value = { name: row.roomName, code: row.roomCode }
  detailDialogVisible.value = true
}

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
}

function handleCurrentChange(val) {
  currentPage.value = val
}

const detailDialogVisible = ref(false)
const currentRecords = ref([])
const currentRoom = ref({})

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
