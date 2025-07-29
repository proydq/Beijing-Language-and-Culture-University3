import request from '@/utils/request'

/**
 * 获取组织机构下拉选项
 */
export function getOrganizationOptions() {
  return request({
    url: '/api/organization/all',
    method: 'get'
  })
}

/**
 * 获取组织机构树结构
 */
export function getOrganizationTree() {
  return request({
    url: '/api/organization/tree',
    method: 'get'
  })
}

/**
 * 获取单个组织详情
 */
export function getOrganizationById(id) {
  return request({
    url: `/api/organization/${id}`,
    method: 'get'
  })
}

/**
 * 新增组织
 */
export function createOrganization(data) {
  return request({
    url: '/api/organization',
    method: 'post',
    data
  })
}

/**
 * 编辑组织
 */
export function updateOrganization(id, data) {
  return request({
    url: `/api/organization/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除组织
 */
export function deleteOrganization(id) {
  return request({
    url: `/api/organization/${id}`,
    method: 'delete'
  })
}