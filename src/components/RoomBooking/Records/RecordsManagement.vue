<template>
  <div class="records-management">
    <div class="layout">
      <div class="tree-section">
        <el-input
          v-model="filterText"
          placeholder="搜索楼层"
          clearable
          class="tree-search"
        />
        <el-tree
          ref="treeRef"
          :data="treeData"
          node-key="id"
          :props="defaultProps"
          :filter-node-method="filterNode"
          default-expand-all
          highlight-current
          @node-click="handleTreeClick"
        />
      </div>
      <div class="table-section">
        <div class="toolbar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索教室"
            clearable
            class="search-input"
          >
            <template #prefix>
              <el-icon><search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="sortOrder" class="sort-select" placeholder="排序方式">
            <el-option label="预约次数降序" value="timesDesc" />
            <el-option label="预约累计时长升序" value="durationAsc" />
          </el-select>
          <el-button @click="exportCurrent">导出当前页</el-button>
          <el-button @click="exportAll">导出全部页</el-button>
        </div>
        <el-table :data="pagedRecords" stripe style="width: 100%">
          <el-table-column prop="roomName" label="预约教室" min-width="160" />
          <el-table-column prop="times" label="预约次数（次）" width="120" />
          <el-table-column prop="duration" label="预约累计时长（分）" width="160" />
          <el-table-column prop="people" label="累计预约人数（人）" width="160" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" link @click="viewDetails(row)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="filteredRecords.length"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const treeRef = ref(null)
const filterText = ref('')

const treeData = [
  {
    id: 1,
    label: '达才楼',
    children: [
      { id: 11, label: 'B2' },
      { id: 12, label: 'B1' },
      { id: 13, label: '1F' },
      { id: 14, label: '2F' },
      { id: 15, label: '3F' },
      { id: 16, label: '4F' },
      { id: 17, label: '5F' },
      { id: 18, label: '6F' }
    ]
  },
  {
    id: 2,
    label: '成彦楼',
    children: [
      { id: 21, label: 'B2' },
      { id: 22, label: 'B1' },
      { id: 23, label: '1F' },
      { id: 24, label: '2F' },
      { id: 25, label: '3F' },
      { id: 26, label: '4F' },
      { id: 27, label: '5F' },
      { id: 28, label: '6F' }
    ]
  }
]

const defaultProps = {
  children: 'children',
  label: 'label'
}

watch(filterText, (val) => {
  treeRef.value && treeRef.value.filter(val)
})

const filterNode = (value, data) => {
  if (!value) return true
  return data.label.includes(value)
}

const selectedBuilding = ref('')
const selectedFloor = ref('')

const handleTreeClick = (node) => {
  if (node.children) {
    selectedBuilding.value = node.label
    selectedFloor.value = ''
  } else {
    selectedFloor.value = node.label
    const parent = treeData.find((b) => b.children.some((f) => f.id === node.id))
    selectedBuilding.value = parent ? parent.label : ''
  }
  currentPage.value = 1
}

const searchKeyword = ref('')
const sortOrder = ref('timesDesc')

const records = ref([
  {
    id: 1,
    roomName: '达才楼 1F 101',
    times: 10,
    duration: 120,
    people: 60,
    building: '达才楼',
    floor: '1F'
  },
  {
    id: 2,
    roomName: '达才楼 2F 201',
    times: 5,
    duration: 90,
    people: 45,
    building: '达才楼',
    floor: '2F'
  },
  {
    id: 3,
    roomName: '成彦楼 1F 102',
    times: 8,
    duration: 160,
    people: 80,
    building: '成彦楼',
    floor: '1F'
  },
  {
    id: 4,
    roomName: '成彦楼 B1 003',
    times: 12,
    duration: 240,
    people: 110,
    building: '成彦楼',
    floor: 'B1'
  },
  {
    id: 5,
    roomName: '成彦楼 6F 601',
    times: 3,
    duration: 60,
    people: 30,
    building: '成彦楼',
    floor: '6F'
  },
  {
    id: 6,
    roomName: '达才楼 B2 005',
    times: 9,
    duration: 200,
    people: 95,
    building: '达才楼',
    floor: 'B2'
  }
])

const filteredRecords = computed(() => {
  let data = records.value
  if (selectedBuilding.value) {
    data = data.filter((r) => r.building === selectedBuilding.value)
  }
  if (selectedFloor.value) {
    data = data.filter((r) => r.floor === selectedFloor.value)
  }
  if (searchKeyword.value) {
    data = data.filter((r) => r.roomName.includes(searchKeyword.value))
  }
  data = [...data]
  if (sortOrder.value === 'timesDesc') {
    data.sort((a, b) => b.times - a.times)
  } else if (sortOrder.value === 'durationAsc') {
    data.sort((a, b) => a.duration - b.duration)
  }
  return data
})

const currentPage = ref(1)
const pageSize = ref(10)

const pagedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRecords.value.slice(start, end)
})

const exportCurrent = () => {
  ElMessage.info('导出当前页数据')
}

const exportAll = () => {
  ElMessage.info('导出全部数据')
}

const viewDetails = (row) => {
  ElMessage.info(`查看 ${row.roomName} 详情`)
}
</script>

<style scoped>
.records-management {
  padding: 20px;
  background: #f9f9f9;
  min-height: calc(100vh - 120px);
}

.layout {
  display: flex;
  gap: 20px;
}

.tree-section {
  width: 250px;
  background: #fff;
  padding: 20px;
  border-right: 1px solid #ebeef5;
}

.tree-search {
  margin-bottom: 10px;
}

.table-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.search-input {
  width: 200px;
}

.sort-select {
  width: 180px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
