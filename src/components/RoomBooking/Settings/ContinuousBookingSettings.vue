<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>连续预约设置</h2>
        <p>配置教室连续预约的天数限制</p>
      </div>
    </div>

    <!-- 提示信息 -->
    <div class="tips-section">
      <p class="tips-text">
        <span class="tips-label">温馨提示：</span>
        <span class="tips-content">
          连续预约天数是指同一用户对同一教室可以连续预约的最大天数。设置为0表示不限制连续预约天数。
        </span>
      </p>
      <p class="example-text">
        例如：设置为3天，则用户最多可以连续预约同一教室3天，第4天需要间隔后才能再次预约。
      </p>
    </div>

    <!-- 设置布局 -->
    <div class="settings-layout-horizontal">
      <!-- 左侧楼层筛选 -->
      <div class="floor-filter-sidebar">
        <div class="search-box">
          <el-input
            v-model="floorSearchKeyword"
            placeholder="搜索楼层"
            clearable
          />
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
        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" @click="batchSetContinuousDays">
            <el-icon><plus /></el-icon>
            批量设置
          </el-button>
          <el-button @click="exportContinuousSettings">
            <el-icon><upload /></el-icon>
            导出设置
          </el-button>
        </div>

        <!-- 教室表格 -->
        <div class="classroom-table">
          <el-table :data="filteredClassrooms" style="width: 100%" border>
            <el-table-column prop="roomName" label="教室名称" width="200" />
            <el-table-column prop="roomType" label="教室类型" width="120" />
            <el-table-column prop="capacity" label="容量" width="80" />
            <el-table-column prop="continuousDays" label="连续预约天数" width="150">
              <template #default="{ row }">
                <span v-if="row.continuousDays === 0" class="no-limit-text">不限制</span>
                <span v-else-if="row.continuousDays <= 7" class="unlimited-text">{{ row.continuousDays }}天</span>
                <span v-else-if="row.continuousDays <= 30" class="monthly-text">{{ row.continuousDays }}天</span>
                <span v-else class="yearly-text">{{ row.continuousDays }}天</span>
              </template>
            </el-table-column>
            <el-table-column prop="floor" label="所在楼层" width="100" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editContinuousDays(row)">编辑</el-button>
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
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'

export default {
  name: 'ContinuousBookingSettings',
  components: {
    Plus,
    Upload
  },
  setup() {
    const selectedFloor = ref('all')
    const floorSearchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(20)
    const totalClassrooms = ref(58)

    // 楼层选项
    const floorOptions = [
      { label: '全部楼层', value: 'all' },
      { label: '1楼', value: '1' },
      { label: '2楼', value: '2' },
      { label: '3楼', value: '3' },
      { label: '4楼', value: '4' },
      { label: '5楼', value: '5' }
    ]

    // 教室数据
    const classroomsData = ref([
      { id: 1, roomName: '多媒体教室101', roomType: '多媒体', capacity: 60, continuousDays: 7, floor: '1楼' },
      { id: 2, roomName: '多媒体教室102', roomType: '多媒体', capacity: 60, continuousDays: 5, floor: '1楼' },
      { id: 3, roomName: '实验室201', roomType: '实验室', capacity: 40, continuousDays: 3, floor: '2楼' },
      { id: 4, roomName: '会议室301', roomType: '会议室', capacity: 20, continuousDays: 0, floor: '3楼' },
      { id: 5, roomName: '多媒体教室401', roomType: '多媒体', capacity: 80, continuousDays: 10, floor: '4楼' }
    ])

    // 过滤后的教室数据
    const filteredClassrooms = computed(() => {
      let filtered = classroomsData.value
      
      if (selectedFloor.value !== 'all') {
        filtered = filtered.filter(room => room.floor === `${selectedFloor.value}楼`)
      }
      
      if (floorSearchKeyword.value) {
        filtered = filtered.filter(room => 
          room.roomName.toLowerCase().includes(floorSearchKeyword.value.toLowerCase()) ||
          room.floor.includes(floorSearchKeyword.value)
        )
      }
      
      return filtered
    })

    const batchSetContinuousDays = () => {
      ElMessage.info('批量设置功能开发中...')
    }

    const exportContinuousSettings = () => {
      ElMessage.success('正在导出教室设置...')
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
      currentPage,
      pageSize,
      totalClassrooms,
      floorOptions,
      classroomsData,
      filteredClassrooms,
      batchSetContinuousDays,
      exportContinuousSettings,
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

.section-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 教室连续预约设置布局 */
.settings-layout-horizontal {
  display: flex;
  gap: 20px;
  min-height: 600px;
}

.floor-filter-sidebar {
  width: 200px;
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

/* 提示信息 */
.tips-section {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border-left: 4px solid #4A90E2;
}

.tips-text {
  margin: 0 0 8px 0;
  font-size: 14px;
}

.tips-label {
  color: #e74c3c;
  font-weight: 600;
}

.tips-content {
  color: #333;
}

.example-text {
  margin: 0;
  color: #e74c3c;
  font-size: 13px;
  line-height: 1.5;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 16px;
}

/* 教室表格 */
.classroom-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.unlimited-text {
  color: #e74c3c;
}

.monthly-text {
  color: #f39c12;
}

.yearly-text {
  color: #27ae60;
}

.no-limit-text {
  color: #27ae60;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>