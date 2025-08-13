<template>
  <div class="permission-url-container">
    <el-card shadow="never">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="URL模式">
            <el-input v-model="searchForm.urlPattern" placeholder="请输入URL模式" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item label="权限码">
            <el-input v-model="searchForm.permCode" placeholder="请输入权限码" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item label="模块">
            <el-select v-model="searchForm.module" placeholder="请选择模块" clearable style="width: 150px">
              <el-option label="用户管理" value="USER" />
              <el-option label="角色管理" value="ROLE" />
              <el-option label="房间管理" value="ROOM" />
              <el-option label="预订管理" value="BOOKING" />
              <el-option label="系统管理" value="SYSTEM" />
            </el-select>
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
      </div>

      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="urlPattern" label="URL模式" width="200" show-overflow-tooltip />
        <el-table-column prop="httpMethod" label="HTTP方法" width="100">
          <template #default="scope">
            <el-tag :type="getMethodTagType(scope.row.httpMethod)" size="small">
              {{ scope.row.httpMethod }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permCode" label="权限码" width="200" show-overflow-tooltip />
        <el-table-column prop="module" label="模块" width="100">
          <template #default="scope">
            <el-tag type="info" size="small">{{ scope.row.module }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
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
      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pageInfo.currentPage"
          v-model:page-size="pageInfo.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="pageInfo.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="URL模式" prop="urlPattern">
          <el-input v-model="formData.urlPattern" placeholder="如：/api/user/**" />
          <div class="form-tip">支持Ant风格通配符：* 匹配一层路径，** 匹配多层路径</div>
        </el-form-item>
        <el-form-item label="HTTP方法" prop="httpMethod">
          <el-select v-model="formData.httpMethod" placeholder="请选择HTTP方法">
            <el-option label="任意方法(*)" value="*" />
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
            <el-option label="PATCH" value="PATCH" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限码" prop="permCode">
          <el-input v-model="formData.permCode" placeholder="如：system:user:view" />
          <div class="form-tip">多个权限码用逗号分隔</div>
        </el-form-item>
        <el-form-item label="模块" prop="module">
          <el-select v-model="formData.module" placeholder="请选择模块">
            <el-option label="用户管理" value="USER" />
            <el-option label="角色管理" value="ROLE" />
            <el-option label="房间管理" value="ROOM" />
            <el-option label="预订管理" value="BOOKING" />
            <el-option label="系统管理" value="SYSTEM" />
            <el-option label="文件管理" value="FILE" />
            <el-option label="报表统计" value="REPORT" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="formData.sortOrder" :min="1" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

// 模拟数据
const mockData = [
  {
    id: '1',
    urlPattern: '/api/sys-user/**',
    httpMethod: 'GET',
    permCode: 'system:user:view',
    module: 'USER',
    description: '查看用户信息',
    sortOrder: 10,
    status: 1
  },
  {
    id: '2',
    urlPattern: '/api/sys-user',
    httpMethod: 'POST',
    permCode: 'system:user:create',
    module: 'USER',
    description: '创建用户',
    sortOrder: 11,
    status: 1
  },
  {
    id: '3',
    urlPattern: '/api/room/**',
    httpMethod: 'GET',
    permCode: 'building:room:view',
    module: 'ROOM',
    description: '查看房间信息',
    sortOrder: 30,
    status: 1
  }
]

// 搜索表单
const searchForm = reactive({
  urlPattern: '',
  permCode: '',
  module: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)
const submitLoading = ref(false)

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
  urlPattern: '',
  httpMethod: '',
  permCode: '',
  module: '',
  description: '',
  sortOrder: 100,
  status: 1
})

// 表单验证规则
const formRules = {
  urlPattern: [
    { required: true, message: '请输入URL模式', trigger: 'blur' }
  ],
  httpMethod: [
    { required: true, message: '请选择HTTP方法', trigger: 'change' }
  ],
  permCode: [
    { required: true, message: '请输入权限码', trigger: 'blur' }
  ],
  module: [
    { required: true, message: '请选择模块', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序值', trigger: 'blur' }
  ]
}

// 获取HTTP方法标签类型
const getMethodTagType = (method) => {
  const typeMap = {
    'GET': 'primary',
    'POST': 'success',
    'PUT': 'warning',
    'DELETE': 'danger',
    'PATCH': 'info',
    '*': ''
  }
  return typeMap[method] || ''
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    let filteredData = [...mockData]
    
    // 模拟搜索过滤
    if (searchForm.urlPattern) {
      filteredData = filteredData.filter(item => 
        item.urlPattern.toLowerCase().includes(searchForm.urlPattern.toLowerCase())
      )
    }
    if (searchForm.permCode) {
      filteredData = filteredData.filter(item => 
        item.permCode.toLowerCase().includes(searchForm.permCode.toLowerCase())
      )
    }
    if (searchForm.module) {
      filteredData = filteredData.filter(item => 
        item.module === searchForm.module
      )
    }
    
    tableData.value = filteredData
    pageInfo.total = filteredData.length
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
  searchForm.urlPattern = ''
  searchForm.permCode = ''
  searchForm.module = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增URL权限映射'
  formData.id = ''
  formData.urlPattern = ''
  formData.httpMethod = ''
  formData.permCode = ''
  formData.module = ''
  formData.description = ''
  formData.sortOrder = 100
  formData.status = 1
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑URL权限映射'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除URL权限映射"${row.urlPattern}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
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
  
  submitLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (formData.id) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error('操作失败：' + (error.message || '未知错误'))
  } finally {
    submitLoading.value = false
  }
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    await request.post('/api/permission/refresh/url')
    ElMessage.success('URL权限映射缓存刷新成功')
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
.permission-url-container {
  .search-area {
    margin-bottom: 20px;
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 4px;
  }
  
  .pagination-area {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .form-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}
</style>