import { request, API_PATHS } from './apiConfig'

/**
 * 认证相关API
 */
export const authAPI = {
  /**
   * 用户登录
   * @param {Object} loginData - 登录数据
   * @param {string} loginData.username - 用户名
   * @param {string} loginData.password - 密码
   * @param {string} [loginData.customerId] - 客户域ID（可选）
   * @returns {Promise} 登录响应
   */
  login(loginData) {
    return request.post(`${API_PATHS.AUTHENTICATION}/login`, loginData)
  },

  /**
   * 用户登出
   * @returns {Promise} 登出响应
   */
  logout() {
    return request.post(`${API_PATHS.AUTHENTICATION}/logout`)
  },

  /**
   * 刷新JWT令牌
   * @returns {Promise} 刷新令牌响应
   */
  refreshToken() {
    return request.post(`${API_PATHS.AUTHENTICATION}/token/refresh`)
  },

  /**
   * 忘记密码
   * @param {Object} data - 忘记密码数据
   * @param {string} data.username - 用户名
   * @param {string} data.phone - 手机号
   * @returns {Promise} 忘记密码响应
   */
  forgotPassword(data) {
    return request.post(`${API_PATHS.AUTHENTICATION}/forgot-password`, data)
  }
}

export default authAPI
