import { PERMISSIONS } from '@/utils/permission'

/**
 * 菜单配置
 * 每个菜单项包含：
 * - title: 菜单标题
 * - path: 路由路径
 * - icon: 图标名称
 * - permissions: 需要的权限（数组，满足任意一个即可）
 * - children: 子菜单
 */
export const menuConfig = [
  {
    title: '首页',
    path: '/dashboard',
    icon: 'home-filled',
    permissions: [] // 所有登录用户都可访问
  },
  {
    title: '用户管理',
    icon: 'user',
    permissions: [PERMISSIONS.USER.VIEW, PERMISSIONS.USER.MANAGE],
    children: [
      {
        title: '用户列表',
        path: '/user-management',
        permissions: [PERMISSIONS.USER.VIEW, PERMISSIONS.USER.MANAGE]
      },
      {
        title: '组织机构',
        path: '/organization-management',
        permissions: [PERMISSIONS.ORG.VIEW, PERMISSIONS.ORG.MANAGE]
      },
      {
        title: '职位管理',
        path: '/position-management',
        permissions: [PERMISSIONS.POSITION.VIEW, PERMISSIONS.POSITION.MANAGE]
      },
      {
        title: '职称管理',
        path: '/level-management',
        permissions: [PERMISSIONS.TITLE.VIEW, PERMISSIONS.TITLE.MANAGE]
      }
    ]
  },
  {
    title: '教室管理',
    icon: 'office-building',
    permissions: [PERMISSIONS.ROOM.VIEW, PERMISSIONS.ROOM.MANAGE],
    children: [
      {
        title: '教室信息',
        path: '/house-management',
        permissions: [PERMISSIONS.ROOM.VIEW, PERMISSIONS.ROOM.MANAGE]
      },
      {
        title: '教室预订',
        path: '/room-booking',
        permissions: [PERMISSIONS.ROOM.VIEW, PERMISSIONS.ROOM.BOOK, PERMISSIONS.BOOKING.VIEW]
      }
    ]
  },
  {
    title: '系统管理',
    icon: 'setting',
    permissions: [PERMISSIONS.SYSTEM.MANAGE, PERMISSIONS.ROLE.MANAGE],
    children: [
      {
        title: '角色管理',
        path: '/role-management',
        permissions: [PERMISSIONS.ROLE.MANAGE, PERMISSIONS.ROLE.SEARCH]
      },
      {
        title: '管理员管理',
        path: '/admin-management',
        permissions: [PERMISSIONS.USER.MANAGE, PERMISSIONS.SYSTEM.MANAGE]
      }
    ]
  },
  {
    title: '个人中心',
    path: '/personal-center',
    icon: 'user',
    permissions: [] // 所有登录用户都可访问
  }
]

/**
 * 根据用户权限过滤菜单
 * @param {Array} menus - 菜单配置
 * @param {Function} hasPermission - 权限检查函数
 * @returns {Array} 过滤后的菜单
 */
export function filterMenuByPermission(menus, hasPermission) {
  return menus.filter(menu => {
    // 如果没有权限要求，则显示
    if (!menu.permissions || menu.permissions.length === 0) {
      // 如果有子菜单，递归过滤
      if (menu.children && menu.children.length > 0) {
        menu.children = filterMenuByPermission(menu.children, hasPermission)
        // 如果子菜单都被过滤掉了，则父菜单也不显示
        return menu.children.length > 0
      }
      return true
    }

    // 检查是否有权限
    const hasMenuPermission = menu.permissions.some(permission => hasPermission(permission))
    
    if (hasMenuPermission) {
      // 如果有子菜单，递归过滤
      if (menu.children && menu.children.length > 0) {
        menu.children = filterMenuByPermission(menu.children, hasPermission)
      }
      return true
    }
    
    return false
  })
}