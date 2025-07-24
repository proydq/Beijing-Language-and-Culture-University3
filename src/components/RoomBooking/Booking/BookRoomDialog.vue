<template>
  <el-dialog
    v-model="dialogVisible"
    title="预约信息"
    width="800px"
    :close-on-click-modal="false"
    @close="handleCancel"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
      <div class="section">
        <h4 class="section-title">基本信息</h4>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约人" prop="applicant">
              <el-input v-model="formData.applicant" placeholder="请输入预约人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约名称">
              <el-input v-model="formData.bookingName" placeholder="请输入预约名称" />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="section">
        <h4 class="section-title">借用时间</h4>
        <el-form-item prop="borrowTime">
          <el-input
            v-model="formData.borrowTime"
            readonly
            placeholder="请选择借用时间"
            style="width: 70%; margin-right: 8px"
          />
          <el-button type="primary" @click="timeDialogVisible = true">选择时间</el-button>
        </el-form-item>
      </div>

      <div class="section">
        <h4 class="section-title">其他信息</h4>
        <el-form-item label="借用描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="参与人">
          <el-select
            v-model="formData.participants"
            multiple
            placeholder="请选择参与人"
            style="width: 100%"
          >
            <el-option
              v-for="item in memberOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注详情">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </div>

      <div class="section">
        <h4 class="section-title">审批信息</h4>
        <el-form-item label="审批人" prop="approvers">
          <el-select
            v-model="formData.approvers"
            multiple
            placeholder="请选择审批人"
            style="width: 100%"
          >
            <el-option
              v-for="item in approverOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="tip">选择多人后，任意一个审批人通过即可流转到下一级</div>
        </el-form-item>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
      </div>
    </template>
  </el-dialog>
  <TimeSelectorDialog
    v-model:visible="timeDialogVisible"
    @selectTime="handleTimeSelected"
  />
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import TimeSelectorDialog from './TimeSelectorDialog.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  room: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const dialogVisible = ref(false)
watch(
  () => props.modelValue,
  val => {
    dialogVisible.value = val
    if (val) {
      nextTick(() => {
        resetForm()
      })
    }
  }
)
watch(dialogVisible, val => emit('update:modelValue', val))

const timeDialogVisible = ref(false)

const formRef = ref()

const formData = reactive({
  applicant: '',
  bookingName: '',
  borrowTime: '',
  description: '',
  participants: [],
  remark: '',
  approvers: []
})

const memberOptions = ref([
  { label: '张三', value: '张三' },
  { label: '李四', value: '李四' },
  { label: '王五', value: '王五' }
])

const approverOptions = ref([
  { label: '赵主管', value: '赵主管' },
  { label: '钱经理', value: '钱经理' },
  { label: '孙校长', value: '孙校长' }
])

const rules = {
  applicant: [{ required: true, message: '请输入预约人姓名', trigger: 'blur' }],
  borrowTime: [{ required: true, message: '请选择借用时间', trigger: 'change' }],
  approvers: [{ required: true, message: '请选择审批人', trigger: 'change' }]
}

const formatSelectedTimes = (times) =>
  times.map(t => `${t.date} 第${t.period + 1}节`).join(', ')

function handleTimeSelected(times) {
  formData.borrowTime = formatSelectedTimes(times)
}

function resetForm() {
  formData.applicant = ''
  formData.bookingName = ''
  formData.borrowTime = ''
  formData.description = ''
  formData.participants = []
  formData.remark = ''
  formData.approvers = []
  formRef.value?.clearValidate()
}

function handleCancel() {
  dialogVisible.value = false
  emit('cancel')
}

async function handleConfirm() {
  try {
    await formRef.value.validate()
    emit('confirm', { ...formData, room: props.room })
    dialogVisible.value = false
  } catch (e) {
    // ignore
  }
}
</script>

<style scoped>
.section {
  margin-bottom: 24px;
}

.section-title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.tip {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

