import api from './index.js'

/**
 * 预约人员权限管理相关API
 */

// 获取预约人员权限列表
export const getPermissionList = (params) => {
  return api.get('/booking-personnel-permission/list', { params })
}

// 新增预约人员权限配置
export const createPermission = (data) => {
  return api.post('/booking-personnel-permission', data)
}

// 编辑预约人员权限配置
export const updatePermission = (id, data) => {
  return api.put(`/booking-personnel-permission/${id}`, data)
}

// 删除预约人员权限配置
export const deletePermission = (id) => {
  return api.delete(`/booking-personnel-permission/${id}`)
}

// 导出预约人员权限列表
export const exportPermissions = (params) => {
  return api.get('/booking-personnel-permission/export', {
    params,
    responseType: 'blob' // 用于文件下载
  })
}

// 获取可选择的用户列表
export const getAvailableUsers = (params) => {
  return api.get('/api/user/available', { params })
}

// 获取可选择的房间列表
export const getAvailableRooms = (params) => {
  return api.get('/api/room/available', { params })
}

// 检查用户是否有指定房间的预约权限
export const checkBookingPermission = (userId, roomId) => {
  return api.get('/booking-personnel-permission/check', {
    params: { userId, roomId }
  })
}

// 根据用户ID获取可预约的房间列表
export const getBookableRoomsByUserId = (userId) => {
  return api.get(`/booking-personnel-permission/bookable-rooms/${userId}`)
}
