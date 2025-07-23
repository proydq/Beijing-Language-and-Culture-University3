<template>
  <el-dialog
    v-model="visible"
    :title="`房屋名称：${roomName}（${roomCode}）`"
    width="80%"
    destroy-on-close
    center
  >
    <div class="filter-wrap">
      <el-form :model="filters" inline class="filter-form">
        <el-form-item label="借用/预约名称">
          <el-input
            v-model="filters.name"
            placeholder="借用/预约名称"
            clearable
            style="width: 200px"
          />
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
          <el-input
            v-model="filters.applicant"
            placeholder="预约人"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="预约时间">
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
          <el-button type="primary" @click="search">搜索</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
        <div class="actions">
          <el-button type="primary" @click="exportCurrent">导出当前页</el-button>
          <el-button type="success" @click="exportAll">导出全部页</el-button>
        </div>
      </el-form>
    </div>

    <el-table :data="pagedRecords" stripe class="records-table" style="width: 100%">
      <el-table-column prop="name" label="借用/预约名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="time" label="预约时间" min-width="200" show-overflow-tooltip />
      <el-table-column prop="description" label="描述" min-width="200">
        <template #default="{ row }">
          <span class="desc-text">{{ row.description }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="applicant" label="预约人" width="120" />
      <el-table-column prop="auditStatus" label="审核状态" width="100">
        <template #default="{ row }">
          <el-tag :type="tagType(row.auditStatus)">{{ row.auditStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="usageStatus" label="使用状态" width="100" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="openDetail(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 50, 100]"
      :total="total"
      class="table-pagination"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="detailVisible" title="预约详情" width="600px" destroy-on-close>
    <div class="room-name">房屋名称：{{ detailData.roomName || roomName }}</div>

    <div class="section">
      <h4 class="section-title">借用信息</h4>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="field-item">
            <span class="label">借用/预约名称</span>
            <span class="value">{{ detailData.name || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">借用类型</span>
            <span class="value">{{ detailData.type || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">审核状态</span>
            <span class="value" :class="approvalClass(detailData.auditStatus)">{{
              detailData.auditStatus || '/'
            }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">使用状态</span>
            <span class="value" :class="statusClass(detailData.usageStatus)">{{
              detailData.usageStatus || '/'
            }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h4 class="section-title">时间安排</h4>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="field-item">
            <span class="label">预约时间</span>
            <span class="value">{{ detailData.time || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">使用时长</span>
            <span class="value">{{ detailData.duration || '/' }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h4 class="section-title">申请人信息</h4>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="field-item">
            <span class="label">预约人</span>
            <span class="value">{{ detailData.applicant || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">工号</span>
            <span class="value">{{ detailData.jobNumber || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">联系方式</span>
            <span class="value">{{ detailData.contact || '/' }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h4 class="section-title">借用详情</h4>
      <el-card shadow="never" class="description-card">
        <div class="description-text">{{ detailData.description || '/' }}</div>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  dialogVisible: { type: Boolean, default: false },
  roomName: { type: String, default: '' },
  roomCode: { type: String, default: '' },
  recordList: { type: Array, default: () => [] },
})

const emit = defineEmits(['update:dialogVisible'])

const visible = computed({
  get: () => props.dialogVisible,
  set: (v) => emit('update:dialogVisible', v),
})

const filters = reactive({
  name: '',
  auditStatus: '',
  usageStatus: '',
  applicant: '',
  dateRange: [],
})

const currentPage = ref(1)
const pageSize = ref(10)

const filteredRecords = computed(() => {
  let data = props.recordList || []
  if (filters.name) {
    data = data.filter((item) => item.name && item.name.includes(filters.name))
  }
  if (filters.auditStatus) {
    data = data.filter((item) => item.auditStatus === filters.auditStatus)
  }
  if (filters.usageStatus) {
    data = data.filter((item) => item.usageStatus === filters.usageStatus)
  }
  if (filters.applicant) {
    data = data.filter((item) => item.applicant && item.applicant.includes(filters.applicant))
  }
  if (filters.dateRange && filters.dateRange.length === 2) {
    const [start, end] = filters.dateRange
    data = data.filter((item) => {
      if (!item.startTime || !item.endTime) return true
      const s = new Date(item.startTime)
      const e = new Date(item.endTime)
      return s >= start && e <= end
    })
  }
  return data
})

const pagedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredRecords.value.slice(start, start + pageSize.value)
})

const total = computed(() => filteredRecords.value.length)

function search() {
  currentPage.value = 1
}

function reset() {
  filters.name = ''
  filters.auditStatus = ''
  filters.usageStatus = ''
  filters.applicant = ''
  filters.dateRange = []
  currentPage.value = 1
}

function exportCurrent() {
  ElMessage.success('导出当前页')
}

function exportAll() {
  ElMessage.success('导出全部页')
}

function handleSizeChange(val) {
  pageSize.value = val
  currentPage.value = 1
}

function handleCurrentChange(val) {
  currentPage.value = val
}

const detailVisible = ref(false)
const detailData = ref({})

function openDetail(row) {
  detailData.value = row
  detailVisible.value = true
}

const tagType = (status) => {
  const map = {
    审核中: 'warning',
    通过: 'success',
    拒绝: 'danger',
    已取消: 'info',
  }
  return map[status] || 'info'
}

const approvalClass = (status) => {
  const map = {
    审核中: 'status-orange',
    通过: 'status-green',
    拒绝: 'status-red',
  }
  return map[status] || 'status-gray'
}

const statusClass = (status) => {
  const map = {
    未开始: 'status-blue',
    进行中: 'status-green',
    已结束: 'status-gray',
  }
  return map[status] || 'status-gray'
}
</script>

<style scoped>
.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}
.actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}
.records-table :deep(.el-table__cell) {
  text-align: center;
}
.desc-text {
  white-space: pre-wrap;
}
.table-pagination {
  margin: 20px 0 0;
  text-align: right;
}
.dialog-footer {
  text-align: right;
}
.room-name {
  margin-bottom: 15px;
  font-size: 14px;
  color: #333;
}
.section {
  margin-bottom: 20px;
}
.section-title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.field-item {
  display: flex;
  line-height: 24px;
  margin-bottom: 8px;
}
.label {
  width: 90px;
  color: #666;
}
.value {
  flex: 1;
  color: #333;
}
.status-orange {
  color: #e6a23c;
}
.status-green {
  color: #67c23a;
}
.status-blue {
  color: #409eff;
}
.status-red {
  color: #f56c6c;
}
.status-gray {
  color: #909399;
}
.description-card {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}
</style>
