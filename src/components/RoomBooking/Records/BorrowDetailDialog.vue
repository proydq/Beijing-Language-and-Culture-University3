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

    <el-table :data="pagedData" stripe style="width: 100%" class="record-table">
      <el-table-column prop="name" label="借用/预约名称" min-width="150" align="center" />
      <el-table-column prop="time" label="预约时间" min-width="200" align="center" />
      <el-table-column prop="description" label="描述" min-width="200" align="center">
        <template #default="{ row }">
          <span class="desc">{{ row.description }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="applicant" label="预约人" width="100" align="center" />
      <el-table-column prop="auditStatus" label="审核状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="auditTagType(row.auditStatus)" effect="plain">{{ row.auditStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="usageStatus" label="使用状态" width="100" align="center" />
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="$emit('view-detail', row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="filteredRecords.length"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, computed, ref } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  recordList: { type: Array, default: () => [] },
  roomName: { type: String, default: '' },
  roomCode: { type: String, default: '' }
})

const emit = defineEmits(['update:visible', 'view-detail'])

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

const filteredRecords = computed(() => {
  let list = props.recordList
  if (filters.name) {
    list = list.filter(item => item.name.includes(filters.name))
  }
  if (filters.auditStatus) {
    list = list.filter(item => item.auditStatus === filters.auditStatus)
  }
  if (filters.usageStatus) {
    list = list.filter(item => item.usageStatus === filters.usageStatus)
  }
  if (filters.applicant) {
    list = list.filter(item => item.applicant.includes(filters.applicant))
  }
  if (filters.dateRange && filters.dateRange.length === 2) {
    const [start, end] = filters.dateRange
    const startTime = new Date(start).getTime()
    const endTime = new Date(end).getTime()
    list = list.filter(item => {
      const t = new Date(item.date || item.time).getTime()
      return t >= startTime && t <= endTime
    })
  }
  return list
})

const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredRecords.value.slice(start, start + pageSize.value)
})

function handleSearch() {
  currentPage.value = 1
}

function resetFilters() {
  filters.name = ''
  filters.auditStatus = ''
  filters.usageStatus = ''
  filters.applicant = ''
  filters.dateRange = ''
  currentPage.value = 1
}

function exportCurrent() {
  ElMessage.success('导出当前页')
}

function exportAll() {
  ElMessage.success('导出全部页')
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

