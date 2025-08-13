import request from './request'

// 用户状态管理API
export const userStatusAPI = {
  // 获取用户状态分布
  getStatusDistribution() {
    return request({
      url: '/debug/user-status/distribution',
      method: 'GET'
    })
  },

  // 修复用户状态
  fixUserStatus() {
    return request({
      url: '/debug/user-status/fix',
      method: 'POST'
    })
  },

  // 检查特定用户状态
  checkUserStatus(username) {
    return request({
      url: `/debug/user-status/check/${username}`,
      method: 'GET'
    })
  },

  // 测试状态枚举
  testStatusEnum(status) {
    return request({
      url: '/debug/user-status/test-enum',
      method: 'POST',
      data: { status }
    })
  }
}