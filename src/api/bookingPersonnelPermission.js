import { request, API_PATHS } from './apiConfig'

/**
 * 预约人员权限管理相关API
 */

// 获取预约人员权限列表
export const getPermissionList = (params) => {
  return request.get(`${API_PATHS.BOOKING_PERSONNEL_PERMISSION}/list`, { params })
}

// 新增预约人员权限配置
export const createPermission = (data) => {
  return request.post(API_PATHS.BOOKING_PERSONNEL_PERMISSION, data)
}

// 编辑预约人员权限配置
export const updatePermission = (id, data) => {
  return request.put(`${API_PATHS.BOOKING_PERSONNEL_PERMISSION}/${id}`, data)
}

// 删除预约人员权限配置
export const deletePermission = (id) => {
  return request.delete(`${API_PATHS.BOOKING_PERSONNEL_PERMISSION}/${id}`)
}

// 导出预约人员权限列表
export const exportPermissions = (params) => {
  return request.get(`${API_PATHS.BOOKING_PERSONNEL_PERMISSION}/export`, {
    params,
    responseType: 'blob' // 用于文件下载
  })
}

// 获取可选择的用户列表
export const getAvailableUsers = (params) => {
  return request.get(`${API_PATHS.USER}/available`, { params })
}

// 获取可选择的房间列表
export const getAvailableRooms = (params) => {
  return request.get(`${API_PATHS.ROOM}/available`, { params })
}

// 检查用户是否有指定房间的预约权限
export const checkBookingPermission = (userId, roomId) => {
  return request.get(`${API_PATHS.BOOKING_PERSONNEL_PERMISSION}/check`, {
    params: { userId, roomId }
  })
}

// 根据用户ID获取可预约的房间列表
export const getBookableRoomsByUserId = (userId) => {
  return request.get(`${API_PATHS.BOOKING_PERSONNEL_PERMISSION}/bookable-rooms/${userId}`)
}
