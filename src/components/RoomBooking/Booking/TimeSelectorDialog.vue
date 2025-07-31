<template>
  <el-dialog
    v-model="dialogVisible"
    title="选择借用时间"
    width="920px"
    :close-on-click-modal="false"
  >
    <div class="select-time-wrapper">
      <!-- 左侧教学周列表 -->
      <div class="week-list">
        <div
          v-for="(week, index) in weeks"
          :key="index"
          :class="['week-item', { active: index === activeWeek } ]"
          @click="activeWeek = index"
        >
          {{ week.weekName || week.label }}
        </div>
      </div>
      <!-- 中间时间表 -->
      <div class="timetable">
        <table class="time-table">
          <thead>
            <tr>
              <th>节次/星期</th>
              <th v-for="day in days" :key="day.date" class="day-header">
                {{ day.label }}<br />{{ day.date }}
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(period, pIndex) in periods" :key="pIndex">
              <th class="period-header">{{ period }}</th>
              <td
                v-for="(day, dIndex) in days"
                :key="dIndex"
                :class="getCellClass(day.date, pIndex)"
                @click="toggleSelect(day.date, pIndex)"
              >
                <span class="cell-text">{{ getCellText(day.date, pIndex) }}</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { getSchoolWeeks, getRoomWeeklySchedule } from '@/api/roomSchedule'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  roomId: { type: String, required: true },
  occupiedTimes: { type: Array, default: () => [] },
  conflictTimes: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:visible', 'selectTime'])

const dialogVisible = ref(false)
watch(
  () => props.visible,
  val => (dialogVisible.value = val)
)
watch(dialogVisible, val => emit('update:visible', val))

// 监听弹窗打开，重置数据并加载默认的第一周课表
watch(dialogVisible, async (newVal) => {
  if (newVal) {
    // 重置选择状态
    selected.value.clear()
    // 如果教学周数据已加载，加载第一周课表
    if (weeks.value.length > 0) {
      await loadRoomSchedule(0) // 加载第一周(索引0)的课表
    }
  }
})

const weeks = ref([])
const roomSchedule = ref({}) // 存储房间课表数据，格式：{week: {date_period: {courseName, teacher, ...}}}
const courseTimeConfig = ref([]) // 存储课程时间配置

// 加载教学周数据
const loadSchoolWeeks = async () => {
  try {
    const response = await getSchoolWeeks()
    if (response.code === 200) {
      weeks.value = response.data || []
      // 如果弹窗是打开状态且有教学周数据，加载第一周课表
      if (dialogVisible.value && weeks.value.length > 0) {
        await loadRoomSchedule(0)
      }
    } else {
      ElMessage.error('获取教学周数据失败')
    }
  } catch (error) {
    console.error('获取教学周数据失败:', error)
    ElMessage.error('获取教学周数据失败')
  }
}

// 加载房间课表数据
const loadRoomSchedule = async (weekIndex) => {
  if (!props.roomId || weekIndex === undefined || !weeks.value[weekIndex]) return
  
  try {
    // 根据选中的教学周计算时间戳
    const selectedWeek = weeks.value[weekIndex]
    let timestamp
    
    if (selectedWeek.startDate) {
      // 使用教学周的开始日期作为时间戳
      const startDate = new Date(selectedWeek.startDate)
      timestamp = startDate.getTime()
    } else {
      // 如果没有开始日期，使用当前时间
      timestamp = Date.now()
    }
    
    console.log(`加载第${weekIndex + 1}周课表，教学周：${selectedWeek.weekName}，时间戳：${timestamp}`)
    
    const response = await getRoomWeeklySchedule({
      classRoomId: props.roomId,
      timestamp: timestamp
    })
    if (response.code === 200) {
      // 处理课表数据，根据实际后端返回的数据结构进行调整
      const scheduleData = response.data || {}
      const weekSchedule = {}
      
      // 保存课程时间配置
      if (scheduleData.courseTimeConfigList) {
        courseTimeConfig.value = scheduleData.courseTimeConfigList
      }
      
      // 后端返回的是 RoomWeeklyScheduleVO 结构，课程数据在 coursePlanningList 中
      if (scheduleData.coursePlanningList && scheduleData.coursePlanningList.length > 0) {
        scheduleData.coursePlanningList.forEach(item => {
          // 格式化日期为 YYYY-MM-DD 格式
          const courseDate = new Date(item.courseDate).toISOString().slice(0, 10)
          // 使用 courseSection 作为节次索引（courseSection从1开始，对应periods数组索引从0开始）
          const periodIndex = item.courseSection - 1
          const key = `${courseDate}_${periodIndex}`
          
          // 获取对应的时间配置
          const timeConfig = courseTimeConfig.value.find(config => config.courseSort === item.courseSection)
          
          weekSchedule[key] = {
            courseName: item.courseName,
            teacherName: item.teacherName,
            courseSection: item.courseSection,
            startTime: timeConfig?.courseStartTime || '',
            endTime: timeConfig?.courseEndTime || '',
            status: 'occupied' // 标记为已占用
          }
        })
      }
      
      roomSchedule.value[weekIndex] = weekSchedule
    } else {
      console.warn(`获取第${weekIndex}周课表失败:`, response.message)
    }
  } catch (error) {
    console.error(`获取第${weekIndex}周课表失败:`, error)
  }
}

onMounted(() => {
  loadSchoolWeeks()
})

