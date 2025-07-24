<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>教室连续预约设置</h2>
      </div>
    </div>

    <!-- 提示信息 -->
    <div class="tips-section">
      <p class="tips-text">
        <span class="tips-label">PS：</span>
        <span class="tips-content">
          表示连续预约时，房屋连续预约天数的限制规则；
        </span>
      </p>
      <p class="example-text">
        例如：设置可连续预约（16）天，可连续时间为：3月1日-3月16日，第17日起不可预约；单独预约的改：3月17日-4月1日，4月2日不可预约；
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
            <el-table-column type="selection" width="55" />
            <el-table-column prop="roomName" label="预约教室" width="200" />
            <el-table-column prop="roomCode" label="房屋号" width="120" />
            <el-table-column prop="building" label="所属楼" width="120" />
            <el-table-column prop="continuousDays" label="可连续预约天数" width="150">
              <template #default="{ row }">
                <span v-if="row.continuousDays === '不可连续预约'" class="unlimited-text">{{ row.continuousDays }}</span>
                <span v-else-if="row.continuousDays === '一天'" class="monthly-text">{{ row.continuousDays }}</span>
                <span v-else-if="row.continuousDays === '一年'" class="yearly-text">{{ row.continuousDays }}</span>
                <span v-else-if="row.continuousDays === '无限制预约'" class="no-limit-text">{{ row.continuousDays }}</span>
                <span v-else>{{ row.continuousDays }}</span>
              </template>
            </el-table-column>
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

    <!-- 设置可连续预约天数弹出框 -->
    <el-dialog v-model="continuousDaysDialogVisible" title="设置可连续预约天数" width="400px">
      <div class="continuous-days-options">
        <el-radio-group v-model="selectedContinuousDays">
          <el-radio value="unlimited">可连续预约（    ）天无限</el-radio>
          <el-radio value="monthly">可连续预约（    ）个月的</el-radio>
          <el-radio value="yearly">可连续预约（    ）年的</el-radio>
          <el-radio value="no_continuous">不可连续预约</el-radio>
          <el-radio value="no_limit">无限制预约</el-radio>
        </el-radio-group>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="continuousDaysDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmContinuousDays">确认</el-button>
        </span>
      </template>
    </el-dialog>
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
    // 响应式变量
    const selectedFloor = ref('all')
    const floorSearchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(20)
    const totalClassrooms = computed(() => filteredClassrooms.value.length)
    
    // 弹出框相关变量
    const continuousDaysDialogVisible = ref(false)
    const selectedContinuousDays = ref('unlimited')
    const currentEditingRow = ref(null)
    const isBatchMode = ref(false)
    
    // 楼层选项
    const floorOptions = [
      { label: '全部', value: 'all' },
      { label: '达才楼', value: 'dacai' },
      { label: 'B2', value: 'b2' },
      { label: 'B1', value: 'b1' },
      { label: '1F', value: '1f' },
      { label: '2F', value: '2f' },
      { label: '3F', value: '3f' },
      { label: '4F', value: '4f' },
      { label: '5F', value: '5f' },
      { label: '6F', value: '6f' },
      { label: '高层楼', value: 'gaoceng' },
      { label: '博雅楼', value: 'boya' }
    ]

    // 教室数据
    const classroomsData = ref([
      { id: 1, roomName: '多媒体教室（101）', roomCode: '101', building: '科研楼', continuousDays: 15 },
      { id: 2, roomName: '多媒体教室（102）', roomCode: '102', building: '科研楼', continuousDays: 15 },
      { id: 3, roomName: '多媒体教室（103）', roomCode: '103', building: '科研楼', continuousDays: 15 },
      { id: 4, roomName: '多媒体教室（104）', roomCode: '104', building: '科研楼', continuousDays: 15 },
      { id: 5, roomName: '多媒体教室（105）', roomCode: '105', building: '科研楼', continuousDays: '不可连续预约' },
      { id: 6, roomName: '多媒体教室（106）', roomCode: '106', building: '科研楼', continuousDays: '一天' },
      { id: 7, roomName: '多媒体教室（107）', roomCode: '107', building: '科研楼', continuousDays: '一年' },
      { id: 8, roomName: '多媒体教室（108）', roomCode: '108', building: '科研楼', continuousDays: 45 },
      { id: 9, roomName: '多媒体教室（109）', roomCode: '109', building: '科研楼', continuousDays: 15 },
      { id: 10, roomName: '多媒体教室（110）', roomCode: '110', building: '科研楼', continuousDays: '无限制预约' }
    ])

    // 过滤后的教室数据
    const filteredClassrooms = computed(() => {
      let filtered = classroomsData.value
      
      // 按楼层过滤
      if (selectedFloor.value !== 'all') {
        // 根据选择的楼层过滤，这里暂时显示所有数据，因为示例数据都是科研楼
        if (selectedFloor.value === 'gaoceng') {
          filtered = filtered.filter(room => room.building === '科研楼')
        } else {
          filtered = filtered.filter(room => room.building.includes(selectedFloor.value) || room.building === '科研楼')
        }
      }
      
      // 按搜索关键词过滤
      if (floorSearchKeyword.value) {
        filtered = filtered.filter(room => 
          room.roomName.toLowerCase().includes(floorSearchKeyword.value.toLowerCase()) ||
          room.building.includes(floorSearchKeyword.value)
        )
      }
      
      return filtered
    })

    const batchSetContinuousDays = () => {
      isBatchMode.value = true
      currentEditingRow.value = null
      selectedContinuousDays.value = 'unlimited'
      continuousDaysDialogVisible.value = true
    }

    const exportContinuousSettings = () => {
      ElMessage.success('正在导出教室设置...')
    }

    const editContinuousDays = (row) => {
      isBatchMode.value = false
      currentEditingRow.value = row
      selectedContinuousDays.value = 'unlimited'
      continuousDaysDialogVisible.value = true
    }

    const confirmContinuousDays = () => {
      if (isBatchMode.value) {
        ElMessage.success('批量设置连续预约天数成功')
      } else {
        ElMessage.success(`已更新教室 ${currentEditingRow.value.roomName} 的连续预约天数`)
      }
      continuousDaysDialogVisible.value = false
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
      handleCurrentChange,
      continuousDaysDialogVisible,
      selectedContinuousDays,
      currentEditingRow,
      isBatchMode,
      confirmContinuousDays
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

/* 弹出框样式 */
.continuous-days-options {
  padding: 20px 0;
}

.continuous-days-options .el-radio {
  display: block;
  margin-bottom: 16px;
  line-height: 32px;
}

.continuous-days-options .el-radio:last-child {
  margin-bottom: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>