import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { generateDynamicRoutes } from './dynamicRoutes'
import Dashboard from '../views/Dashboard.vue'
import UserManagement from '../views/UserManagement.vue'
import OrganizationManagement from '../views/OrganizationManagement.vue'
import PositionManagement from '../views/PositionManagement.vue'
import LevelManagement from '../views/LevelManagement.vue'
import HouseManagement from '../views/HouseManagement.vue'
import PersonalCenter from '../views/PersonalCenter.vue'
import RoleManagement from '../views/RoleManagement.vue'
import AdminManagement from '../views/AdminManagement.vue'
import AddAdmin from '../views/AddAdmin.vue'  // 新增这一行
import RoomBooking from '../views/RoomBooking.vue'
import Login from '../views/Login.vue'
import ApiTest from '../views/ApiTest.vue'
import PermissionDemo from '../views/PermissionDemo.vue'

// 基础静态路由
const staticRoutes = [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard,
      meta: { requiresAuth: true }
    },
    // 临时添加基本路由确保功能正常
    {
      path: '/user-management',
      name: 'UserManagement',
      component: UserManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/house-management',
      name: 'HouseManagement', 
      component: HouseManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/room-booking',
      name: 'RoomBooking',
      component: RoomBooking,
      meta: { requiresAuth: true }
    },
    {
      path: '/role-management',
      name: 'RoleManagement',
      component: RoleManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/api-test',
      name: 'ApiTest',
      component: ApiTest
    },
    {
      path: '/permission-demo',
      name: 'PermissionDemo',
      component: PermissionDemo,
      meta: { requiresAuth: true }
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/login'
    }
  ]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: staticRoutes
})

// 动态添加路由的标记
let dynamicRoutesAdded = false

/**
 * 添加动态路由
 */
function addDynamicRoutes() {
  if (dynamicRoutesAdded) {
    return
  }
  
  try {
    const dynamicRoutes = generateDynamicRoutes()
    console.log('生成的动态路由数量:', dynamicRoutes.length)
    
    dynamicRoutes.forEach(route => {
      router.addRoute(route)
    })
    
    dynamicRoutesAdded = true
    console.log('动态路由添加成功')
  } catch (error) {
    console.error('添加动态路由失败:', error)
  }
}

/**
 * 清除动态路由（退出登录时调用）
 */
function clearDynamicRoutes() {
  dynamicRoutesAdded = false
  // 重新创建路由器来清除动态路由
  const newRouter = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: staticRoutes
  })
  
  // 替换路由器的内部路由表
  router.options.routes = staticRoutes
  console.log('动态路由已清除')
}

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('userToken')
  const userStore = useUserStore()

  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    if (!token) {
      // 未登录，跳转到登录页
      next('/login')
      return
    }

    // 如果有权限树但还没有添加动态路由，先添加
    if (userStore.permissionTree.length > 0 && !dynamicRoutesAdded) {
      console.log('在路由守卫中添加动态路由')
      addDynamicRoutes()
      // 重新导航到目标路由
      console.log('重新导航到:', to.path)
      next({ ...to, replace: true })
      return
    }

    // 检查权限
    if (to.meta.permissions && to.meta.permissions.length > 0) {
      // 检查是否有任意一个权限
      const hasPermission = userStore.hasAnyPermission(...to.meta.permissions)
      
      if (!hasPermission) {
        // 没有权限，提示并跳转到首页
        ElMessage.error('您没有访问该页面的权限')
        next('/dashboard')
        return
      }
    }

    // 已登录且有权限，允许访问
    next()
  } else {
    // 不需要认证的路由
    if (to.path === '/login' && token) {
      // 已登录用户访问登录页，跳转到首页
      next('/dashboard')
    } else {
      next()
    }
  }
})

export { addDynamicRoutes, clearDynamicRoutes }
export default router
