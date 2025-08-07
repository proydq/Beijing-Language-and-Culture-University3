import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '', // 使用相对路径，通过 vite 代理转发
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('userToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    
    // 兼容新旧响应格式
    // 新格式: { code: "0", message: "成功", data: ... }
    // 旧格式: { code: 200, message: "success", data: ... }
    
    // 将字符串code转换为数字，方便前端统一处理
    if (typeof res.code === 'string') {
      if (res.code === '0') {
        res.code = 200  // 成功统一使用200
      } else {
        res.code = parseInt(res.code, 10)  // 其他错误码转为数字
      }
    }
    
    // 成功响应
    if (res.code === 200 || res.code === 0) {
      res.code = 200  // 确保成功都是200
      return res
    } else {
      // 业务错误
      console.error('业务错误:', res.message)
      return Promise.reject(new Error(res.message || '业务处理失败'))
    }
  },
  error => {
    // 统一处理错误
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('userToken')
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
          break
        case 403:
          console.error('没有权限访问该资源')
          break
        case 500:
          console.error('服务器内部错误')
          break
        default:
          console.error('请求失败:', error.response.data?.message || error.message)
      }
    } else {
      console.error('网络错误:', error.message)
    }
    return Promise.reject(error)
  }
)

export default api
