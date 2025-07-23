<template>
  <div class="records-management">
    <div class="records-layout">
      <div class="building-tree">
        <el-tree
          :data="buildingTree"
          node-key="id"
          :highlight-current="true"
          @node-click="handleBuildingClick"
        />
      </div>
      <div class="main-content">
        <div class="search-bar">
          <el-input
            v-model="roomKeyword"
            placeholder="教室名称"
            clearable
            style="width: 200px;"
          />
          <el-select v-model="sortType" style="width: 180px;">
            <el-option label="预约次数倒序" value="times_desc" />
            <el-option label="预约次数升序" value="times_asc" />
            <el-option label="累计时长倒序" value="duration_desc" />
            <el-option label="累计时长升序" value="duration_asc" />
          </el-select>
        </div>
        <div class="export-buttons">
          <el-button type="primary" @click="exportCurrent">导出当前页</el-button>
          <el-button @click="exportAll">导出全部页</el-button>
        </div>
        <div class="content-table">
          <el-table :data="pagedData" style="width: 100%" stripe>
            <el-table-column prop="roomName" label="预约教室" min-width="180" />
            <el-table-column prop="bookingTimes" label="预约次数" width="100" />
            <el-table-column prop="totalDuration" label="预约累计时长" width="120" />
            <el-table-column prop="totalPeople" label="累计预约人数" width="120" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="openDetail(row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10,20,50]"
            :total="filteredData.length"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </div>
    <el-dialog v-model="detailVisible" title="教室预约详情" width="600px">
      <div v-if="currentRow" class="detail-content">
        <p>教室名称：{{ currentRow.roomName }}</p>
        <p>楼栋：{{ currentRow.building }}</p>
        <p>预约次数：{{ currentRow.bookingTimes }}</p>
        <p>累计时长：{{ currentRow.totalDuration }}</p>
        <p>累计人数：{{ currentRow.totalPeople }}</p>
        <div>
          <p>最近预约记录：</p>
          <ul>
            <li v-for="(rec,index) in currentRow.recent" :key="index">{{ rec }}</li>
          </ul>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible=false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

const buildingTree = [
  { id: 1, label: '达才楼 B2~6F' },
  { id: 2, label: '成彦楼' },
  { id: 3, label: '笃行楼' },
  { id: 4, label: '博雅楼' },
  { id: 5, label: '正芯楼' },
  { id: 6, label: '学华楼' }
]

const allData = ref([
  {
    roomName: '多媒体教室101',
    building: '达才楼 B2~6F',
    bookingTimes: 35,
    totalDuration: 70,
    totalPeople: 300,
    recent: ['2025-07-19 09:00 王同学', '2025-07-18 08:00 李老师']
  },
  {
    roomName: '报告厅201',
    building: '成彦楼',
    bookingTimes: 12,
    totalDuration: 40,
    totalPeople: 120,
    recent: ['2025-07-20 14:00 王老师']
  },
  {
    roomName: '智慧教室305',
    building: '笃行楼',
    bookingTimes: 20,
    totalDuration: 50,
    totalPeople: 150,
    recent: ['2025-07-18 16:00 李同学']
  }
])

const activeBuilding = ref('')
const roomKeyword = ref('')
const sortType = ref('times_desc')
const pagination = reactive({ currentPage: 1, pageSize: 10 })

const handleBuildingClick = (data) => {
  activeBuilding.value = data.label
  pagination.currentPage = 1
}

const filteredData = computed(() => {
  let data = allData.value
  if (activeBuilding.value) {
    data = data.filter(item => item.building === activeBuilding.value)
  }
  if (roomKeyword.value) {
    data = data.filter(item => item.roomName.includes(roomKeyword.value))
  }
  data = [...data].sort((a, b) => {
    switch (sortType.value) {
      case 'times_desc':
        return b.bookingTimes - a.bookingTimes
      case 'times_asc':
        return a.bookingTimes - b.bookingTimes
      case 'duration_desc':
        return b.totalDuration - a.totalDuration
      case 'duration_asc':
        return a.totalDuration - b.totalDuration
      default:
        return 0
    }
  })
  return data
})

const pagedData = computed(() => {
  const start = (pagination.currentPage - 1) * pagination.pageSize
  const end = start + pagination.pageSize
  return filteredData.value.slice(start, end)
})

const detailVisible = ref(false)
const currentRow = ref(null)
const openDetail = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

const exportData = (rows, fileName) => {
  const header = '教室名称,楼栋,预约次数,累计时长,累计人数\n'
  const content = rows
    .map(r => `${r.roomName},${r.building},${r.bookingTimes},${r.totalDuration},${r.totalPeople}`)
    .join('\n')
  const blob = new Blob([header + content], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = fileName
  link.click()
  URL.revokeObjectURL(link.href)
  ElMessage.success('导出成功')
}

const exportCurrent = () => exportData(pagedData.value, 'current-page.csv')
const exportAll = () => exportData(filteredData.value, 'all-data.csv')
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
.building-tree {
  width: 200px;
  padding: 15px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
}
.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
.search-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}
.export-buttons {
  margin-bottom: 15px;
}
.content-table {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}
.pagination {
  display: flex;
  justify-content: center;
}
.detail-content p {
  margin: 8px 0;
}
</style>
