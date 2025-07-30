<template>
  <div class="recycle-bin-container">
    <!-- 顶部搜索和操作区域 -->
    <div class="top-section">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入关键词搜索"
          class="search-input"
          clearable
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="action-section">
        <el-input
          v-model="teacherName"
          placeholder="请输入教室名称或房间号"
          class="teacher-input"
          clearable
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button type="success" @click="batchRestore" :disabled="selectedRows.length === 0">批量恢复</el-button>
        <el-button type="danger" @click="batchPermanentDelete" :disabled="selectedRows.length === 0">批量永久删除</el-button>
        <el-button type="warning" @click="clearAll">清空回收站</el-button>
      </div>
    </div>

    <!-- 左侧导航 -->
     <div class="main-content">
       <div class="sidebar">
         <el-tree
           :data="treeData"
           :props="treeProps"
           node-key="id"
           :default-expanded-keys="['all']"
           :highlight-current="true"
           :default-current-key="'all'"
           @node-click="handleNodeClick"
           class="nav-tree"
         >
           <template #default="{ node, data }">
             <span class="tree-node">
               <span>{{ data.label }}</span>
             </span>
           </template>
         </el-tree>
       </div>

      <!-- 主要内容区域 -->
      <div class="content-area">
        <el-table
          :data="recycleData"
          style="width: 100%"
          class="recycle-table"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="roomName" label="教室" width="150" />
          <el-table-column prop="roomNo" label="房间号" width="120" />
          <el-table-column prop="roomAreaName" label="所属楼" width="120" />
          <el-table-column label="是否支持自选审批" width="150">
            <template #default="{ row }">
              <el-tag :type="row.allowBookerSelectApprover === 'yes' ? 'success' : 'info'" size="small">
                {{ row.allowBookerSelectApprover === 'yes' ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="是否需要审批" width="150">
            <template #default="{ row }">
              <el-tag :type="row.needApproval === 'yes' ? 'warning' : 'info'" size="small">
                {{ row.needApproval === 'yes' ? '需要' : '不需要' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="deleteTime" label="删除时间" width="160">
            <template #default="{ row }">
              {{ row.deleteTime ? new Date(row.deleteTime).toLocaleString() : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="200">
            <template #default="{ row }">
              <div class="description-content">
                <div class="desc-line">{{ row.remark || '无备注' }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button type="success" size="small" @click="restoreItem(row)">恢复</el-button>
              <el-button type="danger" size="small" @click="permanentDelete(row)">永久删除</el-button>
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
            layout="prev, pager, next, sizes, ->, total"
            class="pagination"
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
    // 搜索关键词
    const searchKeyword = ref('')
    const teacherName = ref('')

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
      ElMessage.success(`已选择区域：${data.label}`)
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
    const handleSearch = () => {
      currentPage.value = 1
      loadRecycleData()
    }

    // 重置搜索
    const resetSearch = () => {
      searchKeyword.value = ''
      teacherName.value = ''
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
          keyword: searchKeyword.value || '',
          roomName: teacherName.value || '',
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
          `确认恢复教室 "${row.roomName} (${row.roomNumber})" 吗？`,
          '恢复确认'
        )

        const loading = ElLoading.service({
          lock: true,
          text: '恢复中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          await restoreClassroom(row.id)
          ElMessage.success(`已成功恢复教室: ${row.roomName} (${row.roomNumber})`)
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
          `警告：永久删除后无法恢复！\n\n确认永久删除教室 "${row.roomName} (${row.roomNumber})" 吗？`,
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
          ElMessage.success(`已永久删除教室: ${row.roomName} (${row.roomNumber})`)
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
      searchKeyword,
      teacherName,
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
      handleSearch,
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
  background: #f5f5f5;
  min-height: 100vh;
  padding: 0;
}

.top-section {
  background: white;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  flex: 1;
  max-width: 300px;
}

.search-input {
  width: 100%;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.teacher-input {
  width: 250px;
}

.confirm-btn {
  background: #5b9bd5;
  border-color: #5b9bd5;
}

.main-content {
  display: flex;
  height: calc(100vh - 80px);
}

.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 0;
}

.nav-tree {
  padding: 8px 0;
}

.nav-tree :deep(.el-tree-node__content) {
  height: 40px;
  padding-left: 16px;
  border-bottom: 1px solid #f0f0f0;
  color: #666;
  font-size: 14px;
  transition: all 0.3s;
}

.nav-tree :deep(.el-tree-node__content:hover) {
  background: #f5f5f5;
  color: #333;
}

.nav-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: #e6f3ff;
  color: #1890ff;
  border-right: 3px solid #1890ff;
}

.nav-tree :deep(.el-tree-node__expand-icon) {
  color: #666;
  font-size: 12px;
}

.nav-tree :deep(.el-tree-node__expand-icon.expanded) {
  transform: rotate(90deg);
}

.nav-tree :deep(.el-tree-node__children) {
  background: #fafafa;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__content) {
  padding-left: 32px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__content:hover) {
  background: #f0f0f0;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__children .el-tree-node__content) {
  padding-left: 48px;
  background: #f5f5f5;
  border-bottom: 1px solid #f0f0f0;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__children .el-tree-node__content:hover) {
  background: #eeeeee;
}

.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
}

.content-area {
  flex: 1;
  background: white;
  padding: 24px;
  overflow: auto;
}

.recycle-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.recycle-table :deep(.el-table__header) {
  background: #fafafa;
}

.recycle-table :deep(.el-table__header th) {
  background: #fafafa;
  color: #333;
  font-weight: 600;
  border-bottom: 1px solid #e8e8e8;
}

.recycle-table :deep(.el-table__body tr:hover) {
  background: #f5f5f5;
}

.description-content {
  line-height: 1.4;
}

.desc-line {
  margin-bottom: 4px;
  font-size: 12px;
  color: #666;
}

.desc-line:last-child {
  margin-bottom: 0;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.pagination {
  background: white;
}

.pagination :deep(.el-pagination__total) {
  color: #666;
}

.pagination :deep(.el-pagination__sizes) {
  color: #666;
}

.pagination :deep(.el-pager li) {
  background: white;
  border: 1px solid #ddd;
  margin: 0 2px;
}

.pagination :deep(.el-pager li.active) {
  background: #5b9bd5;
  border-color: #5b9bd5;
  color: white;
}

.pagination :deep(.btn-prev),
.pagination :deep(.btn-next) {
  background: white;
  border: 1px solid #ddd;
}

.pagination :deep(.btn-prev:hover),
.pagination :deep(.btn-next:hover) {
  color: #5b9bd5;
}
</style>
