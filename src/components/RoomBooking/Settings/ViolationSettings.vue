<template>
  <div class="violation-settings-container">
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
        <!-- 页面标题 -->
        <div class="page-header">
          <h2>违规配置</h2>
        </div>
        
        <!-- 操作区域 -->
        <div class="header-section">
          <div class="search-filters">
            <div class="filter-item">
              <label>教室名称:</label>
              <el-input
                v-model="roomSearchKeyword"
                placeholder="请输入实验室名称或房间号"
                style="width: 200px"
                clearable
              />
            </div>
            <div class="filter-item">
              <el-button type="primary" @click="searchRooms">搜索</el-button>
              <el-button @click="resetSearch">重置</el-button>
            </div>
          </div>

          <div class="action-buttons">
            <el-button type="primary" @click="batchSetContinuousDays">
              批量修改违规条件
            </el-button>
          </div>
        </div>

        <!-- 教室表格 -->
        <div class="table-section">
          <el-table ref="tableRef" :data="filteredClassrooms" style="width: 100%" border v-loading="loading" row-key="id">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="roomName" label="预约教室" min-width="200" />
            <el-table-column prop="roomNo" label="房间号" width="100" />
            <el-table-column prop="roomAreaName" label="所属楼" width="100" />
            <el-table-column label="超时设置" width="200">
              <template #default="{ row }">
                <div class="violation-info">
                  <div>超时{{ row.startTime || 30 }}分钟</div>
                  <div>违规{{ row.violationCount || 3 }}次拉黑</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button type="warning" size="small" @click="editContinuousDays(row)">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-container">
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

    <!-- 违规配置弹出框 -->
    <el-dialog
      v-model="violationDialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form :model="violationForm" label-width="30px" class="violation-form">
        <el-form-item>
          <div class="form-row">
            <span class="form-text">超过“开始使用时间”（</span>
            <el-input
              v-model="violationForm.startTime"
              placeholder="30"
              style="width: 80px;"
            />
            <span class="form-text">）分钟，无门禁启用或考勤签到，算一次。</span>
          </div>
        </el-form-item>
        
        <el-form-item>
          <div class="form-row">
            <span class="form-text">超过（</span>
            <el-input
              v-model="violationForm.violationCount"
              placeholder="3"
              style="width: 80px;"
            />
            <span class="form-text">）次，拉至黑名单，不允许预约。</span>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="violationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmViolationSettings">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area'
import { searchViolationSettings, updateViolationSetting, batchUpdateViolationSettings } from '@/api/violationSettings'

