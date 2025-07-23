<template>
  <div class="user-list-content">
    <!-- 左侧部门树 -->
    <div class="left-sidebar">
      <div class="sidebar-header">
        <h3>组织架构</h3>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索部门"
          size="small"
          clearable
          style="margin-top: 10px;"
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
      <div class="tree-container">
        <el-tree
          :data="treeData"
          :props="{ children: 'children', label: 'name' }"
          node-key="id"
          @node-click="handleTreeNodeClick"
        />
      </div>
    </div>

    <!-- 右侧主内容 -->
    <div class="main-content">
      <!-- 搜索表单 -->
      <UserSearchForm 
        @search="handleSearch"
        @reset="handleReset"
      />

      <!-- 用户表格 -->
      <UserTable
        :table-data="tableData"
        :total="total"
        @add="handleAdd"
        @sync="handleSync"
        @import="handleImport"
        @export="handleExport"
        @view="handleView"
        @edit="handleEdit"
        @delete="handleDelete"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />

      <!-- 对话框 -->
      <UserDialog
        :detail-dialog-visible="detailDialogVisible"
        :edit-dialog-visible="editDialogVisible"
        :current-user="currentUser"
        :is-edit="isEdit"
        @update:detail-dialog-visible="detailDialogVisible = $event"
        @update:edit-dialog-visible="editDialogVisible = $event"
        @save-user="handleSaveUser"
      />
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import UserSearchForm from './UserSearchForm.vue'
import UserTable from './UserTable.vue'
import UserDialog from './UserDialog.vue'

export default {
  name: 'UserListContent',
  components: {
    Search,
    UserSearchForm,
    UserTable,
    UserDialog
  },
  setup() {
    const searchKeyword = ref('')
    const currentUser = ref(null)
    const detailDialogVisible = ref(false)
    const editDialogVisible = ref(false)
    const isEdit = ref(false)
    const total = ref(100)

    // 模拟部门树数据
    const treeData = ref([
      {
        id: 1,
        name: '总公司',
        children: [
          {
            id: 2,
            name: '技术部',
            children: [
              { id: 3, name: '前端组' },
              { id: 4, name: '后端组' }
            ]
          },
          {
            id: 5,
            name: '行政部',
            children: [
              { id: 6, name: '人事组' },
              { id: 7, name: '财务组' }
            ]
          }
        ]
      }
    ])

    // 模拟用户表格数据
    const tableData = ref([
      {
        id: 1,
        realName: '张三',
        gender: '男',
        phone: '13800138001',
        jobNumber: 'JS001',
        department: '技术部',
        position: '前端工程师',
        jobTitle: '高级工程师',
        status: '正常',
        createTime: '2023-01-15 10:30:00'
      },
      {
        id: 2,
        realName: '李四',
        gender: '女',
        phone: '13800138002',
        jobNumber: 'JS002',
        department: '行政部',
        position: '人事专员',
        jobTitle: '中级专员',
        status: '正常',
        createTime: '2023-02-20 14:20:00'
      },
      {
        id: 3,
        realName: '王五',
        gender: '男',
        phone: '13800138003',
        jobNumber: 'JS003',
        department: '技术部',
        position: '后端工程师',
        jobTitle: '高级工程师',
        status: '禁用',
        createTime: '2023-03-10 09:15:00'
      }
    ])

    // 事件处理方法
    const handleTreeNodeClick = (data) => {
      console.log('选择部门:', data)
      // 根据选择的部门过滤用户数据
    }

    const handleSearch = (searchForm) => {
      console.log('搜索用户:', searchForm)
    }

    const handleReset = () => {
      console.log('重置搜索条件')
    }

    const handleSync = () => {
      ElMessage.success('同步成功')
    }

    const handleImport = () => {
      console.log('导入用户')
    }

    const handleAdd = () => {
      isEdit.value = false
      currentUser.value = null
      editDialogVisible.value = true
    }

    const handleExport = () => {
      console.log('导出用户')
    }

    const handleView = (row) => {
      currentUser.value = row
      detailDialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      currentUser.value = row
      editDialogVisible.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm(`确定要删除用户"${row.realName}"吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
      })
    }

    const handleSaveUser = (userData) => {
      console.log('保存用户数据:', userData)
      // 这里实现保存逻辑
    }

    const handleSizeChange = (size) => {
      console.log('改变页面大小:', size)
    }

    const handleCurrentChange = (page) => {
      console.log('改变当前页:', page)
    }

    return {
      searchKeyword,
      currentUser,
      detailDialogVisible,
      editDialogVisible,
      isEdit,
      total,
      treeData,
      tableData,
      handleTreeNodeClick,
      handleSearch,
      handleReset,
      handleSync,
      handleImport,
      handleAdd,
      handleExport,
      handleView,
      handleEdit,
      handleDelete,
      handleSaveUser,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.user-list-content {
  display: flex;
  height: 100%;
}

.left-sidebar {
  width: 250px;
  background: white;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px 15px;
  border-bottom: 1px solid #e8e8e8;
}

.sidebar-header h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.tree-container {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.main-content {
  flex: 1;
  padding: 20px;
  background: #f9f9f9;
  overflow-y: auto;
}

:deep(.el-tree-node__content) {
  padding: 8px 0;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}
</style>