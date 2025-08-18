import request from '@/utils/request'
import { API_PATHS } from './apiConfig'

/**
 * 分页查询房间列表
 * @param {Object} condition 查询条件
 * @returns {Promise}
 */
export const searchRooms = (condition) => {
  return request.post(`${API_PATHS.ROOM}/search`, condition)
}

/**
 * 根据ID查询房间详情
 * @param {String} id 房间ID
 * @returns {Promise}
 */
export const getRoomById = (id) => {
  return request.get(`${API_PATHS.ROOM}/${id}`)
}

/**
 * 新增房间
 * @param {Object} roomData 房间数据
 * @returns {Promise}
 */
export const addRoom = (roomData) => {
  return request.post(API_PATHS.ROOM, roomData)
}

/**
 * 编辑房间
 * @param {String} id 房间ID
 * @param {Object} roomData 房间数据
 * @returns {Promise}
 */
export const updateRoom = (id, roomData) => {
  return request.put(`${API_PATHS.ROOM}/${id}`, roomData)
}

/**
 * 删除房间
 * @param {String} id 房间ID
 * @returns {Promise}
 */
export const deleteRoom = (id) => {
  return request.delete(`${API_PATHS.ROOM}/${id}`)
}

/**
 * 批量删除房间
 * @param {Array} ids 房间ID数组
 * @returns {Promise}
 */
export const batchDeleteRooms = (ids) => {
  return request.delete(`${API_PATHS.ROOM}/batch`, { data: ids })
}

/**
 * 获取房屋类型列表
 * @returns {Promise}
 */
export const getRoomTypes = () => {
  return request.get(`${API_PATHS.ROOM}/types`)
}

/**
 * 校验房间唯一性
 * @param {String} roomName 房屋名称
 * @param {String} roomNo 房间号码
 * @param {String} excludeId 排除的房间ID（编辑时使用）
 * @returns {Promise}
 */
export const checkRoomUniqueness = (roomName, roomNo, excludeId = null) => {
  const params = { roomName, roomNo }
  if (excludeId) {
    params.excludeId = excludeId
  }
  return request.get(`${API_PATHS.ROOM}/check-uniqueness`, { params })
}

/**
 * 导入房间数据
 * @param {FormData} formData 包含文件的表单数据
 * @returns {Promise}
 */
export const importRooms = (formData) => {
  return request.post(`${API_PATHS.ROOM}/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 导出房间数据
 * @param {Object} condition 查询条件
 * @returns {Promise}
 */
export const exportRooms = (condition) => {
  return request.post(`${API_PATHS.ROOM}/export`, condition, {
    responseType: 'blob'
  })
}

// 房间类型常量 - 目前仅支持四种类型
export const ROOM_TYPES = {
  CLASSROOM: 'CLASSROOM',
  MEETING_ROOM: 'MEETING_ROOM',
  LABORATORY: 'LABORATORY',
  OFFICE: 'OFFICE'
}

// 房间类型标签
export const ROOM_TYPE_LABELS = {
  [ROOM_TYPES.CLASSROOM]: '教室',
  [ROOM_TYPES.MEETING_ROOM]: '会议室',
  [ROOM_TYPES.LABORATORY]: '实验室',
  [ROOM_TYPES.OFFICE]: '办公室'
}