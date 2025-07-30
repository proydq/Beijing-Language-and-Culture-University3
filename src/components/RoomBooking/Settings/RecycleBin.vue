<template>
  <div class="recycle-bin-container">

    <!-- 左侧导航 -->
    <div class="main-content">
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
          :data="treeData"
          :props="treeProps"
          node-key="id"
          :default-expanded-keys="['all']"
            :highlight-current="true"
            @node-click="handleNodeClick"
            class="structure-tree"
          >
          <template #default="{ node, data }">
            <span class="tree-node">
              <span>{{ data.label }}</span>
            </span>
          </template>
          </el-tree>
        </div>
      </div>

      <!-- 主要内容区域 -->
      <div class="content-area">
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
                @keyup.enter="searchRooms"
              />
            </div>
            <div class="filter-item">
              <label>是否需要审批:</label>
              <el-select v-model="approvalFilter" placeholder="全部" style="width: 120px" @change="searchRooms">
                <el-option label="全部" value="all"></el-option>
                <el-option label="需要" value="yes"></el-option>
                <el-option label="不需要" value="no"></el-option>
              </el-select>
            </div>
            <div class="filter-item">
              <el-button type="primary" @click="searchRooms">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </div>
          </div>

          <div class="action-buttons">
            <el-button type="primary" @click="batchRestore" :disabled="selectedRows.length === 0">批量恢复</el-button>
            <el-button type="danger" @click="batchPermanentDelete" :disabled="selectedRows.length === 0">永久删除</el-button>
            <el-button type="danger" plain @click="clearAll">清空回收站</el-button>
          </div>
        </div>
        <el-table
          :data="recycleData"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="roomName" label="教室" width="150" />
          <el-table-column prop="roomNo" label="房间号" width="100" />
          <el-table-column prop="roomAreaName" label="所属楼" width="100" />
          <el-table-column label="是否支持自选审批人" width="160">
            <template #default="{ row }">
              <el-select v-model="row.allowBookerSelectApprover" size="small" disabled>
                <el-option label="是" value="yes"></el-option>
                <el-option label="否" value="no"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="是否需要审批" width="140">
            <template #default="{ row }">
              <el-select v-model="row.needApproval" size="small" disabled>
                <el-option label="是" value="yes"></el-option>
                <el-option label="否" value="no"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="details" label="申请人" min-width="300">
            <template #default="{ row }">
              <div class="details-content">
                <div class="detail-line">第一级审批：{{ row.firstApprover || '-' }}</div>
                <div class="detail-line">第二级审批：{{ row.secondApprover || '-' }}</div>
                <div class="detail-line">第三级审批：{{ row.thirdApprover || '-' }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="restoreItem(row)">恢复设置</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
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
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area'
import { searchDeletedClassrooms, restoreClassroom, batchRestoreClassrooms, permanentDeleteClassroom, batchPermanentDeleteClassrooms, clearRecycleBin, getRecycleBinStats } from '@/api/recycleBin'

export default {
  name: 'RecycleBin',
  components: {
    Search
  },
  setup() {
    // 搜索条件
    const roomNameSearch = ref('')
    const approvalFilter = ref('all')
    const sidebarSearch = ref('')

    // 分页数据
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    // 选中的数据
    const selectedRows = ref([])

    // 楼栋结构树数据
    const treeData = ref([])

    const treeProps = {
      children: 'children',
      label: 'label'
    }

    // 当前选中的楼栋区域信息
    const selectedBuildingArea = ref(null)

    // 回收站统计信息
    const recycleBinStats = ref({
      totalCount: 0,
      todayCount: 0,
      weekCount: 0
    })

    const handleNodeClick = (data) => {
      // 保存选中的区域信息
      selectedBuildingArea.value = {
        id: data.id,
        name: data.label
      }
      // 根据选中的区域过滤回收站数据
      loadRecycleData()
    }

    // 回收站数据
    const recycleData = ref([])

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
          treeData.value = formatTreeData(response.data || [])
        } else {
          ElMessage.error(response.message || '获取楼栋架构失败')
        }
      } catch (error) {
        console.error('获取楼栋架构失败:', error)
        ElMessage.error('获取楼栋架构失败')
      }
    }

    // 处理选择变化
    const handleSelectionChange = (selection) => {
      selectedRows.value = selection
    }

    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadRecycleData()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadRecycleData()
    }

    // 搜索功能
    const searchRooms = () => {
      currentPage.value = 1
      loadRecycleData()
    }

    // 重置搜索
    const resetSearch = () => {
      roomNameSearch.value = ''
      approvalFilter.value = 'all'
      selectedBuildingArea.value = null
      currentPage.value = 1
      loadRecycleData()
    }

    // 加载回收站数据
    const loadRecycleData = async () => {
      const loading = ElLoading.service({
        lock: true,
        text: '加载中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      try {
        const params = {
          pageNumber: currentPage.value,
          pageSize: pageSize.value,
          roomName: roomNameSearch.value || '',
          approvalFilter: approvalFilter.value,
          roomAreaId: selectedBuildingArea.value ? selectedBuildingArea.value.id : ''
        }

        const res = await searchDeletedClassrooms(params)
        recycleData.value = res.data.rows || []
        total.value = res.data.total || 0
      } catch (error) {
        console.error('加载回收站数据失败:', error)
        ElMessage.error('加载回收站数据失败')
      } finally {
        loading.close()
      }
    }

    // 加载统计信息
    const loadStats = async () => {
      try {
        const res = await getRecycleBinStats()
        recycleBinStats.value = res.data || {
          totalCount: 0,
          todayCount: 0,
          weekCount: 0
        }
      } catch (error) {
        console.error('加载统计信息失败:', error)
      }
    }

    // 恢复单个教室
    const restoreItem = async (row) => {
      try {
        await ElMessageBox.confirm(
          `确认恢复教室 "${row.roomName} (${row.roomNo})" 吗？`,
          '恢复确认'
        )

        const loading = ElLoading.service({
          lock: true,
          text: '恢复中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          await restoreClassroom(row.id)
          ElMessage.success(`已成功恢复教室: ${row.roomName} (${row.roomNo})`)
          loadRecycleData()
          loadStats()
        } catch (error) {
          console.error('恢复教室失败:', error)
          ElMessage.error('恢复教室失败')
        } finally {
          loading.close()
        }
      } catch {
        // 用户取消
      }
    }

    // 批量恢复
    const batchRestore = async () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要恢复的数据')
        return
      }

      try {
        await ElMessageBox.confirm(
          `确认恢复选中的 ${selectedRows.value.length} 个教室吗？`,
          '批量恢复确认'
        )

        const loading = ElLoading.service({
          lock: true,
          text: '批量恢复中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          const ids = selectedRows.value.map(row => row.id)
          await batchRestoreClassrooms(ids)
          ElMessage.success(`已成功恢复 ${selectedRows.value.length} 个教室`)
          selectedRows.value = []
          loadRecycleData()
          loadStats()
        } catch (error) {
          console.error('批量恢复失败:', error)
          ElMessage.error('批量恢复失败')
        } finally {
          loading.close()
        }
      } catch {
        // 用户取消
      }
    }

    // 永久删除单个教室
    const permanentDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          `警告：永久删除后无法恢复！\n\n确认永久删除教室 "${row.roomName} (${row.roomNo})" 吗？`,
          '永久删除确认',
          {
            type: 'warning',
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            confirmButtonClass: 'el-button--danger'
          }
        )

        const loading = ElLoading.service({
          lock: true,
          text: '删除中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          await permanentDeleteClassroom(row.id)
          ElMessage.success(`已永久删除教室: ${row.roomName} (${row.roomNo})`)
          loadRecycleData()
          loadStats()
        } catch (error) {
          console.error('永久删除失败:', error)
          ElMessage.error('永久删除失败')
        } finally {
          loading.close()
        }
      } catch {
        // 用户取消
      }
    }

    // 批量永久删除
    const batchPermanentDelete = async () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要删除的数据')
        return
      }

      try {
        await ElMessageBox.confirm(
          `警告：永久删除后无法恢复！\n\n确认永久删除选中的 ${selectedRows.value.length} 个教室吗？`,
          '批量永久删除确认',
          {
            type: 'warning',
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            confirmButtonClass: 'el-button--danger'
          }
        )

        const loading = ElLoading.service({
          lock: true,
          text: '批量删除中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          const ids = selectedRows.value.map(row => row.id)
          await batchPermanentDeleteClassrooms(ids)
          ElMessage.success(`已永久删除 ${selectedRows.value.length} 个教室`)
          selectedRows.value = []
          loadRecycleData()
          loadStats()
        } catch (error) {
          console.error('批量永久删除失败:', error)
          ElMessage.error('批量永久删除失败')
        } finally {
          loading.close()
        }
      } catch {
        // 用户取消
      }
    }

    // 清空回收站
    const clearAll = async () => {
      try {
        await ElMessageBox.confirm(
          `警告：清空回收站将永久删除所有数据，无法恢复！\n\n确认清空回收站吗？`,
          '清空回收站确认',
          {
            type: 'warning',
            confirmButtonText: '确认清空',
            cancelButtonText: '取消',
            confirmButtonClass: 'el-button--danger'
          }
        )

        const loading = ElLoading.service({
          lock: true,
          text: '清空中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          await clearRecycleBin()
          ElMessage.success('已清空回收站')
          loadRecycleData()
          loadStats()
        } catch (error) {
          console.error('清空回收站失败:', error)
          ElMessage.error('清空回收站失败')
        } finally {
          loading.close()
        }
      } catch {
        // 用户取消
      }
    }

    // 页面初始化
    onMounted(() => {
      loadBuildingTree()
      loadRecycleData()
      loadStats()
    })

    return {
      roomNameSearch,
      approvalFilter,
      sidebarSearch,
      currentPage,
      pageSize,
      total,
      treeData,
      treeProps,
      selectedBuildingArea,
      selectedRows,
      recycleBinStats,
      recycleData,
      handleNodeClick,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      searchRooms,
      resetSearch,
      loadRecycleData,
      loadStats,
      loadBuildingTree,
      restoreItem,
      batchRestore,
      permanentDelete,
      batchPermanentDelete,
      clearAll
    }
  }
}
</script>

<style scoped>
.recycle-bin-container {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

.main-content {
  display: flex;
  flex: 1;
}

.sidebar {
  width: 200px;
  background: white;
  margin: 20px 0 20px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.sidebar-content {
  padding: 15px;
}

.search-input {
  margin-bottom: 10px;
}

.structure-tree {
  font-size: 14px;
}

.structure-tree :deep(.el-tree-node__content) {
  height: 36px;
  padding: 0 15px;
}

.structure-tree :deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

.structure-tree :deep(.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
  color: #409eff;
}

.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
  font-size: 14px;
}

.content-area {
  flex: 1;
  background: white;
  padding: 20px;
  margin: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: auto;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-filters {
  display: flex;
  align-items: center;
  gap: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.details-content {
  line-height: 1.4;
}

.detail-line {
  margin-bottom: 4px;
  font-size: 12px;
  color: #666;
}

.detail-line:last-child {
  margin-bottom: 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
