<template>
  <div class="records-management">
    <div class="records-layout">
      <!-- 左侧记录类型菜单 -->
      <div class="records-sidebar">
        <div class="sidebar-header">
          <h3>数据记录</h3>
        </div>
        <div class="sidebar-menu">
          <!-- 预约记录分组 -->
          <div class="menu-group">
            <div
              :class="['menu-group-title', { expanded: expandedGroups.includes('booking') }]"
              @click="toggleGroup('booking')"
            >
              <el-icon><document /></el-icon>
              <span>预约记录</span>
              <el-icon class="expand-icon"><arrow-down /></el-icon>
            </div>
            <div v-show="expandedGroups.includes('booking')" class="submenu">
              <div
                v-for="item in bookingRecordTypes"
                :key="item.key"
                :class="['submenu-item', { active: activeRecordType === item.key }]"
                @click="setActiveRecordType(item.key)"
              >
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>

          <!-- 运营记录分组 -->
          <div class="menu-group">
            <div
              :class="['menu-group-title', { expanded: expandedGroups.includes('operation') }]"
              @click="toggleGroup('operation')"
            >
              <el-icon><setting /></el-icon>
              <span>运营记录</span>
              <el-icon class="expand-icon"><arrow-down /></el-icon>
            </div>
            <div v-show="expandedGroups.includes('operation')" class="submenu">
              <div
                v-for="item in operationRecordTypes"
                :key="item.key"
                :class="['submenu-item', { active: activeRecordType === item.key }]"
                @click="setActiveRecordType(item.key)"
              >
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 主内容区域 -->
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
    </div>

    <el-dialog v-model="detailDialogVisible" title="详情" width="500px" destroy-on-close>
      <div v-if="currentRow" class="record-detail">
        <div class="detail-item">
          <label>教室：</label>
          <span>{{ currentRow.roomName }}</span>
        </div>
        <div class="detail-item">
          <label>预约次数：</label>
          <span>{{ currentRow.times }}</span>
        </div>
        <div class="detail-item">
          <label>预约累计时长：</label>
          <span>{{ currentRow.duration }} 分钟</span>
        </div>
        <div class="detail-item">
          <label>累计人数：</label>
          <span>{{ currentRow.people }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Document,
  DocumentChecked,
  Setting,
  Search,
  ArrowDown
} from '@element-plus/icons-vue'

const expandedGroups = ref(['booking'])
const activeRecordType = ref('data_booking_records')
const bookingRecordTypes = [{ key: 'data_booking_records', label: '数据借用记录', icon: 'Document' }]
const operationRecordTypes = [
  { key: 'operation_door_records', label: '运营开门记录', icon: 'DocumentChecked' }
]

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
const total = ref(0)

const allRecords = ref([
  { id: 1, roomName: 'A-101', times: 5, duration: 300, people: 40, floor: '1F' },
  { id: 2, roomName: 'A-201', times: 3, duration: 180, people: 30, floor: '2F' },
  { id: 3, roomName: 'B-101', times: 8, duration: 400, people: 100, floor: '1F' },
  { id: 4, roomName: 'B-202', times: 2, duration: 90, people: 20, floor: '2F' }
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
  total.value = data.length
  return data
})

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

function toggleGroup(group) {
  const i = expandedGroups.value.indexOf(group)
  if (i > -1) expandedGroups.value.splice(i, 1)
  else expandedGroups.value.push(group)
}

function setActiveRecordType(type) {
  activeRecordType.value = type
}

function exportCurrent() {
  ElMessage.success('导出当前页')
}

function exportAll() {
  ElMessage.success('导出全部页')
}

function viewDetails(row) {
  currentRow.value = row
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
const currentRow = ref()
</script>

<style scoped>
.records-management {
  min-height: calc(100vh - 120px);
  background: #f9f9f9;
}

.records-layout {
  display: flex;
  height: calc(100vh - 120px);
}

.records-sidebar {
  width: 200px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 15px 18px;
  border-bottom: 1px solid #e8e8e8;
  background: #4a90e2;
  color: #fff;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
}

.menu-group-title {
  padding: 12px 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  background: #fafafa;
  border-left: 3px solid transparent;
}
.menu-group-title.expanded {
  background: #e6f3ff;
  border-left-color: #4a90e2;
}
.submenu-item {
  padding: 10px 18px 10px 35px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #666;
  font-size: 13px;
  border-left: 3px solid transparent;
}
.submenu-item.active {
  background: #e6f3ff;
  color: #4a90e2;
  border-left-color: #4a90e2;
  font-weight: 500;
}

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
.record-detail {
  display: grid;
  gap: 15px;
}
.detail-item {
  display: flex;
  gap: 10px;
}
.detail-item label {
  width: 90px;
  color: #666;
  font-weight: 500;
}
</style>
