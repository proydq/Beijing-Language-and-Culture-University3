import request from './index'

// 搜索教室
export function searchClassrooms(params) {
  return request({
    url: '/scheme-management/classrooms/list',
    method: 'post',
    data: params
  })
}

// 添加教室
export function addClassroom(data) {
  return request({
    url: '/scheme-management/classrooms/add',
    method: 'post',
    data
  })
}

// 根据ID获取教室详情
export function getClassroomDetail(id) {
  return request({
    url: `/scheme-management/classrooms/${id}`,
    method: 'get'
  })
}

// 更新教室
export function updateClassroom(data) {
  return request({
    url: '/scheme-management/classrooms/update',
    method: 'put',
    data
  })
}

// 批量删除教室（逻辑删除）
export function batchDeleteClassrooms(ids) {
  return request({
    url: '/scheme-management/classrooms/batch-delete',
    method: 'put',
    data: { ids }
  })
}

// 批量设置审批权限
export function batchSetApproval(data) {
  return request({
    url: '/scheme-management/classrooms/batch-approval',
    method: 'put',
    data
  })
}

// 从房间表同步教室数据
export function syncClassroomsFromRoom() {
  return request({
    url: '/scheme-management/classrooms/sync',
    method: 'post'
  })
}

// 搜索人员
export function searchPersonnel(params) {
  return request({
    url: '/scheme-management/personnel/list',
    method: 'post',
    data: params
  })
}

// 导出教室数据
export function exportClassrooms(ids) {
  return request({
    url: '/scheme-management/classrooms/export',
    method: 'post',
    data: { roomIds: ids },
    responseType: 'blob'
  })
}

// 导入教室数据
export function importClassrooms(formData) {
  return request({
    url: '/scheme-management/classrooms/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
