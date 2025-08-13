<template>
  <div class="dashboard">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">首页</span>
      </div>
      <div class="header-right">
        <div class="header-right">
          <div class="avatar" @click="handleLogout" title="点击退出登录">
            <el-icon size="20"><user /></el-icon>
          </div>
        </div>
        <span class="username">张三</span>
        <el-dropdown>
          <span class="dropdown-link">
            中文
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>中文</el-dropdown-item>
              <el-dropdown-item>English</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 卡片网格布局 -->
      <div class="card-grid">
        <!-- 原有系统 -->
        <div v-if="showBookingManagement" class="function-card" @click="handleCardClick('房屋借用管理')">
          <div class="card-icon">
            <el-icon size="40"><calendar /></el-icon>
          </div>
          <h4>房屋借用管理</h4>
          <p>预约教室，查看预约记录</p>
        </div>
        
        <div v-if="showRoomManagement" class="function-card" @click="handleCardClick('房屋信息管理')">
          <div class="card-icon">
            <el-icon size="40"><office-building /></el-icon>
          </div>
          <h4>房屋信息管理</h4>
          <p>管理教室、会议室等场地</p>
        </div>
        
        <div v-if="showUserManagement" class="function-card" @click="handleCardClick('用户管理')">
          <div class="card-icon">
            <el-icon size="40"><user /></el-icon>
          </div>
          <h4>用户管理</h4>
          <p>管理系统用户信息</p>
        </div>
        
        <div v-if="showRoleManagement" class="function-card" @click="handleCardClick('权限管理')">
          <div class="card-icon">
            <el-icon size="40"><key /></el-icon>
          </div>
          <h4>权限管理</h4>
          <p>管理系统角色和权限</p>
        </div>
      </div>
      
      <!-- 如果没有任何权限，显示提示 -->
      <div v-if="!hasAnyPermission" class="no-permission-hint">
        <el-empty description="您暂时没有访问任何功能模块的权限，请联系管理员">
          <el-button type="primary" @click="handleLogout">重新登录</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { addDynamicRoutes, clearDynamicRoutes } from '@/router/index'
import { onMounted, computed } from 'vue'

