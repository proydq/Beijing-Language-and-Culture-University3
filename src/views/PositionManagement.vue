<template>
  <Layout>
    <div class="position-management">
      <div class="main-container">
        <!-- 左侧导航 -->
        <div class="sidebar">
          <div class="nav-item" 
               :class="{ active: activeTab === 'positionList' }"
               @click="activeTab = 'positionList'">
            职务管理
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="main-content">
          <!-- 标签页 -->
          <div class="tabs">
            <el-tabs v-model="activeTab" @tab-click="handleTabClick">
              <el-tab-pane label="职务列表" name="positionList">
                <!-- 搜索区域 -->
                <div class="search-area">
                  <el-form :model="searchForm" label-width="80px" :inline="true">
                    <el-row :gutter="20">
                      <el-col :span="8">
                        <el-form-item label="职务名称">
                          <el-input v-model="searchForm.positionName" placeholder="请输入职务名称" clearable />
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item label="创建者">
                          <el-input v-model="searchForm.creator" placeholder="请输入创建者" clearable />
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item label="状态">
                          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                            <el-option label="启用" value="1" />
                            <el-option label="禁用" value="0" />
                          </el-select>
                        </el-form-item>
                      </el-col>
                      <el-col :span="24">
                        <div class="search-buttons">
                          <el-button type="primary" @click="handleSearch">查询</el-button>
                          <el-button @click="handleReset">重置</el-button>
                        </div>
                      </el-col>
                    </el-row>
                  </el-form>
                </div>

                <!-- 操作按钮区域 -->
                <div class="action-area">
                  <div class="left-actions">
                    <span class="title">职务列表</span>
                  </div>
                  <div class="right-actions">
                    <el-button type="primary" @click="handleSync">手动同步</el-button>
                    <el-button type="primary" @click="handleImport">导入</el-button>
                    <el-button type="primary" @click="handleAdd">新增</el-button>
                    <el-button type="warning" @click="handleExport">导出</el-button>
                  </div>
                </div>

                <!-- 数据表格 -->
                <div class="table-area">
                  <el-table :data="tableData" style="width: 100%" stripe>
                    <el-table-column prop="id" label="序号" width="80" />
                    <el-table-column prop="positionName" label="职务名称" />
                    <el-table-column prop="positionDesc" label="职务描述" />
                    <el-table-column prop="level" label="职务等级" />
                    <el-table-column prop="department" label="所属部门" />
                    <el-table-column prop="creator" label="创建执行者" />
                    <el-table-column prop="createTime" label="创建时间" />
                    <el-table-column prop="status" label="状态" width="100">
                      <template #default="scope">
                        <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
                          {{ scope.row.status === '1' ? '启用' : '禁用' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="200" fixed="right">
                      <template #default="scope">
                        <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button type="warning" size="small" @click="handleToggleStatus(scope.row)">
                          {{ scope.row.status === '1' ? '禁用' : '启用' }}
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>

                <!-- 分页 -->
                <div class="pagination-area">
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
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>

      <!-- 职务编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        @close="handleDialogClose"
      >
        <el-form
          ref="positionFormRef"
          :model="positionForm"
          :rules="positionFormRules"
          label-width="100px"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="职务名称" prop="positionName">
                <el-input v-model="positionForm.positionName" placeholder="请输入职务名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="职务等级" prop="level">
                <el-select v-model="positionForm.level" placeholder="请选择职务等级">
                  <el-option label="初级" value="1" />
                  <el-option label="中级" value="2" />
                  <el-option label="高级" value="3" />
                  <el-option label="专家级" value="4" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属部门" prop="department">
                <el-select v-model="positionForm.department" placeholder="请选择部门">
                  <el-option label="研发部" value="研发部" />
                  <el-option label="人事部" value="人事部" />
                  <el-option label="行政部" value="行政部" />
                  <el-option label="销售部" value="销售部" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-select v-model="positionForm.status" placeholder="请选择状态">
                  <el-option label="启用" value="1" />
                  <el-option label="禁用" value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="职务描述" prop="positionDesc">
                <el-input 
                  v-model="positionForm.positionDesc" 
                  type="textarea" 
                  :rows="3"
                  placeholder="请输入职务描述" 
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import Layout from '@/components/Layout.vue'

export default {
  name: 'PositionManagement',
  components: {
    Layout
  },
  setup() {
    // 响应式数据
    const activeTab = ref('positionList')
    const dialogVisible = ref(false)
    const editMode = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(50)

    // 搜索表单
    const searchForm = reactive({
      positionName: '',
      creator: '',
      status: ''
    })

    // 职务表单
    const positionForm = reactive({
      id: null,
      positionName: '',
      positionDesc: '',
      level: '',
      department: '',
      status: '1'
    })

    // 表单验证规则
    const positionFormRules = {
      positionName: [
        { required: true, message: '请输入职务名称', trigger: 'blur' }
      ],
      level: [
        { required: true, message: '请选择职务等级', trigger: 'change' }
      ],
      department: [
        { required: true, message: '请选择所属部门', trigger: 'change' }
      ]
    }

    // 表格数据
    const tableData = ref([
      {
        id: 1,
        positionName: '软件工程师',
        positionDesc: '负责软件开发和维护工作',
        level: '中级',
        department: '研发部',
        creator: '管理员',
        createTime: '2024-01-01 10:00:00',
        status: '1'
      },
      {
        id: 2,
        positionName: '产品经理',
        positionDesc: '负责产品规划和设计工作',
        level: '高级',
        department: '产品部',
        creator: '管理员',
        createTime: '2024-01-02 10:00:00',
        status: '1'
      },
      {
        id: 3,
        positionName: '人事专员',
        positionDesc: '负责人力资源管理工作',
        level: '初级',
        department: '人事部',
        creator: '管理员',
        createTime: '2024-01-03 10:00:00',
        status: '0'
      }
    ])

    // 计算属性
    const dialogTitle = computed(() => {
      return editMode.value ? '编辑职务' : '新增职务'
    })

    // 方法
    const handleTabClick = (tab) => {
      console.log('切换标签页:', tab.name)
    }

    const handleSearch = () => {
      console.log('搜索职务:', searchForm)
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        positionName: '',
        creator: '',
        status: ''
      })
    }

    const handleSync = () => {
      ElMessage.success('同步成功')
    }

    const handleImport = () => {
      console.log('导入职务')
    }

    const handleExport = () => {
      console.log('导出职务')
    }

    const handleAdd = () => {
      editMode.value = false
      resetPositionForm()
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      editMode.value = true
      Object.assign(positionForm, row)
      dialogVisible.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm(`确认删除职务"${row.positionName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
      })
    }

    const handleToggleStatus = (row) => {
      const action = row.status === '1' ? '禁用' : '启用'
      ElMessageBox.confirm(`确认${action}职务"${row.positionName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        row.status = row.status === '1' ? '0' : '1'
        ElMessage.success(`${action}成功`)
      })
    }

    const handleSubmit = () => {
      console.log('提交职务表单:', positionForm)
      dialogVisible.value = false
      ElMessage.success(editMode.value ? '编辑成功' : '新增成功')
    }

    const handleDialogClose = () => {
      resetPositionForm()
    }

    const resetPositionForm = () => {
      Object.assign(positionForm, {
        id: null,
        positionName: '',
        positionDesc: '',
        level: '',
        department: '',
        status: '1'
      })
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      console.log('每页条数变化:', val)
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      console.log('当前页变化:', val)
    }

    return {
      activeTab,
      dialogVisible,
      editMode,
      currentPage,
      pageSize,
      total,
      searchForm,
      positionForm,
      positionFormRules,
      tableData,
      dialogTitle,
      handleTabClick,
      handleSearch,
      handleReset,
      handleSync,
      handleImport,
      handleExport,
      handleAdd,
      handleEdit,
      handleDelete,
      handleToggleStatus,
      handleSubmit,
      handleDialogClose,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.position-management {
  background-color: #f5f5f5;
  min-height: calc(100vh - 110px);
}

.main-container {
  display: flex;
  height: calc(100vh - 110px);
}

.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 15px;
  flex-shrink: 0;
}

.nav-item {
  padding: 12px 15px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s;
  color: #666;
}

.nav-item:hover {
  background-color: #f0f2f5;
  color: #4A90E2;
}

.nav-item.active {
  background-color: #4A90E2;
  color: white;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.tabs {
  margin-bottom: 20px;
}

.search-area {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.action-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.left-actions .title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.right-actions {
  display: flex;
  gap: 10px;
}

.table-area {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination-area {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* Element Plus 组件样式覆盖 */
:deep(.el-form-item) {
  margin-bottom: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-tabs__item.is-active) {
  color: #4A90E2;
}

:deep(.el-tabs__active-bar) {
  background-color: #4A90E2;
}

:deep(.el-table) {
  table-layout: fixed;
}

:deep(.el-table th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 8px 0;
}

:deep(.el-table .cell) {
  padding: 0 8px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid #e8e8e8;
  }
  
  .table-area {
    overflow-x: auto;
  }
}
</style>