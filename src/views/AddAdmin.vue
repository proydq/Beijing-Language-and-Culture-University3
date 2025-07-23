<template>
  <div class="add-admin">
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
        <!-- 添加首页导航 -->
        <div class="nav-item" @click="goToHome">
          <el-icon><home /></el-icon>
          <span>首页</span>
        </div>
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
            <el-breadcrumb-item @click="goToAdminManagement" style="cursor: pointer; color: #4A90E2;">管理员设置</el-breadcrumb-item>
            <el-breadcrumb-item>添加管理员</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 页面标题 -->
        <div class="page-header">
          <el-button type="text" @click="goBack" class="back-button">
            <el-icon><arrow-left /></el-icon>
            返回
          </el-button>
        </div>

        <!-- 选择角色区域 -->
        <div class="role-selection">
          <div class="section-title">选择角色：</div>
          <el-select 
            v-model="selectedRole" 
            placeholder="请选择角色名称" 
            style="width: 300px"
            @change="handleRoleChange"
          >
            <el-option 
              v-for="role in roleOptions" 
              :key="role.value" 
              :label="role.label" 
              :value="role.value" 
            />
          </el-select>
        </div>

        <!-- 管理员列表 -->
        <div class="admin-content">
          <div class="content-header">
            <h3 class="content-title">管理员列表</h3>
            <el-button type="primary" @click="showSelectUserDialog">选择用户</el-button>
          </div>

          <!-- 提示信息 -->
          <div class="notice-area">
            <el-alert
              title="注意：用户添加后生效。工号等信息请在后台管理专家，首次绑定微信需要进行一次登录，设置完之后，需要实例保存完成的单据"
              type="warning"
              :closable="false"
              show-icon
            />
          </div>

          <!-- 暂无数据 -->
          <div v-if="selectedUsers.length === 0" class="empty-state">
            <div class="empty-icon">
              <el-icon size="80"><document /></el-icon>
            </div>
            <div class="empty-text">暂无数据</div>
          </div>

          <!-- 已选择的用户列表 -->
          <div v-else class="selected-users">
            <div v-for="(user, index) in selectedUsers" :key="user.id" class="user-item">
              <div class="user-info">
                <span class="user-name">{{ user.name }}</span>
                <span class="user-detail">{{ user.gender }} | {{ user.phone }} | {{ user.department }}</span>
              </div>
              <el-button type="danger" size="small" @click="removeUser(index)">移除</el-button>
            </div>
          </div>
        </div>

        <!-- 底部操作按钮 -->
        <div class="footer-actions">
          <el-button @click="goBack">取消</el-button>
          <el-button type="primary" @click="handleConfirm">确认</el-button>
        </div>
      </div>
    </div>

    <!-- 选择用户弹窗 -->
    <el-dialog
      v-model="selectUserDialogVisible"
      title="选择用户"
      width="80%"
      @close="handleDialogClose"
    >
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" :inline="true">
          <el-form-item label="姓名:">
            <el-input v-model="searchForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号:">
            <el-input v-model="searchForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="工号:">
            <el-input v-model="searchForm.workId" placeholder="请输入工号" />
          </el-form-item>
          <el-form-item label="部门:">
            <el-input v-model="searchForm.department" placeholder="请输入部门" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 用户列表 -->
      <div class="user-table">
        <el-table 
          ref="userTableRef"
          :data="filteredUserList" 
          style="width: 100%" 
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="gender" label="性别" width="80" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="department" label="部门" width="120" />
          <el-table-column prop="workId" label="工号" width="120" />
          <el-table-column label="操作" width="100">
            <template #default="scope">
              <el-button type="primary" size="small">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleUserSelection">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

