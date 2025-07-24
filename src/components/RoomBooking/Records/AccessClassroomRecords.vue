<template>
  <div class="records-main-content">
    <div class="content-layout">
      <!-- 左侧楼栋树 -->
      <div class="tree-panel">
        <el-input
          v-model="treeKeyword"
          placeholder="请输入教室/实验室/教研室信息"
          clearable
          size="small"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
        <el-tree
          ref="treeRef"
          class="building-tree"
          :data="buildingTree"
          node-key="id"
          highlight-current
          :props="{ children: 'children', label: 'label' }"
          :filter-node-method="filterNode"
          @node-click="handleNodeClick"
        />
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
          <el-table :data="pagedData" stripe style="width: 100%">
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
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const treeRef = ref()
const treeKeyword = ref('')
const buildingTree = [
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

watch(treeKeyword, (val) => {
  treeRef.value && treeRef.value.filter(val)
})

function filterNode(value, data) {
  if (!value) return true
  return data.label.includes(value)
}

function handleNodeClick(node) {
  if (node.children) return
  selectedFloor.value = node.label
  currentPage.value = 1
}

const filterForm = reactive({
  basicInfo: '',
  startTime: '',
  endTime: '',
  openMethod: '',
  accessType: ''
})

const allRecords = ref([
  {
    id: 1,
    roomName: 'A-101',
    name: '张三',
    gender: '男',
    employeeId: 'T001',
    phone: '13800138000',
    openMethod: '刷卡',
    accessTime: '2024-06-27 08:00:00',
    accessType: '预约权限',
    floor: '1F'
  },
  {
    id: 2,
    roomName: 'A-102',
    name: '李四',
    gender: '女',
    employeeId: 'T002',
    phone: '13800138001',
    openMethod: '人脸识别',
    accessTime: '2024-06-27 09:20:00',
    accessType: '预约权限',
    floor: '1F'
  },
  {
    id: 3,
    roomName: 'A-201',
    name: '王五',
    gender: '男',
    employeeId: 'T003',
    phone: '13800138002',
    openMethod: '刷卡',
    accessTime: '2024-06-27 10:30:00',
    accessType: '永久权限',
    floor: '2F'
  },
  {
    id: 4,
    roomName: 'A-301',
    name: '赵六',
    gender: '女',
    employeeId: 'T004',
    phone: '13800138003',
    openMethod: '按钮',
    accessTime: '2024-06-27 11:40:00',
    accessType: '预约权限',
    floor: '3F'
  },
  {
    id: 5,
    roomName: 'B-101',
    name: '孙七',
    gender: '男',
    employeeId: 'T005',
    phone: '13800138004',
    openMethod: '人脸识别',
    accessTime: '2024-06-28 08:10:00',
    accessType: '永久权限',
    floor: '1F'
  },
  {
    id: 6,
    roomName: 'B-202',
    name: '周八',
    gender: '女',
    employeeId: 'T006',
    phone: '13800138005',
    openMethod: '刷卡',
    accessTime: '2024-06-28 09:50:00',
    accessType: '预约权限',
    floor: '2F'
  },
  {
    id: 7,
    roomName: 'B-203',
    name: '吴九',
    gender: '男',
    employeeId: 'T007',
    phone: '13800138006',
    openMethod: '按钮',
    accessTime: '2024-06-28 10:00:00',
    accessType: '预约权限',
    floor: '2F'
  },
  {
    id: 8,
    roomName: 'A-103',
    name: '郑十',
    gender: '女',
    employeeId: 'T008',
    phone: '13800138007',
    openMethod: '刷卡',
    accessTime: '2024-06-29 12:00:00',
    accessType: '永久权限',
    floor: '1F'
  }
])

const currentPage = ref(1)
const pageSize = ref(10)

const filteredData = computed(() => {
  let data = allRecords.value
  if (selectedFloor.value) {
    data = data.filter((item) => item.floor === selectedFloor.value)
  }
  if (filterForm.basicInfo) {
    data = data.filter((item) =>
      [item.name, item.employeeId, item.phone].some((v) => v.includes(filterForm.basicInfo))
    )
  }
  if (filterForm.openMethod) {
    data = data.filter((item) => item.openMethod === filterForm.openMethod)
  }
  if (filterForm.accessType) {
    data = data.filter((item) => item.accessType === filterForm.accessType)
  }
  if (filterForm.startTime) {
    data = data.filter((item) => new Date(item.accessTime) >= new Date(filterForm.startTime))
  }
  if (filterForm.endTime) {
    data = data.filter((item) => new Date(item.accessTime) <= new Date(filterForm.endTime))
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
  filterForm.openMethod = ''
  filterForm.accessType = ''
  treeKeyword.value = ''
  selectedFloor.value = ''
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

.building-tree {
  margin-top: 10px;
  flex: 1;
  overflow: auto;
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
