<template>
  <div class="role-management">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo" @click="goToHome" style="cursor: pointer;">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">功能模块</span>
      </div>
      <div class="header-right">
        <div class="avatar">
          <el-icon size="20"><user /></el-icon>
        </div>
        <span class="username">系统管理员</span>
        <el-dropdown>
          <span class="dropdown-link">
            <el-icon><grid /></el-icon>
          </span>
        </el-dropdown>
      </div>
    </div>

    <div class="main-container">
      <!-- 左侧导航 -->
      <div class="sidebar">
        <div class="nav-item active">
          <span>角色管理</span>
        </div>
        <div class="nav-item" @click="goToAdminManagement">
          <span>管理员设置</span>
        </div>
      </div>

      <!-- 右侧主内容 -->
      <div class="main-content">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item @click="goToHome" style="cursor: pointer; color: #4A90E2;">首页</el-breadcrumb-item>
            <el-breadcrumb-item>权限管理</el-breadcrumb-item>
            <el-breadcrumb-item>角色管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 标签页 -->
        <el-tabs v-model="activeTab" class="tabs">
          <el-tab-pane label="权限管理" name="role"></el-tab-pane>
        </el-tabs>

        <!-- 角色管理内容 -->
        <div class="role-content">
          <div class="content-header">
            <h3 class="content-title">角色管理</h3>
            <el-button type="primary" @click="handleAddRole">添加角色</el-button>
          </div>

          <!-- 角色列表表格 -->
          <div class="role-table">
            <el-table :data="roleTableData" style="width: 100%" stripe>
              <el-table-column prop="id" label="序号" width="80" />
              <el-table-column prop="roleName" label="角色名称" />
              <el-table-column prop="roleDesc" label="角色描述" />
              <el-table-column prop="moduleNames" label="管理模块" />
              <el-table-column prop="userCount" label="授权人数" width="100" />
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleEditRole(scope.row)">编辑</el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="handleDeleteRole(scope.row)"
                    :disabled="scope.row.id === 1"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 分页 -->
          <div class="pagination-area">
            <el-pagination
              v-model:current-page="pagination.currentPage"
              v-model:page-size="pagination.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 角色编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '添加角色'"
      width="800px"
      class="role-dialog"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="roleForm" :rules="roleRules" label-width="100px">
        <el-form-item label="角色名称:" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>

        <el-form-item label="角色描述:" prop="roleDesc">
          <el-input
            v-model="roleForm.roleDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>

        <el-form-item label="权限设置:">
          <div class="permission-tree">
            <el-tree
              ref="permissionTreeRef"
              :data="permissionTreeData"
              :props="treeProps"
              show-checkbox
              node-key="id"
              :default-checked-keys="checkedPermissions"
              @check="handlePermissionCheck"
            >
              <template #default="{ node, data }">
                <span class="tree-node">
                  <span class="node-label">{{ node.label }}</span>
                </span>
              </template>
            </el-tree>
          </div>
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

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const activeTab = ref('role')
const formRef = ref()
const permissionTreeRef = ref()
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentRoleData = ref({})
const checkedPermissions = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const roleForm = reactive({
  roleName: '',
  roleDesc: ''
})

const treeProps = {
  children: 'children',
  label: 'label'
}

const permissionTreeData = ref([])
const roleTableData = ref([])

const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度应为2-20个字符', trigger: 'blur' }
  ],
  roleDesc: [
    { required: true, message: '请输入角色描述', trigger: 'blur' }
  ]
}

