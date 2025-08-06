<template>
  <div class="access-records-filter">
    <el-form :model="filterForm" label-width="80px" :inline="true">
      <div class="filter-row">
        <!-- 基础信息搜索 -->
        <el-form-item label="基础信息">
          <el-input
            v-model="filterForm.basicInfo"
            placeholder="请输入姓名/工号/联系方式"
            clearable
            style="width: 200px"
          />
        </el-form-item>

        <!-- 教室选择 -->
        <el-form-item label="教室">
          <el-select
            v-model="filterForm.roomId"
            placeholder="请选择教室"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="room in roomOptions"
              :key="room.value"
              :label="room.label"
              :value="room.value"
            />
          </el-select>
        </el-form-item>

        <!-- 区域选择 -->
        <el-form-item label="区域">
          <el-select
            v-model="filterForm.areaId"
            placeholder="请选择区域"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="area in areaOptions"
              :key="area.value"
              :label="area.label"
              :value="area.value"
            />
          </el-select>
        </el-form-item>

        <!-- 开门方式 -->
        <el-form-item label="开门方式">
          <el-select
            v-model="filterForm.openMethod"
            placeholder="请选择开门方式"
            clearable
            style="width: 140px"
          >
            <el-option label="刷卡" value="刷卡" />
            <el-option label="人脸识别" value="人脸识别" />
            <el-option label="按钮" value="按钮" />
          </el-select>
        </el-form-item>
      </div>

      <div class="filter-row">
        <!-- 通行类型 -->
        <el-form-item label="通行类型">
          <el-select
            v-model="filterForm.accessType"
            placeholder="请选择通行类型"
            clearable
            style="width: 140px"
          >
            <el-option label="预约权限" value="预约权限" />
            <el-option label="永久权限" value="永久权限" />
          </el-select>
        </el-form-item>

        <!-- 时间范围 -->
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 350px"
            @change="handleDateRangeChange"
          />
        </el-form-item>
      </div>

      <!-- 操作按钮 -->
      <div class="filter-actions">
        <el-button type="primary" @click="handleSearch" :loading="searching">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-dropdown @command="handleExport" trigger="click">
          <el-button type="success">
            <el-icon><Download /></el-icon>
            导出
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="current">导出当前页</el-dropdown-item>
              <el-dropdown-item command="all">导出全部数据</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Refresh, Download, ArrowDown } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

// Emits
const emit = defineEmits(['update:modelValue', 'search', 'reset', 'export'])

// 响应式数据
const searching = ref(false)
const dateRange = ref([])

// 筛选表单
const filterForm = reactive({
  areaId: '',
  roomId: '',
  basicInfo: '',
  startTime: '',
  endTime: '',
  openMethod: '',
  accessType: ''
})

// 选项数据
const roomOptions = ref([
  { label: 'A-101', value: 'room001' },
  { label: 'A-102', value: 'room002' },
  { label: 'B-201', value: 'room003' },
  { label: 'B-202', value: 'room004' }
])

const areaOptions = ref([
  { label: '教学楼A', value: 'area001' },
  { label: '教学楼B', value: 'area002' },
  { label: '实验楼', value: 'area003' }
])

// 计算属性 - 同步数据
const syncedFilterForm = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听filterForm变化，同步到父组件
const syncToParent = () => {
  Object.keys(filterForm).forEach(key => {
    syncedFilterForm.value[key] = filterForm[key]
  })
}

// 处理时间范围变化
const handleDateRangeChange = (dates) => {
  if (dates && dates.length === 2) {
    filterForm.startTime = dates[0]
    filterForm.endTime = dates[1]
  } else {
    filterForm.startTime = ''
    filterForm.endTime = ''
  }
  syncToParent()
}

// 搜索处理
const handleSearch = () => {
  searching.value = true
  syncToParent()
  emit('search')
  
  // 模拟搜索延迟
  setTimeout(() => {
    searching.value = false
  }, 1000)
}

// 重置处理
const handleReset = () => {
  Object.keys(filterForm).forEach(key => {
    filterForm[key] = ''
  })
  dateRange.value = []
  syncToParent()
  emit('reset')
}

// 导出处理
const handleExport = (command) => {
  emit('export', command)
}

// 初始化
onMounted(() => {
  // 从父组件同步初始值
  Object.keys(filterForm).forEach(key => {
    if (props.modelValue[key] !== undefined) {
      filterForm[key] = props.modelValue[key]
    }
  })

  // 如果有时间范围，设置到日期选择器
  if (filterForm.startTime && filterForm.endTime) {
    dateRange.value = [filterForm.startTime, filterForm.endTime]
  }
})
</script>

<style scoped>
.access-records-filter {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #e9ecef;
  margin-top: 16px;
}

:deep(.el-form-item) {
  margin-bottom: 8px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-date-editor .el-input__wrapper) {
  border-radius: 6px;
}

@media (max-width: 1200px) {
  .filter-row {
    flex-direction: column;
    gap: 8px;
  }
  
  .filter-actions {
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .access-records-filter {
    padding: 16px;
  }
  
  :deep(.el-form-item) {
    width: 100%;
  }
  
  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-date-picker) {
    width: 100% !important;
  }
}
</style>