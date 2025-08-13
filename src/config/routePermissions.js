/**
 * 路由权限配置
 * 定义每个路由需要的权限
 */

export const routePermissions = {
  // 用户管理 - 需要用户管理相关权限
  '/user-management': {
    permissions: ['system:user:manage', 'system:user:view', 'system:user:create', 'system:user:update', 'system:user:delete'],
    requireAll: false  // 有任意一个权限即可访问
  },
  
  // 房屋信息管理 - 管理员和审批员可以访问
  '/house-management': {
    permissions: [
      'building:room:view',
      'building:room:create', 
      'building:room:update', 
      'building:room:delete',
      'building:room:manage',
      'building:area:view',
      'building:area:create',
      'building:area:update',
      'building:area:delete',
      'building:area:manage',
      // 审批员特殊权限 - 如果有审批权限也可以查看房间信息
      'approval:approval:manage',
      'approval:approval:process'
    ],
    requireAll: false  // 有任意一个权限即可
  },
  
  // 房屋借用管理 - 普通用户和审批员可以访问
  '/room-booking': {
    permissions: [
      'booking:booking:view', 
      'booking:booking:create', 
      'building:room:book', 
      'booking:booking:manage',
      'booking:booking:approve',
      'approval:approval:manage',
      'approval:approval:process',
      'approval:approval:view'
    ],
    requireAll: false
  },
  
  // 角色权限管理 - 需要管理员权限
  '/role-management': {
    permissions: ['system:role:manage', 'system:permission:manage', 'system:system:manage'],
    requireAll: false
  },
  
  // 组织管理
  '/organization-management': {
    permissions: ['system:org:manage', 'system:org:view'],
    requireAll: false
  },
  
  // 职位管理
  '/position-management': {
    permissions: ['system:position:manage', 'system:position:view'],
    requireAll: false
  },
  
  // 职称管理
  '/level-management': {
    permissions: ['system:title:manage', 'system:title:view'],
    requireAll: false
  },
  
  // 管理员管理
  '/admin-management': {
    permissions: ['system:user:manage', 'system:system:manage'],
    requireAll: false
  },
  
  // 添加管理员
  '/add-admin': {
    permissions: ['system:user:create', 'system:user:manage', 'system:system:manage'],
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
    const modulePrefix = permissions[0].includes(':') ? 
      permissions[0].split(':')[0] + ':' + permissions[0].split(':')[1] :  // 新格式：domain:resource
      permissions[0].split('_')[0] + '_'  // 旧格式：MODULE_
    
    const userModulePermissions = userPermissions.filter(p => p.startsWith(modulePrefix))
    
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