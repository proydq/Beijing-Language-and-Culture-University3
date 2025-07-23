<template>
  <el-dialog
    v-model="visible"
    title="审核详情"
    width="900px"
    :close-on-click-modal="false"
  >
    <el-row :gutter="20">
      <el-col :span="10" class="left-col">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="预约名称">
            {{ record.reservationTitle || '/' }}
          </el-descriptions-item>
          <el-descriptions-item label="预约人">
            {{ record.userName || '/' }}
          </el-descriptions-item>
          <el-descriptions-item label="预约周期">
            {{ record.borrowPeriodText || '/' }}
          </el-descriptions-item>
          <el-descriptions-item label="借用描述">
            {{ record.borrowDesc || '/' }}
          </el-descriptions-item>
          <el-descriptions-item label="参与人">
            {{ formatArray(record.participants) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注信息">
            {{ record.remark || '/' }}
          </el-descriptions-item>
          <el-descriptions-item label="审核状态">
            <el-tag :type="statusType" size="small">
              {{ record.approvalStatus || '/' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-col>
      <el-col :span="14" class="right-col">
        <el-table
          :data="record.approvalSteps || []"
          border
          stripe
          height="300"
          :header-cell-style="{ textAlign: 'center' }"
          :cell-style="{ textAlign: 'center' }"
        >
          <el-table-column prop="levelName" label="审批层级" width="100" />
          <el-table-column prop="approvers" label="审批人">
            <template #default="{ row }">{{ formatArray(row.approvers) }}</template>
          </el-table-column>
          <el-table-column prop="confirmedApprover" label="确认审批人">
            <template #default="{ row }">{{ row.confirmedApprover || '/' }}</template>
          </el-table-column>
          <el-table-column prop="approvalTime" label="审批时间">
            <template #default="{ row }">{{ row.approvalTime || '/' }}</template>
          </el-table-column>
          <el-table-column prop="comment" label="审批意见">
            <template #default="{ row }">
              <el-tag v-if="row.comment" :type="commentType(row.comment)" size="small">
                {{ row.comment }}
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
  record: { type: Object, default: () => ({}) }
})

const emit = defineEmits(['update:modelValue'])

const visible = computed({
  get: () => props.modelValue,
  set: v => emit('update:modelValue', v)
})

const close = () => {
  visible.value = false
}

const formatArray = arr => {
  if (!arr || arr.length === 0) return '/'
  return Array.isArray(arr) ? arr.join('，') : arr
}

const statusType = computed(() => {
  const map = {
    审核中: 'warning',
    已拒绝: 'danger',
    已通过: 'success'
  }
  return map[props.record.approvalStatus] || 'info'
})

const commentType = comment => {
  if (!comment) return 'info'
  if (comment.includes('拒绝')) return 'danger'
  if (comment.includes('通过') || comment.includes('同意')) return 'success'
  return 'info'
}
</script>

<style scoped>
.left-col {
  width: 300px;
}
.right-col {
  flex: 1;
}
.dialog-footer {
  text-align: right;
}
</style>
