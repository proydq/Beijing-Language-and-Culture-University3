<template>
  <el-dialog v-model="visible" title="教室借用详情" width="600px" destroy-on-close>
    <el-descriptions :column="1" border>
      <el-descriptions-item label="借用/预约名称">
        {{ detail.name || '/' }}
      </el-descriptions-item>
      <el-descriptions-item label="预约人">
        {{ detail.reserver || '/' }}
      </el-descriptions-item>
      <el-descriptions-item label="借用时间">
        {{ detail.time || '/' }}
      </el-descriptions-item>
      <el-descriptions-item label="审核状态">
        <span :style="{ color: approvalColor(detail.approval) }">
          {{ detail.approval || '/' }}
        </span>
      </el-descriptions-item>
      <el-descriptions-item label="使用状态">
        <span :style="{ color: statusColor(detail.status) }">
          {{ detail.status || '/' }}
        </span>
      </el-descriptions-item>
      <el-descriptions-item label="描述信息">
        <el-input
          :model-value="detail.description || '/'"
          type="textarea"
          autosize
          readonly
        />
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  detail: { type: Object, default: () => ({}) },
})
const emit = defineEmits(['update:visible'])

const visible = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v),
})

const approvalColor = status => {
  const map = {
    审核中: '#e6a23c',
    通过: '#67c23a',
    拒绝: '#f56c6c',
  }
  return map[status] || '#909399'
}

const statusColor = status => {
  const map = {
    未开始: '#409eff',
    进行中: '#67c23a',
    已结束: '#909399',
  }
  return map[status] || '#909399'
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>

