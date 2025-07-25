import api from './index'

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
    return api.post('/sys/user/search', searchCondition)
  },

  /**
   * 根据ID查询用户
   * @param {string} id - 用户ID
   * @returns {Promise} 用户详情响应
   */
  getUserById(id) {
    return api.get(`/sys/user/${id}`)
  },

  /**
   * 保存用户
   * @param {Object} userData - 用户数据
   * @returns {Promise} 保存响应
   */
  saveUser(userData) {
    return api.post('/sys/user/save', userData)
  },

  /**
   * 更新用户
   * @param {Object} userData - 用户数据
   * @returns {Promise} 更新响应
   */
  updateUser(userData) {
    return api.post('/sys/user/update', userData)
  },

  /**
   * 逻辑删除用户
   * @param {Array} ids - 用户ID列表
   * @returns {Promise} 删除响应
   */
  deleteUsers(ids) {
    return api.post('/sys/user/delete', ids)
  },

  /**
   * 批量移动用户到回收站
   * @param {Array} ids - 用户ID列表
   * @returns {Promise} 移动响应
   */
  moveUsersToRecycleBin(ids) {
    return api.post('/sys/user/moveToRecycleBin', ids)
  },

  /**
   * 修改用户状态
   * @param {string} id - 用户ID
   * @param {string} status - 用户状态
   * @returns {Promise} 修改响应
   */
  updateUserStatus(id, status) {
    return api.post('/sys/user/updateStatus', { id, status })
  },

  /**
   * 重置用户密码
   * @param {string} id - 用户ID
   * @returns {Promise} 重置响应
   */
  resetUserPassword(id) {
    return api.post('/sys/user/resetPassword', { id })
  },

  /**
   * 分配用户角色
   * @param {string} userId - 用户ID
   * @param {Array} roleIds - 角色ID列表
   * @returns {Promise} 分配响应
   */
  assignUserRoles(userId, roleIds) {
    return api.post('/sys/user/assignRoles', { userId, roleIds })
  },

  /**
   * 检查用户名是否存在
   * @param {string} username - 用户名
   * @returns {Promise} 检查响应
   */
  checkUsernameExists(username) {
    return api.get(`/sys/user/checkUsername/${username}`)
  },

  /**
   * 检查工号是否存在
   * @param {string} jobNumber - 工号
   * @returns {Promise} 检查响应
   */
  checkJobNumberExists(jobNumber) {
    return api.get(`/sys/user/checkJobNumber/${jobNumber}`)
  },

  /**
   * 获取部门下拉列表
   * @returns {Promise}
   */
  getAllDepartments() {
    return api.get('/api/organization/all')
  },

  /**
   * 获取职务下拉列表
   * @returns {Promise}
   */
  getAllPositions() {
    return api.get('/api/position/all')
  },

  /**
   * 获取职称下拉列表
   * @returns {Promise}
   */
  getAllTitles() {
    return api.get('/api/title/all')
  }
}

export default userAPI
