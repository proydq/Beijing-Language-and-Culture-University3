import request from '@/utils/request'

// ==================== 房间预约管理接口 ====================

/**
 * 获取我的预约列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNumber 页码
 * @param {Number} params.pageSize 页大小
 * @param {String} params.reservationName 预约名称（可选）
 * @param {String} params.approvalStatus 审核状态（可选）：审核中|通过|拒绝
 * @param {String} params.usageStatus 使用状态（可选）：未开始|进行中|已结束
 * @param {String} params.startDate 开始日期（可选）
 * @param {String} params.endDate 结束日期（可选）
 * @returns {Promise}
 */
export const getMyBookings = (params) => {
  return request.post('/api/room-booking/my-bookings', params)
}

/**
 * 获取全部借用列表（管理员权限）
 * @param {Object} params 查询参数
 * @param {Number} params.pageNumber 页码
 * @param {Number} params.pageSize 页大小
 * @param {String} params.reservationName 预约名称（可选）
 * @param {String} params.approvalStatus 审核状态（可选）：审核中|通过|拒绝
 * @param {String} params.usageStatus 使用状态（可选）：未开始|进行中|已结束
 * @param {String} params.startDate 开始日期（可选）
 * @param {String} params.endDate 结束日期（可选）
 * @param {String} params.applicantName 申请人姓名（可选）
 * @returns {Promise}
 */
export const getAllBookings = (params) => {
  return request.post('/api/room-booking/all-bookings', params)
}

/**
 * 获取可预约房间列表
 * @param {Object} params 查询参数
 * @param {String} params.roomAreaId 房间区域ID（可选）
 * @param {String} params.roomName 房间名称关键词（可选）
 * @param {Boolean} params.available 是否只显示可用房间（可选）
 * @returns {Promise}
 */
export const getAvailableRooms = (params) => {
  return request.post('/api/room-booking/rooms/available', params)
}

/**
 * 创建房间预约
 * @param {Object} bookingData 预约数据
 * @param {String} bookingData.roomId 房间ID
 * @param {String} bookingData.roomName 房间名称
 * @param {String} bookingData.applicant 申请人姓名
 * @param {String} bookingData.applicantId 申请人ID
 * @param {String} bookingData.applicantType 申请人类型
 * @param {String} bookingData.applicantPhone 申请人电话
 * @param {String} bookingData.applicantDepartment 申请人部门
 * @param {String} bookingData.bookingName 预约名称
 * @param {String} bookingData.borrowTime 借用时间
 * @param {String} bookingData.bookingStartTime 预约开始时间
 * @param {String} bookingData.bookingEndTime 预约结束时间
 * @param {String} bookingData.description 借用描述
 * @param {String} bookingData.reason 申请理由
 * @param {Array} bookingData.participants 参与人姓名列表
 * @param {String} bookingData.remark 备注详情
 * @param {Array} bookingData.approvers 审批人姓名列表
 * @param {Array} bookingData.participantDetails 参与人详细信息
 * @param {Array} bookingData.approverDetails 审批人详细信息
 * @param {String} bookingData.urgency 紧急程度
 * @returns {Promise}
 */
export const createBooking = (bookingData) => {
  return request.post('/api/room-booking/create', bookingData)
}

/**
 * 获取预约详情
 * @param {String} id 预约ID
 * @returns {Promise}
 */
export const getBookingDetail = (id) => {
  return request.get(`/api/room-booking/detail/${id}`)
}

/**
 * 编辑预约
 * @param {String} id 预约ID
 * @param {Object} bookingData 预约数据
 * @returns {Promise}
 */
export const updateBooking = (id, bookingData) => {
  return request.put(`/api/room-booking/update/${id}`, bookingData)
}

/**
 * 审批预约
 * @param {String} id 预约ID
 * @param {Object} approvalData 审批数据
 * @param {String} approvalData.action 审批动作：APPROVED|REJECTED
 * @param {String} approvalData.comment 审批意见
 * @param {String} approvalData.approverId 审批人ID
 * @param {String} approvalData.approverName 审批人姓名
 * @returns {Promise}
 */
export const approveBooking = (id, approvalData) => {
  return request.post(`/api/room-booking/approve/${id}`, approvalData)
}

/**
 * 取消预约
 * @param {String} id 预约ID
 * @param {Object} cancelData 取消数据
 * @param {String} cancelData.reason 取消原因
 * @returns {Promise}
 */