export default {
  name: 'AddAdmin',
  setup() {
    const router = useRouter()
    const userTableRef = ref()
    const selectUserDialogVisible = ref(false)
    const selectedRole = ref('')
    const selectedUsers = ref([])
    const tempSelectedUsers = ref([])

    const searchForm = reactive({
      name: '',
      phone: '',
      workId: '',
      department: ''
    })

    // 角色选项
    const roleOptions = [
      { label: '默认超级管理员', value: '1' },
      { label: '会议系统管理员', value: '2' },
      { label: '互动系统管理员', value: '3' },
      { label: '门禁系统管理员', value: '4' }
    ]

    // 模拟用户数据
    const userList = ref([
      {
        id: 1,
        name: '张三',
        gender: '男',
        phone: '13111311131',
        department: '产品部门',
        workId: 'k3456789'
      },
      {
        id: 2,
        name: '张四',
        gender: '女',
        phone: '13211321132',
        department: '产品部门',
        workId: 'k3456788'
      },
      {
        id: 3,
        name: '张五',
        gender: '男',
        phone: '13311331133',
        department: '产品部门',
        workId: 'k3456787'
      },
      {
        id: 4,
        name: '李六',
        gender: '男',
        phone: '13411341134',
        department: '技术部门',
        workId: 'k3456786'
      },
      {
        id: 5,
        name: '王七',
        gender: '女',
        phone: '13511351135',
        department: '市场部门',
        workId: 'k3456785'
      }
    ])

    // 过滤后的用户列表
    const filteredUserList = computed(() => {
      return userList.value.filter(user => {
        return (!searchForm.name || user.name.includes(searchForm.name)) &&
               (!searchForm.phone || user.phone.includes(searchForm.phone)) &&
               (!searchForm.workId || user.workId.includes(searchForm.workId)) &&
               (!searchForm.department || user.department === searchForm.department)
      })
    })

    // 返回首页
    const goToHome = () => {
      router.push('/dashboard')
    }

    const goBack = () => {
      router.back()
    }

    const goToRoleManagement = () => {
      router.push('/role-management')
    }

    const goToAdminManagement = () => {
      router.push('/admin-management')
    }

    const handleRoleChange = (value) => {
      console.log('选择角色:', value)
    }

    const showSelectUserDialog = () => {
      if (!selectedRole.value) {
        ElMessage.warning('请先选择角色')
        return
      }
      selectUserDialogVisible.value = true
    }

    const handleDialogClose = () => {
      selectUserDialogVisible.value = false
      tempSelectedUsers.value = []
      // 清空表格选择
      if (userTableRef.value) {
        userTableRef.value.clearSelection()
      }
    }

    const handleSelectionChange = (selection) => {
      tempSelectedUsers.value = selection
    }

    const handleUserSelection = () => {
      if (tempSelectedUsers.value.length === 0) {
        ElMessage.warning('请选择至少一个用户')
        return
      }
      
      // 过滤掉已经存在的用户
      const newUsers = tempSelectedUsers.value.filter(newUser => 
        !selectedUsers.value.some(existUser => existUser.id === newUser.id)
      )
      
      selectedUsers.value.push(...newUsers)
      ElMessage.success(`成功添加 ${newUsers.length} 个用户`)
      handleDialogClose()
    }

    const removeUser = (index) => {
      const removedUser = selectedUsers.value.splice(index, 1)[0]
      ElMessage.success(`已移除用户: ${removedUser.name}`)
    }

    const handleSearch = () => {
      console.log('搜索用户:', searchForm)
    }

    const handleReset = () => {
      searchForm.name = ''
      searchForm.phone = ''
      searchForm.workId = ''
      searchForm.department = ''
    }

    const handleConfirm = () => {
      if (!selectedRole.value) {
        ElMessage.warning('请先选择角色')
        return
      }
      if (selectedUsers.value.length === 0) {
        ElMessage.warning('请选择至少一个用户')
        return
      }

      const roleMap = {
        '1': '默认超级管理员',
        '2': '会议系统管理员',
        '3': '互动系统管理员',
        '4': '门禁系统管理员'
      }

      ElMessage.success(`成功添加 ${selectedUsers.value.length} 个${roleMap[selectedRole.value]}`)
      
      // 这里可以调用API保存数据
      console.log('保存数据:', {
        role: selectedRole.value,
        users: selectedUsers.value
      })

      // 返回上一页
      setTimeout(() => {
        goBack()
      }, 1500)
    }

    return {
      userTableRef,
      selectUserDialogVisible,
      selectedRole,
      selectedUsers,
      searchForm,
      roleOptions,
      filteredUserList,
      UserFilled,
      goToHome,  // 新增返回首页方法
      goBack,
      goToRoleManagement,
      goToAdminManagement,  // 新增返回管理员管理方法
      handleRoleChange,
      showSelectUserDialog,
      handleDialogClose,
      handleSelectionChange,
      handleUserSelection,
      removeUser,
      handleSearch,
      handleReset,
      handleConfirm
    }
  }
}
</script>

<style scoped>
.add-admin {
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

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #4A90E2;
  font-size: 16px;
}

.role-selection {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-weight: 500;
  margin-bottom: 15px;
  color: #333;
}

.admin-content {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
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

.notice-area {
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.empty-icon {
  margin-bottom: 20px;
}

.empty-text {
  font-size: 16px;
}

.selected-users {
  margin-top: 20px;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 10px;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.user-detail {
  font-size: 12px;
  color: #666;
}

.footer-actions {
  background: white;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  gap: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-area {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.user-table {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>