export default {
  name: 'Dashboard',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    
    // 检查用户是否有特定权限
    const hasPermission = (permissionCode) => {
      return userStore.permissions.includes(permissionCode)
    }
    
    // 基于后端接口权限判断前端页面显示
    // 有任何用户相关接口权限就显示用户管理
    const showUserManagement = computed(() => 
      hasPermission('system:user:view') || 
      hasPermission('system:user:create') || 
      hasPermission('system:user:update') || 
      hasPermission('system:user:delete') ||
      hasPermission('system:user:manage')
    )
    
    // 有任何房间或区域相关接口权限就显示房屋信息管理
    const showRoomManagement = computed(() => 
      hasPermission('building:room:view') || 
      hasPermission('building:room:create') || 
      hasPermission('building:room:update') || 
      hasPermission('building:room:delete') ||
      hasPermission('building:room:search') ||
      hasPermission('building:room:manage') ||
      hasPermission('building:area:view') ||
      hasPermission('building:area:create') ||
      hasPermission('building:area:update') ||
      hasPermission('building:area:delete') ||
      hasPermission('building:area:manage')
    )
    
    // 有任何预订相关接口权限就显示房屋借用管理
    const showBookingManagement = computed(() => 
      hasPermission('booking:booking:view') || 
      hasPermission('booking:booking:create') || 
      hasPermission('booking:booking:update') || 
      hasPermission('booking:booking:cancel') ||
      hasPermission('booking:booking:approve') ||
      hasPermission('booking:booking:reject') ||
      hasPermission('booking:booking:manage')
    )
    
    // 有系统管理相关权限就显示系统管理
    const showSystemManagement = computed(() => hasPermission('system:system:manage'))
    
    // 有角色相关接口权限就显示角色管理
    const showRoleManagement = computed(() => 
      hasPermission('system:role:view') || 
      hasPermission('system:role:create') || 
      hasPermission('system:role:update') || 
      hasPermission('system:role:delete') ||
      hasPermission('system:role:manage')
    )
    
    // 有权限相关接口权限就显示权限管理
    const showPermissionManagement = computed(() => hasPermission('system:permission:manage'))
    
    // 检查是否有任何权限
    const hasAnyPermission = computed(() => {
      return showUserManagement.value || 
             showRoomManagement.value || 
             showBookingManagement.value || 
             showSystemManagement.value || 
             showRoleManagement.value || 
             showPermissionManagement.value
    })
    
    // 检查用户类型
    const userType = computed(() => {
      const permissions = userStore.permissions
      
      // 检查是否有系统管理权限（使用新权限格式）
      const hasSystemPermissions = permissions.some(p => 
        p === 'system:user:manage' || 
        p === 'system:role:manage' || 
        p === 'system:system:manage' ||
        p === 'system:permission:manage'
      )
      
      // 检查是否有其他高级管理权限（这些权限普通审批员不应该有）
      const hasAdvancedPermissions = permissions.some(p =>
        p === 'building:area:manage' ||  // 精确匹配，避免误判
        p === 'scheme:scheme:manage' ||
        p === 'system:blacklist:manage' ||
        p === 'system:violation:manage' ||
        p === 'system:recycle-bin:manage' ||
        p === 'building:room:create' ||
        p === 'building:room:update' ||
        p === 'building:room:delete' ||
        p === 'building:room:manage'
      )
      
      // 检查是否有审批权限
      const hasApprovalPermissions = permissions.some(p => 
        p === 'booking:booking:approve' || 
        p === 'booking:booking:reject' ||
        p === 'approval:approval:process' ||
        p === 'approval:approval:manage'
      )
      
      // 判断用户类型
      if (hasSystemPermissions || hasAdvancedPermissions) {
        return 'admin'  // 管理员（有系统管理权限或高级管理权限）
      } else if (hasApprovalPermissions) {
        return 'approver'  // 审批员（只有审批权限）
      } else {
        return 'normal'  // 普通用户
      }
    })
    
    // 是否是普通用户
    const isNormalUser = computed(() => userType.value === 'normal')
    
    // 是否是审批员
    const isApprover = computed(() => userType.value === 'approver')
    
    // 是否是管理员
    const isAdmin = computed(() => userType.value === 'admin')
    
    // 组件挂载时检查动态路由
    onMounted(() => {
      console.log('Dashboard挂载，检查权限树')
      console.log('权限树数据:', userStore.permissionTree)
      
      // 如果没有权限树数据，创建一个临时测试数据
      if (!userStore.permissionTree || userStore.permissionTree.length === 0) {
        console.log('没有权限树数据，创建测试数据')
        const testPermissionTree = [
          {
            id: 'test_dashboard',
            code: 'DASHBOARD',
            name: '首页',
            type: 'MENU',
            path: '/dashboard',
            component: 'Dashboard',
            icon: 'home-filled',
            visible: true,
            sort: 1
          },
          {
            id: 'test_user',
            code: 'USER_MANAGE',
            name: '用户管理',
            type: 'MENU',
            path: '/user-management',
            component: 'UserManagement',
            icon: 'user',
            visible: true,
            sort: 2
          }
        ]
        
        // 临时设置权限树数据
        userStore.permissionTree = testPermissionTree
        console.log('测试权限树已创建:', testPermissionTree)
      }
      
      // 尝试添加动态路由
      if (userStore.permissionTree && userStore.permissionTree.length > 0) {
        console.log('发现权限树数据，添加动态路由')
        addDynamicRoutes()
      }
    })

    const handleCardClick = (cardName) => {
      console.log('点击了卡片:', cardName)
      console.log('当前可用路由:', router.getRoutes().map(r => r.path))
      
      switch (cardName) {
        case '用户信息管理':
          console.log('尝试跳转到 /user-management')
          router.push('/user-management').catch(err => {
            console.error('跳转失败:', err)
          })
          break
        case '房屋信息管理':
          console.log('尝试跳转到 /house-management')
          router.push('/house-management').catch(err => {
            console.error('跳转失败:', err)
          })
          break
        case '房屋借用管理':
          console.log('尝试跳转到 /room-booking')
          router.push('/room-booking').catch(err => {
            console.error('跳转失败:', err)
          })
          break
        case '权限管理':
        case '角色管理':
          console.log('尝试跳转到 /role-management')
          router.push('/role-management').catch(err => {
            console.error('跳转失败:', err)
          })
          break
        case '用户管理':
          console.log('尝试跳转到 /user-management')
          router.push('/user-management').catch(err => {
            console.error('跳转失败:', err)
          })
          break
        case '区域管理':
          console.log('尝试跳转到区域管理页面')
          // 这里需要根据实际的区域管理路由来跳转
          ElMessage.info('区域管理功能开发中')
          break
        case '房屋借用管理':
          console.log('尝试跳转到 /room-booking')
          router.push('/room-booking').catch(err => {
            console.error('跳转失败:', err)
          })
          break
        default:
          console.log('点击了:', cardName)
      }
    }

    const handleLogout = () => {
      // 清除登录信息并跳转到登录页
      userStore.clearUserData()
      clearDynamicRoutes()
      router.replace('/login')
    }

    return {
      handleCardClick,
      handleLogout,
      // 权限相关
      showUserManagement,
      showRoomManagement,
      showBookingManagement,
      showSystemManagement,
      showRoleManagement,
      showPermissionManagement,
      hasAnyPermission,
      // 用户类型
      userType,
      isNormalUser,
      isApprover,
      isAdmin
    }
  },
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: #f4f6f9;
}

.header {
  background: white;
  color: #333;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  border-bottom: 1px solid #e8e8e8;
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
  background: #4a90e2;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
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
  background: #f0f0f0;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.avatar:hover {
  background: #e0e0e0;
}

.username {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-content {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.function-card {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e8e8e8;
  position: relative;
  text-align: center;
  min-height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.function-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #f5f5f5;
  color: #999;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.card-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4a90e2;
}

.function-card h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.function-card p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .header {
    padding: 15px 20px;
    flex-direction: column;
    gap: 15px;
  }

  .main-content {
    padding: 20px;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }

  .function-card {
    padding: 15px;
  }
}
</style>
