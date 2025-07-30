import request from '@/utils/request'

/**
 * 分页查询黑名单
 * @param {Object} condition 查询条件
 * @returns {Promise}
 */
export const searchBlacklist = (condition) => {
  return request.post('/blacklist/search', condition)
}

/**
 * 添加用户到黑名单
 * @param {Object} data 黑名单数据
 * @returns {Promise}
 */
export const addToBlacklist = (data) => {
  return request.post('/blacklist', data)
}

/**
 * 从黑名单中移除用户
 * @param {String} blacklistId 黑名单记录ID
 * @returns {Promise}
 */
export const removeFromBlacklist = (blacklistId) => {
  return request.delete(`/blacklist/${blacklistId}`)
}

/**
 * 批量移除黑名单用户
 * @param {Array} blacklistIds 黑名单记录ID数组
 * @returns {Promise}
 */
export const batchRemoveFromBlacklist = (blacklistIds) => {
  return request.delete('/blacklist/batch', { data: blacklistIds })
}

/**
 * 检查用户是否在黑名单中
 * @param {String} userId 用户ID
 * @returns {Promise}
 */
export const isUserInBlacklist = (userId) => {
  return request.get(`/blacklist/check/${userId}`)
}

/**
 * 获取用户违规记录
 * @param {String} userId 用户ID
 * @param {Number} pageNumber 页码
 * @param {Number} pageSize 页大小
 * @returns {Promise}
 */
export const getUserViolationRecords = (userId, pageNumber = 1, pageSize = 10) => {
  return request.get(`/blacklist/violation-records/${userId}`, {
    params: { pageNumber, pageSize }
  })
}