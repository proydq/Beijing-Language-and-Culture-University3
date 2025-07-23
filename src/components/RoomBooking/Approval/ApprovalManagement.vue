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
              <el-date-picker
                v-model="searchForm.dateRange"
                type="daterange"
              />
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
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Sidebar from '../Layout/Sidebar.vue'
import ApprovalTable from './ApprovalTable.vue'
import ApprovalDialog from './ApprovalDialog.vue'

export default {
  name: 'ApprovalManagement',
  components: {
    Sidebar,
    ApprovalTable,
    ApprovalDialog
  },
  props: {
    approvalData: {
      type: Array,
      default: () => []
    }
  },
  emits: ['approve', 'reject'],
  setup(props, { emit }) {
    // 审批类型菜单
    const approvalMenuItems = [
      { key: 'all', label: '全部审批', icon: 'DocumentChecked' },
      { key: 'pending', label: '待审批', icon: 'Clock' },
      { key: 'approved', label: '已通过', icon: 'CircleCheck' },
      { key: 'rejected', label: '已拒绝', icon: 'CircleClose' }
    ]

    const activeApprovalType = ref('pending')
    const loading = ref(false)

    // 搜索过滤器
    const searchForm = reactive({
      name: '',
      applicant: '',
      dateRange: []
    })

    // 分页
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

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
        urgency: '普通'
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
        urgency: '普通'
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
        urgency: '紧急'
      }
    ])

    // 过滤审批数据
    const filteredApprovalData = computed(() => {
      let data = props.approvalData.length > 0 ? props.approvalData : mockApprovalData.value

      // 按审批类型过滤
      if (activeApprovalType.value !== 'all') {
        const statusMap = {
          pending: 'PENDING',
          approved: 'APPROVED',
          rejected: 'REJECTED'
        }
        data = data.filter(item => item.status === statusMap[activeApprovalType.value])
      }

      // 按搜索条件过滤
      if (searchForm.name) {
        data = data.filter(item => item.bookingName.includes(searchForm.name))
      }

      if (searchForm.applicant) {
        data = data.filter(item => item.applicant.includes(searchForm.applicant))
      }

      if (searchForm.dateRange && searchForm.dateRange.length === 2) {
        data = data.filter(item => {
          const applyDate = item.applyTime.split(' ')[0]
          return applyDate >= searchForm.dateRange[0] && applyDate <= searchForm.dateRange[1]
        })
      }

      pagination.total = data.length

      // 分页
      const start = (pagination.currentPage - 1) * pagination.pageSize
      const end = start + pagination.pageSize
      return data.slice(start, end).map(item => ({
        ...item,
        name: item.bookingName,
        cycle: item.bookingTime,
        description: item.reason,
        applicantName: item.applicant
      }))
    })

    const setActiveApprovalType = (type) => {
      activeApprovalType.value = typeof type === 'object' ? type.key : type
      pagination.currentPage = 1
    }

    const handleSearch = () => {
      pagination.currentPage = 1
      console.log('搜索审批:', searchForm)
    }

    const handleReset = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = key === 'dateRange' ? [] : ''
      })
      pagination.currentPage = 1
    }

    const handleSizeChange = (val) => {
      pagination.pageSize = val
      pagination.currentPage = 1
    }

    const handleCurrentChange = (val) => {
      pagination.currentPage = val
    }

    // 审批相关操作
    const openReview = (row) => {
      currentApproval.value = row
      dialogVisible.value = true
    }

    const handleApprovalConfirm = (result) => {
      loading.value = true

      setTimeout(() => {
        const index = mockApprovalData.value.findIndex(item => item.id === currentApproval.value.id)
        if (index !== -1) {
          mockApprovalData.value[index].status = result.status
        }
        ElMessage.success('操作成功')
        emit('approve', { ...currentApproval.value, ...result })

        loading.value = false
        dialogVisible.value = false
      }, 1000)
    }

    const handleApprovalCancel = () => {
      dialogVisible.value = false
      currentApproval.value = null
    }

    onMounted(() => {
      // 初始化加载数据
      console.log('审批管理模块初始化')
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
      handleApprovalCancel
    }
  }
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