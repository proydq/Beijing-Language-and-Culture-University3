import { request, API_PATHS } from './apiConfig'

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
    return request.post(`${API_PATHS.ROOM_BOOKING}/remote-access-records`, params)
  },

  /**
   * 导出远程开门记录
   * @param {Object} params 导出参数
   * @returns {Promise}
   */
  exportRemoteAccessRecords(params) {
    return request.post(`${API_PATHS.ROOM_BOOKING}/remote-access-records/export`, params)
  },

  /**
   * 获取远程开门统计信息
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getRemoteAccessStats(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return request.get(`${API_PATHS.ROOM_BOOKING}/remote-access-records/stats${queryString ? '?' + queryString : ''}`)
  },

  /**
   * 执行远程开门操作
   * @param {Object} params 开门参数
   * @returns {Promise}
   */
  executeRemoteOpenDoor(params) {
    return request.post(`${API_PATHS.ROOM_BOOKING}/remote-access-records/open-door`, params)
  },

  /**
   * 获取远程开门操作日志
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getOperationLogs(params) {
    return request.post(`${API_PATHS.ROOM_BOOKING}/remote-access-records/operation-logs`, params)
  },

  /**
   * 获取可远程开门的教室列表
   * @param {Object} params 查询参数
   * @returns {Promise}
   */
  getAvailableRemoteRooms(params = {}) {
    const queryString = new URLSearchParams(params).toString()
    return request.get(`${API_PATHS.ROOM_BOOKING}/remote-access-records/available-rooms${queryString ? '?' + queryString : ''}`)
  }
}

export default remoteAccessRecordsApi