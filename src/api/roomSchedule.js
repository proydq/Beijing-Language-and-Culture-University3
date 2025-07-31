import request from '@/utils/request'

// ==================== 教室课程安排接口 ====================

/**
 * 获取教学周列表
 * @returns {Promise}
 */
export const getSchoolWeeks = () => {
  return request.get('/api/room-schedule/weeks')
}

/**
 * 获取教室周课表详情
 * @param {Object} params 查询参数
 * @param {String} params.classRoomId 教室ID
 * @param {Number} params.timestamp 时间戳
 * @returns {Promise}
 */
export const getRoomWeeklySchedule = (params) => {
  return request.post('/api/room-schedule/week-detail', params)
}