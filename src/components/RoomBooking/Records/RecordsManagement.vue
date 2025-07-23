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

      <!-- 中间楼层导航（仅在房间使用记录时显示） -->
      <div v-if="showFloorFilter" class="records-middle-sidebar">
        <div class="search-box">
          <el-input
            v-model="floorSearchKeyword"
            placeholder="搜索楼层..."
            clearable
            size="small"
          >
            <template #prefix>
              <el-icon><search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="floor-list">
          <div 
            v-for="floor in filteredFloors" 
            :key="floor"
            :class="['floor-item', { active: activeFloor === floor }]"
            @click="setActiveFloor(floor)"
          >
            {{ floor }}
          </div>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="records-main-content">
        <div class="records-page">
          <!-- 搜索排序栏 -->
          <div class="search-sort-bar">
            <div class="search-section">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索记录内容..."
                clearable
                style="width: 300px;"
              >
                <template #prefix>
                  <el-icon><search /></el-icon>
                </template>
              </el-input>
            </div>
            <div class="sort-section">
              <span class="sort-label">排序：</span>
              <el-select v-model="sortBy" style="width: 150px;">
                <el-option label="时间降序" value="time_desc" />
                <el-option label="时间升序" value="time_asc" />
                <el-option label="名称升序" value="name_asc" />
                <el-option label="名称降序" value="name_desc" />
              </el-select>
            </div>
          </div>

          <!-- 内容表格 -->
          <div class="content-table">
            <el-table 
              v-loading="loading"
              :data="filteredRecords" 
              style="width: 100%"
              stripe
            >
              <el-table-column prop="title" label="记录标题" min-width="200">
                <template #default="{ row }">
                  <div class="record-title">
                    <el-icon :color="getRecordTypeColor(row.type)">
                      <component :is="getRecordIcon(row.type)" />
                    </el-icon>
                    <span>{{ row.title }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="operator" label="操作人" width="120" />
              <el-table-column prop="createTime" label="创建时间" width="160">
                <template #default="{ row }">
                  <div class="time-info">
                    <el-icon><clock /></el-icon>
                    <span>{{ formatTime(row.createTime) }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusColor(row.status)" size="small">
                    {{ row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="viewRecord(row)">
                    <el-icon><view /></el-icon>
                    查看
                  </el-button>
                  <el-button 
                    v-if="canDelete(row)" 
                    type="danger" 
                    size="small" 
                    @click="deleteRecord(row)"
                  >
                    <el-icon><delete /></el-icon>
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 分页 -->
          <div class="pagination">
            <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 记录详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="记录详情"
      width="600px"
      destroy-on-close
    >
      <div v-if="currentRecord" class="record-detail">
        <div class="detail-item">
          <label>标题：</label>
          <span>{{ currentRecord.title }}</span>
        </div>
        <div class="detail-item">
          <label>类型：</label>
          <span>{{ getRecordTypeLabel(currentRecord.type) }}</span>
        </div>
        <div class="detail-item">
          <label>操作人：</label>
          <span>{{ currentRecord.operator }}</span>
        </div>
        <div class="detail-item">
          <label>创建时间：</label>
          <span>{{ currentRecord.createTime }}</span>
        </div>
        <div class="detail-item">
          <label>状态：</label>
          <el-tag :type="getStatusColor(currentRecord.status)">
            {{ currentRecord.status }}
          </el-tag>
        </div>
        <div class="detail-item full-width">
          <label>详细内容：</label>
          <div class="content-detail">{{ currentRecord.content }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, 
  DocumentChecked, 
  Setting, 
  Search, 
  ArrowDown, 
  Clock, 
  View, 
  Delete,
  Calendar,
  User,
  Connection,
  Warning
} from '@element-plus/icons-vue'

export default {
  name: 'RecordsManagement',
  components: {
    Document,
    DocumentChecked,
    Setting,
    Search,
    ArrowDown,
    Clock,
    View,
    Delete,
    Calendar,
    User,
    Connection,
    Warning
  },
  props: {
    recordsData: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const loading = ref(false)
    const searchKeyword = ref('')
    const floorSearchKeyword = ref('')
    const sortBy = ref('time_desc')
    const activeRecordType = ref('data_booking_records')
    const activeFloor = ref('')
    const expandedGroups = ref(['booking'])

    // 对话框相关
    const detailDialogVisible = ref(false)
    const currentRecord = ref(null)

    // 分页
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    // 记录类型定义
    const bookingRecordTypes = [
      { key: 'data_booking_records', label: '数据借用记录', icon: 'Document' }
    ]

    const operationRecordTypes = [
      { key: 'operation_door_records', label: '运营开门记录', icon: 'Connection' },
      { key: 'access_records', label: '出入记录', icon: 'User' }
    ]

    const floors = ['全部', '1F', '2F', '3F', '4F', '5F', '6F', 'B1', 'B2']

    // 模拟记录数据
    const mockRecordsData = ref([
      {
        id: 1,
        title: '【教师培训】多媒体教室预约成功',
        type: 'data_booking_records',
        operator: '张老师',
        createTime: '2025-07-16 14:30:00',
        status: '成功',
        content: '张老师成功预约多媒体教室（101），用于新教师入职培训，时间：2025-07-20 09:00-12:00'
      },
      {
        id: 2,
        title: '【学生活动】会议室预约被拒绝',
        type: 'data_booking_records',
        operator: '李同学',
        createTime: '2025-07-16 10:15:00',
        status: '失败',
        content: '李同学预约会议室A被拒绝，原因：时间冲突，该时间段已被其他用户预约'
      },
      {
        id: 3,
        title: '【人员进入】张老师进入教学楼',
        type: 'access_records',
        operator: '张老师',
        createTime: '2025-07-16 09:45:00',
        status: '成功',
        content: '张老师通过门禁卡进入教学楼一楼大厅'
      },
      {
        id: 4,
        title: '【开门记录】管理员远程开门',
        type: 'operation_door_records',
        operator: '管理员',
        createTime: '2025-07-16 08:30:00',
        status: '成功',
        content: '管理员远程为访客开启教学楼大门'
      },
      {
        id: 5,
        title: '【人员离开】李同学离开会议室',
        type: 'access_records',
        operator: '李同学',
        createTime: '2025-07-16 03:20:00',
        status: '成功',
        content: '李同学离开会议室A，门禁系统记录离开时间'
      }
    ])

    // 计算属性
    const showFloorFilter = computed(() => {
      return activeRecordType.value.includes('access') || activeRecordType.value.includes('door')
    })

    const filteredFloors = computed(() => {
      if (!floorSearchKeyword.value) return floors
      return floors.filter(floor => 
        floor.toLowerCase().includes(floorSearchKeyword.value.toLowerCase())
      )
    })

    const filteredRecords = computed(() => {
      let data = props.recordsData.length > 0 ? props.recordsData : mockRecordsData.value

      // 按记录类型过滤
      if (activeRecordType.value !== 'all_records') {
        data = data.filter(item => item.type === activeRecordType.value)
      }

      // 按搜索关键词过滤
      if (searchKeyword.value) {
        data = data.filter(item =>
          item.title.includes(searchKeyword.value) ||
          item.operator.includes(searchKeyword.value) ||
          item.content.includes(searchKeyword.value)
        )
      }

      // 排序
      data = [...data].sort((a, b) => {
        switch (sortBy.value) {
          case 'time_desc':
            return new Date(b.createTime) - new Date(a.createTime)
          case 'time_asc':
            return new Date(a.createTime) - new Date(b.createTime)
          case 'name_asc':
            return a.title.localeCompare(b.title)
          case 'name_desc':
            return b.title.localeCompare(a.title)
          default:
            return 0
        }
      })

      pagination.total = data.length

      // 分页
      const start = (pagination.currentPage - 1) * pagination.pageSize
      const end = start + pagination.pageSize
      return data.slice(start, end)
    })

    // 方法
    const toggleGroup = (group) => {
      const index = expandedGroups.value.indexOf(group)
      if (index > -1) {
        expandedGroups.value.splice(index, 1)
      } else {
        expandedGroups.value.push(group)
      }
    }

    const setActiveRecordType = (type) => {
      activeRecordType.value = type
      pagination.currentPage = 1
    }

    const setActiveFloor = (floor) => {
      activeFloor.value = floor === '全部' ? '' : floor
    }

    const getRecordIcon = (type) => {
      const iconMap = {
        'data_booking_records': 'Document',
        'access_records': 'User',
        'operation_door_records': 'Connection'
      }
      return iconMap[type] || 'Document'
    }

    const getRecordTypeColor = (type) => {
      const colorMap = {
        'data_booking_records': '#4A90E2',
        'access_records': '#67C23A',
        'operation_door_records': '#E6A23C'
      }
      return colorMap[type] || '#909399'
    }

    const getRecordTypeLabel = (type) => {
      const labelMap = {
        'data_booking_records': '数据借用记录',
        'access_records': '出入记录',
        'operation_door_records': '运营开门记录'
      }
      return labelMap[type] || '未知类型'
    }

    const getStatusColor = (status) => {
      const colorMap = {
        '成功': 'success',
        '失败': 'danger',
        '错误': 'danger',
        '警告': 'warning',
        '处理中': 'info'
      }
      return colorMap[status] || 'info'
    }

    const formatTime = (timeStr) => {
      return timeStr.replace(' ', '\n')
    }

    const canDelete = (record) => {
      // 只有某些类型的记录可以删除
      return record.type === 'data_booking_records' && record.status === '失败'
    }

    const viewRecord = (record) => {
      currentRecord.value = record
      detailDialogVisible.value = true
    }

    const deleteRecord = async (record) => {
      try {
        await ElMessageBox.confirm(
          `确认删除记录"${record.title}"吗？`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        ElMessage.success('删除成功')
        // 这里添加实际的删除逻辑
      } catch {
        // 用户取消删除
      }
    }

    const handleSizeChange = (val) => {
      pagination.pageSize = val
      pagination.currentPage = 1
    }

    const handleCurrentChange = (val) => {
      pagination.currentPage = val
    }

    onMounted(() => {
      console.log('数据记录模块初始化')
    })

    return {
      loading,
      searchKeyword,
      floorSearchKeyword,
      sortBy,
      activeRecordType,
      activeFloor,
      expandedGroups,
      detailDialogVisible,
      currentRecord,
      pagination,
      bookingRecordTypes,
      operationRecordTypes,
      showFloorFilter,
      filteredFloors,
      filteredRecords,
      toggleGroup,
      setActiveRecordType,
      setActiveFloor,
      getRecordIcon,
      getRecordTypeColor,
      getRecordTypeLabel,
      getStatusColor,
      formatTime,
      canDelete,
      viewRecord,
      deleteRecord,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.records-management {
  min-height: calc(100vh - 120px);
  background: #f9f9f9;
}

.records-layout {
  display: flex;
  height: calc(100vh - 120px);
  gap: 0;
}

/* 数据记录左侧导航 */
.records-sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.records-sidebar .sidebar-header {
  padding: 15px 18px;
  border-bottom: 1px solid #e8e8e8;
  background: #4A90E2;
  color: white;
}

.records-sidebar .sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.records-sidebar .sidebar-menu {
  flex: 1;
  overflow-y: auto;
}

/* 数据记录菜单组样式 */
.records-sidebar .menu-group {
  border-bottom: 1px solid #f0f0f0;
}

.records-sidebar .menu-group-title {
  padding: 12px 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  color: #333;
  background: #fafafa;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.records-sidebar .menu-group-title:hover {
  background: #f0f7ff;
  color: #4A90E2;
}

.records-sidebar .menu-group-title.expanded {
  background: #e6f3ff;
  color: #4A90E2;
  border-left-color: #4A90E2;
}

.records-sidebar .menu-group-title .expand-icon {
  margin-left: auto;
  transition: transform 0.3s;
  font-size: 14px;
}

.records-sidebar .menu-group-title.expanded .expand-icon {
  transform: rotate(180deg);
}

/* 数据记录子菜单样式 */
.records-sidebar .submenu {
  background: white;
}

.records-sidebar .submenu-item {
  padding: 10px 18px 10px 35px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #666;
  font-size: 13px;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.records-sidebar .submenu-item:hover {
  background: #f5f5f5;
  color: #333;
}

.records-sidebar .submenu-item.active {
  background: #e6f3ff;
  color: #4A90E2;
  border-left-color: #4A90E2;
  font-weight: 500;
}

.records-sidebar .submenu-item .el-icon {
  font-size: 13px;
}

/* 中间楼层导航 */
.records-middle-sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  padding: 15px;
}

.records-middle-sidebar .search-box {
  margin-bottom: 15px;
}

.records-middle-sidebar .floor-list {
  flex: 1;
  overflow-y: auto;
}

.records-middle-sidebar .floor-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: #666;
  font-size: 13px;
  margin-bottom: 2px;
}

.records-middle-sidebar .floor-item:hover {
  background: #f5f5f5;
  color: #333;
}

.records-middle-sidebar .floor-item.active {
  background: #4A90E2;
  color: white;
}

/* 数据记录主要内容区域 */
.records-main-content {
  flex: 1;
  background: #f9f9f9;
  overflow-y: auto;
}

.records-page {
  padding: 20px;
  min-height: 100%;
}

/* 搜索排序栏 */
.search-sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
  gap: 15px;
}

.sort-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-label {
  font-size: 14px;
  color: #666;
}

/* 内容表格 */
.content-table {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-table .el-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.content-table .el-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.content-table .el-table td {
  border-bottom: 1px solid #f0f0f0;
}

.content-table .el-table tr:hover {
  background-color: #f5f7fa;
}

.record-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #666;
  white-space: pre-line;
  font-size: 13px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 记录详情对话框 */
.record-detail {
  display: grid;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.detail-item.full-width {
  grid-column: 1 / -1;
  flex-direction: column;
  gap: 8px;
}

.detail-item label {
  font-weight: 500;
  color: #666;
  min-width: 80px;
  flex-shrink: 0;
}

.content-detail {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  border-left: 3px solid #4A90E2;
  line-height: 1.6;
  color: #333;
}
</style>