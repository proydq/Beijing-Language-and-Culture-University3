<template>
  <!-- 用户详情对话框 -->
  <el-dialog
    :model-value="detailDialogVisible"
    @update:model-value="val => emit('update:detailDialogVisible', val)"
    title="用户详情"
    width="50%"
  >
    <div v-if="currentUser" class="user-detail">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ currentUser.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentUser.gender }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="工号">{{ currentUser.jobNumber }}</el-descriptions-item>
        <el-descriptions-item label="所属部门">{{ currentUser.department }}</el-descriptions-item>
        <el-descriptions-item label="职务">{{ currentUser.position }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ currentUser.jobTitle }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentUser.createTime }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </el-dialog>

  <!-- 新增/编辑用户对话框 -->
  <el-dialog
    :model-value="editDialogVisible"
    @update:model-value="val => emit('update:editDialogVisible', val)"
    :title="isEdit ? '编辑用户' : '新增用户'"
    width="700px"
    center
    destroy-on-close
    close-on-click-modal
    @close="handleDialogClose"
  >
    <el-form
      ref="formRef"
      :model="userForm"
      :rules="formRules"
      label-width="90px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="头像" prop="avatar">
            <el-upload
              v-model:file-list="avatarList"
              list-type="picture-card"
              :auto-upload="false"
              :limit="1"
              :on-change="handleAvatarChange"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="人脸识别照片" prop="faceImage">
            <el-upload
              v-model:file-list="faceList"
              list-type="picture-card"
              :auto-upload="false"
              :limit="1"
              :on-change="handleFaceChange"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="userForm.realName" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="userForm.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="userForm.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="所属部门" prop="department">
            <el-select v-model="userForm.department" placeholder="请选择部门" style="width: 100%">
              <el-option v-for="item in departments" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="工号" prop="jobNumber">
            <el-input v-model="userForm.jobNumber" placeholder="请输入工号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职务" prop="position">
            <el-select v-model="userForm.position" placeholder="请选择职务" style="width: 100%">
              <el-option v-for="item in positions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职称" prop="jobTitle">
            <el-select v-model="userForm.jobTitle" placeholder="请选择职称" style="width: 100%">
              <el-option v-for="item in titles" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="一卡通编号" prop="cardNumber">
            <el-input v-model="userForm.cardNumber" placeholder="请输入一卡通编号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="无感考勤编号" prop="attendanceNumber">
            <el-input v-model="userForm.attendanceNumber" placeholder="请输入无感考勤编号" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleDialogClose">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const props = defineProps({
  detailDialogVisible: { type: Boolean, default: false },
  editDialogVisible: { type: Boolean, default: false },
  currentUser: { type: Object, default: null },
  isEdit: { type: Boolean, default: false }
})

const emit = defineEmits(['update:detailDialogVisible', 'update:editDialogVisible', 'save-user'])

const formRef = ref()
const loading = ref(false)
const avatarList = ref([])
const faceList = ref([])

const userForm = reactive({
  avatar: '',
  faceImage: '',
  realName: '',
  gender: '',
  phone: '',
  department: '',
  jobNumber: '',
  position: '',
  jobTitle: '',
  cardNumber: '',
  attendanceNumber: ''
})

const departments = ref([
  { label: '技术部', value: '技术部' },
  { label: '行政部', value: '行政部' }
])

const positions = ref([
  { label: '前端工程师', value: '前端工程师' },
  { label: '后端工程师', value: '后端工程师' },
  { label: '人事专员', value: '人事专员' }
])

const titles = ref([
  { label: '初级工程师', value: '初级工程师' },
  { label: '中级工程师', value: '中级工程师' },
  { label: '高级工程师', value: '高级工程师' }
])

const formRules = {
  avatar: [{ required: true, message: '请上传头像', trigger: 'change' }],
  faceImage: [{ required: true, message: '请上传人脸识别照片', trigger: 'change' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  department: [{ required: true, message: '请选择部门', trigger: 'change' }],
  jobNumber: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  position: [{ required: true, message: '请选择职务', trigger: 'change' }],
  jobTitle: [{ required: true, message: '请选择职称', trigger: 'change' }],
  cardNumber: [{ required: true, message: '请输入一卡通编号', trigger: 'blur' }],
  attendanceNumber: [{ required: true, message: '请输入无感考勤编号', trigger: 'blur' }]
}

watch(
  () => props.editDialogVisible,
  val => {
    if (val) {
      if (props.isEdit && props.currentUser) {
        Object.assign(userForm, props.currentUser)
        avatarList.value = userForm.avatar ? [{ url: userForm.avatar }] : []
        faceList.value = userForm.faceImage ? [{ url: userForm.faceImage }] : []
      } else {
        resetForm()
      }
    }
  }
)

const resetForm = () => {
  Object.keys(userForm).forEach(k => (userForm[k] = ''))
  avatarList.value = []
  faceList.value = []
  formRef.value?.resetFields()
}

const handleDialogClose = () => {
  emit('update:editDialogVisible', false)
  resetForm()
}

const handleSubmit = async () => {
  loading.value = true
  try {
    await formRef.value.validate()
    emit('save-user', { ...userForm })
    ElMessage.success(props.isEdit ? '编辑成功' : '新增成功')
    handleDialogClose()
  } catch (e) {
    console.log('validate error', e)
  } finally {
    loading.value = false
  }
}

const handleAvatarChange = (file, fileList) => {
  avatarList.value = fileList.slice(-1)
  if (file.raw) {
    userForm.avatar = URL.createObjectURL(file.raw)
  }
}

const handleFaceChange = (file, fileList) => {
  faceList.value = fileList.slice(-1)
  if (file.raw) {
    userForm.faceImage = URL.createObjectURL(file.raw)
  }
}
</script>

<style scoped>
.user-detail {
  margin: 20px 0;
}

.dialog-footer {
  text-align: center;
}

:deep(.el-dialog__header) {
  background-color: #f8f9fa;
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8e8;
}

:deep(.el-dialog__body) {
  padding: 30px 24px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}
</style>
