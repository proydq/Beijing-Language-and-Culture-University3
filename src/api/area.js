import { request, API_PATHS } from './apiConfig'

/**
 * 区域管理相关API
 */

/**
 * 获取区域树形结构
 * @param {Object} params - 查询参数
 * @param {string} params.cstmId - 客户域ID（可选）
 * @param {string} params.type - 区域类型（可选）
 * @returns {Promise} 区域树形数据
 */
export const getAreaTree = (params = {}) => {
  return request.get(`${API_PATHS.AREA}/tree`, { params })
}

/**
 * 获取单个区域详情
 * @param {string} id - 区域ID
 * @returns {Promise} 区域详情
 */
export const getAreaById = (id) => {
  return request.get(`${API_PATHS.AREA}/${id}`)
}

/**
 * 新增区域
 * @param {Object} areaData - 区域数据
 * @param {string} areaData.areaName - 区域名称
 * @param {string} areaData.parentId - 父节点ID
 * @param {string} areaData.type - 区域类型
 * @param {string} areaData.extend1 - 扩展字段1
 * @param {string} areaData.extend2 - 扩展字段2
 * @returns {Promise} 新增结果
 */
export const addArea = (areaData) => {
  return request.post(API_PATHS.AREA, areaData)
}

/**
 * 更新区域
 * @param {string} id - 区域ID
 * @param {Object} areaData - 区域数据
 * @returns {Promise} 更新结果
 */
export const updateArea = (id, areaData) => {
  return request.put(`${API_PATHS.AREA}/${id}`, areaData)
}

/**
 * 删除区域
 * @param {string} id - 区域ID
 * @returns {Promise} 删除结果
 */
export const deleteArea = (id) => {
  return request.delete(`${API_PATHS.AREA}/${id}`)
}

/**
 * 级联删除区域
 * @param {string} id - 区域ID
 * @returns {Promise} 删除结果
 */
export const cascadeDeleteArea = (id) => {
  return request.delete(`${API_PATHS.AREA}/${id}/cascade`)
}

/**
 * 分页查询区域
 * @param {Object} condition - 查询条件
 * @param {string} condition.areaName - 区域名称
 * @param {string} condition.parentId - 父节点ID
 * @param {string} condition.type - 区域类型
 * @param {number} condition.pathDeep - 路径深度
 * @param {string} condition.extend1 - 扩展字段1
 * @param {string} condition.extend2 - 扩展字段2
 * @param {number} condition.pageNumber - 页码
 * @param {number} condition.pageSize - 每页大小
 * @returns {Promise} 分页结果
 */
export const searchArea = (condition) => {
  return request.post(`${API_PATHS.AREA}/search`, condition)
}

/**
 * 区域类型枚举
 */
export const AREA_TYPES = {
  BUILDING: 'building',
  FLOOR: 'floor',
  ROOM: 'room'
}

/**
 * 区域类型中文映射
 */
export const AREA_TYPE_LABELS = {
  [AREA_TYPES.BUILDING]: '楼宇',
  [AREA_TYPES.FLOOR]: '楼层',
  [AREA_TYPES.ROOM]: '房间'
}
