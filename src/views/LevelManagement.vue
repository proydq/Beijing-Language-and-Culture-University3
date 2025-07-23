<template>
  <Layout>
    <div class="level-management">
      <div class="main-container">
        <!-- 左侧导航 -->
        <div class="sidebar">
          <div class="nav-item" 
               :class="{ active: activeTab === 'levelList' }"
               @click="activeTab = 'levelList'">
            等级管理
          </div>
        </div>

        <!-- 右侧内容区域 -->
        <div class="main-content">
          <!-- 标签页 -->
          <div class="tabs">
            <el-tabs v-model="activeTab" @tab-click="handleTabClick">
              <el-tab-pane label="等级列表" name="levelList">
                <!-- 搜索区域 -->
                <div class="search-area">
                  <el-form :model="searchForm" label-width="80px" :inline="true">
                    <el-row :gutter="20">
                      <el-col :span="8">
                        <el-form-item label="等级名称">
                          <el-input v-model="searchForm.levelName" placeholder="请输入等级名称" clearable />
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item label="等级类型">
                          <el-select v-model="searchForm.levelType" placeholder="请选择等级类型" clearable>
                            <el-option label="职务等级" value="position" />
                            <el-option label="技能等级" value="skill" />
                            <el-option label="管理等级" value="management" />
                          </el-select>
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
                    <span class="title">等级列表</span>
                  </div>
                  <div class="right-actions">
                    <el-button type="primary" @click="handleAdd">新增</el-button>
                    <el-button type="success" @click="handleImport">导入</el-button>
                    <el-button type="warning" @click="handleExport">导出</el-button>
                  </div>
                </div>

                <!-- 数据表格 -->
                <div class="table-area">
                  <el-table :data="tableData" style="width: 100%" stripe>
                    <el-table-column prop="id" label="序号" width="80" />
                    <el-table-column prop="levelName" label="等级名称" />
                    <el-table-column prop="levelCode" label="等级编码" />
                    <el-table-column prop="levelType" label="等级类型">
                      <template #default="scope">
                        <el-tag v-if="scope.row.levelType === 'position'" type="primary">职务等级</el-tag>
                        <el-tag v-else-if="scope.row.levelType === 'skill'" type="success">技能等级</el-tag>
                        <el-tag v-else type="warning">管理等级</el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="levelValue" label="等级数值" />
                    <el-table-column prop="description" label="等级描述" />
                    <el-table-column prop="creator" label="创建者" />
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

      <!-- 等级编辑对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="600px"
        @close="handleDialogClose"
      >
        <el-form
          ref="levelFormRef"
          :model="levelForm"
          :rules="levelFormRules"
          label-width="100px"
        >
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="等级名称" prop="levelName">
                <el-input v-model="levelForm.levelName" placeholder="请输入等级名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="等级编码" prop="levelCode">
                <el-input v-model="levelForm.levelCode" placeholder="请输入等级编码" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="等级类型" prop="levelType">
                <el-select v-model="levelForm.levelType" placeholder="请选择等级类型">
                  <el-option label="职务等级" value="position" />
                  <el-option label="技能等级" value="skill" />
                  <el-option label="管理等级" value="management" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="等级数值" prop="levelValue">
                <el-input-number 
                  v-model="levelForm.levelValue" 
                  :min="1" 
                  :max="100" 
                  placeholder="请输入等级数值" 
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="权重" prop="weight">
                <el-input-number 
                  v-model="levelForm.weight" 
                  :min="0" 
                  :max="100" 
                  placeholder="请输入权重" 
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-select v-model="levelForm.status" placeholder="请选择状态">
                  <el-option label="启用" value="1" />
                  <el-option label="禁用" value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="等级描述" prop="description">
                <el-input 
                  v-model="levelForm.description" 
                  type="textarea" 
                  :rows="3"
                  placeholder="请输入等级描述" 
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
  name: 'LevelManagement',
  components: {
    Layout
  },
  setup() {
    // 响应式数据
    const activeTab = ref('levelList')
    const dialogVisible = ref(false)
    const editMode = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(30)

    // 搜索表单
    const searchForm = reactive({
      levelName: '',
      levelType: '',
      status: ''
    })

    // 等级表单
    const levelForm = reactive({
      id: null,
      levelName: '',
      levelCode: '',
      levelType: '',
      levelValue: null,
      weight: null,
      description: '',
      status: '1'
    })

    // 表单验证规则
    const levelFormRules = {
      levelName: [
        { required: true, message: '请输入等级名称', trigger: 'blur' }
      ],
      levelCode: [
        { required: true, message: '请输入等级编码', trigger: 'blur' }
      ],
      levelType: [
        { required: true, message: '请选择等级类型', trigger: 'change' }
      ],
      levelValue: [
        { required: true, message: '请输入等级数值', trigger: 'change' }
      ]
    }

    // 表格数据
    const tableData = ref([
      {
        id: 1,
        levelName: '初级工程师',
        levelCode: 'L1',
        levelType: 'position',
        levelValue: 1,
        weight: 10,
        description: '初级技术人员',
        creator: '管理员',
        createTime: '2024-01-01 10:00:00',
        status: '1'
      },
      {
        id: 2,
        levelName: '中级工程师',
        levelCode: 'L2',
        levelType: 'position',
        levelValue: 2,
        weight: 20,
        description: '中级技术人员',
        creator: '管理员',
        createTime: '2024-01-02 10:00:00',
        status: '1'
      },
      {
        id: 3,
        levelName: '高级工程师',
        levelCode: 'L3',
        levelType: 'position',
        levelValue: 3,
        weight: 30,
        description: '高级技术人员',
        creator: '管理员',
        createTime: '2024-01-03 10:00:00',
        status: '1'
      },
      {
        id: 4,
        levelName: '技术专家',
        levelCode: 'L4',
        levelType: 'skill',
        levelValue: 4,
        weight: 40,
        description: '技术专家级别',
        creator: '管理员',
        createTime: '2024-01-04 10:00:00',
        status: '1'
      },
      {
        id: 5,
        levelName: '项目经理',
        levelCode: 'M1',
        levelType: 'management',
        levelValue: 1,
        weight: 25,
        description: '项目管理层级',
        creator: '管理员',
        createTime: '2024-01-05 10:00:00',
        status: '0'
      }
    ])

    // 计算属性
    const dialogTitle = computed(() => {
      return editMode.value ? '编辑等级' : '新增等级'
    })

    // 方法
    const handleTabClick = (tab) => {
      console.log('切换标签页:', tab.name)
    }

    const handleSearch = () => {
      console.log('搜索等级:', searchForm)
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        levelName: '',
        levelType: '',
        status: ''
      })
    }

    const handleImport = () => {
      console.log('导入等级')
    }

    const handleExport = () => {
      console.log('导出等级')
    }

    const handleAdd = () => {
      editMode.value = false
      resetLevelForm()
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      editMode.value = true
      Object.assign(levelForm, row)
      dialogVisible.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm(`确认删除等级"${row.levelName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
      })
    }

    const handleToggleStatus = (row) => {
      const action = row.status === '1' ? '禁用' : '启用'
      ElMessageBox.confirm(`确认${action}等级"${row.levelName}"吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        row.status = row.status === '1' ? '0' : '1'
        ElMessage.success(`${action}成功`)
      })
    }

    const handleSubmit = () => {
      console.log('提交等级表单:', levelForm)
      dialogVisible.value = false
      ElMessage.success(editMode.value ? '编辑成功' : '新增成功')
    }

    const handleDialogClose = () => {
      resetLevelForm()
    }

    const resetLevelForm = () => {
      Object.assign(levelForm, {
        id: null,
        levelName: '',
        levelCode: '',
        levelType: '',
        levelValue: null,
        weight: null,
        description: '',
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
      levelForm,
      levelFormRules,
      tableData,
      dialogTitle,
      handleTabClick,
      handleSearch,
      handleReset,
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
.level-management {
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

:deep(.el-input-number) {
  width: 100%;
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