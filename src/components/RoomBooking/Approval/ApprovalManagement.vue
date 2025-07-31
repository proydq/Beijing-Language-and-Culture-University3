<template>
  <div class="approval-management">
    <div class="approval-layout">
      <!-- 左侧审批类型菜单 -->
      <Sidebar
        title="审批类型"
        :menu-items="approvalMenuItems"
        :active-item="activeApprovalType"
        @menu-click="setActiveApprovalType"
      />

      <!-- 主内容区域 -->
      <div class="main-content">
        <!-- 搜索过滤器 -->
        <div class="search-filters">
          <el-form :model="searchForm" inline>
            <el-form-item label="预约名称">
              <el-input v-model="searchForm.name" placeholder="请输入信息" />
            </el-form-item>
            <el-form-item label="预约人">
              <el-input v-model="searchForm.applicant" placeholder="请输入信息" />
            </el-form-item>
            <el-form-item label="预约时间">
              <el-date-picker v-model="searchForm.dateRange" type="daterange" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 审批表格 -->
        <ApprovalTable
          :approval-data="filteredApprovalData"
          :loading="loading"
          @review="openReview"
          @view="openDetail"
        />

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 审批对话框 -->
    <ApprovalDialog
      v-model="dialogVisible"
      :approval-data="currentApproval"
      @confirm="handleApprovalConfirm"
      @cancel="handleApprovalCancel"
    />
    <ReservationDetailDialog
      v-model="detailDialogVisible"
      :detail="reservationDetail"
      :show-cancel-reservation="false"
      width="80%"
    />
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Sidebar from '../Layout/Sidebar.vue'
import ApprovalTable from './ApprovalTable.vue'
import ApprovalDialog from './ApprovalDialog.vue'
import ReservationDetailDialog from '../Booking/ReservationDetailDialog.vue'
import { 
  getPendingApprovals, 
  getAllApprovals, 
  approveBooking,
  getBookingDetail,
  getApprovalHistory,
  APPROVAL_STATUS_MAP,
  APPROVAL_ACTIONS
} from '@/api/approvalManagement'

