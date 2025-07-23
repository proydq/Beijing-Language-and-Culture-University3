<template>
  <el-dialog
    v-model="dialogVisible"
    title="借用审批"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div v-if="approvalData">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="预约名称">{{ approvalData.name }}</el-descriptions-item>
        <el-descriptions-item label="预约周期">{{ approvalData.cycle }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ approvalData.description || '/' }}</el-descriptions-item>
        <el-descriptions-item label="预约人">{{ approvalData.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="预约教室">{{ approvalData.roomName }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="form" class="mt-4">
        <el-form-item label="审批意见">
          <el-input v-model="form.comment" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="审批结果">
          <el-radio-group v-model="form.result">
            <el-radio label="APPROVED">同意</el-radio>
            <el-radio label="REJECTED">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, reactive, computed, watch } from 'vue'
export default {
  name: 'ApprovalDialog',
  props: {
    modelValue: { type: Boolean, default: false },
    approvalData: { type: Object, default: null }
  },
  emits: ['update:modelValue', 'confirm', 'cancel'],
  setup(props, { emit }) {
    const dialogVisible = computed({
      get: () => props.modelValue,
      set: v => emit('update:modelValue', v)
    })
    const form = reactive({ comment: '', result: 'APPROVED' })
    const loading = ref(false)

    const handleClose = () => {
      emit('cancel')
    }

    const handleSubmit = () => {
      loading.value = true
      setTimeout(() => {
        emit('confirm', { ...form })
        loading.value = false
        dialogVisible.value = false
      }, 300)
    }

    watch(dialogVisible, val => {
      if (val) {
        form.comment = ''
        form.result = 'APPROVED'
      }
    })

    return { dialogVisible, form, loading, handleClose, handleSubmit }
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
