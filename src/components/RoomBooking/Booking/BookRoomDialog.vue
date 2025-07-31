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
          <div class="person-selector">
            <el-input
              :model-value="participantsDisplay"
              readonly
              placeholder="点击选择参与人"
              style="flex: 1; margin-right: 8px"
            />
            <el-button type="primary" @click="showParticipantSelector">选择</el-button>
          </div>
          <div v-if="selectedParticipants.length > 0" class="selected-persons">
            <el-tag
              v-for="person in selectedParticipants"
              :key="person.id"
              closable
              @close="removeParticipant(person)"
              style="margin: 4px 4px 0 0"
            >
              {{ person.name }}
            </el-tag>
          </div>
        </el-form-item>
        <el-form-item label="备注详情">
          <el-input v-model="formData.remark" type="textarea" :rows="3" />
        </el-form-item>
      </div>

      <div class="section">
        <h4 class="section-title">审批信息</h4>
        <el-form-item label="审批人" prop="approvers">
          <div class="person-selector">
            <el-input
              :model-value="approversDisplay"
              readonly
              placeholder="点击选择审批人"
              style="flex: 1; margin-right: 8px"
            />
            <el-button type="primary" @click="showApproverSelector">选择</el-button>
          </div>
          <div v-if="selectedApprovers.length > 0" class="selected-persons">
            <el-tag
              v-for="person in selectedApprovers"
              :key="person.id"
              closable
              @close="removeApprover(person)"
              style="margin: 4px 4px 0 0"
            >
              {{ person.name }}
            </el-tag>
          </div>
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
  
  <!-- 人员选择弹窗 -->
  <PersonSelectorDialog
    v-model:visible="participantSelectorVisible"
    :selected-values="selectedParticipants"
    person-type="member"
    @confirm="handleParticipantSelected"
  />
  
  <PersonSelectorDialog
    v-model:visible="approverSelectorVisible"
    :selected-values="selectedApprovers"
    person-type="approver"
    @confirm="handleApproverSelected"
  />
</template>

<script setup>
import { ref, reactive, watch, nextTick, computed } from 'vue'
import TimeSelectorDialog from './TimeSelectorDialog.vue'
import PersonSelectorDialog from './PersonSelectorDialog.vue'

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
const participantSelectorVisible = ref(false)
const approverSelectorVisible = ref(false)

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

// 人员选择相关
const selectedParticipants = ref([])
const selectedApprovers = ref([])

// 显示文本
const participantsDisplay = computed(() => {
  return selectedParticipants.value.map(p => p.name).join(', ')
})

const approversDisplay = computed(() => {
  return selectedApprovers.value.map(p => p.name).join(', ')
})

const rules = {
  applicant: [{ required: true, message: '请输入预约人姓名', trigger: 'blur' }],
  borrowTime: [{ required: true, message: '请选择借用时间', trigger: 'change' }],
  approvers: [
    { 
      required: true, 
      message: '请选择审批人', 
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (selectedApprovers.value.length === 0) {
          callback(new Error('请选择审批人'))
        } else {
          callback()
        }
      }
    }
  ]
}

const formatSelectedTimes = (times) =>
  times.map(t => `${t.date} 第${t.period + 1}节`).join(', ')

function handleTimeSelected(times) {
  formData.borrowTime = formatSelectedTimes(times)
}

// 显示参与人选择器
function showParticipantSelector() {
  participantSelectorVisible.value = true
}

// 显示审批人选择器
function showApproverSelector() {
  approverSelectorVisible.value = true
}

// 处理参与人选择
function handleParticipantSelected(persons) {
  selectedParticipants.value = persons
  formData.participants = persons.map(p => p.name)
}

// 处理审批人选择
function handleApproverSelected(persons) {
  selectedApprovers.value = persons
  formData.approvers = persons.map(p => p.name)
  // 触发表单验证
  formRef.value?.validateField('approvers')
}

// 移除参与人
function removeParticipant(person) {
  const index = selectedParticipants.value.findIndex(p => p.id === person.id)
  if (index > -1) {
    selectedParticipants.value.splice(index, 1)
    formData.participants = selectedParticipants.value.map(p => p.name)
  }
}

// 移除审批人
function removeApprover(person) {
  const index = selectedApprovers.value.findIndex(p => p.id === person.id)
  if (index > -1) {
    selectedApprovers.value.splice(index, 1)
    formData.approvers = selectedApprovers.value.map(p => p.name)
    // 触发表单验证
    formRef.value?.validateField('approvers')
  }
}

function resetForm() {
  formData.applicant = ''
  formData.bookingName = ''
  formData.borrowTime = ''
  formData.description = ''
  formData.participants = []
  formData.remark = ''
  formData.approvers = []
  selectedParticipants.value = []
  selectedApprovers.value = []
  formRef.value?.clearValidate()
}

function handleCancel() {
  dialogVisible.value = false
  emit('cancel')
}

async function handleConfirm() {
  try {
    await formRef.value.validate()
    const submitData = {
      ...formData,
      room: props.room,
      participantDetails: selectedParticipants.value,
      approverDetails: selectedApprovers.value
    }
    emit('confirm', submitData)
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

.person-selector {
  display: flex;
  align-items: center;
}

.selected-persons {
  margin-top: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>

