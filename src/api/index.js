import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:10086', // 后端服务地址
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
    // 统一处理响应数据
    return response.data
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