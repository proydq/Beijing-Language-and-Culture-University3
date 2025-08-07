import { request, API_PATHS } from './apiConfig'

// ==================== 用户预约限制相关接口 ====================

/**
 * 获取用户预约限制设置
 * @param {String} userId 用户ID
 * @returns {Promise}
 */
export const getUserBookingLimit = (userId) => {
  return request.get(`${API_PATHS.USER_BOOKING_LIMITS}/${userId}`)
}

/**
 * 设置用户预约限制
 * @param {Object} limitData 预约限制信息
 * @returns {Promise}
 */
export const setUserBookingLimit = (limitData) => {
  return request.post(API_PATHS.USER_BOOKING_LIMITS, limitData)
}

/**
 * 批量设置用户预约限制
 * @param {Array} limitDataList 预约限制信息列表
 * @returns {Promise}
 */
export const batchSetUserBookingLimits = (limitDataList) => {
  return request.post(`${API_PATHS.USER_BOOKING_LIMITS}/batch`, limitDataList)
}

/**
 * 删除用户预约限制设置
 * @param {String} userId 用户ID
 * @returns {Promise}
 */
export const deleteUserBookingLimit = (userId) => {
  return request.delete(`${API_PATHS.USER_BOOKING_LIMITS}/${userId}`)
}

/**
 * 获取所有用户的预约限制设置
 * @returns {Promise}
 */
export const getAllUserBookingLimits = () => {
  return request.get(`${API_PATHS.USER_BOOKING_LIMITS}/all`)
}

// ==================== 预约时间规则相关接口 ====================

/**
 * 获取预约时间规则列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNum 页码
 * @param {Number} params.pageSize 页大小
 * @param {String} params.ruleName 规则名称（可选）
 * @returns {Promise}
 */
export const getBookingTimeRuleList = (params) => {
  return request.get(`${API_PATHS.BOOKING_TIME_RULES}/list`, { params })
}

/**
 * 根据ID获取预约时间规则详情
 * @param {String} id 规则ID
 * @returns {Promise}
 */
export const getBookingTimeRuleById = (id) => {
  return request.get(`${API_PATHS.BOOKING_TIME_RULES}/${id}`)
}

/**
 * 新增预约时间规则
 * @param {Object} ruleData 规则信息
 * @returns {Promise}
 */
export const createBookingTimeRule = (ruleData) => {
  return request.post(API_PATHS.BOOKING_TIME_RULES, ruleData)
}

/**
 * 编辑预约时间规则
 * @param {String} id 规则ID
 * @param {Object} ruleData 规则信息
 * @returns {Promise}
 */
export const updateBookingTimeRule = (id, ruleData) => {
  return request.put(`${API_PATHS.BOOKING_TIME_RULES}/${id}`, ruleData)
}

/**
 * 删除预约时间规则
 * @param {String} id 规则ID
 * @returns {Promise}
 */
export const deleteBookingTimeRule = (id) => {
  return request.delete(`${API_PATHS.BOOKING_TIME_RULES}/${id}`)
}

/**
 * 根据用户ID获取适用的预约时间规则
 * @param {String} userId 用户ID
 * @returns {Promise}
 */
export const getBookingTimeRulesByUserId = (userId) => {
  return request.get(`${API_PATHS.BOOKING_TIME_RULES}/user/${userId}`)
}

// ==================== 预约时间验证相关接口 ====================

/**
 * 验证预约时间是否有效
 * @param {Object} request 验证请求
 * @param {String} request.userId 用户ID
 * @param {String} request.roomId 房间ID
 * @param {String} request.startTime 开始时间
 * @param {String} request.endTime 结束时间
 * @param {String} request.bookingDate 预约日期
 * @returns {Promise}
 */
export const checkBookingTime = (requestData) => {
  return request.post(`${API_PATHS.BOOKING_VALIDATION}/check-time`, requestData)
}

/**
 * 获取用户可预约的时间范围
 * @param {String} userId 用户ID
 * @param {String} date 预约日期 (YYYY-MM-DD格式)
 * @returns {Promise}
 */
export const getAvailableBookingTimes = (userId, date) => {
  return request.get(`${API_PATHS.BOOKING_VALIDATION}/available-times`, {
    params: { userId, date }
  })
}

// ==================== 常量定义 ====================

// 预约限制类型
export const BOOKING_LIMIT_TYPES = {
  DAILY: 'DAILY',           // 每日限制
  WEEKLY: 'WEEKLY',         // 每周限制
  MONTHLY: 'MONTHLY',       // 每月限制
  DURATION: 'DURATION'      // 时长限制
}

// 预约限制类型标签
export const BOOKING_LIMIT_TYPE_LABELS = {
  [BOOKING_LIMIT_TYPES.DAILY]: '每日限制',
  [BOOKING_LIMIT_TYPES.WEEKLY]: '每周限制',
  [BOOKING_LIMIT_TYPES.MONTHLY]: '每月限制',
  [BOOKING_LIMIT_TYPES.DURATION]: '时长限制'
}

// 时间规则类型
export const TIME_RULE_TYPES = {
  ADVANCE_BOOKING: 'ADVANCE_BOOKING',     // 提前预约时间
  BOOKING_WINDOW: 'BOOKING_WINDOW',       // 预约时间窗口
  BLACKOUT_PERIOD: 'BLACKOUT_PERIOD'      // 禁止预约时段
}

// 时间规则类型标签
export const TIME_RULE_TYPE_LABELS = {
  [TIME_RULE_TYPES.ADVANCE_BOOKING]: '提前预约时间',
  [TIME_RULE_TYPES.BOOKING_WINDOW]: '预约时间窗口',
  [TIME_RULE_TYPES.BLACKOUT_PERIOD]: '禁止预约时段'
}

// 星期常量
export const WEEKDAYS = {
  MONDAY: 1,
  TUESDAY: 2,
  WEDNESDAY: 3,
  THURSDAY: 4,
  FRIDAY: 5,
  SATURDAY: 6,
  SUNDAY: 7
}

// 星期标签
export const WEEKDAY_LABELS = {
  [WEEKDAYS.MONDAY]: '周一',
  [WEEKDAYS.TUESDAY]: '周二',
  [WEEKDAYS.WEDNESDAY]: '周三',
  [WEEKDAYS.THURSDAY]: '周四',
  [WEEKDAYS.FRIDAY]: '周五',
  [WEEKDAYS.SATURDAY]: '周六',
  [WEEKDAYS.SUNDAY]: '周日'
}