export default {
  name: 'ViolationSettings',
  components: {
    Search
  },
  setup() {
    const roomSearchKeyword = ref('')
    const sidebarSearch = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalClassrooms = ref(0)
    
    // 楼栋结构树数据
    const treeData = ref([])
    
    const treeProps = {
      children: 'children',
      label: 'label'
    }
    
    // 当前选中的楼栋区域信息
    const selectedBuildingArea = ref(null)

    // 教室数据
    const classroomsData = ref([])
    const loading = ref(false)

    // 过滤后的教室数据（后端已经处理分页，直接返回）
    const filteredClassrooms = computed(() => {
      return classroomsData.value || []
    })
    
    // 加载楼栋架构数据
    const loadBuildingTree = async () => {
      try {
        const response = await getAreaTree()
        if (response && response.code === 200) {
          // 将后端返回的区域树数据转换为前端需要的格式
          const formatTreeData = (nodes) => {
            if (!nodes || !Array.isArray(nodes)) return []
            return nodes.map(node => ({
              label: node.areaName || node.label || 'Unknown',
              id: node.id,
              type: node.type,
              children: node.children && node.children.length > 0 ? formatTreeData(node.children) : undefined
            }))
          }
          treeData.value = formatTreeData(response.data || [])
        } else {
          console.warn('获取楼栋架构响应异常:', response)
          ElMessage.error((response && response.message) || '获取楼栋架构失败')
        }
      } catch (error) {
        console.error('获取楼栋架构失败:', error)
        ElMessage.error('获取楼栋架构失败')
      }
    }
    
    const handleNodeClick = (data) => {
      // 保存选中的区域信息
      selectedBuildingArea.value = {
        id: data.id,
        name: data.label
      }
      // 根据选中的区域加载数据
      loadClassroomData()
    }
    
    // 加载教室数据
    const loadClassroomData = async () => {
      try {
        loading.value = true
        
        const condition = {
          pageNumber: currentPage.value,
          pageSize: pageSize.value,
          roomName: roomSearchKeyword.value || null,
          roomAreaId: selectedBuildingArea.value?.id || null
        }

        const response = await searchViolationSettings(condition)
        
        if (response && response.code === 200) {
          classroomsData.value = response.data?.rows || []
          totalClassrooms.value = response.data?.total || 0
        } else {
          console.warn('获取违规设置数据响应异常:', response)
          ElMessage.error((response && response.message) || '获取违规设置数据失败')
        }
      } catch (error) {
        console.error('获取违规设置数据失败:', error)
        ElMessage.error('获取违规设置数据失败')
        classroomsData.value = []
        totalClassrooms.value = 0
      } finally {
        loading.value = false
      }
    }
    
    // 搜索功能
    const searchRooms = () => {
      currentPage.value = 1
      loadClassroomData()
    }
    
    // 重置搜索
    const resetSearch = () => {
      roomSearchKeyword.value = ''
      selectedBuildingArea.value = null
      currentPage.value = 1
      loadClassroomData()
    }

    // 弹出框相关数据
    const violationDialogVisible = ref(false)
    const dialogTitle = ref('')
    const violationForm = ref({
      startTime: '30',
      violationCount: '3'
    })
    const currentEditRow = ref(null)

    const batchSetContinuousDays = () => {
      dialogTitle.value = '批量修改违规条件'
      violationForm.value = {
        startTime: '30',
        violationCount: '3'
      }
      violationDialogVisible.value = true
    }

    const editContinuousDays = (row) => {
      dialogTitle.value = `编辑违规条件 - ${row.roomName}`
      currentEditRow.value = row
      violationForm.value = {
        startTime: row.startTime?.toString() || '30',
        violationCount: row.violationCount?.toString() || '3'
      }
      violationDialogVisible.value = true
    }

    const confirmViolationSettings = async () => {
      // 验证输入
      if (!violationForm.value.startTime || !violationForm.value.violationCount) {
        ElMessage.warning('请完整填写违规设置')
        return
      }

      const startTime = parseInt(violationForm.value.startTime)
      const violationCount = parseInt(violationForm.value.violationCount)

      if (isNaN(startTime) || startTime <= 0) {
        ElMessage.warning('请输入有效的超时分钟数')
        return
      }

      if (isNaN(violationCount) || violationCount <= 0) {
        ElMessage.warning('请输入有效的违规次数')
        return
      }

      try {
        const loadingInstance = ElLoading.service({ text: '保存中...' })
        
        if (currentEditRow.value) {
          // 单个编辑
          const updateData = {
            startTime: startTime,
            violationCount: violationCount
          }
          
          const response = await updateViolationSetting(currentEditRow.value.roomId, updateData)
          
          if (response && response.code === 200) {
            ElMessage.success(`已更新 ${currentEditRow.value.roomName} 的违规条件`)
            // 刷新数据
            await loadClassroomData()
          } else {
            ElMessage.error((response && response.message) || '更新失败')
          }
        } else {
          // 批量修改 - 获取所有选中的行
          const selectedRows = tableRef.value?.getSelectionRows() || []
          
          if (selectedRows.length === 0) {
            ElMessage.warning('请先选择要修改的教室')
            return
          }

          const roomIds = selectedRows.map(row => row.roomId)
          const batchData = {
            roomIds: roomIds,
            startTime: startTime,
            violationCount: violationCount
          }

          const response = await batchUpdateViolationSettings(batchData)
          
          if (response && response.code === 200) {
            ElMessage.success(`已批量更新 ${selectedRows.length} 个教室的违规条件`)
            // 刷新数据
            await loadClassroomData()
          } else {
            ElMessage.error((response && response.message) || '批量更新失败')
          }
        }
        
        violationDialogVisible.value = false
        currentEditRow.value = null
        loadingInstance.close()
      } catch (error) {
        console.error('保存违规设置失败:', error)
        ElMessage.error('保存失败')
      }
    }

    const tableRef = ref(null)

    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadClassroomData()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadClassroomData()
    }
    
    // 页面初始化
    onMounted(() => {
      loadBuildingTree()
      loadClassroomData()
    })

    return {
      roomSearchKeyword,
      sidebarSearch,
      treeData,
      treeProps,
      selectedBuildingArea,
      currentPage,
      pageSize,
      totalClassrooms,
      classroomsData,
      filteredClassrooms,
      loading,
      tableRef,
      batchSetContinuousDays,
      editContinuousDays,
      handleSizeChange,
      handleCurrentChange,
      handleNodeClick,
      loadBuildingTree,
      loadClassroomData,
      searchRooms,
      resetSearch,
      violationDialogVisible,
      dialogTitle,
      violationForm,
      currentEditRow,
      confirmViolationSettings,
      Search
    }
  }
}
</script>

<style scoped>
.violation-settings-container {
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

.page-header {
  margin-bottom: 20px;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 15px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 500;
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

.table-section {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.violation-info {
  font-size: 12px;
  line-height: 1.4;
}

.violation-info div {
  margin-bottom: 2px;
}

.violation-info div:last-child {
  margin-bottom: 0;
}

/* 违规配置弹出框样式 */
.violation-form {
  padding: 20px 0;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  line-height: 1.8;
}

.form-text {
  color: #333;
  font-size: 14px;
  white-space: nowrap;
  line-height: 1.8;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 10px 20px;
  border-bottom: 1px solid #e8e8e8;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 10px 20px 20px 20px;
  border-top: 1px solid #e8e8e8;
}
</style>