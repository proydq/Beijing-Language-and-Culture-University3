import { ElMessage } from 'element-plus'

/**
 * API 响应处理辅助函数
 * 统一处理API响应，避免重复代码
 */

/**
 * 处理API响应
 * @param {Promise} apiCall - API调用的Promise
 * @param {Object} options - 配置选项
 * @param {String} options.successMessage - 成功时的提示消息
 * @param {String} options.errorMessage - 失败时的提示消息
 * @param {Boolean} options.showSuccess - 是否显示成功提示，默认false
 * @param {Boolean} options.showError - 是否显示错误提示，默认true
 * @returns {Promise} 返回处理后的数据或null
 */
export async function handleApiResponse(apiCall, options = {}) {
  const {
    successMessage = '',
    errorMessage = '操作失败',
    showSuccess = false,
    showError = true
  } = options

  try {
    const response = await apiCall
    
    // 检查是否是成功响应
    // 由于request.js已经将code统一转换为数字，这里只需要检查200
    const isSuccess = response.code === 200
    
    if (isSuccess) {
      if (showSuccess && successMessage) {
        ElMessage.success(successMessage)
      }
      return response.data || response
    } else {
      // 业务错误
      const message = response.message || errorMessage
      if (showError) {
        ElMessage.error(message)
      }
      return null
    }
  } catch (error) {
    // 网络错误或其他异常
    console.error('API调用失败:', error)
    if (showError) {
      const message = error.message || errorMessage
      ElMessage.error(message)
    }
    return null
  }
}

/**
 * 检查响应是否成功
 * @param {Object} response - API响应对象
 * @returns {Boolean} 是否成功
 */
export function isSuccess(response) {
  return response && response.code === 200
}

/**
 * 获取响应错误消息
 * @param {Object} response - API响应对象
 * @param {String} defaultMessage - 默认错误消息
 * @returns {String} 错误消息
 */
export function getErrorMessage(response, defaultMessage = '操作失败') {
  if (!response) return defaultMessage
  return response.message || defaultMessage
}