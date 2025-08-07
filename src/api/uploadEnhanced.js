import { request, API_PATHS } from './apiConfig'

/**
 * 增强的文件上传API
 * 支持文件去重、秒传等功能
 */

/**
 * 检查文件是否已存在（支持秒传）
 * @param {Object} fileInfo - 文件信息
 * @returns {Promise}
 */
export function checkFile(fileInfo) {
  return request.post(`${API_PATHS.UPLOAD}/v2/check`, fileInfo)
}

/**
 * 上传文件（支持秒传）
 * @param {FormData} formData - 包含文件和其他参数的表单数据
 * @returns {Promise}
 */
export function uploadFileEnhanced(formData) {
  return request.post(`${API_PATHS.UPLOAD}/v2/upload`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新文件引用
 * @param {Object} params - 引用参数
 * @returns {Promise}
 */
export function updateFileReference(params) {
  return request.post(`${API_PATHS.UPLOAD}/v2/reference`, null, { params })
}

/**
 * 获取文件统计信息
 * @returns {Promise}
 */
export function getFileStatistics() {
  return request.get(`${API_PATHS.UPLOAD}/v2/statistics`)
}