<template>
  <div class="permission-alias-container">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="旧权限码">
          <el-input v-model="searchForm.oldCode" placeholder="请输入旧权限码" clearable />
        </el-form-item>
        <el-form-item label="新权限码">
          <el-input v-model="searchForm.newCode" placeholder="请输入新权限码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd" v-permission="'system:permission:create'">
            新增映射
          </el-button>
          <el-button type="warning" @click="handleRefreshCache" v-permission="'system:permission:refresh'">
            刷新缓存
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="oldCode" label="旧权限码" width="200" />
        <el-table-column prop="newCode" label="新权限码" width="250" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建人" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleEdit(scope.row)"
              v-permission="'system:permission:update'"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              @click="handleDelete(scope.row)"
              v-permission="'system:permission:delete'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageInfo.currentPage"
        v-model:page-size="pageInfo.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        :total="pageInfo.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="旧权限码" prop="oldCode">
          <el-input v-model="formData.oldCode" placeholder="如：USER_ADD" />
        </el-form-item>
        <el-form-item label="新权限码" prop="newCode">
          <el-input v-model="formData.newCode" placeholder="如：system:user:create" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { formatDate } from '@/utils/format'

// 搜索表单
const searchForm = reactive({
  oldCode: '',
  newCode: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页信息
const pageInfo = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const formData = reactive({
  id: '',
  oldCode: '',
  newCode: '',
  description: ''
})

// 表单验证规则
const formRules = {
  oldCode: [
    { required: true, message: '请输入旧权限码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '旧权限码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  newCode: [
    { required: true, message: '请输入新权限码', trigger: 'blur' },
    { pattern: /^[a-z]+:[a-z-]+:[a-z-]+$/, message: '新权限码格式：domain:resource:action', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/permission-alias', {
      params: {
        ...searchForm,
        page: pageInfo.currentPage - 1,
        size: pageInfo.pageSize
      }
    })
    tableData.value = response.data.content || []
    pageInfo.total = response.data.totalElements || 0
  } catch (error) {
    ElMessage.error('获取数据失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  pageInfo.currentPage = 1
  getList()
}

// 重置
const handleReset = () => {
  searchForm.oldCode = ''
  searchForm.newCode = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增权限别名'
  formData.id = ''
  formData.oldCode = ''
  formData.newCode = ''
  formData.description = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑权限别名'
  formData.id = row.id
  formData.oldCode = row.oldCode
  formData.newCode = row.newCode
  formData.description = row.description
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除权限别名映射"${row.oldCode} -> ${row.newCode}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await request.delete(`/api/permission-alias/${row.id}`)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  
  try {
    if (formData.id) {
      // 编辑
      await request.put(`/api/permission-alias/${formData.id}`, formData)
      ElMessage.success('更新成功')
    } else {
      // 新增
      await request.post('/api/permission-alias', formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('操作失败：' + (error.message || '未知错误'))
  }
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    await request.post('/api/permission/refresh/alias')
    ElMessage.success('权限别名缓存刷新成功')
  } catch (error) {
    ElMessage.error('刷新缓存失败：' + (error.message || '未知错误'))
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageInfo.pageSize = val
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pageInfo.currentPage = val
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.permission-alias-container {
  .search-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    .el-pagination {
      margin-top: 20px;
      justify-content: flex-end;
    }
  }
}</style>