export const cancelBooking = (id, cancelData) => {
  return request.post(`/api/room-booking/cancel/${id}`, cancelData)
}

// 审批历史信息已包含在getBookingDetail接口的approvalSteps字段中，无需单独接口

/**
 * 检查房间可用性
 * @param {Object} checkData 检查数据
 * @param {String} checkData.roomId 房间ID
 * @param {String} checkData.startDateTime 开始时间
 * @param {String} checkData.endDateTime 结束时间
 * @param {String} checkData.excludeBookingId 排除的预约ID（编辑时使用）
 * @returns {Promise}
 */
export const checkRoomAvailability = (checkData) => {
  return request.post('/api/room-booking/check-availability', checkData)
}

// ==================== 统计接口 ====================

/**
 * 获取借用统计数据
 * @param {String} startTime 开始时间（可选）
 * @param {String} endTime 结束时间（可选）
 * @returns {Promise}
 */
export const getBookingStats = (startTime, endTime) => {
  const params = {}
  if (startTime) params.startTime = startTime
  if (endTime) params.endTime = endTime
  return request.get('/api/room-booking/stats', { params })
}

/**
 * 获取借用数据分布（饼图数据）
 * @param {String} startTime 开始时间（可选）
 * @param {String} endTime 结束时间（可选）
 * @returns {Promise}
 */
export const getBookingDistribution = (startTime, endTime) => {
  const params = {}
  if (startTime) params.startTime = startTime
  if (endTime) params.endTime = endTime
  return request.get('/api/room-booking/distribution', { params })
}

/**
 * 获取借用趋势数据（折线图数据）
 * @param {Number} days 天数（7、15、30、90）
 * @returns {Promise}
 */
export const getBookingTrend = (days = 7) => {
  return request.get('/api/room-booking/trend', {
    params: { days }
  })
}

/**
 * 获取借用趋势数据（自定义时间范围）
 * @param {String} startTime 开始时间
 * @param {String} endTime 结束时间
 * @returns {Promise}
 */
export const getBookingTrendCustom = (startTime, endTime) => {
  return request.get('/api/room-booking/trend/custom', {
    params: { startTime, endTime }
  })
}

// ==================== 教室借用记录接口 ====================

/**
 * 获取教室预约统计列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNum 页码，默认1
 * @param {Number} params.pageSize 每页条数，默认10
 * @param {String} params.roomName 教室名称（可选，支持模糊搜索）
 * @param {String} params.areaId 区域ID（可选，用于楼栋筛选）
 * @returns {Promise}
 */
export const getRoomBookingStats = (params) => {
  return request.post('/api/room-booking/access-records/booking-stats', params)
}

/**
 * 获取教室预约详情
 * @param {String} roomId 教室ID
 * @param {Object} params 查询参数
 * @param {Number} params.pageNum 页码，默认1
 * @param {Number} params.pageSize 每页条数，默认10
 * @param {String} params.bookingName 借用/预约名称（可选，支持模糊搜索）
 * @param {String} params.auditStatus 审核状态（通过/审核中/拒绝，可选）
 * @param {String} params.usageStatus 使用状态（未开始/进行中/已结束，可选）
 * @param {String} params.applicantName 预约人（可选）
 * @param {String} params.startTime 预约开始日期（可选，格式: YYYY-MM-DD）
 * @param {String} params.endTime 预约结束日期（可选，格式: YYYY-MM-DD）
 * @returns {Promise}
 */
export const getRoomBookingDetails = (roomId, params) => {
  return request.post(`/api/room-booking/access-records/room/${roomId}/details`, params)
}

/**
 * 导出教室预约详情
 * @param {String} roomId 教室ID
 * @param {Object} params 导出参数
 * @param {String} params.exportType 导出类型：current-当前页，all-全部数据
 * @param {Number} params.pageNum 页码（exportType为current时必填）
 * @param {Number} params.pageSize 每页条数（exportType为current时必填）
 * @param {String} params.bookingName 借用/预约名称（可选）
 * @param {String} params.auditStatus 审核状态（可选）
 * @param {String} params.usageStatus 使用状态（可选）
 * @param {String} params.applicantName 预约人（可选）
 * @param {String} params.startTime 预约开始日期（可选）
 * @param {String} params.endTime 预约结束日期（可选）
 * @returns {Promise}
 */
