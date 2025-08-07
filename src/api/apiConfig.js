/**
 * API 路径配置
 * 根据后端实际路径映射
 */

// 后端控制器路径映射
export const API_PATHS = {
  // 带 /api 前缀的路径
  ROOM: '/api/room',
  ROOM_BOOKING: '/api/room-booking',
  ROOM_SCHEDULE: '/api/room-schedule',
  USER: '/api/user',
  ORGANIZATION: '/api/organization',
  POSITION: '/api/position',
  TITLE: '/api/title',
  UPLOAD: '/api/upload',
  
  // 不带 /api 前缀的路径
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
  SYSTEM_USER: '/system/user'
}

// 统一使用 request 实例
export { default as request } from '@/utils/request'