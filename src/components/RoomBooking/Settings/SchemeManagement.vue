<template>
  <div class="scheme-management-container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>楼栋架构</h3>
      </div>
      <div class="sidebar-content">
        <el-input
          v-model="sidebarSearch"
          placeholder="搜索楼栋"
          :prefix-icon="Search"
          clearable
          class="search-input"
        />
        <el-tree
          :data="buildingTreeData"
          default-expand-all
          class="structure-tree"
          @node-click="handleBuildingNodeClick"
        />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area.js'

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
    
    // 楼栋结构树数据
    const buildingTreeData = ref([])
    
    // 当前选中的楼栋区域信息
    const selectedBuildingArea = ref(null)

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

    // 加载楼栋架构数据
    const loadBuildingTree = async () => {
      try {
        const response = await getAreaTree()
        if (response.code === 200) {
          // 将后端返回的区域树数据转换为前端需要的格式
          const formatTreeData = (nodes) => {
            return nodes.map(node => ({
              label: node.areaName,
              id: node.id,
              type: node.type,
              children: node.children && node.children.length > 0 ? formatTreeData(node.children) : undefined
            }))
          }
          buildingTreeData.value = formatTreeData(response.data || [])
        } else {
          ElMessage.error(response.message || '获取楼栋架构失败')
        }
      } catch (error) {
        console.error('获取楼栋架构失败:', error)
        ElMessage.error('获取楼栋架构失败')
      }
    }

    // 处理楼栋架构树节点点击事件
    const handleBuildingNodeClick = (data) => {
      // 保存选中的区域信息
      selectedBuildingArea.value = {
        id: data.id,
        name: data.label
      }
      ElMessage.success(`已选择区域：${data.label}`)
      // 这里可以根据选中的区域过滤房间数据
      // 例如：根据区域ID过滤roomData
    }

    // 页面初始化
    onMounted(() => {
      loadBuildingTree()
    })

    return {
      sidebarSearch,
      roomNameSearch,
      approvalFilter,
      currentPage,
      pageSize,
      total,
      selectedRows,
      roomData,
      buildingTreeData,
      selectedBuildingArea,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      managePersonnel,
      exportData,
      importData,
      deleteSelected,
      viewDetails,
      batchSetPermissions,
      handleBuildingNodeClick,
      loadBuildingTree,
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
  width: 250px;
  background-color: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.sidebar-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.search-input {
  margin-bottom: 15px;
}

.structure-tree {
  margin-top: 10px;
}

.structure-tree :deep(.el-tree-node__content) {
  height: 32px;
  line-height: 32px;
}

.structure-tree :deep(.el-tree-node__label) {
  font-size: 14px;
  color: #606266;
}

.structure-tree :deep(.el-tree-node:focus > .el-tree-node__content) {
  background-color: #f5f7fa;
}

.structure-tree :deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
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
