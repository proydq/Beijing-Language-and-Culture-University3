import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

/**
 * 房屋借用管理组合式函数
 */
export function useRoomBooking() {
  // 响应式数据
  const loading = ref(false)
  const bookingList = ref([])
  const currentBooking = ref(null)
  const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0
  })

  // 查询条件
  const searchCondition = reactive({
    bookingName: '',
    applicantName: '',
    applicantId: '',
    roomId: '',
    approvalStatus: null,
    usageStatus: null,
    urgency: null,
    startTimeFrom: null,
    startTimeTo: null,
    createTimeFrom: null,
    createTimeTo: null,
    isCancelled: null,
    pageNumber: 1,
    pageSize: 10
  })

  // 统计数据
  const stats = ref({
    totalBookings: 0,
    teacherBookings: 0,
    studentBookings: 0,
    pendingApprovals: 0,
    approvedBookings: 0,
    rejectedBookings: 0,
    ongoingBookings: 0,
    completedBookings: 0,
    successRate: 0
  })

  // API 基础路径
  const API_BASE = '/api/room-booking'

  /**
   * 分页查询预约列表
   */
  const searchBookings = async (condition = {}) => {
    loading.value = true
    try {
      const params = {
        ...searchCondition,
        ...condition,
        pageNumber: pagination.current,
        pageSize: pagination.pageSize
      }

      const response = await request.post(`${API_BASE}/search`, params)

      if (response.data.code === 200) {
        const data = response.data.data
        bookingList.value = data.content || []
        pagination.total = data.totalElements || 0
        pagination.current = data.number + 1 || 1
      } else {
        ElMessage.error(response.data.message || '查询失败')
      }
    } catch (error) {
      console.error('查询预约列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  /**
   * 根据ID查询预约详情
   */
  const getBookingById = async (id) => {
    loading.value = true
    try {
      const response = await request.get(`${API_BASE}/${id}`)

      if (response.data.code === 200) {
        currentBooking.value = response.data.data
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return null
      }
    } catch (error) {
      console.error('查询预约详情失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 创建预约
   */
  const createBooking = async (bookingData) => {
    loading.value = true
    try {
      const response = await request.post(API_BASE, bookingData)

      if (response.data.code === 200) {
        ElMessage.success('预约创建成功')
        await searchBookings() // 刷新列表
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '创建失败')
        return null
      }
    } catch (error) {
      console.error('创建预约失败:', error)
      ElMessage.error('创建失败，请稍后重试')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新预约
   */
  const updateBooking = async (id, bookingData) => {
    loading.value = true
    try {
      const response = await request.put(`${API_BASE}/${id}`, bookingData)

      if (response.data.code === 200) {
        ElMessage.success('预约更新成功')
        await searchBookings() // 刷新列表
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '更新失败')
        return null
      }
    } catch (error) {
      console.error('更新预约失败:', error)
      ElMessage.error('更新失败，请稍后重试')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 删除预约
   */
  const deleteBooking = async (id) => {
    try {
      await ElMessageBox.confirm('确定要删除这个预约吗？', '确认删除', {
        type: 'warning'
      })

      loading.value = true
      const response = await request.delete(`${API_BASE}/${id}`)

      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        await searchBookings() // 刷新列表
        return true
      } else {
        ElMessage.error(response.data.message || '删除失败')
        return false
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除预约失败:', error)
        ElMessage.error('删除失败，请稍后重试')
      }
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 批量删除预约
   */
  const deleteBookings = async (ids) => {
    try {
      await ElMessageBox.confirm(`确定要删除选中的 ${ids.length} 个预约吗？`, '确认删除', {
        type: 'warning'
      })

      loading.value = true
      const response = await request.delete(`${API_BASE}/batch`, { data: ids })

      if (response.data.code === 200) {
        ElMessage.success('批量删除成功')
        await searchBookings() // 刷新列表
        return true
      } else {
        ElMessage.error(response.data.message || '删除失败')
        return false
      }
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除预约失败:', error)
        ElMessage.error('删除失败，请稍后重试')
      }
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 取消预约
   */
  const cancelBooking = async (id, cancelReason) => {
    loading.value = true
    try {
      const response = await request.put(`${API_BASE}/${id}/cancel`, null, {
        params: { cancelReason }
      })

      if (response.data.code === 200) {
        ElMessage.success('预约取消成功')
        await searchBookings() // 刷新列表
        return true
      } else {
        ElMessage.error(response.data.message || '取消失败')
        return false
      }
    } catch (error) {
      console.error('取消预约失败:', error)
      ElMessage.error('取消失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 审批预约
   */
  const approveBooking = async (id, approvalStatus, approvalComment = '') => {
    loading.value = true
    try {
      const response = await request.put(`${API_BASE}/${id}/approve`, null, {
        params: { approvalStatus, approvalComment }
      })

      if (response.data.code === 200) {
        const action = approvalStatus === 'APPROVED' ? '通过' : '拒绝'
        ElMessage.success(`审批${action}成功`)
        await searchBookings() // 刷新列表
        return true
      } else {
        ElMessage.error(response.data.message || '审批失败')
        return false
      }
    } catch (error) {
      console.error('审批预约失败:', error)
      ElMessage.error('审批失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 检查时间冲突
   */
  const checkTimeConflict = async (roomId, startTime, endTime, excludeBookingId = null) => {
    try {
      const params = {
        roomId,
        startTime: startTime.toISOString().slice(0, 19).replace('T', ' '),
        endTime: endTime.toISOString().slice(0, 19).replace('T', ' ')
      }

      if (excludeBookingId) {
        params.excludeBookingId = excludeBookingId
      }

      const response = await request.get(`${API_BASE}/check-conflict`, { params })

      if (response.data.code === 200) {
        return response.data.data
      } else {
        console.error('检查时间冲突失败:', response.data.message)
        return false
      }
    } catch (error) {
      console.error('检查时间冲突失败:', error)
      return false
    }
  }

  /**
   * 获取我的预约列表
   */
  const getMyBookings = async (applicantId) => {
    loading.value = true
    try {
      const response = await request.get(`${API_BASE}/my-bookings`, {
        params: { applicantId }
      })

      if (response.data.code === 200) {
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('获取我的预约列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取待审批预约列表
   */
  const getPendingApprovals = async () => {
    loading.value = true
    try {
      const response = await request.get(`${API_BASE}/pending-approvals`)

      if (response.data.code === 200) {
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('获取待审批预约列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取预约统计数据
   */
  const getBookingStats = async (startTime = null, endTime = null) => {
    loading.value = true
    try {
      const params = {}
      if (startTime) {
        params.startTime = startTime.toISOString().slice(0, 19).replace('T', ' ')
      }
      if (endTime) {
        params.endTime = endTime.toISOString().slice(0, 19).replace('T', ' ')
      }

      const response = await request.get(`${API_BASE}/stats`, { params })

      if (response.data.code === 200) {
        stats.value = response.data.data
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return null
      }
    } catch (error) {
      console.error('获取预约统计数据失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return null
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取借用数据分布（饼图数据）
   */
  const getBookingDistribution = async (startTime = null, endTime = null) => {
    loading.value = true
    try {
      const params = {}
      if (startTime) {
        params.startTime = startTime.toISOString().slice(0, 19).replace('T', ' ')
      }
      if (endTime) {
        params.endTime = endTime.toISOString().slice(0, 19).replace('T', ' ')
      }

      const response = await request.get(`${API_BASE}/distribution`, { params })

      if (response.data.code === 200) {
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('获取借用数据分布失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取借用趋势数据（快捷天数选择）
   */
  const getBookingTrend = async (days) => {
    loading.value = true
    try {
      const response = await request.get(`${API_BASE}/trend`, {
        params: { days }
      })

      if (response.data.code === 200) {
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('获取借用趋势数据失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取自定义时间范围的借用趋势数据
   */
  const getBookingTrendCustom = async (startTime, endTime) => {
    loading.value = true
    try {
      const params = {
        startTime: startTime.toISOString().slice(0, 19).replace('T', ' '),
        endTime: endTime.toISOString().slice(0, 19).replace('T', ' ')
      }

      const response = await request.get(`${API_BASE}/trend/custom`, { params })

      if (response.data.code === 200) {
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('获取自定义趋势数据失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新预约使用状态
   */
  const updateUsageStatus = async (id, usageStatus) => {
    loading.value = true
    try {
      const response = await request.put(`${API_BASE}/${id}/usage-status`, null, {
        params: { usageStatus }
      })

      if (response.data.code === 200) {
        ElMessage.success('状态更新成功')
        await searchBookings() // 刷新列表
        return true
      } else {
        ElMessage.error(response.data.message || '更新失败')
        return false
      }
    } catch (error) {
      console.error('更新预约使用状态失败:', error)
      ElMessage.error('更新失败，请稍后重试')
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * 根据房间ID获取预约列表
   */
  const getBookingsByRoom = async (roomId) => {
    loading.value = true
    try {
      const response = await request.get(`${API_BASE}/by-room/${roomId}`)

      if (response.data.code === 200) {
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('根据房间ID获取预约列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 检查预约是否可以编辑
   */
  const canEditBooking = async (id) => {
    try {
      const response = await request.get(`${API_BASE}/${id}/can-edit`)
      return response.data.code === 200 ? response.data.data : false
    } catch (error) {
      console.error('检查预约是否可以编辑失败:', error)
      return false
    }
  }

  /**
   * 检查预约是否可以取消
   */
  const canCancelBooking = async (id) => {
    try {
      const response = await request.get(`${API_BASE}/${id}/can-cancel`)
      return response.data.code === 200 ? response.data.data : false
    } catch (error) {
      console.error('检查预约是否可以取消失败:', error)
      return false
    }
  }

  /**
   * 重置查询条件
   */
  const resetSearchCondition = () => {
    Object.assign(searchCondition, {
      bookingName: '',
      applicantName: '',
      applicantId: '',
      roomId: '',
      approvalStatus: null,
      usageStatus: null,
      urgency: null,
      startTimeFrom: null,
      startTimeTo: null,
      createTimeFrom: null,
      createTimeTo: null,
      isCancelled: null,
      pageNumber: 1,
      pageSize: 10
    })
    pagination.current = 1
  }

  /**
   * 处理分页变化
   */
  const handlePageChange = (page) => {
    pagination.current = page
    searchBookings()
  }

  /**
   * 处理页面大小变化
   */
  const handlePageSizeChange = (size) => {
    pagination.pageSize = size
    pagination.current = 1
    searchBookings()
  }

  // 计算属性
  const hasData = computed(() => bookingList.value.length > 0)
  const isEmpty = computed(() => !loading.value && bookingList.value.length === 0)

  return {
    // 响应式数据
    loading,
    bookingList,
    currentBooking,
    pagination,
    searchCondition,
    stats,

    // 计算属性
    hasData,
    isEmpty,

    // 方法
    searchBookings,
    getBookingById,
    createBooking,
    updateBooking,
    deleteBooking,
    deleteBookings,
    cancelBooking,
    approveBooking,
    checkTimeConflict,
    getMyBookings,
    getPendingApprovals,
    getBookingStats,
    getBookingDistribution,
    getBookingTrend,
    getBookingTrendCustom,
    updateUsageStatus,
    getBookingsByRoom,
    canEditBooking,
    canCancelBooking,
    resetSearchCondition,
    handlePageChange,
    handlePageSizeChange
  }
}

/**
 * 预约审批管理组合式函数
 */
export function useBookingApproval() {
  const loading = ref(false)
  const approvalList = ref([])

  const API_BASE = '/api/booking-approval'

  /**
   * 根据预约ID获取审批记录
   */
  const getApprovalsByBookingId = async (bookingId) => {
    loading.value = true
    try {
      const response = await request.get(`${API_BASE}/by-booking/${bookingId}`)

      if (response.data.code === 200) {
        approvalList.value = response.data.data
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('获取审批记录失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    approvalList,
    getApprovalsByBookingId
  }
}

/**
 * 预约记录管理组合式函数
 */
export function useBookingRecord() {
  const loading = ref(false)
  const recordList = ref([])
  const recordStats = ref({})

  const API_BASE = '/api/booking-record'

  /**
   * 根据预约ID获取记录列表
   */
  const getRecordsByBookingId = async (bookingId) => {
    loading.value = true
    try {
      const response = await request.get(`${API_BASE}/by-booking/${bookingId}`)

      if (response.data.code === 200) {
        recordList.value = response.data.data
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return []
      }
    } catch (error) {
      console.error('获取记录列表失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return []
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取记录统计数据
   */
  const getRecordStats = async (startTime = null, endTime = null) => {
    loading.value = true
    try {
      const params = {}
      if (startTime) {
        params.startTime = startTime.toISOString().slice(0, 19).replace('T', ' ')
      }
      if (endTime) {
        params.endTime = endTime.toISOString().slice(0, 19).replace('T', ' ')
      }

      const response = await request.get(`${API_BASE}/stats`, { params })

      if (response.data.code === 200) {
        recordStats.value = response.data.data
        return response.data.data
      } else {
        ElMessage.error(response.data.message || '查询失败')
        return null
      }
    } catch (error) {
      console.error('获取记录统计数据失败:', error)
      ElMessage.error('查询失败，请稍后重试')
      return null
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    recordList,
    recordStats,
    getRecordsByBookingId,
    getRecordStats
  }
}
