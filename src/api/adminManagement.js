import { request, API_PATHS } from './apiConfig'

/**
 * 管理员管理相关API
 */
export const adminManagementAPI = {
  /**
   * 分页查询管理员列表
   * @param {Object} condition - 查询条件
   * @returns {Promise} 管理员列表响应
   */
  searchAdmins(condition) {
    return request.post(`${API_PATHS.ADMIN_MANAGEMENT}/admins/search`, condition)
  },

  /**
   * 添加管理员
   * @param {Object} request - 添加管理员请求
   * @returns {Promise} 添加响应
   */
  addAdmins(addRequest) {
    return request.post(`${API_PATHS.ADMIN_MANAGEMENT}/admins/add`, addRequest)
  },

  /**
   * 更新管理员角色
   * @param {string} userId - 用户ID
   * @param {Object} request - 更新角色请求
   * @returns {Promise} 更新响应
   */
  updateAdminRoles(userId, updateRequest) {
    return request.put(`${API_PATHS.ADMIN_MANAGEMENT}/admins/${userId}/roles`, updateRequest)
  },

  /**
   * 删除管理员
   * @param {string} userId - 用户ID
   * @returns {Promise} 删除响应
   */
  deleteAdmin(userId) {
    return request.delete(`${API_PATHS.ADMIN_MANAGEMENT}/admins/${userId}`)
  },

  /**
   * 批量删除管理员
   * @param {Object} request - 批量删除请求
   * @returns {Promise} 删除响应
   */
  batchDeleteAdmins(deleteRequest) {
    return request.delete(`${API_PATHS.ADMIN_MANAGEMENT}/admins/batch`, { data: deleteRequest })
  },

  /**
   * 更新管理员状态
   * @param {string} userId - 用户ID
   * @param {Object} request - 更新状态请求
   * @returns {Promise} 更新响应
   */
  updateAdminStatus(userId, statusRequest) {
    return request.put(`${API_PATHS.ADMIN_MANAGEMENT}/admins/${userId}/status`, statusRequest)
  },

  /**
   * 获取可分配角色列表
   * @returns {Promise} 角色列表响应
   */
  getAvailableRoles() {
    return request.get(`${API_PATHS.ADMIN_MANAGEMENT}/roles/available`)
  }
}

export default adminManagementAPI