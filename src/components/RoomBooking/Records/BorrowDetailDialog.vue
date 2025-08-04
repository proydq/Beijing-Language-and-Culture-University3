<template>
  <el-dialog
    v-model="visible"
    :title="`房屋名称：${roomName}（${roomCode}）`"
    width="80%"
    destroy-on-close
    center
  >
    <div class="filter-wrapper">
      <el-form :inline="true" :model="filters" class="filter-form">
        <el-form-item label="借用/预约名称">
          <el-input v-model="filters.name" placeholder="" style="width: 200px" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="filters.auditStatus" placeholder="审核状态" style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="审核中" value="审核中" />
            <el-option label="通过" value="通过" />
            <el-option label="拒绝" value="拒绝" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item label="使用状态">
          <el-select v-model="filters.usageStatus" placeholder="使用状态" style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
            <el-option label="/" value="/" />
          </el-select>
        </el-form-item>
        <el-form-item label="预约人">
          <el-input v-model="filters.applicant" placeholder="" style="width: 180px" />
        </el-form-item>
        <el-form-item label="预约时间范围">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            placeholder="请选择预约时间范围"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="export-buttons">
        <el-button type="primary" @click="exportCurrent">导出当前页</el-button>
        <el-button type="success" @click="exportAll">导出全部页</el-button>
      </div>
    </div>

    <el-table 
      v-loading="loading"
      :data="pagedData" 
      stripe 
      style="width: 100%" 
      class="record-table"
    >
      <el-table-column prop="bookingName" label="借用/预约名称" min-width="150" align="center" />
      <el-table-column prop="bookingTime" label="预约时间" min-width="200" align="center" />
      <el-table-column prop="description" label="描述" min-width="200" align="center">
        <template #default="{ row }">
          <span class="desc">{{ row.description }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="applicantName" label="预约人" width="100" align="center" />
      <el-table-column prop="auditStatus" label="审核状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="auditTagType(row.auditStatus)" effect="plain">{{ row.auditStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="usageStatus" label="使用状态" width="100" align="center" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openDetail(row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
  <!-- 预约详情弹窗 -->
  <ReservationDetailDialog
    v-model="reservationDetailDialogVisible"
    :detail="reservationDetail"
    :show-cancel-reservation="false"
    width="600px"
  />
</template>

<script setup>
import { reactive, computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoomBookingDetails, exportRoomBookingDetails, getBookingDetail } from '@/api/roomBookingManagement'
import ReservationDetailDialog from '../Booking/ReservationDetailDialog.vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  roomId: { type: String, default: '' },
  roomName: { type: String, default: '' },
  roomCode: { type: String, default: '' }
})

const emit = defineEmits(['update:visible'])

const visible = computed({
  get: () => props.visible,
  set: val => emit('update:visible', val)
})

