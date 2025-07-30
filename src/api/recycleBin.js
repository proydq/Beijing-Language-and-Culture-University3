import request from './index'

// 搜索回收站中的教室
export function searchDeletedClassrooms(params) {
  return request({
    url: '/recycle-bin/classrooms/search',
    method: 'post',
    data: params
  })
}

// 恢复教室（从回收站恢复）
export function restoreClassroom(id) {
  return request({
    url: `/scheme-management/recycle-bin/classrooms/${id}/restore`,
    method: 'put'
  })
}

// 批量恢复教室
export function batchRestoreClassrooms(ids) {
  return request({
    url: '/scheme-management/recycle-bin/classrooms/batch-restore',
    method: 'put',
    data: { ids }
  })
}

// 永久删除教室
export function permanentDeleteClassroom(id) {
  return request({
    url: `/scheme-management/recycle-bin/classrooms/${id}/permanent-delete`,
    method: 'delete'
  })
}

// 批量永久删除教室
export function batchPermanentDeleteClassrooms(ids) {
  return request({
    url: '/scheme-management/recycle-bin/classrooms/batch-permanent-delete',
    method: 'delete',
    data: { ids }
  })
}

// 清空回收站
export function clearRecycleBin() {
  return request({
    url: '/scheme-management/recycle-bin/classrooms/clear',
    method: 'delete'
  })
}

// 获取回收站统计信息
export function getRecycleBinStats() {
  return request({
    url: '/scheme-management/recycle-bin/stats',
    method: 'get'
  })
}
