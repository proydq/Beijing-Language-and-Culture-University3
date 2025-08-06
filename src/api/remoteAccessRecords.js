import api from './index.js'

/**
 * 远程开门记录相关API
 */
export const remoteAccessRecordsApi = {
  /**
   * 获取远程开门记录列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getRemoteAccessRecords(params) {
    return api.post('/api/room-booking/remote-access-records', params)
  },

  /**
   * 导出远程开门记录
   * @param {Object} params 导出参数
   * @returns {Promise}
   */
  exportRemoteAccessRecords(params) {
    return api.post('/api/room-booking/remote-access-records/export', params)
  },

  /**
   * 获取远程开门统计信息
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getRemoteAccessStats(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return api.get(`/api/room-booking/remote-access-records/stats${queryString ? '?' + queryString : ''}`)
  },

  /**
   * 执行远程开门操作
   * @param {Object} params 开门参数
   * @returns {Promise}
   */
  executeRemoteOpenDoor(params) {
    return api.post('/api/room-booking/remote-access-records/open-door', params)
  },

  /**
   * 获取远程开门操作日志
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getOperationLogs(params) {
    return api.post('/api/room-booking/remote-access-records/operation-logs', params)
  },

  /**
   * 获取可远程开门的教室列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getAvailableRemoteRooms(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return api.get(`/api/room-booking/remote-access-records/available-rooms${queryString ? '?' + queryString : ''}`)
  }
}

export default remoteAccessRecordsApi