import request from '@/utils/request'

/**
 * 获取预约时间配置信息
 * @param {string} roomId - 教室ID
 * @returns {Promise<Object>} 预约时间配置
 */
export function getBookingTimeConfig(roomId) {
  return request({
    url: '/api/room-booking/booking-time-config',
    method: 'get',
    params: {
      roomId
    }
  })
}

/**
 * 获取当前时间和教学周信息
 * @returns {Promise<Object>} 当前时间信息
 */
export function getCurrentTimeInfo() {
  return request({
    url: '/api/room-booking/current-time-info',
    method: 'get'
  })
}

/**
 * 验证预约时间合法性
 * @param {Object} data - 验证参数
 * @param {string} data.roomId - 教室ID
 * @param {string} data.bookingStartTime - 预约开始时间 (yyyy-MM-dd HH:mm:ss)
 * @param {string} data.bookingEndTime - 预约结束时间 (yyyy-MM-dd HH:mm:ss)
 * @returns {Promise<Object>} 验证结果
 */
export function validateBookingTime(data) {
  return request({
    url: '/api/room-booking/validate-booking-time',
    method: 'post',
    data
  })
}