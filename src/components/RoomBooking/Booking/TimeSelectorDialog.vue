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
          {{ week.label }}
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
import { ref, computed, watch } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
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

const weeks = ref([
  { label: '第一教学周', start: '2025-04-21' },
  { label: '第二教学周', start: '2025-04-28' },
  { label: '第三教学周', start: '2025-05-05' },
  { label: '第四教学周', start: '2025-05-12' }
])

const periods = ref([
  '第一节',
  '第二节',
  '第三节',
  '第四节',
  '第五节',
  '第六节',
  '第七节',
  '第八节'
])

const activeWeek = ref(0)

const days = computed(() => {
  const start = new Date(weeks.value[activeWeek.value].start)
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

function toggleSelect(date, period) {
  const key = `${date}_${period}`
  if (occupiedSet.value.has(key) || conflictSet.value.has(key)) return
  if (selected.value.has(key)) selected.value.delete(key)
  else selected.value.add(key)
}

function getCellClass(date, period) {
  const key = `${date}_${period}`
  return {
    occupied: occupiedSet.value.has(key),
    conflict: conflictSet.value.has(key),
    selected: selected.value.has(key)
  }
}

function getCellText(date, period) {
  const key = `${date}_${period}`
  if (occupiedSet.value.has(key)) return '已借用'
  if (conflictSet.value.has(key)) return '冲突'
  if (selected.value.has(key)) return '已选'
  return ''
}

function save() {
  const result = Array.from(selected.value).map(str => {
    const [date, p] = str.split('_')
    return { date, period: Number(p) }
  })
  emit('selectTime', result)
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

.cell-text {
  pointer-events: none;
}

.dialog-footer {
  text-align: right;
}
</style>
