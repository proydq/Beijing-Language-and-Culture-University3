<template>
  <el-dialog v-model="visible" title="教室借用详情" width="600px" destroy-on-close>
    <div class="room-name">房屋名称：{{ detail.roomName || '/' }}</div>

    <div class="section">
      <h4 class="section-title">借用信息</h4>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="field-item">
            <span class="label">借用/预约名称</span>
            <span class="value">{{ detail.name || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">借用类型</span>
            <span class="value">{{ detail.type || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">审核状态</span>
            <span class="value" :class="approvalClass(detail.approvalStatus)">
              {{ detail.approvalStatus || '/' }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">使用状态</span>
            <span class="value" :class="statusClass(detail.useStatus)">
              {{ detail.useStatus || '/' }}
            </span>
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
            <span class="value">{{ detail.timeRange || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">使用时长</span>
            <span class="value">{{ detail.duration || '/' }}</span>
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
            <span class="value">{{ detail.reserver || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">工号</span>
            <span class="value">{{ detail.jobNumber || '/' }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="field-item">
            <span class="label">联系方式</span>
            <span class="value">{{ detail.contact || '/' }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="section">
      <h4 class="section-title">借用详情</h4>
      <el-card shadow="never" class="description-card">
        <div class="description-text">
          {{ detail.description || '/' }}
        </div>
      </el-card>
    </div>

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

const approvalClass = status => {
  const map = {
    审核中: 'status-orange',
    通过: 'status-green',
    拒绝: 'status-red',
  }
  return map[status] || 'status-gray'
}

const statusClass = status => {
  const map = {
    未开始: 'status-blue',
    进行中: 'status-green',
    已结束: 'status-gray',
  }
  return map[status] || 'status-gray'
}
</script>

<style scoped>
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

.dialog-footer {
  text-align: right;
}
</style>

