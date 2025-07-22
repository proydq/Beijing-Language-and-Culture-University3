<template>
  <div class="table-container">
    <!-- 表格操作栏 -->
    <div class="table-header">
      <div class="header-left">
        <el-button type="primary" @click="handleAdd">
          <el-icon><plus /></el-icon>
          新增用户
        </el-button>
        <el-button type="success" @click="handleSync">
          <el-icon><refresh /></el-icon>
          同步
        </el-button>
        <el-button type="warning" @click="handleImport">
          <el-icon><upload /></el-icon>
          导入
        </el-button>
        <el-button type="info" @click="handleExport">
          <el-icon><download /></el-icon>
          导出
        </el-button>
      </div>
    </div>

    <!-- 用户表格 -->
    <el-table 
      :data="tableData" 
      style="width: 100%;"
      border
      stripe
    >
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="jobNumber" label="工号" width="120" />
      <el-table-column prop="department" label="所属部门" width="120" />
      <el-table-column prop="position" label="职务" width="120" />
      <el-table-column prop="jobTitle" label="职称" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
          <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button 
            type="text" 
            size="small" 
            @click="handleDelete(row)" 
            style="color: #f56c6c;"
          >
            删除
          </el-button>
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
</template>

<script>
import { ref } from 'vue'
import { Plus, Refresh, Upload, Download } from '@element-plus/icons-vue'

export default {
  name: 'UserTable',
  components: {
    Plus,
    Refresh,
    Upload,
    Download
  },
  props: {
    tableData: {
      type: Array,
      default: () => []
    },
    total: {
      type: Number,
      default: 0
    }
  },
  emits: [
    'add', 
    'sync', 
    'import', 
    'export', 
    'view', 
    'edit', 
    'delete', 
    'size-change', 
    'current-change'
  ],
  setup(props, { emit }) {
    const currentPage = ref(1)
    const pageSize = ref(10)

    const handleAdd = () => {
      emit('add')
    }

    const handleSync = () => {
      emit('sync')
    }

    const handleImport = () => {
      emit('import')
    }

    const handleExport = () => {
      emit('export')
    }

    const handleView = (row) => {
      emit('view', row)
    }

    const handleEdit = (row) => {
      emit('edit', row)
    }

    const handleDelete = (row) => {
      emit('delete', row)
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      emit('size-change', size)
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      emit('current-change', page)
    }

    return {
      currentPage,
      pageSize,
      handleAdd,
      handleSync,
      handleImport,
      handleExport,
      handleView,
      handleEdit,
      handleDelete,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.table-container {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  margin-bottom: 20px;
}

.table-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
}

:deep(.el-table) {
  border-radius: 0 0 6px 6px;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #333;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__row:hover > td) {
  background-color: #f5f7fa;
}

:deep(.el-button--text) {
  padding: 4px 8px;
  margin-right: 8px;
}

:deep(.el-button--text:last-child) {
  margin-right: 0;
}
</style>