// 动态生成节次列表，基于课程时间配置
const periods = computed(() => {
  if (courseTimeConfig.value.length === 0) {
    // 如果没有时间配置，使用默认节次
    return [
      '第一节',
      '第二节', 
      '第三节',
      '第四节',
      '第五节',
      '第六节',
      '第七节',
      '第八节'
    ]
  }
  
  // 根据课程时间配置生成节次显示
  return courseTimeConfig.value
    .sort((a, b) => a.courseSort - b.courseSort) // 按节次排序
    .map(config => `第${config.courseSort}节 ${config.courseStartTime}-${config.courseEndTime}`)
})

const activeWeek = ref(0)

// 监听教学周切换，加载对应课表
watch(activeWeek, async (newWeekIndex) => {
  if (weeks.value[newWeekIndex]) {
    await loadRoomSchedule(newWeekIndex)
  }
}, { immediate: false })

const days = computed(() => {
  if (!weeks.value.length || activeWeek.value >= weeks.value.length) {
    return []
  }
  
  const currentWeek = weeks.value[activeWeek.value]
  if (!currentWeek || !currentWeek.startDate) {
    return []
  }
  
  const start = new Date(currentWeek.startDate)
  const arr = []
  const labels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  
  for (let i = 0; i < 7; i++) {
    const d = new Date(start)
    d.setDate(d.getDate() + i)
    const dateStr = d.toISOString().slice(0, 10)
    arr.push({ date: dateStr, label: labels[i] })
  }
  return arr
})

const selected = ref(new Set())

const occupiedSet = computed(() => new Set(props.occupiedTimes.map(t => `${t.date}_${t.period}`)))
const conflictSet = computed(() => new Set(props.conflictTimes.map(t => `${t.date}_${t.period}`)))

// 获取当前周的课表数据
const currentWeekSchedule = computed(() => {
  return roomSchedule.value[activeWeek.value] || {}
})

function toggleSelect(date, period) {
  const key = `${date}_${period}`
  // 检查是否被占用、冲突或有课程安排
  if (occupiedSet.value.has(key) || conflictSet.value.has(key) || currentWeekSchedule.value[key]) {
    return
  }
  if (selected.value.has(key)) selected.value.delete(key)
  else selected.value.add(key)
}

function getCellClass(date, period) {
  const key = `${date}_${period}`
  return {
    occupied: occupiedSet.value.has(key),
    conflict: conflictSet.value.has(key),
    selected: selected.value.has(key),
    'has-course': !!currentWeekSchedule.value[key] // 新增：有课程的样式
  }
}

function getCellText(date, period) {
  const key = `${date}_${period}`
  if (occupiedSet.value.has(key)) return '已借用'
  if (conflictSet.value.has(key)) return '冲突'
  if (currentWeekSchedule.value[key]) {
    const course = currentWeekSchedule.value[key]
    // 显示课程名称和教师名称
    return `${course.courseName}\n${course.teacherName}`
  }
  if (selected.value.has(key)) return '已选'
  return ''
}

function save() {
  const result = Array.from(selected.value).map(str => {
    const [date, periodIndex] = str.split('_')
    const period = Number(periodIndex)
    
    // 获取对应的时间配置
    const timeConfig = courseTimeConfig.value.find(config => config.courseSort === (period + 1))
    
    return { 
      date, 
      period: period,
      courseSort: period + 1, // courseSort从1开始
      startTime: timeConfig?.courseStartTime || '',
      endTime: timeConfig?.courseEndTime || ''
    }
  })
  
  // 生成格式化的时间描述
  const timeDescription = result.map(item => {
    // 格式化日期为 YYYY-MM-DD 格式
    const date = new Date(item.date)
    const formattedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    
    if (item.startTime && item.endTime) {
      // 格式化为 YYYY-MM-DD HH:mm:ss-HH:mm:ss
      // 检查时间格式，如果已经包含秒数就不添加，否则添加 :00
      const startTimeParts = item.startTime.split(':')
      const endTimeParts = item.endTime.split(':')
      
      const startTime = startTimeParts.length === 3 
        ? item.startTime 
        : `${item.startTime}:00`
      const endTime = endTimeParts.length === 3 
        ? item.endTime 
        : `${item.endTime}:00`
      
      return `${formattedDate} ${startTime}-${endTime}`
    } else {
      return `${formattedDate} 第${item.courseSort}节`
    }
  }).join('; ')
  
  emit('selectTime', { selections: result, description: timeDescription })
  dialogVisible.value = false
}

function cancel() {
  dialogVisible.value = false
}
</script>

<style scoped>
.select-time-wrapper {
  display: flex;
  min-height: 500px;
}

.week-list {
  width: 130px;
  border-right: 1px solid #e8e8e8;
  overflow-y: auto;
}

.week-item {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}
.week-item.active {
  background: #409eff;
  color: #fff;
}

.timetable {
  flex: 1;
  overflow: auto;
  padding: 10px;
}

.time-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}

.time-table th,
.time-table td {
  border: 1px solid #ebeef5;
  padding: 6px 4px;
  min-height: 50px;
  vertical-align: middle;
}

.period-header {
  width: 80px;
  background: #fafafa;
}

.day-header {
  width: 110px;
}

.occupied {
  background: #fde2e2;
  color: #d13636;
}

.conflict {
  background: #fff1cc;
  color: #b8860b;
}

.selected {
  background: #e1f3d8;
  color: #529b2e;
}

.has-course {
  background: #ffeaa7;
  color: #2d3436;
  cursor: not-allowed;
  font-weight: 500;
}

.has-course .cell-text {
  font-size: 11px;
  font-weight: 500;
}

.cell-text {
  pointer-events: none;
  white-space: pre-line; /* 支持换行符 */
  font-size: 12px;
  line-height: 1.2;
}

.dialog-footer {
  text-align: right;
}
</style>
