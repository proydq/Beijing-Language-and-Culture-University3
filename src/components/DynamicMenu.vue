<template>
  <el-menu
    :default-active="activeMenu"
    class="dynamic-menu"
    :collapse="collapse"
    @select="handleSelect"
  >
    <template v-for="menu in menuItems" :key="menu.id">
      <!-- 有子菜单的情况 -->
      <el-sub-menu 
        v-if="menu.children && menu.children.length > 0 && hasVisibleChildren(menu)" 
        :index="menu.code"
      >
        <template #title>
          <el-icon v-if="menu.icon">
            <component :is="menu.icon" />
          </el-icon>
          <span>{{ menu.name }}</span>
        </template>
        <el-menu-item 
          v-for="child in getVisibleChildren(menu)" 
          :key="child.id"
          :index="child.path || child.code"
        >
          <template #title>
            <span>{{ child.name }}</span>
          </template>
        </el-menu-item>
      </el-sub-menu>
      
      <!-- 没有子菜单的情况 -->
      <el-menu-item 
        v-else-if="menu.visible && menu.path" 
        :index="menu.path"
      >
        <el-icon v-if="menu.icon">
          <component :is="menu.icon" />
        </el-icon>
        <template #title>
          <span>{{ menu.name }}</span>
        </template>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const props = defineProps({
  collapse: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 从权限树中获取菜单项
const menuItems = computed(() => {
  if (!userStore.permissionTree || userStore.permissionTree.length === 0) {
    return []
  }
  
  // 过滤出类型为MENU且visible为true的权限
  return filterMenuItems(userStore.permissionTree)
})

// 过滤菜单项
function filterMenuItems(items) {
  return items
    .filter(item => item.type === 'MENU' && item.visible !== false)
    .map(item => {
      const menu = { ...item }
      if (item.children && item.children.length > 0) {
        menu.children = filterMenuItems(item.children)
      }
      return menu
    })
    .sort((a, b) => (a.sort || 0) - (b.sort || 0))
}

// 检查是否有可见的子菜单
function hasVisibleChildren(menu) {
  if (!menu.children || menu.children.length === 0) {
    return false
  }
  return menu.children.some(child => 
    child.type === 'MENU' && child.visible !== false && child.path
  )
}

// 获取可见的子菜单
function getVisibleChildren(menu) {
  if (!menu.children) return []
  return menu.children
    .filter(child => child.type === 'MENU' && child.visible !== false && child.path)
    .sort((a, b) => (a.sort || 0) - (b.sort || 0))
}

// 处理菜单选择
function handleSelect(path) {
  if (path && path.startsWith('/')) {
    router.push(path)
  }
}
</script>

<style scoped>
.dynamic-menu {
  height: 100%;
  border-right: none;
}

.el-menu-item.is-active {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}
</style>