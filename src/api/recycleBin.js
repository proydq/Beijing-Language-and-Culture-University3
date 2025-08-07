import { request, API_PATHS } from './apiConfig'

// 搜索回收站中的教室
export function searchDeletedClassrooms(params) {
  return request.post(`${API_PATHS.RECYCLE_BIN}/classrooms/search`, params)
}

// 恢复教室（从回收站恢复）
export function restoreClassroom(id) {
  return request.put(`${API_PATHS.RECYCLE_BIN}/classrooms/${id}/restore`)
}

// 批量恢复教室
export function batchRestoreClassrooms(ids) {
  return request.put(`${API_PATHS.RECYCLE_BIN}/classrooms/batch-restore`, { ids })
}

// 永久删除教室
export function permanentDeleteClassroom(id) {
  return request.delete(`${API_PATHS.RECYCLE_BIN}/classrooms/${id}`)
}

// 批量永久删除教室
export function batchPermanentDeleteClassrooms(ids) {
  return request.delete(`${API_PATHS.RECYCLE_BIN}/classrooms/batch-delete`, { data: { ids } })
}

// 清空回收站
export function clearRecycleBin() {
  return request.delete(`${API_PATHS.RECYCLE_BIN}/clear`)
}

// 获取回收站统计信息
export function getRecycleBinStats() {
  return request.get(`${API_PATHS.RECYCLE_BIN}/stats`)
}
