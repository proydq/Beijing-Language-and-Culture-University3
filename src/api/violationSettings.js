import request from '@/utils/request'

/**
 * 分页查询违规设置
 * @param {Object} condition 查询条件
 * @returns {Promise}
 */
export const searchViolationSettings = (condition) => {
  return request.post('/violation-settings/search', condition)
}

/**
 * 获取单个教室违规设置详情
 * @param {String} roomId 教室ID
 * @returns {Promise}
 */
export const getViolationSettingByRoomId = (roomId) => {
  return request.get(`/violation-settings/${roomId}`)
}

/**
 * 更新单个教室违规设置
 * @param {String} roomId 教室ID
 * @param {Object} data 违规设置数据
 * @returns {Promise}
 */
export const updateViolationSetting = (roomId, data) => {
  return request.put(`/violation-settings/${roomId}`, data)
}

/**
 * 批量更新教室违规设置
 * @param {Object} data 批量更新数据
 * @returns {Promise}
 */
export const batchUpdateViolationSettings = (data) => {
  return request.put('/violation-settings/batch', data)
}

/**
 * 创建或更新违规设置
 * @param {String} roomId 教室ID
 * @param {Number} startTime 超时分钟数
 * @param {Number} violationCount 违规次数阈值
 * @returns {Promise}
 */
export const createOrUpdateViolationSetting = (roomId, startTime, violationCount) => {
  return request.post('/violation-settings', null, {
    params: { roomId, startTime, violationCount }
  })
}

/**
 * 删除违规设置
 * @param {String} roomId 教室ID
 * @returns {Promise}
 */
export const deleteViolationSetting = (roomId) => {
  return request.delete(`/violation-settings/${roomId}`)
}

/**
 * 批量删除违规设置
 * @param {Array} roomIds 教室ID数组
 * @returns {Promise}
 */
export const batchDeleteViolationSettings = (roomIds) => {
  return request.delete('/violation-settings/batch', { data: roomIds })
}

/**
 * 检查用户是否应该加入黑名单
 * @param {String} userId 用户ID
 * @param {String} roomId 教室ID
 * @returns {Promise}
 */
export const shouldAddToBlacklist = (userId, roomId) => {
  return request.get('/violation-settings/check-blacklist', {
    params: { userId, roomId }
  })
}

/**
 * 处理用户违规
 * @param {String} userId 用户ID
 * @param {String} roomId 教室ID
 * @param {String} bookingId 预约ID
 * @param {Number} overtimeMinutes 超时分钟数
 * @returns {Promise}
 */
export const processUserViolation = (userId, roomId, bookingId, overtimeMinutes) => {
  return request.post('/violation-settings/process-violation', null, {
    params: { userId, roomId, bookingId, overtimeMinutes }
  })
}