<template>
  <div class="position-content">
    <!-- 搜索区域 -->
    <div class="position-search-area">
      <el-form inline>
        <el-form-item label="职务名称:">
          <el-input 
            v-model="searchKeyword" 
            placeholder="请输入职务名称" 
            clearable 
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格容器 -->
    <div class="position-table-container">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><plus /></el-icon>
          新增职务
        </el-button>
      </div>

      <el-table :data="tableData" style="width: 100%;" border stripe>
        <el-table-column prop="name" label="职务名称" width="200" />
        <el-table-column prop="description" label="职务描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
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
      <div class="position-pagination-container">
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :model-value="dialogVisible"
      @update:model-value="dialogVisible = $event"
      :title="dialogMode === 'add' ? '新增职务' : '编辑职务'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="职务名称:" prop="name">
          <el-input v-model="formData.name" placeholder="请输入职务名称" />
        </el-form-item>
        <el-form-item label="职务描述:" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入职务描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'PositionContent',
  components: {
    Plus
  },
  setup() {
    const formRef = ref()
    const searchKeyword = ref('')
    const dialogVisible = ref(false)
    const dialogMode = ref('add')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(50)

    // 表格数据
    const tableData = ref([
      {
        id: 1,
        name: '前端工程师',
        description: '负责前端页面开发和用户交互设计',
        createTime: '2023-01-15 10:30:00'
      },
      {
        id: 2,
        name: '后端工程师',
        description: '负责服务器端开发和数据库设计',
        createTime: '2023-01-16 14:20:00'
      },
      {
        id: 3,
        name: '产品经理',
        description: '负责产品规划和需求分析',
        createTime: '2023-01-17 09:15:00'
      }
    ])

    // 表单数据
    const formData = reactive({
      name: '',
      description: ''
    })

    // 表单验证规则
    const formRules = {
      name: [
        { required: true, message: '请输入职务名称', trigger: 'blur' },
        { min: 2, max: 20, message: '职务名称长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入职务描述', trigger: 'blur' },
        { min: 5, max: 200, message: '职务描述长度在 5 到 200 个字符', trigger: 'blur' }
      ]
    }

    // 事件处理方法
    const handleSearch = () => {
      console.log('搜索职务:', searchKeyword.value)
    }

    const handleReset = () => {
      searchKeyword.value = ''
    }

    const handleAdd = () => {
      dialogMode.value = 'add'
      Object.assign(formData, {
        name: '',
        description: ''
      })
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      dialogMode.value = 'edit'
      Object.assign(formData, row)
      dialogVisible.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm(`确定要删除职务"${row.name}"吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
      })
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
    }

    const handleDialogClose = () => {
      dialogVisible.value = false
      Object.assign(formData, {
        name: '',
        description: ''
      })
    }

    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        ElMessage.success(dialogMode.value === 'add' ? '新增成功' : '编辑成功')
        handleDialogClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    return {
      formRef,
      searchKeyword,
      dialogVisible,
      dialogMode,
      currentPage,
      pageSize,
      total,
      tableData,
      formData,
      formRules,
      handleSearch,
      handleReset,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSizeChange,
      handleCurrentChange,
      handleDialogClose,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.position-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.position-search-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.position-table-container {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  margin-bottom: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.table-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e8e8e8;
}

.position-pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
  margin-top: auto;
}

:deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-table) {
  flex: 1;
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

:deep(.el-dialog__header) {
  background-color: #f8f9fa;
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8e8;
}
</style>