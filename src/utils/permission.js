import { useUserStore } from '@/stores/user'

/**
 * 权限判断工具函数
 */

/**
 * 检查是否有指定权限
 * @param {string} permission - 权限代码
 * @returns {boolean}
 */
export function hasPermission(permission) {
  const userStore = useUserStore()
  return userStore.hasPermission(permission)
}

/**
 * 检查是否有任意一个权限
 * @param {...string} permissions - 权限代码列表
 * @returns {boolean}
 */
export function hasAnyPermission(...permissions) {
  const userStore = useUserStore()
  return userStore.hasAnyPermission(...permissions)
}

/**
 * 检查是否有所有权限
 * @param {...string} permissions - 权限代码列表
 * @returns {boolean}
 */
export function hasAllPermissions(...permissions) {
  const userStore = useUserStore()
  return userStore.hasAllPermissions(...permissions)
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
 * 权限常量定义（与后端保持一致）
 */
export const PERMISSIONS = {
  // 用户管理权限
  USER: {
    MANAGE: 'USER_MANAGE',
    SEARCH: 'USER_SEARCH',
    VIEW: 'USER_VIEW',
    ADD: 'USER_ADD',
    EDIT: 'USER_EDIT',
    DELETE: 'USER_DELETE',
    RESET_PWD: 'USER_RESET_PWD',
    ASSIGN_ROLE: 'USER_ASSIGN_ROLE',
    EXPORT: 'USER_EXPORT',
    IMPORT: 'USER_IMPORT'
  },
  
  // 角色管理权限
  ROLE: {
    MANAGE: 'ROLE_MANAGE',
    SEARCH: 'ROLE_SEARCH',
    ADD: 'ROLE_ADD',
    EDIT: 'ROLE_EDIT',
    DELETE: 'ROLE_DELETE',
    ASSIGN_PERM: 'ROLE_ASSIGN_PERM'
  },
  
  // 权限管理权限
  PERMISSION: {
    MANAGE: 'PERMISSION_MANAGE',
    SEARCH: 'PERMISSION_SEARCH',
    ADD: 'PERMISSION_ADD',
    EDIT: 'PERMISSION_EDIT',
    DELETE: 'PERMISSION_DELETE'
  },
  
  // 教室管理权限
  ROOM: {
    MANAGE: 'ROOM_MANAGE',
    SEARCH: 'ROOM_SEARCH',
    VIEW: 'ROOM_VIEW',
    ADD: 'ROOM_ADD',
    EDIT: 'ROOM_EDIT',
    DELETE: 'ROOM_DELETE',
    BOOK: 'ROOM_BOOK',
    CANCEL_BOOK: 'ROOM_CANCEL_BOOK',
    APPROVE: 'ROOM_APPROVE'
  },
  
  // 预订管理权限
  BOOKING: {
    MANAGE: 'BOOKING_MANAGE',
    SEARCH: 'BOOKING_SEARCH',
    VIEW: 'BOOKING_VIEW',
    CREATE: 'BOOKING_CREATE',
    EDIT: 'BOOKING_EDIT',
    CANCEL: 'BOOKING_CANCEL',
    APPROVE: 'BOOKING_APPROVE',
    REJECT: 'BOOKING_REJECT'
  },
  
  // 系统设置权限
  SYSTEM: {
    MANAGE: 'SYSTEM_MANAGE',
    CONFIG: 'SYSTEM_CONFIG',
    LOG_VIEW: 'SYSTEM_LOG_VIEW',
    BACKUP: 'SYSTEM_BACKUP'
  },
  
  // 报表统计权限
  REPORT: {
    VIEW: 'REPORT_VIEW',
    EXPORT: 'REPORT_EXPORT'
  },
  
  // 文件管理权限
  FILE: {
    MANAGE: 'FILE_MANAGE',
    UPLOAD: 'FILE_UPLOAD',
    VIEW: 'FILE_VIEW'
  },
  
  // 组织机构权限
  ORG: {
    MANAGE: 'ORG_MANAGE',
    VIEW: 'ORG_VIEW',
    ADD: 'ORG_ADD',
    EDIT: 'ORG_EDIT',
    DELETE: 'ORG_DELETE'
  },
  
  // 职位管理权限
  POSITION: {
    MANAGE: 'POSITION_MANAGE',
    VIEW: 'POSITION_VIEW',
    ADD: 'POSITION_ADD',
    EDIT: 'POSITION_EDIT',
    DELETE: 'POSITION_DELETE'
  },
  
  // 职称管理权限
  TITLE: {
    MANAGE: 'TITLE_MANAGE',
    VIEW: 'TITLE_VIEW',
    ADD: 'TITLE_ADD',
    EDIT: 'TITLE_EDIT',
    DELETE: 'TITLE_DELETE'
  },
  
  // 黑名单管理权限
  BLACKLIST: {
    MANAGE: 'BLACKLIST_MANAGE',
    VIEW: 'BLACKLIST_VIEW',
    ADD: 'BLACKLIST_ADD',
    DELETE: 'BLACKLIST_DELETE'
  },
  
  // 违规管理权限
  VIOLATION: {
    MANAGE: 'VIOLATION_MANAGE'
  },
  
  // 区域管理权限
  AREA: {
    MANAGE: 'AREA_MANAGE',
    VIEW: 'AREA_VIEW',
    ADD: 'AREA_ADD',
    EDIT: 'AREA_EDIT',
    DELETE: 'AREA_DELETE'
  },
  
  // 预订人员权限管理
  BOOKING_PERSONNEL: {
    MANAGE: 'BOOKING_PERSONNEL_MANAGE',
    VIEW: 'BOOKING_PERSONNEL_VIEW',
    ADD: 'BOOKING_PERSONNEL_ADD',
    EDIT: 'BOOKING_PERSONNEL_EDIT',
    DELETE: 'BOOKING_PERSONNEL_DELETE'
  },
  
  // 连续预订设置权限
  CONTINUOUS_BOOKING: {
    MANAGE: 'CONTINUOUS_BOOKING_MANAGE',
    VIEW: 'CONTINUOUS_BOOKING_VIEW',
    EDIT: 'CONTINUOUS_BOOKING_EDIT',
    DELETE: 'CONTINUOUS_BOOKING_DELETE'
  },
  
  // 用户预订限制权限
  USER_BOOKING_LIMIT: {
    MANAGE: 'USER_BOOKING_LIMIT_MANAGE',
    VIEW: 'USER_BOOKING_LIMIT_VIEW',
    EDIT: 'USER_BOOKING_LIMIT_EDIT',
    DELETE: 'USER_BOOKING_LIMIT_DELETE'
  },
  
  // 预订时间规则权限
  BOOKING_TIME_RULE: {
    MANAGE: 'BOOKING_TIME_RULE_MANAGE',
    VIEW: 'BOOKING_TIME_RULE_VIEW',
    ADD: 'BOOKING_TIME_RULE_ADD',
    EDIT: 'BOOKING_TIME_RULE_EDIT',
    DELETE: 'BOOKING_TIME_RULE_DELETE'
  },
  
  // 回收站管理权限
  RECYCLE_BIN: {
    MANAGE: 'RECYCLE_BIN_MANAGE',
    VIEW: 'RECYCLE_BIN_VIEW',
    RESTORE: 'RECYCLE_BIN_RESTORE',
    DELETE: 'RECYCLE_BIN_DELETE'
  },
  
  // 方案管理权限
  SCHEME: {
    MANAGE: 'SCHEME_MANAGE',
    VIEW: 'SCHEME_VIEW',
    ADD: 'SCHEME_ADD',
    EDIT: 'SCHEME_EDIT',
    DELETE: 'SCHEME_DELETE'
  },
  
  // 教室日程权限
  ROOM_SCHEDULE: {
    MANAGE: 'ROOM_SCHEDULE_MANAGE',
    VIEW: 'ROOM_SCHEDULE_VIEW'
  },
  
  // 预订审批权限
  BOOKING_APPROVAL: {
    MANAGE: 'BOOKING_APPROVAL_MANAGE',
    VIEW: 'BOOKING_APPROVAL_VIEW',
    APPROVE: 'BOOKING_APPROVAL_APPROVE'
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