// 查询角色列表
const fetchRoleList = async () => {
  try {
    const { code, message, data } = await request.post('/role-management/search', {
      roleName: '',
      pageNumber: pagination.currentPage,
      pageSize: pagination.pageSize
    })
    if (code === 200) {
      roleTableData.value = data.rows || []
      pagination.total = data.total || 0
    } else {
      ElMessage.error(message || '查询角色失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '查询角色失败')
  }
}

// 获取权限树
const fetchPermissionTree = async () => {
  try {
    const { code, data, message } = await request.get('/role-management/permissions')
    if (code === 200) {
      permissionTreeData.value = data || []
    } else {
      ElMessage.error(message || '获取权限树失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '获取权限树失败')
  }
}

// 保存角色（新增或更新）
const saveRole = async () => {
  try {
    await formRef.value.validate()
    const permissionIds = permissionTreeRef.value.getCheckedKeys()
    const payload = {
      ...roleForm,
      permissionIds
    }
    if (isEdit.value) {
      payload.id = currentRoleData.value.id
    }
    const { code, message } = await request.post('/role-management/save', payload)
    if (code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      fetchRoleList()
    } else {
      ElMessage.error(message || '保存失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}

// 删除角色
const deleteRole = async (roleId) => {
  try {
    const { code, message } = await request.delete(`/role-management/delete?id=${roleId}`)
    if (code === 200) {
      ElMessage.success('删除成功')
      fetchRoleList()
    } else {
      ElMessage.error(message || '删除失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

// 加载角色详情
const loadRoleDetail = async (roleId) => {
  try {
    const { code, data, message } = await request.get(`/role-management/findById?id=${roleId}`)
    if (code === 200) {
      roleForm.roleName = data.roleName
      roleForm.roleDesc = data.roleDesc
      checkedPermissions.value = data.permissionIds || []
    } else {
      ElMessage.error(message || '获取角色详情失败')
    }
  } catch (error) {
    ElMessage.error(error.message || '获取角色详情失败')
  }
}

const goToHome = () => {
  router.push('/dashboard')
}

const goToAdminManagement = () => {
  router.push('/admin-management')
}

const handleAddRole = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEditRole = async (row) => {
  if (row.id === 1) {
    ElMessage.warning('默认超级管理员角色不允许编辑')
    return
  }
  isEdit.value = true
  currentRoleData.value = { ...row }
  dialogVisible.value = true
  await loadRoleDetail(row.id)
}

const handlePermissionCheck = (data, checked) => {
  console.log('权限选择变化:', data, checked)
}

const handleDeleteRole = (row) => {
  if (row.id === 1) {
    ElMessage.warning('默认超级管理员角色不允许删除')
    return
  }
  ElMessageBox.confirm(
    `确定要删除角色 "${row.roleName}" 吗？删除后该角色的所有用户权限将被清除。`,
    '确认删除',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    deleteRole(row.id)
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchRoleList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  fetchRoleList()
}

const resetForm = () => {
  roleForm.roleName = ''
  roleForm.roleDesc = ''
  checkedPermissions.value = []
}

const handleDialogClose = () => {
  dialogVisible.value = false
  resetForm()
}

const handleSubmit = () => {
  saveRole()
}

onMounted(() => {
  fetchRoleList()
  fetchPermissionTree()
})
</script>

<style scoped>
.role-management {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: white;
  color: #4A90E2;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.05);
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.username {
  font-size: 16px;
  font-weight: 500;
}

.dropdown-link {
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-container {
  display: flex;
  height: calc(100vh - 70px);
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
  display: flex;
  align-items: center;
  gap: 8px;
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

.breadcrumb {
  margin-bottom: 20px;
  padding: 15px 0;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child)) {
  cursor: pointer;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child):hover) {
  color: #357ABD;
}

.tabs {
  margin-bottom: 20px;
}

.role-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.content-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.role-table {
  margin-bottom: 20px;
}

.pagination-area {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.permission-tree {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  max-height: 300px;
  overflow-y: auto;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-label {
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 深度样式 */
:deep(.role-dialog .el-dialog) {
  height: 700px;
}

:deep(.role-dialog .el-dialog__body) {
  padding: 20px 24px;
  height: calc(100% - 140px);
}

:deep(.el-tree-node__content) {
  height: 32px;
  line-height: 32px;
}

:deep(.el-tree-node__expand-icon) {
  color: #666;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #4A90E2;
  border-color: #4A90E2;
}

:deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
  background-color: #4A90E2;
  border-color: #4A90E2;
}
</style>
