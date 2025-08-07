import { request, API_PATHS } from './apiConfig'

// 搜索教室
export function searchClassrooms(params) {
  return request.post(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/list`, params)
}

// 添加教室
export function addClassroom(data) {
  return request.post(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/add`, data)
}

// 根据ID获取教室详情
export function getClassroomDetail(id) {
  return request.get(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/${id}`)
}

// 更新教室
export function updateClassroom(data) {
  return request.put(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/update`, data)
}

// 批量删除教室（逻辑删除）
export function batchDeleteClassrooms(ids) {
  return request.put(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/batch-delete`, { ids })
}

// 批量设置审批权限
export function batchSetApproval(data) {
  return request.put(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/batch-approval`, data)
}

// 从房间表同步教室数据
export function syncClassroomsFromRoom() {
  return request.post(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/sync`)
}

// 搜索人员
export function searchPersonnel(params) {
  return request.post(`${API_PATHS.SCHEME_MANAGEMENT}/personnel/list`, params)
}

// 导出教室数据
export function exportClassrooms(ids) {
  return request.post(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/export`, { roomIds: ids }, {
    responseType: 'blob'
  })
}

// 导入教室数据
export function importClassrooms(formData) {
  return request.post(`${API_PATHS.SCHEME_MANAGEMENT}/classrooms/import`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
