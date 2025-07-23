<template>
  <div class="records-main-content">
    <div class="content-layout">
      <div class="tree-panel">
        <el-input
          v-model="treeFilter"
          placeholder="搜索楼层..."
          clearable
          size="small"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
        <el-tree
          ref="treeRef"
          class="floor-tree"
          :data="buildingTreeData"
          node-key="id"
          highlight-current
          :props="{ children: 'children', label: 'label' }"
          :filter-node-method="filterNode"
          @node-click="handleNodeClick"
        />
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
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import BorrowDetailDialog from './BorrowDetailDialog.vue'

const treeRef = ref()
const treeFilter = ref('')
const buildingTreeData = [
  {
    id: 1,
    label: '教学楼A',
    children: [
      { id: 'A1', label: '1F' },
      { id: 'A2', label: '2F' },
      { id: 'A3', label: '3F' }
    ]
  },
  {
    id: 2,
    label: '教学楼B',
    children: [
      { id: 'B1', label: '1F' },
      { id: 'B2', label: '2F' }
    ]
  }
]
const selectedFloor = ref('')

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

const filteredData = computed(() => {
  let data = allRecords.value
  if (selectedFloor.value) {
    data = data.filter(item => item.floor === selectedFloor.value)
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

function filterNode(value, data) {
  if (!value) return true
  return data.label.includes(value)
}

function handleNodeClick(data) {
  if (data.children) return
  selectedFloor.value = data.label
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
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.floor-tree {
  margin-top: 10px;
  flex: 1;
  overflow: auto;
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
