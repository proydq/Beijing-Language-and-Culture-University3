<template>
  <div class="room-booking">
    <!-- 顶部导航栏 -->
    <RoomBookingHeader
      @profile="handleProfile"
      @logout="handleLogout"
    />

    <!-- 主导航菜单 -->
    <MainNavigation
      v-model="activeMainTab"
    />

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 数据看板 -->
      <DashboardContent
        v-if="activeMainTab === 'dashboard'"
        :stats="stats"
        :distribution-data="distributionData"
        :trend-data="trendData"
        :loading="loading"
        @time-range-change="handleTimeRangeChange"
        @custom-range-change="handleCustomRangeChange"
      />

      <!-- 借用管理 -->
      <BookingManagement
        v-else-if="activeMainTab === 'booking'"
        :booking-data="bookingData"
        :all-booking-data="allBookingData"
        :rooms="rooms"
        @edit="handleEdit"
        @approve="handleApprove"
        @book-room="handleBookRoom"
      />

      <!-- 审批管理 -->
      <ApprovalManagement
        v-else-if="activeMainTab === 'approval'"
        :approval-data="approvalData"
        @approve="handleApprovalSubmit"
        @reject="handleApprovalReject"
      />

      <!-- 数据记录 -->
      <RecordsManagement
        v-else-if="activeMainTab === 'records'"
        :records-data="recordsData"
      />

      <!-- 设置 -->
      <SettingsManagement
        v-else-if="activeMainTab === 'settings'"
        :settings-data="settingsData"
        @save-settings="handleSaveSettings"
      />

      <!-- 默认占位符 -->
      <div v-else class="content-placeholder">
        <el-icon size="64" color="#ccc"><document /></el-icon>
        <p>请从导航菜单选择要使用的功能模块</p>
      </div>
    </div>

    <BookRoomDialog
      v-model="bookDialogVisible"
      :room="selectedRoom"
      @confirm="handleBookingConfirm"
      @cancel="handleBookingCancel"
    />
  </div>
</template>

<script>
import { ref, onMounted, defineAsyncComponent } from 'vue'
import { ElMessage } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import { useRoomBooking } from '@/composables/useRoomBooking'

// 导入拆分后的组件
import RoomBookingHeader from '@/components/RoomBooking/Layout/RoomBookingHeader.vue'
import MainNavigation from '@/components/RoomBooking/Layout/MainNavigation.vue'
import DashboardContent from '@/components/RoomBooking/Dashboard/DashboardContent.vue'
import BookingManagement from '@/components/RoomBooking/Booking/BookingManagement.vue'
import ApprovalManagement from '@/components/RoomBooking/Approval/ApprovalManagement.vue'
import BookRoomDialog from '@/components/RoomBooking/Booking/BookRoomDialog.vue'

// 延迟加载其他组件（可选优化）
const RecordsManagement = defineAsyncComponent(() => import('@/components/RoomBooking/Records/RecordsManagement.vue'))
const SettingsManagement = defineAsyncComponent(() => import('@/components/RoomBooking/Settings/SettingsManagement.vue'))

