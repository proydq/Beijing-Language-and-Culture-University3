import { createRouter, createWebHistory } from 'vue-router'
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

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
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
    {
      path: '/user-management',
      name: 'UserManagement',
      component: UserManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/organization-management',
      name: 'OrganizationManagement',
      component: OrganizationManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/position-management',
      name: 'PositionManagement',
      component: PositionManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/level-management',
      name: 'LevelManagement',
      component: LevelManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/house-management',
      name: 'HouseManagement',
      component: HouseManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/personal-center',
      name: 'PersonalCenter',
      component: PersonalCenter,
      meta: { requiresAuth: true }
    },
    {
      path: '/role-management',
      name: 'RoleManagement',
      component: RoleManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/admin-management',
      name: 'AdminManagement',
      component: AdminManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/add-admin',  // 新增这个路由配置
      name: 'AddAdmin',
      component: AddAdmin,
      meta: { requiresAuth: true }
    },
    {
      path: '/room-booking',
      name: 'RoomBooking',
      component: RoomBooking,
      meta: { requiresAuth: true }
    },
    {
      path: '/api-test',
      name: 'ApiTest',
      component: ApiTest
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
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('userToken')
  
  // 如果路由需要认证
  if (to.meta.requiresAuth) {
    if (token) {
      // 已登录，允许访问
      next()
    } else {
      // 未登录，跳转到登录页
      next('/login')
    }
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

export default router