<template>
  <el-dialog
    v-model="visible"
    title="审核状态"
    width="800px"
    :close-on-click-modal="false"
  >
    <el-row gutter="20">
      <el-col :span="8" class="left-col">
        <div class="info-item"><label>预约名称：</label><span>{{ info.reservationName }}</span></div>
        <div class="info-item"><label>预约人：</label><span>{{ info.applicant }}</span></div>
        <div class="info-item"><label>预约周期：</label><span>{{ info.reservationPeriod }}</span></div>
        <div class="info-item"><label>借用描述：</label><span>{{ info.description }}</span></div>
        <div class="info-item"><label>参与人：</label><span>{{ info.participants }}</span></div>
        <div class="info-item"><label>备注详情：</label><span>{{ info.remark }}</span></div>
        <div class="info-item">
          <label>审核状态：</label>
          <el-tag :type="getStatusType(info.approvalStatus)" size="small">
            {{ info.approvalStatus }}
          </el-tag>
        </div>
      </el-col>
      <el-col :span="16" class="right-col">
        <el-table
          :data="auditDetails"
          border
          stripe
          height="300"
          :header-cell-style="{ textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="level" label="审批层级" width="90" />
          <el-table-column prop="approvers" label="审批人">
            <template #default="{ row }">
              {{ Array.isArray(row.approvers) ? row.approvers.join('，') : row.approvers }}
            </template>
          </el-table-column>
          <el-table-column prop="confirmApprover" label="确认审批人">
            <template #default="{ row }">
              {{ row.confirmApprover || '/' }}
            </template>
          </el-table-column>
          <el-table-column prop="time" label="审批时间">
            <template #default="{ row }">
              {{ row.time || '/' }}
            </template>
          </el-table-column>
          <el-table-column prop="result" label="审批意见">
            <template #default="{ row }">
              <el-tag v-if="row.result" :type="getResultType(row.result)" size="small">
                {{ row.result }}
              </el-tag>
              <span v-else>/</span>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="close">取消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  info: { type: Object, default: () => ({}) },
  auditDetails: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: v => emit('update:modelValue', v)
})

const close = () => {
  visible.value = false
}

function getStatusType(status) {
  const map = {
    审批中: 'warning',
    已拒绝: 'danger',
    已通过: 'success'
  }
  return map[status] || 'info'
}

function getResultType(result) {
  const map = { 通过: 'success', 拒绝: 'danger' }
  return map[result] || 'info'
}
</script>

<style scoped>
.left-col {
  width: 300px;
}
.right-col {
  flex: 1;
}
.info-item {
  margin-bottom: 8px;
  display: flex;
  gap: 4px;
}
.dialog-footer {
  text-align: right;
}
</style>
