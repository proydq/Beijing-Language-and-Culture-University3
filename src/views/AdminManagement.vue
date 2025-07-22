<template>
  <div class="admin-management">
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
        <div class="nav-item" @click="goToRoleManagement">
          <span>角色管理</span>
        </div>
        <div class="nav-item active">
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
            <el-breadcrumb-item>管理员设置</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 标签页 - 只保留权限管理 -->
        <el-tabs v-model="activeTab" class="tabs">
          <el-tab-pane label="权限管理" name="admin"></el-tab-pane>
        </el-tabs>

        <!-- 搜索筛选区域 -->
        <div class="search-area">
          <el-form :model="searchForm" class="search-form">
            <el-row :gutter="20" align="middle">
              <el-col :span="5">
                <el-form-item label="管理员名称:" prop="adminName">
                  <el-input v-model="searchForm.adminName" placeholder="请输入姓名信息" />
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="登录账号:" prop="loginAccount">
                  <el-input v-model="searchForm.loginAccount" placeholder="请输入姓名信息" />
                </el-form-item>
              </el-col>
              <el-col :span="4">
                <el-form-item label="账号状态:" prop="accountStatus">
                  <el-select v-model="searchForm.accountStatus" placeholder="全部" style="width: 100%">
                    <el-option label="全部" value="" />
                    <el-option label="正常" value="normal" />
                    <el-option label="禁用" value="disabled" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="所属角色:" prop="roleId">
                  <el-select v-model="searchForm.roleId" placeholder="请选择角色信息" style="width: 100%">
                    <el-option label="请选择角色信息" value="" />
                    <el-option label="默认超级管理员" value="1" />
                    <el-option label="会议系统管理员" value="2" />
                    <el-option label="互动系统管理员" value="3" />
                    <el-option label="门禁系统管理员" value="4" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item>
                  <div class="search-buttons">
                    <el-button type="primary" @click="handleSearch">搜索</el-button>
                    <el-button @click="handleReset">重置</el-button>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>

        <!-- 管理员列表 -->
        <div class="admin-content">
          <div class="content-header">
            <h3 class="content-title">管理员列表</h3>
            <el-button type="primary" @click="handleAddAdmin">添加管理员</el-button>
          </div>

          <!-- 管理员表格 -->
          <div class="admin-table">
            <el-table :data="adminTableData" style="width: 100%" stripe>
              <el-table-column prop="name" label="姓名" width="100" />
              <el-table-column prop="gender" label="性别" width="80" />
              <el-table-column prop="phone" label="手机号" width="130" />
              <el-table-column prop="department" label="所属部门" width="120" />
              <el-table-column label="所属角色" width="150">
                <template #default="scope">
                  <el-tag :color="scope.row.roleColor" effect="dark" style="color: white;">
                    {{ scope.row.roleName }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="accountNumber" label="后台登录账号" width="150" />
              <el-table-column prop="addTime" label="添加时间" width="120" />
              <el-table-column prop="loginStrategy" label="登录策略" width="120" />
              <el-table-column prop="wechatPhone" label="微信绑定手机" width="130" />
              <el-table-column prop="wechatBindTime" label="微信绑定时间" width="130" />
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleEditAdmin(scope.row)">编辑</el-button>
                  <el-button 
                    :type="scope.row.status === 'disabled' ? 'success' : 'danger'" 
                    size="small" 
                    @click="handleToggleStatus(scope.row)"
                  >
                    {{ scope.row.status === 'disabled' ? '解除禁用' : '删除' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination">
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
    </div>

    <!-- 管理员编辑弹窗 -->
    <el-dialog
      v-model="adminDialogVisible"
      :title="isEditAdmin ? '编辑管理员' : '添加管理员'"
      width="500px"
      @close="handleAdminDialogClose"
    >
      <el-form ref="adminFormRef" :model="adminForm" :rules="adminRules" label-width="120px">
        <el-form-item label="管理员姓名:" prop="name">
          <el-input v-model="adminForm.name" placeholder="请输入管理员姓名" />
        </el-form-item>
        
        <el-form-item label="性别:" prop="gender">
          <el-select v-model="adminForm.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="手机号:" prop="phone">
          <el-input v-model="adminForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="所属部门:" prop="department">
          <el-input v-model="adminForm.department" placeholder="请输入所属部门" />
        </el-form-item>
        
        <el-form-item label="选择角色:" prop="roleId">
          <el-select v-model="adminForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option label="默认超级管理员" value="1" />
            <el-option label="会议系统管理员" value="2" />
            <el-option label="互动系统管理员" value="3" />
            <el-option label="门禁系统管理员" value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="后台登录账号:" prop="accountNumber">
          <el-input v-model="adminForm.accountNumber" placeholder="请输入登录账号" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleAdminDialogClose">取消</el-button>
          <el-button type="primary" @click="handleAdminSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

export default {
  name: 'AdminManagement',
  setup() {
    const router = useRouter()
    const activeTab = ref('admin')
    const adminFormRef = ref()
    const adminDialogVisible = ref(false)
    const isEditAdmin = ref(false)
    const currentAdminData = ref({})

    const searchForm = reactive({
      adminName: '',
      loginAccount: '',
      accountStatus: '',
      roleId: ''
    })

    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 100
    })

    const adminForm = reactive({
      name: '',
      gender: '',
      phone: '',
      department: '',
      roleId: '',
      accountNumber: ''
    })

    const adminRules = {
      name: [
        { required: true, message: '请输入管理员姓名', trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      department: [
        { required: true, message: '请输入所属部门', trigger: 'blur' }
      ],
      roleId: [
        { required: true, message: '请选择角色', trigger: 'change' }
      ],
      accountNumber: [
        { required: true, message: '请输入登录账号', trigger: 'blur' }
      ]
    }

    // 管理员数据
    const adminTableData = ref([
      {
        id: 1,
        name: '张三',
        gender: '男',
        phone: '13111311131',
        department: '产品部门',
        roleName: '默认超级管理员',
        roleColor: '#ff0000',
        accountNumber: 'k3456789',
        addTime: '2021.12.02',
        loginStrategy: '小顾子',
        wechatPhone: '13111311131',
        wechatBindTime: '2021.12.02',
        status: 'normal'
      },
      {
        id: 2,
        name: '张四',
        gender: '女',
        phone: '13211321132',
        department: '产品部门',
        roleName: '会议系统管理员',
        roleColor: '#ff9900',
        accountNumber: 'k3456788',
        addTime: '2021.12.02',
        loginStrategy: '小顾子涵',
        wechatPhone: '15787887788',
        wechatBindTime: '2021.12.02',
        status: 'normal'
      },
      {
        id: 3,
        name: '张五',
        gender: '男',
        phone: '13311331133',
        department: '产品部门',
        roleName: '互动系统管理员',
        roleColor: '#ff9966',
        accountNumber: 'k3456787',
        addTime: '2021.12.02',
        loginStrategy: '小顾子回家',
        wechatPhone: '13311331133',
        wechatBindTime: '2021.12.02',
        status: 'normal'
      },
      {
        id: 4,
        name: '张五',
        gender: '男',
        phone: '13311331133',
        department: '产品部门',
        roleName: '门禁系统管理员',
        roleColor: '#ffcc00',
        accountNumber: 'k3456787',
        addTime: '2021.12.02',
        loginStrategy: '/',
        wechatPhone: '/',
        wechatBindTime: '/',
        status: 'disabled'
      }
    ])

    // 返回首页
    const goToHome = () => {
      router.push('/dashboard')
    }

    const goToRoleManagement = () => {
      router.push('/role-management')
    }

    const handleSearch = () => {
      console.log('搜索管理员:', searchForm)
    }

    const handleReset = () => {
      searchForm.adminName = ''
      searchForm.loginAccount = ''
      searchForm.accountStatus = ''
      searchForm.roleId = ''
    }

    const handleAddAdmin = () => {
      router.push('/add-admin')
    }

    const handleEditAdmin = (row) => {
      isEditAdmin.value = true
      currentAdminData.value = { ...row }
      Object.assign(adminForm, {
        name: row.name,
        gender: row.gender,
        phone: row.phone,
        department: row.department,
        roleId: row.roleId || '1',
        accountNumber: row.accountNumber
      })
      adminDialogVisible.value = true
    }

    const handleToggleStatus = (row) => {
      const action = row.status === 'disabled' ? '解除禁用' : '删除'
      const actionType = row.status === 'disabled' ? 'success' : 'warning'
      
      ElMessageBox.confirm(
        `确认要${action}管理员"${row.name}"吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: actionType,
        }
      ).then(() => {
        // 切换状态
        row.status = row.status === 'disabled' ? 'normal' : 'disabled'
        ElMessage.success(`${action}成功`)
      }).catch(() => {
        // 取消操作
      })
    }

    const handleSizeChange = (val) => {
      pagination.pageSize = val
      pagination.currentPage = 1
    }

    const handleCurrentChange = (val) => {
      pagination.currentPage = val
    }

    const handleAdminDialogClose = () => {
      adminDialogVisible.value = false
      isEditAdmin.value = false
      currentAdminData.value = {}
      
      // 重置表单
      Object.assign(adminForm, {
        name: '',
        gender: '',
        phone: '',
        department: '',
        roleId: '',
        accountNumber: ''
      })
      
      // 清除表单验证
      if (adminFormRef.value) {
        adminFormRef.value.clearValidate()
      }
    }

    const handleAdminSubmit = async () => {
      try {
        await adminFormRef.value.validate()
        
        // 角色映射
        const roleMap = {
          '1': { name: '默认超级管理员', color: '#ff0000' },
          '2': { name: '会议系统管理员', color: '#ff9900' },
          '3': { name: '互动系统管理员', color: '#ff9966' },
          '4': { name: '门禁系统管理员', color: '#ffcc00' }
        }
        
        if (isEditAdmin.value) {
          // 编辑管理员
          const index = adminTableData.value.findIndex(item => item.id === currentAdminData.value.id)
          if (index !== -1) {
            adminTableData.value[index] = {
              ...adminTableData.value[index],
              ...adminForm,
              roleName: roleMap[adminForm.roleId].name,
              roleColor: roleMap[adminForm.roleId].color
            }
          }
          ElMessage.success('管理员信息更新成功')
        } else {
          ElMessage.success('管理员信息更新成功')
        }
        
        handleAdminDialogClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    return {
      activeTab,
      adminFormRef,
      adminDialogVisible,
      isEditAdmin,
      searchForm,
      pagination,
      adminForm,
      adminRules,
      adminTableData,
      UserFilled,
      goToHome,  // 新增返回首页方法
      goToRoleManagement,
      handleSearch,
      handleReset,
      handleAddAdmin,
      handleEditAdmin,
      handleToggleStatus,
      handleSizeChange,
      handleCurrentChange,
      handleAdminDialogClose,
      handleAdminSubmit
    }
  }
}
</script>

<style scoped>
.admin-management {
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

.search-area {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

.search-buttons {
  display: flex;
  gap: 10px;
}

.admin-content {
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

.admin-table {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>