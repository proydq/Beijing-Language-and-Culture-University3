import api from './index.js'

/**
 * 连续预约设置相关API
 */

/**
 * 获取教室连续预约设置列表
 * @param {Object} params - 查询参数
 * @param {string} params.areaId - 区域ID（可选）
 * @param {number} params.pageNumber - 页码
 * @param {number} params.pageSize - 每页大小
 * @returns {Promise} 设置列表
 */
export const getContinuousBookingSettings = (params = {}) => {
  return api.post('/continuous-booking-settings/list', params)
}

/**
 * 搜索连续预约设置（包含房间信息）
 * @param {Object} condition - 查询条件
 * @param {string} condition.roomAreaId - 区域ID（可选）
 * @param {string} condition.roomName - 房间名称（可选）
 * @param {string} condition.roomNo - 房间号码（可选）
 * @param {string} condition.roomTypeName - 房屋类型名称（可选）
 * @param {string} condition.roomAreaName - 区域名称（可选）
 * @param {string} condition.roomCode - 房间编码（可选）
 * @param {number} condition.pageNumber - 页码
 * @param {number} condition.pageSize - 每页大小
 * @returns {Promise} 设置列表（包含房间信息）
 */
export const searchContinuousBookingSettings = (condition = {}) => {
  return api.post('/continuous-booking-settings/search', condition)
}

/**
 * 根据房间ID获取连续预约设置
 * @param {string} roomId - 房间ID
 * @returns {Promise} 设置详情
 */
export const getContinuousBookingSettingByRoomId = (roomId) => {
  return api.get(`/continuous-booking-settings/${roomId}`)
}

/**
 * 更新单个教室连续预约设置
 * @param {string} roomId - 房间ID
 * @param {Object} settingData - 设置数据
 * @param {number} settingData.continuousDays - 连续天数
 * @param {string} settingData.continuousType - 连续类型（DAYS/WEEKS/MONTHS）
 * @param {boolean} settingData.isUnlimited - 是否无限制
 * @param {boolean} settingData.canContinuous - 是否可连续
 * @returns {Promise} 更新结果
 */
export const updateContinuousBookingSetting = (roomId, settingData) => {
  return api.put(`/continuous-booking-settings/${roomId}`, settingData)
}

/**
 * 批量更新教室连续预约设置
 * @param {Object} batchData - 批量更新数据
 * @param {Array} batchData.roomIds - 房间ID列表
 * @param {number} batchData.continuousDays - 连续天数
 * @param {string} batchData.continuousType - 连续类型
 * @param {boolean} batchData.isUnlimited - 是否无限制
 * @param {boolean} batchData.canContinuous - 是否可连续
 * @returns {Promise} 批量更新结果
 */
export const batchUpdateContinuousBookingSettings = (batchData) => {
  return api.post('/continuous-booking-settings/batch-update', batchData)
}

/**
 * 获取楼层选项列表
 * @returns {Promise} 楼层选项
 */
export const getFloorOptions = () => {
  return api.get('/continuous-booking-settings/floor-options')
}

/**
 * 删除房间连续预约设置
 * @param {string} roomId - 房间ID
 * @returns {Promise} 删除结果
 */
export const deleteContinuousBookingSetting = (roomId) => {
  return api.delete(`/continuous-booking-settings/${roomId}`)
}

/**
 * 连续类型枚举
 */
export const CONTINUOUS_TYPES = {
  DAYS: 'DAYS',
  WEEKS: 'WEEKS', 
  MONTHS: 'MONTHS'
}

/**
 * 连续类型标签
 */
export const CONTINUOUS_TYPE_LABELS = {
  [CONTINUOUS_TYPES.DAYS]: '天',
  [CONTINUOUS_TYPES.WEEKS]: '周',
  [CONTINUOUS_TYPES.MONTHS]: '月'
}