export default {
  name: 'RoomBooking',
  components: {
    RoomBookingHeader,
    MainNavigation,
    DashboardContent,
    BookingManagement,
    ApprovalManagement,
    BookRoomDialog,
    RecordsManagement,
    SettingsManagement,
    Document
  },
  setup() {
    // 使用房屋借用管理组合式函数
    const {
      loading,
      stats,
      getBookingStats,
      getBookingDistribution,
      getBookingTrend,
      getBookingTrendCustom
    } = useRoomBooking()

    // 主导航状态
    const activeMainTab = ref('dashboard')
    const bookDialogVisible = ref(false)
    const selectedRoom = ref(null)

    // 数据分布和趋势数据
    const distributionData = ref([])
    const trendData = ref([])

    const bookingData = ref([
      {
        id: 1,
        reservationName: '【活动1】的教室借用',
        reservationPeriod: '2025.08.24 星期四 第三节次',
        description: '班级活动使用，需使用投影设备',
        applicantName: '王鹏',
        roomName: '多媒体教室（101）',
        approvalStatus: '审核中',
        usageStatus: '未开始'
      },
      {
        id: 2,
        reservationName: '【学生会】定期会议',
        reservationPeriod: '2025.08.25 星期五 第五节次',
        description: '学生组织定期内部会议',
        applicantName: '李明',
        roomName: '智慧教室（202）',
        approvalStatus: '通过',
        usageStatus: '未开始'
      },
        {
          id: 3,
          reservationName: '【外聘讲座】演讲厅借用',
          reservationPeriod: '2025.08.20 星期一 第九节次',
          description: '外聘教授举办讲座，要求提前布场',
          applicantName: '赵敏',
          roomName: '演讲厅（301）',
          approvalStatus: '通过',
          usageStatus: '已结束'
        },
        {
          id: 4,
          reservationName: '【活动八定名】的教室借用',
          reservationPeriod: '2025.04.24 第四节次',
          description: '实验班借用智慧教室用于演示活动',
          applicantName: '王鹏',
          roomName: '多媒体教室（101）',
          approvalStatus: '拒绝',
          usageStatus: '/'
        },
        {
          id: 5,
          reservationName: '【活动八定名】的教室借用',
          reservationPeriod: '2025.04.24 第四节次',
          description: '实验班借用智慧教室用于演示活动',
          applicantName: '王鹏',
          roomName: '多媒体教室（101）',
          approvalStatus: '通过',
          usageStatus: '已结束'
        },
        {
          id: 6,
          reservationName: '【活动八定名】的教室借用',
          reservationPeriod: '2025.04.24 第四节次',
          description: '实验班借用智慧教室用于演示活动',
          applicantName: '王鹏',
          roomName: '多媒体教室（101）',
          approvalStatus: '已取消',
          usageStatus: '/'
        }
      ])

    // 全部借用样例数据，包含审核中、通过、拒绝、已取消
    const allBookingData = ref([
      {
        id: 1,
        bookingName: '【教师人员专】的配置管理',
        bookingTime: '2025.04.24 第一节次、第二节次、第三节次、第四节次',
        description: '各类通过这里发布各类信息进行各类信息',
        applicant: '张老师',
        roomName: '多媒体教室（101）',
        auditStatus: '审核中',
        useStatus: '未开始'
      },
      {
        id: 2,
        bookingName: '【学生活动】社团会议',
        bookingTime: '2025.04.25 第一节次、第二节次',
        description: '学生社团定期会议活动安排',
        applicant: '李同学',
        roomName: '多媒体教室（102）',
        auditStatus: '通过',
        useStatus: '未开始'
      },
      {
        id: 3,
        bookingName: '【教师培训】新教师培训',
        bookingTime: '2025.04.26 第一节次、第二节次、第三节次',
        description: '新入职教师培训活动',
        applicant: '王主任',
        roomName: '多媒体教室（103）',
        auditStatus: '拒绝',
        useStatus: '进行中'
      },
      {
        id: 4,
        bookingName: '【活动撤销】教室预约',
        bookingTime: '2025.04.28 第二节次',
        description: '社团活动取消，撤销预约',
        applicant: '张红',
        roomName: '多媒体教室（105）',
        auditStatus: '已取消',
        useStatus: '未开始'
      }
    ])

    const rooms = ref([
      {
        id: 1,
        name: '多媒体教室（101）',
        capacity: '未设置',
        available: false,
        building: '达力楼',
        floor: '1F'
      },
      {
        id: 2,
        name: '多媒体教室（102）',
        capacity: '20人',
        available: true,
        building: '达力楼',
        floor: '1F'
      },
      {
        id: 3,
        name: '多媒体教室（103）',
        capacity: '20人',
        available: true,
        building: '达力楼',
        floor: '1F'
      },
      {
        id: 4,
        name: '多媒体教室（104）',
        capacity: '20人',
        available: true,
        building: '达力楼',
        floor: '1F'
      },
      {
        id: 5,
        name: '多媒体教室（105）',
        capacity: '20人',
        available: true,
        building: '达力楼',
        floor: '2F'
      }
    ])

    const approvalData = ref([
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
        bookingName: '【部门会议】月度总结',
        applicant: '王主任',
        applicantType: '管理员',
        roomName: '会议室A',
        bookingTime: '2025-07-25 15:00-17:00',
        applyTime: '2025-07-14 16:45:00',
        reason: '部门月度工作总结和计划讨论',
        status: 'APPROVED',
        urgency: '紧急'
      },
      {
        id: 4,
        bookingName: '【社团活动】临时聚会',
        applicant: '赵敏',
        applicantType: '学生',
        roomName: '多媒体教室（103）',
        bookingTime: '2025-07-22 13:00-15:00',
        applyTime: '2025-07-15 09:00:00',
        reason: '临时聚会讨论新活动方案',
        status: 'REJECTED',
        urgency: '普通'
      }
    ])

    const recordsData = ref([
      {
        id: 1,
        title: '【教师培训】多媒体教室预约成功',
        type: 'booking_approved',
        operator: '张老师',
        createTime: '2025-07-16 14:30:00',
        status: '成功',
        content: '张老师成功预约多媒体教室（101），用于新教师入职培训'
      },
      {
        id: 2,
        title: '【学生活动】会议室预约被拒绝',
        type: 'booking_rejected',
        operator: '李同学',
        createTime: '2025-07-16 10:15:00',
        status: '失败',
        content: '李同学预约会议室A被拒绝，原因：时间冲突'
      }
    ])

    const settingsData = ref({
      systemName: '房屋借用管理系统',
      version: 'v1.0.0',
      adminEmail: 'admin@example.com'
    })

    // 事件处理函数
    const handleProfile = () => {
      ElMessage.info('跳转到个人中心')
    }

    const handleLogout = () => {
      ElMessage.info('退出登录')
    }

    const handleTimeRangeChange = async (timeRange) => {
      console.log('时间范围变更:', timeRange)
      // 重新加载统计数据
      await loadStats(timeRange)
    }

    const handleCustomRangeChange = async (startDate, endDate) => {
      console.log('自定义时间范围变更:', startDate, endDate)
      // 加载自定义时间范围的数据
      await loadCustomRangeStats(startDate, endDate)
    }

    const handleEdit = (row) => {
      ElMessage.info(`编辑预约: ${row.bookingName}`)
    }

    const handleApprove = (row) => {
      ElMessage.info(`审核预约: ${row.bookingName}`)
    }

    const handleBookRoom = (room) => {
      selectedRoom.value = room
      bookDialogVisible.value = true
    }

    const handleBookingConfirm = (data) => {
      ElMessage.success('预约提交成功')
      bookDialogVisible.value = false
      selectedRoom.value = null
    }

    const handleBookingCancel = () => {
      bookDialogVisible.value = false
      selectedRoom.value = null
    }

    const handleApprovalSubmit = (approval) => {
      ElMessage.success('审批通过')
    }

    const handleApprovalReject = (approval) => {
      ElMessage.error('审批拒绝')
    }

    const handleSaveSettings = (settings) => {
      ElMessage.success('设置保存成功')
      console.log('保存设置:', settings)
    }

    // 数据加载函数
    const loadStats = async (timeRange = null) => {
      try {
        let startTime = null
        let endTime = null

        // 根据时间范围计算开始和结束时间
        if (timeRange) {
          const now = new Date()
          endTime = now

          switch (timeRange) {
            case '近7天':
              startTime = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000)
              break
            case '近15天':
              startTime = new Date(now.getTime() - 15 * 24 * 60 * 60 * 1000)
              break
            case '近30天':
              startTime = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000)
              break
            case '近90天':
              startTime = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000)
              break
          }
        }

        // 加载统计数据
        await getBookingStats(startTime, endTime)

        // 加载数据分布
        const distribution = await getBookingDistribution(startTime, endTime)
        distributionData.value = distribution

        // 加载趋势数据
        let trend = []
        if (timeRange) {
          const days = timeRange === '近7天' ? 7 :
                      timeRange === '近15天' ? 15 :
                      timeRange === '近30天' ? 30 : 90
          trend = await getBookingTrend(days)
        } else {
          // 默认加载近15天的趋势数据
          trend = await getBookingTrend(15)
        }
        trendData.value = trend

        console.log('统计数据加载完成', { stats: stats.value, distribution, trend })
      } catch (error) {
        console.error('加载统计数据失败:', error)
        ElMessage.error('加载统计数据失败')
      }
    }

    // 加载自定义时间范围的数据
    const loadCustomRangeStats = async (startDate, endDate) => {
      try {
        const startTime = new Date(startDate)
        const endTime = new Date(endDate)

        // 加载统计数据
        await getBookingStats(startTime, endTime)

        // 加载数据分布
        const distribution = await getBookingDistribution(startTime, endTime)
        distributionData.value = distribution

        // 加载趋势数据
        const trend = await getBookingTrendCustom(startTime, endTime)
        trendData.value = trend

        console.log('自定义时间范围数据加载完成', { stats: stats.value, distribution, trend })
      } catch (error) {
        console.error('加载自定义时间范围数据失败:', error)
        ElMessage.error('加载数据失败')
      }
    }

    const loadBookingData = async () => {
      try {
        // 这里调用API加载预约数据
        console.log('加载预约数据')
      } catch (error) {
        ElMessage.error('加载预约数据失败')
      }
    }

    // 初始化
    onMounted(() => {
      loadStats()
      loadBookingData()
    })

    return {
      activeMainTab,
      loading,
      stats,
      distributionData,
      trendData,
      bookingData,
      allBookingData,
      rooms,
      approvalData,
      recordsData,
      settingsData,
      handleProfile,
      handleLogout,
      handleTimeRangeChange,
      handleCustomRangeChange,
      handleEdit,
      handleApprove,
      handleBookRoom,
      handleBookingConfirm,
      handleBookingCancel,
      handleApprovalSubmit,
      handleApprovalReject,
      handleSaveSettings,
      bookDialogVisible,
      selectedRoom
    }
  }
}
</script>

<style scoped>
.room-booking {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.main-content {
  flex: 1;
  min-height: calc(100vh - 120px);
}

.content-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #999;
  background: white;
  margin: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
