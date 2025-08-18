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
          <el-avatar 
            v-if="userStore.userInfo.avatarUrl" 
            :src="userStore.userInfo.avatarUrl"
            :size="32"
            @error="handleAvatarError"
          >
            <el-icon v-if="avatarError" size="20"><user /></el-icon>
          </el-avatar>
          <el-icon v-else size="20"><user /></el-icon>
        </div>
        <span class="username">{{ userStore.displayName }}</span>
        <el-dropdown>
          <span class="dropdown-link">
            <el-icon><grid /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToPersonalCenter">个人中心</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
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

        <!-- 搜索筛选区域 -->
        <div class="search-area">
          <el-form :model="searchForm" class="search-form" inline>
            <el-form-item label="管理员名称:">
              <el-input v-model="searchForm.adminName" placeholder="请输入搜索信息" clearable />
            </el-form-item>
            <el-form-item label="登录账号:">
              <el-input v-model="searchForm.loginAccount" placeholder="请输入搜索信息" clearable />
            </el-form-item>
            <el-form-item label="账号状态:">
              <el-select v-model="searchForm.accountStatus" placeholder="全部" clearable style="width: 120px">
                <el-option label="全部" value="" />
                <el-option label="正常" value="ACTIVE" />
                <el-option label="禁用" value="INACTIVE" />
              </el-select>
            </el-form-item>
            <el-form-item label="所属角色:">
              <el-select v-model="searchForm.roleId" placeholder="请选择角色" clearable style="width: 200px">
                <el-option label="全部" value="" />
                <el-option 
                  v-for="role in availableRoles" 
                  :key="role.roleId" 
                  :label="role.roleName" 
                  :value="role.roleId" 
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
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
            <el-table :data="adminTableData" style="width: 100%" v-loading="loading" element-loading-text="加载中...">
              <el-table-column type="selection" width="55" />
              <el-table-column type="index" label="序号" width="60" align="center" />
              <el-table-column label="头像" width="80" align="center">
                <template #default="{ row }">
                  <div class="avatar-cell">
                    <el-avatar v-if="row.avatarUrl" :size="40" :src="row.avatarUrl" />
                    <el-icon v-else :size="40" color="#c0c4cc"><user-filled /></el-icon>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="realName" label="姓名" min-width="80" />
              <el-table-column prop="gender" label="性别" width="60" align="center" />
              <el-table-column prop="phone" label="手机号" min-width="120" />
              <el-table-column prop="departmentName" label="所属部门" min-width="120" />
              <el-table-column label="所属角色" min-width="120">
                <template #default="{ row }">
                  <div v-if="row.roles && row.roles.length > 0">
                    <el-tag v-for="role in row.roles" :key="role.roleId" style="margin-right: 5px">
                      {{ role.roleName }}
                    </el-tag>
                  </div>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="username" label="后台登录账号" min-width="140" />
              <el-table-column label="管理员账号状态" min-width="120" align="center">
                <template #default="{ row }">
                  <span :style="{ color: row.accountStatus === 'ACTIVE' ? '#67C23A' : '#909399' }">
                    {{ row.accountStatus === 'ACTIVE' ? '正常' : '禁用' }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="添加时间" min-width="160">
                <template #default="{ row }">
                  {{ row.createTime ? new Date(row.createTime).toLocaleDateString('zh-CN') : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="微信昵称" min-width="100">
                <template #default="{ row }">
                  {{ row.wechatNickname || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="微信绑定手机号" min-width="140">
                <template #default="{ row }">
                  {{ row.wechatPhone || '-' }}
                </template>
              </el-table-column>
              <el-table-column label="微信绑定时间" min-width="160">
                <template #default="{ row }">
                  {{ row.wechatBindTime ? new Date(row.wechatBindTime).toLocaleDateString('zh-CN') : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                <template #default="{ row }">
                  <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
                  <el-button v-if="row.accountStatus === 'INACTIVE'" type="primary" size="small" plain @click="handleEdit(row)">解除禁用</el-button>
                  <el-button v-else type="warning" size="small" @click="handleDisable(row)">禁用</el-button>
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
      width="60%"
      :close-on-click-modal="false"
      @close="handleAdminDialogClose"
    >
      <div class="admin-dialog-content">
        <!-- 顶部选择角色区域 -->
        <div class="role-selection">
          <label class="role-label">选择角色：</label>
          <el-select v-model="adminForm.roleId" placeholder="请选择角色名称" style="width: 300px">
            <el-option 
              v-for="role in availableRoles" 
              :key="role.roleId" 
              :label="role.roleName" 
              :value="role.roleId" 
            />
          </el-select>
          <el-button type="primary" class="select-user-btn" @click="handleSelectUser">选择用户</el-button>
        </div>

        <!-- 管理员列表表格 -->
        <div class="admin-list-section">
          <h3 class="section-title">管理员列表</h3>
          <div class="notice-text">
            您好，用户需加开提交后，工号将自动成为后台登录账号，首次登录后需要修改密码；否则后台将无法进行第二次登录；设置成功后，需要在小程序端无规则绑定，以免影响移动端使用
          </div>
          
          <!-- 空状态 -->
          <div v-if="selectedUsers.length === 0" class="empty-state">
            <el-icon class="empty-icon" size="64" color="#d9d9d9">
              <document />
            </el-icon>
            <p>暂无数据</p>
          </div>
          
          <!-- 用户列表 -->
          <el-table v-else :data="selectedUsers" style="width: 100%" border>
            <el-table-column type="selection" width="55" />
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="department" label="部门" />
            <el-table-column prop="position" label="职位" />
            <el-table-column prop="phone" label="电话" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column label="操作" width="100" align="center">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleRemoveUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleAdminDialogClose">取消</el-button>
          <el-button type="primary" @click="handleAdminSubmit">确认</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 选择用户弹窗 -->
    <el-dialog
      v-model="userSelectDialogVisible"
      title="选择用户"
      width="70%"
      :close-on-click-modal="false"
    >
      <div class="user-select-content">
        <!-- 搜索区域 -->
        <el-form :model="userSearchForm" inline class="user-search-form">
          <el-form-item label="姓名:">
            <el-input v-model="userSearchForm.name" placeholder="请输入姓名" clearable @keyup.enter="handleUserSearch" />
          </el-form-item>
          <el-form-item label="部门:">
            <el-input v-model="userSearchForm.department" placeholder="请输入部门" clearable @keyup.enter="handleUserSearch" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUserSearch">搜索</el-button>
            <el-button @click="handleUserSearchReset">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 用户列表 -->
        <el-table
          ref="userTableRef"
          :data="userTableData"
          @selection-change="handleUserSelectionChange"
          style="width: 100%"
          border
        >
          <el-table-column type="selection" width="55" />
          <el-table-column type="index" label="序号" width="60" align="center" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="department" label="部门" width="150" />
          <el-table-column prop="position" label="职位" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="phone" label="电话" width="130" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="userPagination.currentPage"
            v-model:page-size="userPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="userPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleUserSizeChange"
            @current-change="handleUserCurrentChange"
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="userSelectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleUserSelectConfirm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { UserFilled, Document } from '@element-plus/icons-vue'
import { searchPersonnel } from '@/api/schemeManagement'
import { adminManagementAPI } from '@/api/adminManagement'

export default {
  name: 'AdminManagement',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const avatarError = ref(false)
    
    // 处理头像加载错误
    const handleAvatarError = () => {
      avatarError.value = true
    }
    const activeTab = ref('admin')
    const adminFormRef = ref()
    const userTableRef = ref()
    const adminDialogVisible = ref(false)
    const userSelectDialogVisible = ref(false)
    const isEditAdmin = ref(false)
    const currentAdminData = ref({})
    const selectedUsers = ref([])
    const tempSelectedUsers = ref([])

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
      roleId: '',
      users: []
    })

    const userSearchForm = reactive({
      name: '',
      department: ''
    })

    const userPagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 50
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

    // 管理员数据从接口获取
    const adminTableData = ref([])
    const loading = ref(false)
    const availableRoles = ref([])

    // 用户数据从接口获取
    const userTableData = ref([])


    const getStatusTagType = (status) => {
      if (status === 'INACTIVE') return 'danger'
      if (status === 'ACTIVE') return 'success'
      return 'warning'
    }

    const getStatusText = (status) => {
      if (status === 'INACTIVE') return '禁用'
      if (status === 'ACTIVE') return '正常'
      return '/'
    }

    // 返回首页
    const goToHome = () => {
      router.push('/dashboard')
    }

    const goToPersonalCenter = () => {
      router.push('/personal-center')
    }

    const logout = () => {
      // 清除用户数据
      userStore.clearUserData()
      
      // 显示退出成功提示
      ElMessage.success('退出登录成功')
      
      // 跳转到登录页
      router.push('/login')
    }

    const goToRoleManagement = () => {
      router.push('/role-management')
    }

    // 加载管理员列表
    const loadAdminList = async () => {
      try {
        loading.value = true
        const condition = {
          adminName: searchForm.adminName,
          loginAccount: searchForm.loginAccount,
          accountStatus: searchForm.accountStatus,
          roleId: searchForm.roleId,
          roleName: searchForm.roleName,
          pageNumber: pagination.currentPage,
          pageSize: pagination.pageSize
        }

        const response = await adminManagementAPI.searchAdmins(condition)
        
        console.log('管理员列表API响应:', response)
        console.log('response.data:', response.data)
        
        if (response.code === 200 && response.data) {
          // 处理数据，确保所有字段都有默认值，并处理特殊值
          adminTableData.value = (response.data.rows || []).map(row => ({
            ...row,
            id: row.id || '',
            realName: row.realName || '',
            gender: row.gender || '',
            phone: row.phone || '',
            departmentName: row.departmentName || '',
            roles: row.roles || [],
            username: row.username || '',
            accountStatus: row.accountStatus || 'ACTIVE',
            createTime: row.createTime || null,
            // 处理 "/" 值，转换为空字符串或默认显示
            wechatNickname: (row.wechatNickname === '/' || !row.wechatNickname) ? '' : row.wechatNickname,
            wechatPhone: (row.wechatPhone === '/' || !row.wechatPhone) ? '' : row.wechatPhone,
            wechatBindTime: row.wechatBindTime || null,
            // 添加头像字段默认值
            avatarUrl: row.avatarUrl || null
          }))
          pagination.total = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取管理员列表失败')
        }
      } catch (error) {
        console.error('获取管理员列表失败:', error)
        ElMessage.error(error.response?.data?.message || '获取管理员列表失败')
      } finally {
        loading.value = false
      }
    }

    // 加载可分配角色列表
    const loadAvailableRoles = async () => {
      try {
        const response = await adminManagementAPI.getAvailableRoles()
        
        if (response.code === 200 && response.data) {
          // 处理角色数据，确保有正确的字段
          availableRoles.value = (response.data || []).map(role => ({
            ...role,
            roleId: role.roleId || role.id || '',
            roleName: role.roleName || role.name || ''
          }))
        } else {
          ElMessage.error(response.message || '获取角色列表失败')
        }
      } catch (error) {
        console.error('获取角色列表失败:', error)
        ElMessage.error('获取角色列表失败')
      }
    }

    const handleSearch = () => {
      pagination.currentPage = 1
      loadAdminList()
    }

    const handleReset = () => {
      searchForm.adminName = ''
      searchForm.loginAccount = ''
      searchForm.accountStatus = ''
      searchForm.roleId = ''
      handleSearch()
    }

    const handleAddAdmin = () => {
      adminDialogVisible.value = true
      isEditAdmin.value = false
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

    const handleDelete = (row) => {
      ElMessageBox.confirm(
        `确认要删除管理员\"${row.realName}\"吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'danger',
        }
      )
        .then(async () => {
          try {
            const userId = row.id || row.userId
            if (!userId) {
              ElMessage.error('用户ID不存在')
              return
            }
            const response = await adminManagementAPI.deleteAdmin(userId)
            if (response.code === 200) {
              ElMessage.success('删除成功')
              loadAdminList()
            } else {
              ElMessage.error(response.message || '删除失败')
            }
          } catch (error) {
            console.error('删除管理员失败:', error)
            ElMessage.error('删除失败')
          }
        })
        .catch(() => {})
    }

    const handleDisable = (row) => {
      ElMessageBox.confirm(
        `确认要禁用管理员\"${row.realName}\"吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      )
        .then(async () => {
          try {
            const userId = row.id || row.userId
            if (!userId) {
              ElMessage.error('用户ID不存在')
              return
            }
            const response = await adminManagementAPI.updateAdminStatus(userId, { status: 'INACTIVE' })
            if (response.code === 200) {
              ElMessage.success('已禁用')
              loadAdminList()
            } else {
              ElMessage.error(response.message || '禁用失败')
            }
          } catch (error) {
            console.error('禁用管理员失败:', error)
            ElMessage.error('禁用失败')
          }
        })
        .catch(() => {})
    }

    const handleEdit = (row) => {
      ElMessageBox.confirm(
        `确认要解除禁用管理员\"${row.realName}\"吗？`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'primary',
        }
      )
        .then(async () => {
          try {
            const userId = row.id || row.userId
            if (!userId) {
              ElMessage.error('用户ID不存在')
              return
            }
            const response = await adminManagementAPI.updateAdminStatus(userId, { status: 'ACTIVE' })
            if (response.code === 200) {
              ElMessage.success('解除禁用成功')
              loadAdminList()
            } else {
              ElMessage.error(response.message || '解除禁用失败')
            }
          } catch (error) {
            console.error('解除禁用失败:', error)
            ElMessage.error('解除禁用失败')
          }
        })
        .catch(() => {})
    }

    const handleSizeChange = (val) => {
      pagination.pageSize = val
      pagination.currentPage = 1
      loadAdminList()
    }

    const handleCurrentChange = (val) => {
      pagination.currentPage = val
      loadAdminList()
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
      if (!adminForm.roleId) {
        ElMessage.error('请选择角色')
        return
      }
      
      if (selectedUsers.value.length === 0) {
        ElMessage.error('请选择用户')
        return
      }
      
      try {
        const userIds = selectedUsers.value.map(user => user.id || user.userId).filter(id => id)
        if (userIds.length === 0) {
          ElMessage.error('选择的用户没有有效的ID')
          return
        }
        
        const roleIds = [adminForm.roleId]
        
        const request = {
          userIds,
          roleIds
        }
        
        const response = await adminManagementAPI.addAdmins(request)
        if (response.code === 200) {
          const result = response.data
          if (result.successCount > 0) {
            ElMessage.success(`成功添加 ${result.successCount} 个管理员`)
          }
          if (result.failCount > 0) {
            ElMessage.warning(`${result.failCount} 个用户添加失败`)
          }
          
          // 刷新管理员列表
          loadAdminList()
          handleAdminDialogClose()
        } else {
          ElMessage.error(response.message || '添加管理员失败')
        }
      } catch (error) {
        console.error('添加管理员失败:', error)
        ElMessage.error('添加管理员失败')
      }
    }

    // 选择用户相关方法
    const handleSelectUser = () => {
      userSelectDialogVisible.value = true
      // 打开弹窗时加载用户数据
      loadUserData()
    }

    const loadUserData = async () => {
      try {
        const params = {
          pageNumber: userPagination.currentPage,
          pageSize: userPagination.pageSize,
          name: userSearchForm.name || '',
          department: userSearchForm.department || '',
          position: ''
        }

        const response = await searchPersonnel(params)
        if (response.code === 200) {
          userTableData.value = response.data.rows || []
          userPagination.total = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取用户数据失败')
        }
      } catch (error) {
        console.error('获取用户数据失败:', error)
        ElMessage.error('获取用户数据失败')
      }
    }

    const handleUserSearch = () => {
      userPagination.currentPage = 1
      loadUserData()
    }

    const handleUserSearchReset = () => {
      userSearchForm.name = ''
      userSearchForm.department = ''
      userPagination.currentPage = 1
      loadUserData()
    }

    const handleUserSelectionChange = (selection) => {
      tempSelectedUsers.value = selection
    }

    const handleUserSelectConfirm = () => {
      selectedUsers.value = [...tempSelectedUsers.value]
      userSelectDialogVisible.value = false
      ElMessage.success(`已选择 ${selectedUsers.value.length} 个用户`)
    }

    const handleRemoveUser = (user) => {
      const index = selectedUsers.value.findIndex(item => item.id === user.id)
      if (index !== -1) {
        selectedUsers.value.splice(index, 1)
        ElMessage.success('用户移除成功')
      }
    }

    const handleUserSizeChange = (val) => {
      userPagination.pageSize = val
      userPagination.currentPage = 1
      loadUserData()
    }

    const handleUserCurrentChange = (val) => {
      userPagination.currentPage = val
      loadUserData()
    }

    // 页面初始化
    onMounted(() => {
      loadAdminList()
      loadAvailableRoles()
    })

    return {
      activeTab,
      adminFormRef,
      userTableRef,
      adminDialogVisible,
      userSelectDialogVisible,
      isEditAdmin,
      searchForm,
      pagination,
      userStore,
      avatarError,
      handleAvatarError,
      adminForm,
      adminRules,
      adminTableData,
      userTableData,
      userSearchForm,
      userPagination,
      selectedUsers,
      tempSelectedUsers,
      loading,
      availableRoles,
      UserFilled,
      Document,
      goToHome,
      goToRoleManagement,
      handleSearch,
      handleReset,
      handleAddAdmin,
      handleEditAdmin,
      handleDelete,
      handleDisable,
      handleEdit,
      handleSizeChange,
      handleCurrentChange,
      handleAdminDialogClose,
      handleAdminSubmit,
      handleSelectUser,
      loadUserData,
      handleUserSearch,
      handleUserSearchReset,
      handleUserSelectionChange,
      handleUserSelectConfirm,
      handleRemoveUser,
      handleUserSizeChange,
      handleUserCurrentChange,
      loadAdminList,
      loadAvailableRoles,
      getStatusTagType,
      getStatusText
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

.avatar-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.admin-table :deep(.el-table__header) {
  background-color: #f5f7fa;
}

.admin-table :deep(.el-table__row) {
  height: 60px;
}

.admin-table :deep(.el-button--small) {
  padding: 5px 10px;
}

/* 添加管理员弹窗样式 */
.admin-dialog-content {
  padding: 10px 0;
}

.role-selection {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.role-label {
  font-weight: 500;
  color: #333;
  white-space: nowrap;
}

.select-user-btn {
  margin-left: auto;
}

.admin-list-section {
  margin-top: 20px;
}

.section-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.notice-text {
  background-color: #fff2e8;
  border: 1px solid #ffd591;
  border-radius: 6px;
  padding: 12px;
  font-size: 13px;
  color: #d46b08;
  margin-bottom: 20px;
  line-height: 1.5;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  margin-bottom: 16px;
}

/* 选择用户弹窗样式 */
.user-select-content {
  padding: 10px 0;
}

.user-search-form {
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}
</style>