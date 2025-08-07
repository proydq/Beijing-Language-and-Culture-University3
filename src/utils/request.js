import axios from 'axios'

const request = axios.create({
  baseURL: '', // 使用相对路径，通过 vite 代理转发
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('userToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
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
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          localStorage.removeItem('userToken')
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
          break
        case 403:
          console.error('没有权限访问该资源')
          break
        case 400:
          // 参数验证错误
          console.error('参数错误:', data?.message || '请求参数不正确')
          break
        case 500:
          console.error('服务器内部错误:', data?.message || '服务器异常')
          break
        default:
          console.error('请求失败:', data?.message || error.message)
      }
      
      // 返回错误信息，便于组件处理
      return Promise.reject(new Error(data?.message || error.message))
    } else {
      console.error('网络错误:', error.message)
      return Promise.reject(new Error('网络连接失败，请检查网络'))
    }
  }
)

export default request
