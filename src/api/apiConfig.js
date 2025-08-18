/**
 * API 路径配置
 * 根据后端实际路径映射
 */

// 后端控制器路径映射
export const API_PATHS = {
  // 所有路径统一不带 /api 前缀（会在 request.js 中自动添加）
  ROOM: '/room',
  ROOM_BOOKING: '/room-booking',
  ROOM_SCHEDULE: '/room-schedule',
  USER: '/user',
  ORGANIZATION: '/organization',
  POSITION: '/position',
  TITLE: '/title',
  UPLOAD: '/upload',
  AUTHENTICATION: '/authentication',
  BLACKLIST: '/blacklist',
  VIOLATION_SETTINGS: '/violation-settings',
  AREA: '/area',
  BOOKING_PERSONNEL_PERMISSION: '/booking-personnel-permission',
  CONTINUOUS_BOOKING_SETTINGS: '/continuous-booking-settings',
  USER_BOOKING_LIMITS: '/user-booking-limits',
  BOOKING_TIME_RULES: '/booking-time-rules',
  BOOKING_VALIDATION: '/booking-validation',
  RECYCLE_BIN: '/recycle-bin',
  ROLE_MANAGEMENT: '/role-management',
  SCHEME_MANAGEMENT: '/scheme-management',
  SYSTEM_USER: '/system/user',
  ADMIN_MANAGEMENT: '/admin-management'
}

// 统一使用 request 实例
export { default as request } from '@/utils/request'