import { request, API_PATHS } from './apiConfig'

/**
 * 用户管理相关API
 */
export const userAPI = {
  /**
   * 分页查询用户
   * @param {Object} searchCondition - 查询条件
   * @returns {Promise} 用户列表响应
   */
  searchUsers(searchCondition) {
    return request.post(`${API_PATHS.USER}/search`, searchCondition)
  },

  /**
   * 根据ID查询用户
   * @param {string} id - 用户ID
   * @returns {Promise} 用户详情响应
   */
  getUserById(id) {
    return request.get(`${API_PATHS.USER}/${id}`)
  },

  /**
   * 保存用户
   * @param {Object} userData - 用户数据
   * @returns {Promise} 保存响应
   */
  saveUser(userData) {
    return request.post(API_PATHS.USER, userData)
  },

  /**
   * 更新用户
   * @param {Object} userData - 用户数据
   * @returns {Promise} 更新响应
   */
  updateUser(userData) {
    return request.put(`${API_PATHS.USER}/${userData.id}`, userData)
  },

  /**
   * 逻辑删除用户
   * @param {Array} ids - 用户ID列表
   * @returns {Promise} 删除响应
   */
  deleteUsers(ids) {
    return request.post(`${API_PATHS.SYSTEM_USER}/delete`, ids)
  },

  /**
   * 批量移动用户到回收站
   * @param {Array} ids - 用户ID列表
   * @returns {Promise} 移动响应
   */
  moveUsersToRecycleBin(ids) {
    return request.post(`${API_PATHS.SYSTEM_USER}/moveToRecycleBin`, ids)
  },

  /**
   * 修改用户状态
   * @param {string} id - 用户ID
   * @param {string} status - 用户状态
   * @returns {Promise} 修改响应
   */
  updateUserStatus(id, status) {
    return request.post(`${API_PATHS.SYSTEM_USER}/updateStatus`, { id, status })
  },

  /**
   * 重置用户密码
   * @param {string} id - 用户ID
   * @returns {Promise} 重置响应
   */
  resetUserPassword(id) {
    return request.post(`${API_PATHS.SYSTEM_USER}/resetPassword`, { id })
  },

  /**
   * 分配用户角色
   * @param {string} userId - 用户ID
   * @param {Array} roleIds - 角色ID列表
   * @returns {Promise} 分配响应
   */
  assignUserRoles(userId, roleIds) {
    return request.post(`${API_PATHS.SYSTEM_USER}/assignRoles`, { userId, roleIds })
  },

  /**
   * 检查用户名是否存在
   * @param {string} username - 用户名
   * @returns {Promise} 检查响应
   */
  checkUsernameExists(username) {
    return request.get(`${API_PATHS.SYSTEM_USER}/checkUsername/${username}`)
  },

  /**
   * 检查工号是否存在
   * @param {string} jobNumber - 工号
   * @returns {Promise} 检查响应
   */
  checkJobNumberExists(jobNumber) {
    return request.get(`${API_PATHS.SYSTEM_USER}/checkJobNumber/${jobNumber}`)
  },

  /**
   * 获取部门下拉列表
   * @returns {Promise}
   */
  getAllDepartments() {
    return request.get(`${API_PATHS.ORGANIZATION}/all`)
  },

  /**
   * 获取职务下拉列表
   * @returns {Promise}
   */
  getAllPositions() {
    return request.get(`${API_PATHS.POSITION}/all`)
  },

  /**
   * 获取职称下拉列表
   * @returns {Promise}
   */
  getAllTitles() {
    return request.get(`${API_PATHS.TITLE}/all`)
  }
}

export default userAPI