export default {
  name: 'ApprovalManagement',
  components: {
    Sidebar,
    ApprovalTable,
    ApprovalDialog,
    ReservationDetailDialog,
  },
  props: {
    approvalData: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['approve', 'reject'],
  setup(props, { emit }) {
    // 审批类型菜单
    const approvalMenuItems = [
      { key: 'all', label: '全部审批', icon: 'DocumentChecked' },
      { key: 'pending', label: '待审批', icon: 'Clock' },
      { key: 'approved', label: '已通过', icon: 'CircleCheck' },
      { key: 'rejected', label: '已拒绝', icon: 'CircleClose' },
    ]

    const activeApprovalType = ref('pending')
    const loading = ref(false)

    // 搜索过滤器
    const searchForm = reactive({
      name: '',
      applicant: '',
      dateRange: [],
    })

    // 分页
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0,
    })

    // 审批数据
    const approvalData = ref([])

    // 对话框相关
    const dialogVisible = ref(false)
    const currentApproval = ref(null)

    // 模拟审批数据
    const mockApprovalData = ref([
      {
        id: 1,
        bookingName: '【教师培训】新教师入职培训会议',
        applicant: '张老师',
        applicantType: '教师',
        roomName: '多媒体教室（101）',
        bookingTime: '2025-07-20 09:00-12:00',
        applyTime: '2025-07-16 14:30:00',
        reason: '新教师入职培训，需要使用多媒体设备进行培训演示',
        status: 'PENDING',
        urgency: '普通',
      },
      {
        id: 2,
        bookingName: '【学生活动】计算机社团技术分享会',
        applicant: '李同学',
        applicantType: '学生',
        roomName: '多媒体教室（102）',
        bookingTime: '2025-07-21 14:00-17:00',
        applyTime: '2025-07-15 10:20:00',
        reason: '计算机社团定期技术分享活动，邀请行业专家进行技术讲座',
        status: 'PENDING',
        urgency: '普通',
      },
      {
        id: 3,
        bookingName: '【会议】部门月度工作总结会',
        applicant: '王主任',
        applicantType: '管理员',
        roomName: '会议室A',
        bookingTime: '2025-07-25 15:00-17:00',
        applyTime: '2025-07-14 16:45:00',
        reason: '部门月度工作总结和下月工作计划讨论',
        status: 'APPROVED',
        urgency: '紧急',
      },
    ])

    // 过滤审批数据
    const filteredApprovalData = computed(() => {
      return approvalData.value.map((item) => ({
        ...item,
        name: item.bookingName,
        cycle: item.bookingPeriod || item.bookingTime,
        description: item.reason || item.description,
        applicantName: item.applicantName,
        status: item.approvalStatus
      }))
    })

    const setActiveApprovalType = async (type) => {
      activeApprovalType.value = typeof type === 'object' ? type.key : type
      pagination.currentPage = 1
      await loadApprovalData()
    }

    const handleSearch = async () => {
      pagination.currentPage = 1
      await loadApprovalData()
    }

    const handleReset = async () => {
      Object.keys(searchForm).forEach((key) => {
        searchForm[key] = key === 'dateRange' ? [] : ''
      })
      pagination.currentPage = 1
      await loadApprovalData()
    }

    const handleSizeChange = async (val) => {
      pagination.pageSize = val
      pagination.currentPage = 1
      await loadApprovalData()
    }

    const handleCurrentChange = async (val) => {
      pagination.currentPage = val
      await loadApprovalData()
    }

    // 审批相关操作
    const openReview = (row) => {
      currentApproval.value = row
      dialogVisible.value = true
    }

    // 加载审批数据
    const loadApprovalData = async () => {
      try {
        loading.value = true
        
        // 构建请求参数
        const params = {
          pageNumber: pagination.currentPage,
          pageSize: pagination.pageSize,
          reservationName: searchForm.name || undefined,
          applicantName: searchForm.applicant || undefined,
          startDate: searchForm.dateRange.length > 0 ? searchForm.dateRange[0] : undefined,
          endDate: searchForm.dateRange.length > 1 ? searchForm.dateRange[1] : undefined
        }
        
        let response
        if (activeApprovalType.value === 'pending') {
          // 获取待审批列表
          response = await getPendingApprovals(params)
        } else {
          // 获取全部审批列表
          const statusMap = {
            all: undefined,
            pending: 'PENDING',
            approved: 'APPROVED',
            rejected: 'REJECTED'
          }
          params.approvalStatus = statusMap[activeApprovalType.value]
          response = await getAllApprovals(params)
        }
        
        if (response.code === 200) {
          approvalData.value = response.data.rows || []
          pagination.total = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取审批数据失败')
        }
      } catch (error) {
        console.error('获取审批数据失败:', error)
        ElMessage.error('获取审批数据失败')
      } finally {
        loading.value = false
      }
    }

    const handleApprovalConfirm = async (result) => {
      try {
        loading.value = true
        
        const approvalRequest = {
          action: result.result, // APPROVED 或 REJECTED
          comment: result.comment
        }
        
        const response = await approveBooking(currentApproval.value.id, approvalRequest)
        
        if (response.code === 200) {
          ElMessage.success('审批操作成功')
          // 重新加载数据
          await loadApprovalData()
          emit('approve', { ...currentApproval.value, ...result })
        } else {
          ElMessage.error(response.message || '审批操作失败')
        }
      } catch (error) {
        console.error('审批操作失败:', error)
        ElMessage.error('审批操作失败')
      } finally {
        loading.value = false
        dialogVisible.value = false
      }
    }

    const handleApprovalCancel = () => {
      dialogVisible.value = false
      currentApproval.value = null
    }

    const detailDialogVisible = ref(false)
    const reservationDetail = ref({})

    const bookingBaseInfo = {
      1: {
        reservationName: '【活动1】的教室借用',
        applicant: '王鹏',
        reservationPeriod: '2025.08.24 星期四 第三节次',
        description: '班级活动使用，需使用投影设备',
        participants: '张三, 李四',
        remark: '需要提前布置',
      },
      2: {
        reservationName: '【学生会】定期会议',
        applicant: '李明',
        reservationPeriod: '2025.08.25 星期五 第五节次',
        description: '学生组织定期内部会议',
        participants: '刘强, 陈伟',
        remark: '无',
      },
      3: {
        reservationName: '【外聘讲座】演讲厅借用',
        applicant: '赵敏',
        reservationPeriod: '2025.08.20 星期一 第九节次',
        description: '外聘教授举办讲座，要求提前布场',
        participants: '张三, 李四, 王五',
        remark: '讲座需准备扩音设备',
      },
      4: {
        reservationName: '【活动八定名】的教室借用',
        applicant: '王鹏',
        reservationPeriod: '2025.04.24 第四节次',
        description: '实验班借用智慧教室用于演示活动',
        participants: '李四, 王五',
        remark: '活动已取消',
      },
    }

    const auditDetailData = {
      1: [
        {
          levelName: '自动审批',
          approvers: ['系统'],
          confirmedApprover: '系统',
          approvalTime: '2025-07-21 10:12:33',
          comment: '系统自动通过',
        },
      ],
      2: [
        {
          levelName: '自动审批',
          approvers: ['系统'],
          confirmedApprover: '系统',
          approvalTime: '2025-08-21 09:00',
          comment: '系统自动通过',
        },
        {
          levelName: '一级审批',
          approvers: ['赵主管', '钱经理'],
          confirmedApprover: '钱经理',
          approvalTime: '2025-08-22 12:00',
          comment: '同意：排课正常，无异议',
        },
      ],
      3: [
        {
          levelName: '自动审批',
          approvers: ['系统'],
          confirmedApprover: '系统',
          approvalTime: '2025-08-15 08:00',
          comment: '系统自动通过',
        },
        {
          levelName: '一级审批',
          approvers: ['赵主管'],
          confirmedApprover: '赵主管',
          approvalTime: '2025-08-16 09:30',
          comment: '同意：排课正常，无异议',
        },
      ],
      4: [
        {
          levelName: '自动审批',
          approvers: ['系统'],
          confirmedApprover: '系统',
          approvalTime: '2025-04-20 09:00',
          comment: '系统自动通过',
        },
        {
          levelName: '一级审批',
          approvers: ['赵主管'],
          confirmedApprover: '赵主管',
          approvalTime: '2025-04-21 11:00',
          comment: '拒绝：活动已取消',
        },
      ],
    }

    const openDetail = (row) => {
      const base = bookingBaseInfo[row.id] || {}
      reservationDetail.value = {
        userName: base.applicant || row.applicant,
        reservationTitle: base.reservationName || row.bookingName,
        borrowTime: base.reservationPeriod || row.bookingTime,
        borrowDesc: base.description || row.reason,
        participants: base.participants ? base.participants.split(', ') : [],
        remark: base.remark || '',
        approvalSteps: auditDetailData[row.id] || [],
      }
      detailDialogVisible.value = true
    }

    onMounted(async () => {
      // 初始化加载数据
      console.log('审批管理模块初始化')
      await loadApprovalData()
    })

    return {
      approvalMenuItems,
      activeApprovalType,
      loading,
      searchForm,
      pagination,
      filteredApprovalData,
      dialogVisible,
      currentApproval,
      setActiveApprovalType,
      handleSearch,
      handleReset,
      handleSizeChange,
      handleCurrentChange,
      openReview,
      handleApprovalConfirm,
      handleApprovalCancel,
      detailDialogVisible,
      reservationDetail,
      openDetail,
      loadApprovalData
    }
  },
}
</script>

<style scoped>
.approval-management {
  min-height: calc(100vh - 120px);
  background: #f9f9f9;
}

.approval-layout {
  display: flex;
  height: calc(100vh - 120px);
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.search-filters {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 200px;
}

.filter-item label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>
