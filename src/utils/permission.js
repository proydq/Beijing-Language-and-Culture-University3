import { useUserStore } from '@/stores/user'

/**
 * 权限判断工具函数
 * 使用新版权限码格式：domain:resource:action
 */


/**
 * 检查是否有指定权限
 * @param {string} permission - 权限代码
 * @returns {boolean}
 */
export function hasPermission(permission) {
  const userStore = useUserStore()
  
  // 直接检查权限（新权限码）
  if (userStore.hasPermission(permission)) {
    return true
  }
  
  // 检查是否有对应的manage权限（权限层级推理）
  if (permission.includes(':')) {
    const parts = permission.split(':')
    if (parts.length === 3 && parts[2] !== 'manage') {
      // 检查资源级别的manage权限
      const resourceManagePermission = `${parts[0]}:${parts[1]}:manage`
      if (userStore.hasPermission(resourceManagePermission)) {
        return true
      }
      
      // 检查域级别的manage权限
      const domainManagePermission = `${parts[0]}:*:manage`
      if (userStore.hasPermission(domainManagePermission)) {
        return true
      }
    }
  }
  
  return false
}

/**
 * 检查是否有任意一个权限
 * @param {...string} permissions - 权限代码列表
 * @returns {boolean}
 */
export function hasAnyPermission(...permissions) {
  return permissions.some(permission => hasPermission(permission))
}

/**
 * 检查是否有所有权限
 * @param {...string} permissions - 权限代码列表
 * @returns {boolean}
 */
export function hasAllPermissions(...permissions) {
  return permissions.every(permission => hasPermission(permission))
}

/**
 * 检查是否有指定角色
 * @param {string} role - 角色代码
 * @returns {boolean}
 */
export function hasRole(role) {
  const userStore = useUserStore()
  return userStore.hasRole(role)
}

/**
 * 新版权限码常量定义
 */
export const PERMISSIONS = {
  // 系统管理域
  SYSTEM: {
    USER: {
      MANAGE: 'system:user:manage',
      VIEW: 'system:user:view',
      CREATE: 'system:user:create',
      UPDATE: 'system:user:update',
      DELETE: 'system:user:delete',
      RESET_PASSWORD: 'system:user:resetPassword',
      ASSIGN_ROLES: 'system:user:assignRoles',
      EXPORT: 'system:user:export',
      IMPORT: 'system:user:import'
    },
    ROLE: {
      MANAGE: 'system:role:manage',
      VIEW: 'system:role:view',
      CREATE: 'system:role:create',
      UPDATE: 'system:role:update',
      DELETE: 'system:role:delete',
      ASSIGN: 'system:role:assign'
    },
    PERMISSION: {
      MANAGE: 'system:permission:manage',
      VIEW: 'system:permission:view',
      REFRESH: 'system:permission:refresh'
    },
    ORGANIZATION: {
      MANAGE: 'system:org:manage',
      VIEW: 'system:org:view',
      CREATE: 'system:org:create',
      UPDATE: 'system:org:update',
      DELETE: 'system:org:delete'
    },
    POSITION: {
      MANAGE: 'system:position:manage',
      VIEW: 'system:position:view',
      CREATE: 'system:position:create',
      UPDATE: 'system:position:update',
      DELETE: 'system:position:delete'
    },
    TITLE: {
      MANAGE: 'system:title:manage',
      VIEW: 'system:title:view',
      CREATE: 'system:title:create',
      UPDATE: 'system:title:update',
      DELETE: 'system:title:delete'
    },
    SETTING: {
      MANAGE: 'system:config:manage',
      CONFIG: 'system:config:update'
    },
    FILE: {
      MANAGE: 'system:file:manage',
      UPLOAD: 'system:file:upload',
      VIEW: 'system:file:view',
      DELETE: 'system:file:delete'
    },
    RECYCLE: {
      MANAGE: 'system:recycle:manage',
      VIEW: 'system:recycle:view',
      RESTORE: 'system:recycle:restore',
      DELETE: 'system:recycle:delete'
    }
  },
  
  // 建筑管理域
  BUILDING: {
    AREA: {
      MANAGE: 'building:area:manage',
      VIEW: 'building:area:view',
      CREATE: 'building:area:create',
      UPDATE: 'building:area:update',
      DELETE: 'building:area:delete'
    },
    ROOM: {
      MANAGE: 'building:room:manage',
      VIEW: 'building:room:view',
      CREATE: 'building:room:create',
      UPDATE: 'building:room:update',
      DELETE: 'building:room:delete',
      IMPORT: 'building:room:import',
      EXPORT: 'building:room:export',
      BATCH_DELETE: 'building:room:batchDelete',
      BATCH_UPDATE: 'building:room:batchUpdate'
    },
    SCHEDULE: {
      MANAGE: 'building:schedule:manage',
      VIEW: 'building:schedule:view',
      CREATE: 'building:schedule:create',
      UPDATE: 'building:schedule:update',
      DELETE: 'building:schedule:delete'
    }
  },
  
  // 预订管理域
  BOOKING: {
    RESERVATION: {
      MANAGE: 'booking:reservation:manage',
      VIEW: 'booking:reservation:view',
      CREATE: 'booking:reservation:create',
      UPDATE: 'booking:reservation:update',
      DELETE: 'booking:reservation:delete',
      CANCEL: 'booking:reservation:cancel',
      AUDIT: 'booking:reservation:audit',
      EXPORT: 'booking:reservation:export',
      MY_VIEW: 'booking:reservation:myView',
      MY_CREATE: 'booking:reservation:myCreate',
      MY_CANCEL: 'booking:reservation:myCancel'
    },
    APPROVAL: {
      MANAGE: 'booking:approval:manage',
      VIEW: 'booking:approval:view',
      PROCESS: 'booking:approval:process',
      BATCH: 'booking:approval:batch'
    },
    SETTING: {
      MANAGE: 'booking:setting:manage',
      TIME_RULE: 'booking:setting:timeRule',
      PERSONNEL: 'booking:setting:personnel',
      CONTINUOUS: 'booking:setting:continuous',
      LIMIT: 'booking:setting:limit'
    },
    VIOLATION: {
      MANAGE: 'booking:violation:manage',
      VIEW: 'booking:violation:view',
      CREATE: 'booking:violation:create',
      UPDATE: 'booking:violation:update',
      DELETE: 'booking:violation:delete'
    },
    BLACKLIST: {
      MANAGE: 'booking:blacklist:manage',
      VIEW: 'booking:blacklist:view',
      CREATE: 'booking:blacklist:create',
      DELETE: 'booking:blacklist:delete'
    },
    REMOTE: {
      OPEN_DOOR: 'booking:remote:openDoor',
      VIEW: 'booking:remote:view',
      EXPORT: 'booking:remote:export'
    },
    SCHEME: {
      MANAGE: 'booking:scheme:manage',
      VIEW: 'booking:scheme:view',
      CREATE: 'booking:scheme:create',
      UPDATE: 'booking:scheme:update',
      DELETE: 'booking:scheme:delete',
      APPLY: 'booking:scheme:apply'
    },
    REPORT: {
      VIEW: 'booking:report:view',
      EXPORT: 'booking:report:export'
    }
  }
}

// 导出默认权限检查函数
export default {
  install(app) {
    // 全局方法
    app.config.globalProperties.$hasPermission = hasPermission
    app.config.globalProperties.$hasAnyPermission = hasAnyPermission
    app.config.globalProperties.$hasAllPermissions = hasAllPermissions
    app.config.globalProperties.$hasRole = hasRole
    app.config.globalProperties.$PERMISSIONS = PERMISSIONS
  }
}