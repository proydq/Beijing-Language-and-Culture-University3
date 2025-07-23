<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>违规配置</h2>
      </div>
    </div>

    <!-- 设置布局 -->
    <div class="settings-layout-horizontal">
      <!-- 左侧楼层筛选 -->
      <div class="floor-filter-sidebar">
        <div class="search-box">
          <el-input
            v-model="floorSearchKeyword"
            placeholder="请输入要查找项目"
            clearable
          >
            <template #suffix>
              <el-icon><search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="floor-list">
          <div
            v-for="floor in floorOptions"
            :key="floor.value"
            :class="['floor-item', { active: selectedFloor === floor.value }]"
            @click="selectedFloor = floor.value"
          >
            {{ floor.label }}
          </div>
        </div>
      </div>

      <!-- 右侧主要内容 -->
      <div class="main-content-area">
        <!-- 操作区域 -->
        <div class="action-area">
          <div class="search-section">
            <span class="search-label">教室名称：</span>
            <el-input
              v-model="roomSearchKeyword"
              placeholder="请输入实验室名称或房间号"
              style="width: 300px; margin-right: 20px;"
              clearable
            />
            <el-button type="primary" @click="batchSetContinuousDays">
              批量修改连续预约条件
            </el-button>
          </div>
        </div>

        <!-- 教室表格 -->
        <div class="classroom-table">
          <el-table :data="filteredClassrooms" style="width: 100%" border>
            <el-table-column type="selection" width="55" />
            <el-table-column prop="roomName" label="预约教室" min-width="200" />
            <el-table-column prop="roomNumber" label="房间号" width="100" />
            <el-table-column prop="floor" label="所属楼" width="100" />
            <el-table-column prop="continuousDays" label="超时次数" width="100" />
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="warning" size="small" @click="editContinuousDays(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalClassrooms"
            layout="prev, pager, next, jumper, total, sizes"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
          <div class="pagination-info">
            共 {{ totalClassrooms }} 条
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'ViolationSettings',
  components: {
    Search
  },
  setup() {
    const selectedFloor = ref('all')
    const floorSearchKeyword = ref('')
    const roomSearchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalClassrooms = ref(2010)

    // 楼层选项
    const floorOptions = [
      { label: '全部', value: 'all' },
      { label: '达才楼', value: 'dcl' },
      { label: 'B2', value: 'B2' },
      { label: 'B1', value: 'B1' },
      { label: '1F', value: '1F' },
      { label: '2F', value: '2F' },
      { label: '3F', value: '3F' },
      { label: '4F', value: '4F' },
      { label: '5F', value: '5F' },
      { label: '6F', value: '6F' },
      { label: '成德楼', value: 'cdl' },
      { label: '善行楼', value: 'sxl' },
      { label: '博雅楼', value: 'byl' },
      { label: '正志楼', value: 'zzl' },
      { label: '善华楼', value: 'shl' }
    ]

    // 教室数据
    const classroomsData = ref([
      { id: 1, roomName: '多媒体教室（101）', roomNumber: '101', floor: '科研楼', continuousDays: 5 },
      { id: 2, roomName: '多媒体教室（102）', roomNumber: '102', floor: '科研楼', continuousDays: 5 },
      { id: 3, roomName: '多媒体教室（103）', roomNumber: '103', floor: '科研楼', continuousDays: 5 },
      { id: 4, roomName: '多媒体教室（104）', roomNumber: '104', floor: '科研楼', continuousDays: 5 },
      { id: 5, roomName: '多媒体教室（105）', roomNumber: '105', floor: '科研楼', continuousDays: 5 },
      { id: 6, roomName: '多媒体教室（106）', roomNumber: '106', floor: '科研楼', continuousDays: 5 },
      { id: 7, roomName: '多媒体教室（107）', roomNumber: '107', floor: '科研楼', continuousDays: 5 },
      { id: 8, roomName: '多媒体教室（108）', roomNumber: '108', floor: '科研楼', continuousDays: 5 },
      { id: 9, roomName: '多媒体教室（109）', roomNumber: '109', floor: '科研楼', continuousDays: 5 },
      { id: 10, roomName: '多媒体教室（110）', roomNumber: '110', floor: '科研楼', continuousDays: 5 },
      { id: 11, roomName: '多媒体教室（111）', roomNumber: '111', floor: '科研楼', continuousDays: 5 }
    ])

    // 过滤后的教室数据
    const filteredClassrooms = computed(() => {
      let filtered = classroomsData.value
      
      if (selectedFloor.value !== 'all') {
        filtered = filtered.filter(room => room.floor.includes(selectedFloor.value))
      }
      
      if (roomSearchKeyword.value) {
        filtered = filtered.filter(room => 
          room.roomName.toLowerCase().includes(roomSearchKeyword.value.toLowerCase()) ||
          room.roomNumber.includes(roomSearchKeyword.value)
        )
      }
      
      return filtered
    })

    const batchSetContinuousDays = () => {
      ElMessage.info('批量修改连续预约条件功能开发中...')
    }

    const editContinuousDays = (row) => {
      ElMessage.info(`编辑教室: ${row.roomName}`)
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
    }

    return {
      selectedFloor,
      floorSearchKeyword,
      roomSearchKeyword,
      currentPage,
      pageSize,
      totalClassrooms,
      floorOptions,
      classroomsData,
      filteredClassrooms,
      batchSetContinuousDays,
      editContinuousDays,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.setting-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

/* 水平布局 */
.settings-layout-horizontal {
  display: flex;
  gap: 20px;
  min-height: 600px;
}

.floor-filter-sidebar {
  width: 240px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  height: fit-content;
}

.floor-filter-sidebar .search-box {
  margin-bottom: 16px;
}

.floor-filter-sidebar .floor-list {
  max-height: 400px;
  overflow-y: auto;
}

.floor-filter-sidebar .floor-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: #666;
  font-size: 14px;
  margin-bottom: 4px;
}

.floor-filter-sidebar .floor-item:hover {
  background: #f5f5f5;
  color: #333;
}

.floor-filter-sidebar .floor-item.active {
  background: #4A90E2;
  color: white;
}

.main-content-area {
  flex: 1;
}

/* 操作区域 */
.action-area {
  margin-bottom: 16px;
}

.search-section {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.search-label {
  font-size: 14px;
  color: #333;
  margin-right: 10px;
  white-space: nowrap;
}

/* 教室表格 - 修复右侧空白问题 */
.classroom-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
}

/* 关键：表格样式优化，解决右侧空白 */
:deep(.el-table) {
  width: 100% !important;
  table-layout: auto !important; /* 改为自动布局 */
}

:deep(.el-table__header-wrapper) {
  width: 100% !important;
}

:deep(.el-table__body-wrapper) {
  width: 100% !important;
}

:deep(.el-table th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table .cell) {
  padding: 0 12px;
  word-break: break-word;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  width: 100%;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}
</style>