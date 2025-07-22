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

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard
    },
    {
      path: '/user-management',
      name: 'UserManagement',
      component: UserManagement
    },
    {
      path: '/organization-management',
      name: 'OrganizationManagement',
      component: OrganizationManagement
    },
    {
      path: '/position-management',
      name: 'PositionManagement',
      component: PositionManagement
    },
    {
      path: '/level-management',
      name: 'LevelManagement',
      component: LevelManagement
    },
    {
      path: '/house-management',
      name: 'HouseManagement',
      component: HouseManagement
    },
    {
      path: '/personal-center',
      name: 'PersonalCenter',
      component: PersonalCenter
    },
    {
      path: '/role-management',
      name: 'RoleManagement',
      component: RoleManagement
    },
    {
      path: '/admin-management',
      name: 'AdminManagement',
      component: AdminManagement
    },
    {
      path: '/add-admin',  // 新增这个路由配置
      name: 'AddAdmin',
      component: AddAdmin
    },
    {
      path: '/room-booking',
      name: 'RoomBooking',
      component: RoomBooking
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue')
    }
  ]
})

export default router