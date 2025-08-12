<template>
  <div class="permission-demo">
    <el-card class="header-card">
      <h1>权限控制示例</h1>
      <p>当前用户：{{ userStore.displayName }}</p>
      <p>角色：{{ userStore.roles.join(', ') || '无' }}</p>
    </el-card>

    <!-- 用户权限信息 -->
    <el-card class="info-card">
      <template #header>
        <h2>用户权限信息</h2>
      </template>
      <div class="permission-list">
        <el-tag 
          v-for="permission in userStore.permissions" 
          :key="permission"
          type="primary"
          class="permission-tag"
        >
          {{ permission }}
        </el-tag>
      </div>
    </el-card>

    <!-- 按钮权限示例 -->
    <el-card class="demo-card">
      <template #header>
        <h2>按钮权限示例</h2>
      </template>
      
      <div class="demo-section">
        <h3>用户管理</h3>
        <el-button v-permission="'USER_VIEW'" type="primary">
          查看用户（需要 USER_VIEW）
        </el-button>
        <el-button v-permission="'USER_ADD'" type="success">
          新增用户（需要 USER_ADD）
        </el-button>
        <el-button v-permission="'USER_EDIT'" type="warning">
          编辑用户（需要 USER_EDIT）
        </el-button>
        <el-button v-permission="'USER_DELETE'" type="danger">
          删除用户（需要 USER_DELETE）
        </el-button>
        <el-button v-permission="['USER_VIEW', 'USER_MANAGE']" type="info">
          查看或管理（需要 USER_VIEW 或 USER_MANAGE）
        </el-button>
      </div>

      <div class="demo-section">
        <h3>教室管理</h3>
        <el-button v-permission="'ROOM_VIEW'" type="primary">
          查看教室（需要 ROOM_VIEW）
        </el-button>
        <el-button v-permission="'ROOM_BOOK'" type="success">
          预订教室（需要 ROOM_BOOK）
        </el-button>
        <el-button v-permission="'ROOM_APPROVE'" type="warning">
          审批预订（需要 ROOM_APPROVE）
        </el-button>
      </div>

      <div class="demo-section">
        <h3>多权限组合</h3>
        <el-button v-permission.all="['USER_VIEW', 'ROOM_VIEW']" type="primary">
          同时需要用户查看和教室查看权限
        </el-button>
        <el-button v-permission="['USER_MANAGE', 'ROOM_MANAGE']" type="success">
          需要用户管理或教室管理权限之一
        </el-button>
      </div>
    </el-card>

    <!-- 角色权限示例 -->
    <el-card class="demo-card">
      <template #header>
        <h2>角色权限示例</h2>
      </template>
      
      <div class="demo-section">
        <el-button v-role="'admin'" type="primary">
          仅管理员可见（需要 admin 角色）
        </el-button>
        <el-button v-role="'role001'" type="success">
          超级管理员可见（需要 role001）
        </el-button>
        <el-button v-role="['role002', 'role003']" type="warning">
          普通用户或部门管理员可见
        </el-button>
      </div>
    </el-card>

    <!-- 程序化权限检查示例 -->
    <el-card class="demo-card">
      <template #header>
        <h2>程序化权限检查</h2>
      </template>
      
      <div class="demo-section">
        <p>使用 v-if 条件渲染：</p>
        <el-button v-if="hasPermission('USER_ADD')" type="primary">
          v-if 权限检查（USER_ADD）
        </el-button>
        
        <p>使用计算属性：</p>
        <el-button v-if="canManageUsers" type="success">
          可以管理用户
        </el-button>
        
        <p>使用方法调用：</p>
        <el-button @click="checkAndExecute">
          点击检查权限后执行
        </el-button>
      </div>
    </el-card>

    <!-- 菜单权限示例 -->
    <el-card class="demo-card">
      <template #header>
        <h2>菜单权限过滤示例</h2>
      </template>
      
      <el-menu class="demo-menu">
        <template v-for="menu in filteredMenus" :key="menu.path || menu.title">
          <el-sub-menu v-if="menu.children" :index="menu.title">
            <template #title>
              <el-icon v-if="menu.icon">
                <component :is="menu.icon" />
              </el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item 
              v-for="child in menu.children" 
              :key="child.path"
              :index="child.path"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.path">
            <el-icon v-if="menu.icon">
              <component :is="menu.icon" />
            </el-icon>
            <span>{{ menu.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { hasPermission, hasAnyPermission, PERMISSIONS } from '@/utils/permission'
import { menuConfig, filterMenuByPermission } from '@/config/menu'
import { ElMessage } from 'element-plus'

// 使用用户store
const userStore = useUserStore()

// 计算属性示例
const canManageUsers = computed(() => {
  return hasPermission(PERMISSIONS.USER.MANAGE)
})

// 过滤后的菜单
const filteredMenus = computed(() => {
  return filterMenuByPermission([...menuConfig], permission => hasPermission(permission))
})

// 方法示例
const checkAndExecute = () => {
  if (hasPermission(PERMISSIONS.USER.ADD)) {
    ElMessage.success('您有权限执行此操作！')
  } else {
    ElMessage.error('您没有权限执行此操作！')
  }
}
</script>

<style scoped>
.permission-demo {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-card {
  margin-bottom: 20px;
  text-align: center;
}

.header-card h1 {
  margin: 0 0 10px 0;
  color: #303133;
}

.header-card p {
  margin: 5px 0;
  color: #606266;
}

.info-card {
  margin-bottom: 20px;
}

.permission-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.permission-tag {
  margin: 0;
}

.demo-card {
  margin-bottom: 20px;
}

.demo-card h2 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.demo-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.demo-section:last-child {
  margin-bottom: 0;
}

.demo-section h3 {
  margin: 0 0 15px 0;
  color: #409eff;
  font-size: 16px;
}

.demo-section p {
  margin: 10px 0;
  color: #606266;
}

.demo-section .el-button {
  margin: 5px;
}

.demo-menu {
  border: none;
  background: #f5f7fa;
}
</style>