<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">{{ pageTitle }}</span>
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
      <!-- 左侧导航栏 -->
      <div class="sidebar">
        <div class="nav-item" 
             :class="{ active: $route.name === 'Dashboard' }"
             @click="navigateTo('/dashboard')">
          <el-icon><home /></el-icon>
          <span>仪表板</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'UserManagement' }"
             @click="navigateTo('/user-management')">
          <el-icon><user /></el-icon>
          <span>用户管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'OrganizationManagement' }"
             @click="navigateTo('/organization-management')">
          <el-icon><office-building /></el-icon>
          <span>组织管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'PositionManagement' }"
             @click="navigateTo('/position-management')">
          <el-icon><briefcase /></el-icon>
          <span>职务管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'LevelManagement' }"
             @click="navigateTo('/level-management')">
          <el-icon><medal /></el-icon>
          <span>等级管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'HouseManagement' }"
             @click="navigateTo('/house-management')">
          <el-icon><house /></el-icon>
          <span>房屋管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: isAccessControlActive }"
             @click="navigateTo('/access-control/dashboard')">
          <el-icon><lock /></el-icon>
          <span>门禁管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'RoleManagement' }"
             @click="navigateTo('/role-management')">
          <el-icon><key /></el-icon>
          <span>角色管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'AdminManagement' }"
             @click="navigateTo('/admin-management')">
          <el-icon><user-filled /></el-icon>
          <span>管理员管理</span>
        </div>
        <div class="nav-item" 
             :class="{ active: $route.name === 'PersonalCenter' }"
             @click="navigateTo('/personal-center')">
          <el-icon><setting /></el-icon>
          <span>个人中心</span>
        </div>
      </div>

      <!-- 右侧内容区域 -->
      <div class="main-content">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'

export default {
  name: 'Layout',
  setup() {
    const router = useRouter()
    const route = useRoute()

    // 根据当前路由动态设置页面标题
    const pageTitle = computed(() => {
      const titleMap = {
        'Dashboard': '综合管理平台',
        'UserManagement': '用户管理',
        'OrganizationManagement': '组织管理',
        'PositionManagement': '职务管理',
        'LevelManagement': '等级管理',
        'HouseManagement': '房屋管理',
        'AccessControlDashboard': '门禁管理',
        'AccessControlRecords': '门禁管理 - 通行记录',
        'AccessControlPermissions': '门禁管理 - 权限分配',
        'AccessControlDeviceManagement': '门禁管理 - 设备管理',
        'RoleManagement': '角色管理',
        'AdminManagement': '管理员管理',
        'PersonalCenter': '个人中心'
      }
      return titleMap[route.name] || '综合管理平台'
    })

    // 判断是否是门禁管理相关页面
    const isAccessControlActive = computed(() => {
      return route.path.startsWith('/access-control')
    })

    const navigateTo = (path) => {
      router.push(path)
    }

    const goToPersonalCenter = () => {
      router.push('/personal-center')
    }

    const logout = () => {
      // 这里可以添加退出登录的逻辑
      console.log('退出登录')
      // 例如：清除token，跳转到登录页等
    }

    return {
      pageTitle,
      isAccessControlActive,
      navigateTo,
      goToPersonalCenter,
      logout
    }
  }
}
</script>

<style scoped>
.layout {
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
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
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
  margin-top: 70px; /* 为固定头部留出空间 */
  min-height: calc(100vh - 70px);
}

.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 15px;
  flex-shrink: 0;
  position: fixed;
  left: 0;
  top: 70px;
  height: calc(100vh - 70px);
  overflow-y: auto;
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
  gap: 10px;
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
  margin-left: 200px; /* 为固定侧边栏留出空间 */
  overflow-y: auto;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
  }
  
  .sidebar {
    position: relative;
    width: 100%;
    height: auto;
    top: 0;
  }
  
  .main-content {
    margin-left: 0;
  }
}

/* Element Plus 组件样式覆盖 */
:deep(.el-dropdown) {
  color: white;
}
</style>