const filters = reactive({
  name: '',
  auditStatus: '',
  usageStatus: '',
  applicant: '',
  dateRange: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const total = ref(0)
const bookingDetails = ref([])


// 预约详情弹窗相关
const reservationDetailDialogVisible = ref(false)
const reservationDetail = ref({
  userName: '',
  reservationTitle: '',
  borrowTime: '',
  borrowDesc: '',
  participants: [],
  remark: '',
  approvalSteps: []
})

// 加载教室预约详情数据
const loadRoomBookingDetails = async () => {
  if (!props.roomId) return
  
  try {
    loading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      bookingName: filters.name || undefined,
      auditStatus: filters.auditStatus || undefined,
      usageStatus: filters.usageStatus || undefined,
      applicantName: filters.applicant || undefined,
      startTime: filters.dateRange && filters.dateRange.length === 2 ? formatDate(filters.dateRange[0]) : undefined,
      endTime: filters.dateRange && filters.dateRange.length === 2 ? formatDate(filters.dateRange[1]) : undefined
    }
    
    const response = await getRoomBookingDetails(props.roomId, params)
    if (response.code === 200) {
      bookingDetails.value = response.data.pageData?.list || []
      total.value = response.data.pageData?.total || 0
    } else {
      ElMessage.error(response.message || '获取教室预约详情失败')
    }
  } catch (error) {
    console.error('获取教室预约详情失败:', error)
    ElMessage.error('获取教室预约详情失败')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toISOString().slice(0, 10)
}

const pagedData = computed(() => bookingDetails.value)

function handleSearch() {
  currentPage.value = 1
  loadRoomBookingDetails()
}

function resetFilters() {
  filters.name = ''
  filters.auditStatus = ''
  filters.usageStatus = ''
  filters.applicant = ''
  filters.dateRange = ''
  currentPage.value = 1
  loadRoomBookingDetails()
}

// 导出当前页
async function exportCurrent() {
  if (!props.roomId) return
  
  try {
    const params = {
      exportType: 'current',
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      bookingName: filters.name || undefined,
      auditStatus: filters.auditStatus || undefined,
      usageStatus: filters.usageStatus || undefined,
      applicantName: filters.applicant || undefined,
      startTime: filters.dateRange && filters.dateRange.length === 2 ? formatDate(filters.dateRange[0]) : undefined,
      endTime: filters.dateRange && filters.dateRange.length === 2 ? formatDate(filters.dateRange[1]) : undefined
    }
    
    const response = await exportRoomBookingDetails(props.roomId, params)
    if (response.code === 200) {
      ElMessage.success(`导出成功，共${response.data.recordCount}条记录`)
      // 这里可以添加下载文件的逻辑
      // window.open(response.data.fileUrl)
    } else {
      ElMessage.error(response.message || '导出失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导出全部页
async function exportAll() {
  if (!props.roomId) return
  
  try {
    const params = {
      exportType: 'all',
      bookingName: filters.name || undefined,
      auditStatus: filters.auditStatus || undefined,
      usageStatus: filters.usageStatus || undefined,
      applicantName: filters.applicant || undefined,
      startTime: filters.dateRange && filters.dateRange.length === 2 ? formatDate(filters.dateRange[0]) : undefined,
      endTime: filters.dateRange && filters.dateRange.length === 2 ? formatDate(filters.dateRange[1]) : undefined
    }
    
    const response = await exportRoomBookingDetails(props.roomId, params)
    if (response.code === 200) {
      ElMessage.success(`导出成功，共${response.data.recordCount}条记录`)
      // 这里可以添加下载文件的逻辑
      // window.open(response.data.fileUrl)
    } else {
      ElMessage.error(response.message || '导出失败')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

async function openDetail(row) {
  try {
    const response = await getBookingDetail(row.id)
    if (response.code === 200) {
      const data = response.data
      
      // 转换审批步骤数据格式
      const approvalSteps = (data.approvalSteps || []).map(step => ({
        levelName: step.levelName,
        approvers: step.approvers || [],
        confirmedApprover: step.confirmedApprover || '',
        approvalTime: step.approvalTime ? 
          new Date(step.approvalTime).toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
          }) : '/',
        comment: step.comment || '/'
      }))

      // 提取参与人姓名
      const participants = data.participantDetails 
        ? data.participantDetails.map(p => p.name) 
        : (data.participants || [])

      reservationDetail.value = {
        id: data.id,
        userName: data.applicantName,
        reservationTitle: data.bookingName,
        borrowTime: data.bookingPeriod,
        borrowDesc: data.description,
        participants: participants,
        remark: data.remark || data.reason || '',
        approvalSteps: approvalSteps
      }
      reservationDetailDialogVisible.value = true
    } else {
      ElMessage.error('获取预约详情失败：' + (response.message || '未知错误'))
    }
  } catch (error) {
    console.error('获取预约详情失败：', error)
    ElMessage.error('获取预约详情失败，请稍后重试')
  }
}


function auditTagType(status) {
  const map = {
    审核中: 'warning',
    通过: 'success',
    拒绝: 'danger',
    已取消: 'info'
  }
  return map[status] || ''
}

// 分页处理
function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
  loadRoomBookingDetails()
}

function handleCurrentChange(val) {
  currentPage.value = val
  loadRoomBookingDetails()
}

// 监听弹窗显示状态和roomId变化
watch(() => props.visible, (newVal) => {
  if (newVal && props.roomId) {
    currentPage.value = 1
    loadRoomBookingDetails()
  }
})

watch(() => props.roomId, (newVal) => {
  if (newVal && props.visible) {
    currentPage.value = 1
    loadRoomBookingDetails()
  }
})
</script>

<style scoped>
.filter-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}
.filter-form {
  flex: 1;
}
.export-buttons {
  display: flex;
  gap: 10px;
}
.record-table :deep(.el-table__cell) {
  word-break: break-all;
}
.desc {
  white-space: pre-wrap;
}
.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
.dialog-footer {
  text-align: right;
}
</style>

