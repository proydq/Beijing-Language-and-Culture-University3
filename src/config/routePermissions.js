/**
 * 路由权限配置
 * 定义每个路由需要的权限
 */

export const routePermissions = {
  // 用户管理 - 需要用户管理相关权限
  '/user-management': {
    permissions: ['USER_MANAGE', 'USER_VIEW', 'USER_ADD', 'USER_EDIT', 'USER_DELETE'],
    requireAll: false  // 有任意一个权限即可访问
  },
  
  // 房屋信息管理 - 管理员和审批员可以访问
  '/house-management': {
    permissions: [
      'ROOM_ADD', 
      'ROOM_EDIT', 
      'ROOM_DELETE',
      // 审批员特殊权限 - 如果有审批权限也可以查看房间信息
      'BOOKING_APPROVAL_MANAGE',
      'BOOKING_APPROVAL_APPROVE'
    ],
    requireAll: false,  // 有任意一个权限即可
    // 排除只有查看权限但没有审批权限的用户
    customCheck: (userPermissions) => {
      // 如果有增删改权限，直接允许
      const hasEditPermissions = userPermissions.some(p => 
        ['ROOM_ADD', 'ROOM_EDIT', 'ROOM_DELETE'].includes(p)
      )
      if (hasEditPermissions) return true
      
      // 如果有审批权限，也允许访问（只读）
      const hasApprovalPermissions = userPermissions.some(p =>
        p.includes('BOOKING_APPROVAL_') || p.includes('BOOKING_APPROVE')
      )
      if (hasApprovalPermissions) return true
      
      // 其他情况不允许
      return false
    }
  },
  
  // 房屋借用管理 - 普通用户和审批员可以访问
  '/room-booking': {
    permissions: [
      'BOOKING_VIEW', 
      'BOOKING_CREATE', 
      'ROOM_BOOK', 
      'BOOKING_MANAGE',
      'BOOKING_APPROVE',
      'BOOKING_APPROVAL_MANAGE',
      'BOOKING_APPROVAL_APPROVE',
      'BOOKING_APPROVAL_VIEW'
    ],
    requireAll: false
  },
  
  // 角色权限管理 - 需要管理员权限
  '/role-management': {
    permissions: ['ROLE_MANAGE', 'PERMISSION_MANAGE', 'SYSTEM_MANAGE'],
    requireAll: false
  },
  
  // 组织管理
  '/organization-management': {
    permissions: ['ORGANIZATION_MANAGE', 'ORGANIZATION_VIEW'],
    requireAll: false
  },
  
  // 职位管理
  '/position-management': {
    permissions: ['POSITION_MANAGE', 'POSITION_VIEW'],
    requireAll: false
  },
  
  // 职称管理
  '/level-management': {
    permissions: ['TITLE_MANAGE', 'TITLE_VIEW'],
    requireAll: false
  },
  
  // 管理员管理
  '/admin-management': {
    permissions: ['USER_MANAGE', 'SYSTEM_MANAGE'],
    requireAll: false
  },
  
  // 添加管理员
  '/add-admin': {
    permissions: ['USER_ADD', 'USER_MANAGE', 'SYSTEM_MANAGE'],
    requireAll: false
  }
}

/**
 * 检查用户是否有访问路由的权限
 * @param {string} path - 路由路径
 * @param {array} userPermissions - 用户拥有的权限列表
 * @returns {boolean} - 是否有权限访问
 */
export function checkRoutePermission(path, userPermissions) {
  const routeConfig = routePermissions[path]
  
  if (!routeConfig) {
    // 没有配置权限要求，默认允许访问
    return true
  }
  
  const { permissions, requireAll, excludeIfOnly, customCheck } = routeConfig
  
  // 如果有自定义检查函数，优先使用
  if (customCheck && typeof customCheck === 'function') {
    return customCheck(userPermissions)
  }
  
  // 如果配置了排除规则，检查用户是否只有被排除的权限
  if (excludeIfOnly && excludeIfOnly.length > 0) {
    // 获取用户在该模块的所有权限
    const modulePrefix = permissions[0].split('_')[0]  // 如 ROOM, USER 等
    const userModulePermissions = userPermissions.filter(p => p.startsWith(modulePrefix + '_'))
    
    // 如果用户只有被排除的权限，则无权访问
    const hasOnlyExcludedPermissions = userModulePermissions.length > 0 && 
      userModulePermissions.every(p => excludeIfOnly.includes(p))
    
    if (hasOnlyExcludedPermissions) {
      return false
    }
  }
  
  // 检查是否有所需权限
  if (requireAll) {
    // 需要所有权限
    return permissions.every(p => userPermissions.includes(p))
  } else {
    // 只需要任意一个权限
    return permissions.some(p => userPermissions.includes(p))
  }
}