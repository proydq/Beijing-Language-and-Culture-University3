<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div v-if="approvalData" class="approval-dialog-content">
      <!-- 申请信息 -->
      <div class="section">
        <h4 class="section-title">申请信息</h4>
        <div class="info-grid">
          <div class="info-item">
            <label>预约名称：</label>
            <span>{{ approvalData.bookingName }}</span>
          </div>
          <div class="info-item">
            <label>申请人：</label>
            <span>
              {{ approvalData.applicant }}
              <el-tag 
                :type="getApplicantTypeColor(approvalData.applicantType)" 
                size="small"
                style="margin-left: 8px;"
              >
                {{ approvalData.applicantType }}
              </el-tag>
            </span>
          </div>
          <div class="info-item">
            <label>房间名称：</label>
            <span>{{ approvalData.roomName }}</span>
          </div>
          <div class="info-item">
            <label>预约时间：</label>
            <span>{{ approvalData.bookingTime }}</span>
          </div>
          <div class="info-item">
            <label>申请时间：</label>
            <span>{{ approvalData.applyTime }}</span>
          </div>
          <div class="info-item full-width">
            <label>申请理由：</label>
            <span class="reason">{{ approvalData.reason }}</span>
          </div>
        </div>
      </div>

      <!-- 审批操作 -->
      <div class="section">
        <h4 class="section-title">
          {{ actionType === 'approve' ? '审批通过' : '审批拒绝' }}
        </h4>
        
        <el-form :model="formData" :rules="formRules" ref="formRef" label-width="80px">
          <el-form-item label="审批意见" prop="comment">
            <el-input
              v-model="formData.comment"
              type="textarea"
              :rows="4"
              :placeholder="actionType === 'approve' ? '请输入通过理由（可选）' : '请输入拒绝理由'"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item v-if="actionType === 'approve'" label="备注信息" prop="notes">
            <el-input
              v-model="formData.notes"
              type="textarea"
              :rows="2"
              placeholder="请输入备注信息（可选）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          :type="actionType === 'approve' ? 'success' : 'danger'"
          :loading="submitLoading"
          @click="handleSubmit"
        >
          <el-icon>
            <component :is="actionType === 'approve' ? 'Check' : 'Close'" />
          </el-icon>
          {{ actionType === 'approve' ? '确认通过' : '确认拒绝' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, computed, reactive, watch, nextTick } from 'vue'
import { Check, Close } from '@element-plus/icons-vue'

export default {
  name: 'ApprovalDialog',
  components: {
    Check,
    Close
  },
  props: {
    modelValue: {
      type: Boolean,
      default: false
    },
    approvalData: {
      type: Object,
      default: null
    },
    actionType: {
      type: String,
      default: 'approve', // 'approve' | 'reject'
      validator: (value) => ['approve', 'reject'].includes(value)
    }
  },
  emits: ['update:modelValue', 'confirm', 'cancel'],
  setup(props, { emit }) {
    const formRef = ref(null)
    const submitLoading = ref(false)

    const dialogVisible = computed({
      get: () => props.modelValue,
      set: (value) => emit('update:modelValue', value)
    })

    const dialogTitle = computed(() => {
      if (!props.approvalData) return ''
      const action = props.actionType === 'approve' ? '审批通过' : '审批拒绝'
      return `${action} - ${props.approvalData.bookingName}`
    })

    const formData = reactive({
      comment: '',
      notes: ''
    })

    const formRules = computed(() => ({
      comment: props.actionType === 'reject' ? [
        { required: true, message: '请输入拒绝理由', trigger: 'blur' },
        { min: 5, message: '拒绝理由至少5个字符', trigger: 'blur' }
      ] : []
    }))

    const getApplicantTypeColor = (type) => {
      const typeMap = {
        '教师': 'success',
        '学生': 'primary',
        '管理员': 'warning'
      }
      return typeMap[type] || 'info'
    }

    const resetForm = () => {
      formData.comment = ''
      formData.notes = ''
      if (formRef.value) {
        formRef.value.clearValidate()
      }
    }

    const handleClose = () => {
      resetForm()
      emit('cancel')
    }

    const handleSubmit = async () => {
      if (!formRef.value) return

      try {
        await formRef.value.validate()
        
        submitLoading.value = true
        
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        const result = {
          action: props.actionType,
          comment: formData.comment,
          notes: formData.notes,
          approvalTime: new Date().toISOString(),
          approver: '当前用户' // 实际项目中从用户状态获取
        }
        
        emit('confirm', result)
        resetForm()
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        submitLoading.value = false
      }
    }

    // 监听对话框打开，重置表单
    watch(() => props.modelValue, (newVal) => {
      if (newVal) {
        nextTick(() => {
          resetForm()
        })
      }
    })

    return {
      formRef,
      submitLoading,
      dialogVisible,
      dialogTitle,
      formData,
      formRules,
      getApplicantTypeColor,
      handleClose,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.approval-dialog-content {
  max-height: 500px;
  overflow-y: auto;
}

.section {
  margin-bottom: 24px;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #4A90E2;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.info-item.full-width {
  grid-column: 1 / -1;
  flex-direction: column;
  gap: 4px;
}

.info-item label {
  font-weight: 500;
  color: #666;
  min-width: 80px;
  flex-shrink: 0;
}

.info-item span {
  color: #333;
  line-height: 1.4;
}

.info-item .reason {
  background: #f5f5f5;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid #4A90E2;
  white-space: pre-wrap;
  word-break: break-word;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 自定义滚动条 */
.approval-dialog-content::-webkit-scrollbar {
  width: 6px;
}

.approval-dialog-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.approval-dialog-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.approval-dialog-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-item {
    flex-direction: column;
    gap: 4px;
  }
  
  .info-item label {
    min-width: auto;
  }
}
</style>