export const exportRoomBookingDetails = (roomId, params) => {
  return request.post(`/api/room-booking/access-records/room/${roomId}/export`, params)
}

/**
 * 导出教室预约统计数据
 * @param {Object} params 导出参数
 * @param {String} params.exportType 导出类型：current-当前页，all-全部数据
 * @param {Number} params.pageNum 页码（exportType为current时必填）
 * @param {Number} params.pageSize 每页条数（exportType为current时必填）
 * @param {String} params.areaId 区域ID（可选）
 * @param {String} params.roomName 教室名称（可选，支持模糊搜索）
 * @param {String} params.sortBy 排序字段（可选：bookingCount-预约次数，totalDuration-累计时长，totalPeople-累计人数）
 * @param {String} params.sortOrder 排序方向（可选：asc-升序，desc-降序）
 * @returns {Promise}
 */
export const exportRoomBookingStats = (params) => {
  return request.post('/api/room-booking/access-records/booking-stats/export', params)
}

// ==================== 教室借用记录相关接口 ====================

/**
 * 获取教室借用记录列表
 * @param {Object} params 查询参数
 * @param {Number} params.pageNum 页码，默认1
 * @param {Number} params.pageSize 每页条数，默认10
 * @param {String} params.areaId 区域ID（可选）
 * @param {String} params.roomId 教室ID（可选）
 * @param {String} params.basicInfo 基础信息（姓名/工号/联系方式，可选）
 * @param {String} params.startTime 开始时间（可选）
 * @param {String} params.endTime 结束时间（可选）
 * @param {String} params.openMethod 开门方式（可选）
 * @param {String} params.accessType 通行类型（可选）
 * @returns {Promise}
 */
export const getAccessRecords = (params) => {
  return request.post('/api/room-booking/access-records', params)
}

/**
 * 导出教室借用记录
 * @param {Object} params 导出参数
 * @param {String} params.exportType 导出类型：current-当前页，all-全部页
 * @param {Number} params.pageNum 页码（exportType为current时必填）
 * @param {Number} params.pageSize 每页条数（exportType为current时必填）
 * @param {String} params.areaId 区域ID（可选）
 * @param {String} params.roomId 教室ID（可选）
 * @param {String} params.basicInfo 基础信息（可选）
 * @param {String} params.startTime 开始时间（可选）
 * @param {String} params.endTime 结束时间（可选）
 * @param {String} params.openMethod 开门方式（可选）
 * @param {String} params.accessType 通行类型（可选）
 * @returns {Promise}
 */
export const exportAccessRecords = (params) => {
  return request.post('/api/room-booking/access-records/export', params)
}

/**
 * 获取教室借用统计信息
 * @param {Object} params 查询参数
 * @param {String} params.startTime 开始时间（可选）
 * @param {String} params.endTime 结束时间（可选）
 * @param {String} params.areaId 区域ID（可选）
 * @returns {Promise}
 */
export const getAccessStats = (params = {}) => {
  return request.get('/api/room-booking/access-records/stats', { params })
}

/**
 * 获取教室实时使用状态
 * @param {String} areaId 区域ID（可选）
 * @returns {Promise}
 */
export const getRoomStatus = (areaId) => {
  const params = areaId ? { areaId } : {}
  return request.get('/api/room-booking/access-records/room-status', { params })
}

// ==================== 常量定义 ====================

// 审批状态
export const APPROVAL_STATUS = {
  PENDING: '审核中',
  APPROVED: '通过',
  REJECTED: '拒绝',
  CANCELLED: '已取消'
}

// 使用状态
export const USAGE_STATUS = {
  NOT_STARTED: '未开始',
  IN_PROGRESS: '进行中',
  COMPLETED: '已结束',
  CANCELLED: '已取消'
}

// 紧急程度
export const URGENCY_LEVELS = {
  NORMAL: '普通',
  URGENT: '紧急',
  VERY_URGENT: '非常紧急'
}

// 申请人类型
export const APPLICANT_TYPES = {
  TEACHER: '教师',
  STUDENT: '学生',
  ADMIN: '管理员'
}

// 开门方式
export const OPEN_METHODS = {
  CARD: '刷卡',
  FACE: '人脸识别',
  BUTTON: '按钮'
}

// 通行类型
export const ACCESS_TYPES = {
  RESERVATION: '预约权限',
  PERMANENT: '永久权限'
}