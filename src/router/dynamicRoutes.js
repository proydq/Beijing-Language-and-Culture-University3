import { useUserStore } from '@/stores/user'

// 组件映射表
const componentMap = {
  'Dashboard': () => import('../views/Dashboard.vue'),
  'UserManagement': () => import('../views/UserManagement.vue'),
  'RoleManagement': () => import('../views/RoleManagement.vue'),
  'HouseManagement': () => import('../views/HouseManagement.vue'),
  'RoomBooking': () => import('../views/RoomBooking.vue'),
  'OrganizationManagement': () => import('../views/OrganizationManagement.vue'),
  'PositionManagement': () => import('../views/PositionManagement.vue'),
  'LevelManagement': () => import('../views/LevelManagement.vue'),
  'PersonalCenter': () => import('../views/PersonalCenter.vue'),
  'AdminManagement': () => import('../views/AdminManagement.vue'),
  'AddAdmin': () => import('../views/AddAdmin.vue'),
  'ApiTest': () => import('../views/ApiTest.vue'),
  'PermissionDemo': () => import('../views/PermissionDemo.vue'),
  'AccessControlDashboard': () => import('../views/AccessControlDashboard.vue'),
  'AccessControlRecords': () => import('../views/AccessControlRecords.vue'),
  'ClassroomRecordsManagement': () => import('../views/ClassroomRecordsManagement.vue')
}

/**
 * 根据权限树生成动态路由
 */
export function generateDynamicRoutes() {
  try {
    const userStore = useUserStore()
    const permissionTree = userStore.permissionTree || []
    
    if (permissionTree.length === 0) {
      console.log('权限树为空，返回空路由列表')
      return []
    }
    
    const routes = []
  
  // 遍历权限树生成路由
  function processNode(node, parentPath = '') {
    console.log('处理节点:', node.code, node.type, node.path, node.component)
    
    // 只处理有路径的MENU类型权限
    if (node.type === 'MENU' && node.path && node.component) {
      // 检查组件是否存在
      if (!componentMap[node.component]) {
        console.warn(`组件 ${node.component} 不存在，跳过路由: ${node.path}`)
        console.log('可用组件:', Object.keys(componentMap))
        return
      }
      
      const route = {
        path: node.path,
        name: node.component,
        component: componentMap[node.component],
        meta: {
          title: node.name,
          requiresAuth: true,
          permissions: [node.code],
          icon: node.icon,
          keepAlive: node.keepAlive || false,
          hidden: !node.visible
        }
      }
      
      // 如果有重定向
      if (node.redirect) {
        route.redirect = node.redirect
      }
      
      console.log('生成路由:', route.path, route.name)
      routes.push(route)
    }
    
    // 递归处理子节点
    if (node.children && node.children.length > 0) {
      node.children.forEach(child => processNode(child, node.path || parentPath))
    }
  }
  
  // 处理所有根节点
  permissionTree.forEach(node => processNode(node))
  
  console.log('生成的动态路由:', routes)
    console.log(`生成了 ${routes.length} 个动态路由`)
    return routes
  } catch (error) {
    console.error('生成动态路由出错:', error)
    return []
  }
}

/**
 * 检查路由权限
 */
export function hasRoutePermission(route) {
  const userStore = useUserStore()
  
  if (!route.meta || !route.meta.permissions) {
    return true
  }
  
  return userStore.hasAnyPermission(...route.meta.permissions)
}