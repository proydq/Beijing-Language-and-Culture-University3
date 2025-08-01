import request from '@/utils/request'

// ==================== 审批管理接口 ====================

/**
 * 获取待审批列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNumber 页码
 * @param {Number} params.pageSize 页大小
 * @param {String} params.reservationName 预约名称（可选）
 * @param {String} params.applicantName 申请人姓名（可选）
 * @param {String} params.startDate 开始日期（可选）
 * @param {String} params.endDate 结束日期（可选）
 * @returns {Promise}
 */
export const getPendingApprovals = (params) => {
  return request.post('/api/room-booking/approvals/pending', params)
}

/**
 * 获取全部审批列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNumber 页码
 * @param {Number} params.pageSize 页大小
 * @param {String} params.reservationName 预约名称（可选）
 * @param {String} params.applicantName 申请人姓名（可选）
 * @param {String} params.approvalStatus 审批状态（可选）：PENDING|APPROVED|REJECTED
 * @param {String} params.startDate 开始日期（可选）
 * @param {String} params.endDate 结束日期（可选）
 * @returns {Promise}
 */
export const getAllApprovals = (params) => {
  return request.post('/api/room-booking/approvals/all', params)
}

/**
 * 获取已通过审批列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNumber 页码
 * @param {Number} params.pageSize 页大小
 * @param {String} params.reservationName 预约名称（可选）
 * @param {String} params.applicantName 申请人姓名（可选）
 * @param {String} params.startDate 开始日期（可选）
 * @param {String} params.endDate 结束日期（可选）
 * @returns {Promise}
 */// 获取已通过审批列表
export const getApprovedApprovals = (params) => {
  return request.post('/api/room-booking/approvals/approved', params)
}

// 获取已拒绝审批列表
export const getRejectedApprovals = (params) => {
  return request.post('/api/room-booking/approvals/rejected', params)
}

/**
 * 审批预约
 * @param {String} bookingId 预约ID
 * @param {Object} approvalData 审批数据
 * @param {String} approvalData.action 审批动作：APPROVED|REJECTED
 * @param {String} approvalData.comment 审批意见
 * @returns {Promise}
 */
export const approveBooking = (bookingId, approvalData) => {
  return request.post(`/api/room-booking/approve/${bookingId}`, approvalData)
}

/**
 * 获取预约详情
 * @param {String} bookingId 预约ID
 * @returns {Promise}
 */
export const getBookingDetail = (bookingId) => {
  return request.get(`/api/room-booking/detail/${bookingId}`)
}

/**
 * 获取审批历史
 * @param {String} bookingId 预约ID
 * @returns {Promise}
 */
export const getApprovalHistory = (bookingId) => {
  return request.get(`/api/room-booking/approval-history/${bookingId}`)
}

/**
 * 批量审批
 * @param {Object} batchData 批量审批数据
 * @param {Array} batchData.bookingIds 预约ID列表
 * @param {String} batchData.action 审批动作：APPROVED|REJECTED
 * @param {String} batchData.comment 批量审批意见
 * @returns {Promise}
 */
export const batchApprove = (batchData) => {
  return request.post('/api/room-booking/batch-approve', batchData)
}

// ==================== 常量定义 ====================

// 审批状态映射
export const APPROVAL_STATUS_MAP = {
  PENDING: '待审批',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  CANCELLED: '已取消'
}

// 审批动作
export const APPROVAL_ACTIONS = {
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED'
}

// 紧急程度映射
export const URGENCY_MAP = {
  NORMAL: '普通',
  URGENT: '紧急',
  VERY_URGENT: '非常紧急'
}
