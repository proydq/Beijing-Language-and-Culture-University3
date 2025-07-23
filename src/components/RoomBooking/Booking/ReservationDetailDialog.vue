<template>
  <el-dialog v-model="visible" title="预约详情" :width="dialogWidth" :close-on-click-modal="false">
    <div class="section">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约人">
          {{ detail.userName || '/' }}
        </el-descriptions-item>
        <el-descriptions-item label="预约名称">
          {{ detail.reservationTitle || '/' }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="section borrow-time">
      <div class="time-text">
        借用时间：{{ detail.borrowTime || '/' }}
        <span class="tip">选择方式为：从课表中选择</span>
      </div>
    </div>

    <div class="section">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="借用描述">
          <el-input :model-value="detail.borrowDesc || '/'" type="textarea" autosize readonly />
        </el-descriptions-item>
        <el-descriptions-item label="参与人">
          <el-input :model-value="formatParticipants(detail.participants)" readonly />
        </el-descriptions-item>
        <el-descriptions-item label="备注详情">
          <el-input :model-value="detail.remark || '/'" type="textarea" autosize readonly />
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="section">
      <el-table :data="detail.approvalList || []" border stripe>
        <el-table-column prop="level" label="审批层级" width="90" />
        <el-table-column prop="approver" label="审批人">
          <template #default="{ row }">
            {{ formatApprover(row.approver) }}
          </template>
        </el-table-column>
        <el-table-column prop="confirmApprover" label="确认审批人">
          <template #default="{ row }">{{ row.confirmApprover || '/' }}</template>
        </el-table-column>
        <el-table-column prop="time" label="审批时间">
          <template #default="{ row }">{{ row.time || '/' }}</template>
        </el-table-column>
        <el-table-column prop="comment" label="审批意见">
          <template #default="{ row }">
            <el-tag
              v-if="row.comment"
              :type="row.comment === '通过' || row.comment === '同意' ? 'success' : 'danger'"
              size="small"
            >
              {{ row.comment }}
            </el-tag>
            <span v-else>/</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="close">取消</el-button>
        <el-button type="danger" @click="cancelReservation">取消预约</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  detail: { type: Object, default: () => ({}) },
})

const emit = defineEmits(['update:modelValue', 'cancel'])

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

const dialogWidth = '90%'

const close = () => {
  visible.value = false
}

const cancelReservation = () => {
  ElMessage.success('已取消预约')
  emit('cancel', detail)
  visible.value = false
}

const formatParticipants = (val) => {
  if (!val || val.length === 0) return '/'
  return Array.isArray(val) ? val.join('，') : val
}

const formatApprover = (val) => {
  if (!val) return '/'
  return Array.isArray(val) ? val.join('，') : val
}
</script>

<style scoped>
.section {
  margin-bottom: 20px;
}
.borrow-time {
  position: relative;
}
.time-text {
  padding: 10px 16px;
  background: #f5f7fa;
  border-radius: 4px;
  position: relative;
}
.tip {
  position: absolute;
  top: 0;
  right: 8px;
  color: #f56c6c;
  font-size: 12px;
}
.dialog-footer {
  text-align: right;
}
</style>
