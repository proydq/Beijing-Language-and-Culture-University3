<template>
  <div class="scheme-management-container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="search-section">
        <el-input v-model="sidebarSearch" placeholder="请输入关键词搜索" clearable>
          <template #suffix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="nav-menu">
        <div class="nav-item active">全部</div>
        <div class="nav-item">设置楼</div>
        <div class="nav-item">B2</div>
        <div class="nav-item">B1</div>
        <div class="nav-item">1F</div>
        <div class="nav-item">2F</div>
        <div class="nav-item">3F</div>
        <div class="nav-item">4F</div>
        <div class="nav-item">5F</div>
        <div class="nav-item">6F</div>
        <div class="nav-item">高楼层</div>
        <div class="nav-item">高楼层</div>
        <div class="nav-item">博雅楼</div>
        <div class="nav-item">正学楼</div>
        <div class="nav-item">客学楼</div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 顶部操作栏 -->
      <div class="header-section">
        <div class="search-filters">
          <div class="filter-item">
            <label>房间名称:</label>
            <el-input
              v-model="roomNameSearch"
              placeholder="请输入房间名称或房间号"
              style="width: 200px"
              clearable
            />
          </div>
          <div class="filter-item">
            <label>是否需要审批:</label>
            <el-select v-model="approvalFilter" placeholder="全部" style="width: 120px">
              <el-option label="全部" value="all"></el-option>
              <el-option label="需要" value="yes"></el-option>
              <el-option label="不需要" value="no"></el-option>
            </el-select>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="warning" @click="exportData">导出</el-button>
          <el-button type="success" @click="importData">导入</el-button>
          <el-button type="danger" @click="deleteSelected">删除</el-button>
          <el-button type="primary" @click="viewDetails">添加教室</el-button>
          <el-button type="primary" @click="viewDetails">手动同步</el-button>
          <el-button type="info" @click="batchSetPermissions">批量配置审批权限</el-button>
        </div>
      </div>

      <!-- 表格区域 -->
      <div class="table-section">
        <el-table
          :data="roomData"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="roomName" label="教室" width="150" />
          <el-table-column prop="roomNumber" label="房间号" width="100" />
          <el-table-column prop="location" label="所属楼" width="100" />
          <el-table-column label="是否交换" width="120">
            <template #default="{ row }">
              <el-select v-model="row.isExchange" size="small">
                <el-option label="是" value="yes"></el-option>
                <el-option label="否" value="no"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="是否需要审批" width="140">
            <template #default="{ row }">
              <el-select v-model="row.needApproval" size="small">
                <el-option label="是" value="yes"></el-option>
                <el-option label="否" value="no"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="details" label="申请人" min-width="300">
            <template #default="{ row }">
              <div class="details-content">
                <div class="detail-line">第一级审批：{{ row.firstApprover }}</div>
                <div class="detail-line">第二级审批：{{ row.secondApprover }}</div>
                <div class="detail-line">第三级审批：{{ row.thirdApprover }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="managePersonnel(row)"
              >审批人设置</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-section">
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
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'SchemeManagement',
  components: {
    Search,
  },
  setup() {
    // 搜索和筛选
    const sidebarSearch = ref('')
    const roomNameSearch = ref('')
    const approvalFilter = ref('all')

    // 分页
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(201)

    // 选中的行
    const selectedRows = ref([])

    // 房间数据
    const roomData = ref([
      {
        id: 1,
        roomName: '多媒体教室',
        roomNumber: '101',
        location: '科研楼',
        isExchange: 'yes',
        needApproval: 'yes',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 2,
        roomName: '多媒体教室',
        roomNumber: '102',
        location: '科研楼',
        isExchange: 'yes',
        needApproval: 'yes',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 3,
        roomName: '多媒体教室',
        roomNumber: '103',
        location: '科研楼',
        isExchange: 'no',
        needApproval: 'no',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 4,
        roomName: '多媒体教室',
        roomNumber: '104',
        location: '科研楼',
        isExchange: 'yes',
        needApproval: 'yes',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 5,
        roomName: '多媒体教室',
        roomNumber: '105',
        location: '科研楼',
        isExchange: 'no',
        needApproval: 'no',
        firstApprover: '',
        secondApprover: '',
        thirdApprover: '',
      },
      {
        id: 6,
        roomName: '多媒体教室',
        roomNumber: '106',
        location: '科研楼',
        isExchange: 'no',
        needApproval: 'no',
        firstApprover: '',
        secondApprover: '',
        thirdApprover: '',
      },
    ])

    const handleSelectionChange = (selection) => {
      selectedRows.value = selection
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
    }

    const managePersonnel = (row) => {
      ElMessage.info(`管理人员: ${row.roomName} ${row.roomNumber}`)
    }

    const exportData = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要导出的数据')
        return
      }
      ElMessage.success(`正在导出 ${selectedRows.value.length} 条数据`)
    }

    const importData = () => {
      ElMessage.info('导入功能开发中')
    }

    const deleteSelected = async () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要删除的数据')
        return
      }
      try {
        await ElMessageBox.confirm(
          `确认删除选中的 ${selectedRows.value.length} 条数据吗？`,
          '删除确认',
        )
        ElMessage.success('删除成功')
        selectedRows.value = []
      } catch {
        // 用户取消
      }
    }

    const viewDetails = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要查看的数据')
        return
      }
      ElMessage.info('查看详情功能开发中')
    }

    const batchSetPermissions = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要设置权限的数据')
        return
      }
      ElMessage.info('批量设置权限功能开发中')
    }

    return {
      sidebarSearch,
      roomNameSearch,
      approvalFilter,
      currentPage,
      pageSize,
      total,
      selectedRows,
      roomData,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      managePersonnel,
      exportData,
      importData,
      deleteSelected,
      viewDetails,
      batchSetPermissions,
    }
  },
}
</script>

<style scoped>
.scheme-management-container {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

/* 左侧导航栏 */
.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.search-section {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.nav-menu {
  flex: 1;
  padding: 8px 0;
}

.nav-item {
  padding: 12px 20px;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  transition: all 0.3s;
}

.nav-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.nav-item.active {
  background: #409eff;
  color: white;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 顶部操作栏 */
.header-section {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafbfc;
}

.search-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* 表格区域 */
.table-section {
  flex: 1;
  padding: 0 20px;
  overflow: auto;
}

.details-content {
  line-height: 1.6;
}

.detail-line {
  margin-bottom: 4px;
  font-size: 13px;
  color: #606266;
}

.detail-line:last-child {
  margin-bottom: 0;
}

/* 分页区域 */
.pagination-section {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
  background: #fafbfc;
  display: flex;
  justify-content: center;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-select) {
  width: 100%;
}
</style>
