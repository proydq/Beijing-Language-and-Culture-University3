import api from './index.js'

/**
 * 教室出入记录相关API
 */
export const accessRecordsApi = {
  /**
   * 获取教室借用记录列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getAccessRecords(params) {
    return api.post('/api/room-booking/access-records', params)
  },

  /**
   * 导出教室借用记录
   * @param {Object} params 导出参数
   * @returns {Promise}
   */
  exportAccessRecords(params) {
    return api.post('/api/room-booking/access-records/export', params)
  },

  /**
   * 获取教室借用统计信息
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getAccessStats(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return api.get(`/api/room-booking/access-records/stats${queryString ? '?' + queryString : ''}`)
  },

  /**
   * 获取教室实时使用状态
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getRoomStatus(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return api.get(`/api/room-booking/access-records/room-status${queryString ? '?' + queryString : ''}`)
  },

  /**
   * 获取教室预约统计列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getRoomBookingStats(params) {
    return api.post('/api/room-booking/access-records/booking-stats', params)
  },

  /**
   * 获取教室预约详情
   * @param {string} roomId 教室ID
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getRoomBookingDetails(roomId, params) {
    return api.post(`/api/room-booking/access-records/room/${roomId}/details`, params)
  },

  /**
   * 导出教室预约统计数据
   * @param {Object} params 导出参数
   * @returns {Promise}
   */
  exportRoomBookingStats(params) {
    return api.post('/api/room-booking/access-records/booking-stats/export', params)
  },

  /**
   * 导出指定教室的预约详情
   * @param {string} roomId 教室ID
   * @param {Object} params 导出参数
   * @returns {Promise}
   */
  exportRoomBookingDetails(roomId, params) {
    return api.post(`/api/room-booking/access-records/room/${roomId}/export`, params)
  }
}

export default accessRecordsApi