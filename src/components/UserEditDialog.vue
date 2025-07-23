<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑用户' : '添加新用户'"
    width="800px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="user-form"
    >
      <!-- 照片上传区域 -->
      <div class="photo-upload-section">
        <div class="photo-item">
          <div class="photo-label">头像</div>
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            action="#"
            :auto-upload="false"
            :on-change="handleAvatarChange"
          >
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
        
        <div class="photo-item">
          <div class="photo-label">人脸识别</div>
          <el-upload
            class="face-uploader"
            :show-file-list="false"
            :before-upload="beforeFaceUpload"
            action="#"
            :auto-upload="false"
            :on-change="handleFaceChange"
          >
            <img v-if="formData.faceImage" :src="formData.faceImage" class="face-image" />
            <el-icon v-else class="face-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
      </div>

      <!-- 基本信息 -->
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="姓名:" prop="name">
            <el-input v-model="formData.name" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="性别:" prop="gender">
            <el-select v-model="formData.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="手机号:" prop="phone">
            <el-input v-model="formData.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="所属部门:" prop="department">
            <el-select v-model="formData.department" placeholder="请选择部门" style="width: 100%">
              <el-option label="产品部门" value="产品部门" />
              <el-option label="开发部门" value="开发部门" />
              <el-option label="销售部门" value="销售部门" />
              <el-option label="市场部门" value="市场部门" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="工号:" prop="employeeId">
            <el-input v-model="formData.employeeId" placeholder="请输入工号" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="职务:" prop="position">
            <el-select v-model="formData.position" placeholder="请选择职务" style="width: 100%">
              <el-option label="产品经理" value="产品经理" />
              <el-option label="开发工程师" value="开发工程师" />
              <el-option label="销售经理" value="销售经理" />
              <el-option label="市场专员" value="市场专员" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="职级:" prop="role">
            <el-select v-model="formData.role" placeholder="请选择职级" style="width: 100%">
              <el-option label="初级" value="初级" />
              <el-option label="中级" value="中级" />
              <el-option label="高级" value="高级" />
              <el-option label="专家" value="专家" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 其他信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="一卡通:" prop="cardNumber">
            <el-input v-model="formData.cardNumber" placeholder="请输入一卡通号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="无感考勤编号:" prop="attendanceNumber">
            <el-input v-model="formData.attendanceNumber" placeholder="请输入无感考勤编号" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">立即提交</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, reactive, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'UserEditDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    userData: {
      type: Object,
      default: () => ({})
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  emits: ['update:visible', 'submit'],
  setup(props, { emit }) {
    const dialogVisible = ref(false)
    const formRef = ref()

    const formData = reactive({
      name: '',
      gender: '',
      phone: '',
      department: '',
      employeeId: '',
      position: '',
      role: '',
      cardNumber: '',
      attendanceNumber: '',
      avatar: '',
      faceImage: ''
    })

    const formRules = {
      name: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      gender: [
        { required: true, message: '请选择性别', trigger: 'change' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      department: [
        { required: true, message: '请选择所属部门', trigger: 'change' }
      ],
      employeeId: [
        { required: true, message: '请输入工号', trigger: 'blur' }
      ]
    }

    // 监听弹窗显示状态
    watch(() => props.visible, (newVal) => {
      dialogVisible.value = newVal
      if (newVal && props.isEdit && props.userData) {
        // 编辑模式，填充数据
        Object.assign(formData, props.userData)
      } else if (newVal && !props.isEdit) {
        // 新增模式，清空数据
        resetForm()
      }
    })

    // 监听内部弹窗状态变化
    watch(dialogVisible, (newVal) => {
      emit('update:visible', newVal)
    })

    const resetForm = () => {
      Object.keys(formData).forEach(key => {
        formData[key] = ''
      })
    }

    const handleClose = () => {
      dialogVisible.value = false
      resetForm()
    }

    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        emit('submit', { ...formData })
        ElMessage.success(props.isEdit ? '用户信息更新成功' : '用户添加成功')
        handleClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    // 头像上传相关
    const beforeAvatarUpload = (file) => {
      const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPGorPNG) {
        ElMessage.error('头像图片只能是 JPG/PNG 格式!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('头像图片大小不能超过 2MB!')
        return false
      }
      return true
    }

    const handleAvatarChange = (file) => {
      if (beforeAvatarUpload(file.raw)) {
        formData.avatar = URL.createObjectURL(file.raw)
      }
    }

    // 人脸识别图片上传相关
    const beforeFaceUpload = (file) => {
      const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPGorPNG) {
        ElMessage.error('人脸识别图片只能是 JPG/PNG 格式!')
        return false
      }
      if (!isLt2M) {
        ElMessage.error('人脸识别图片大小不能超过 2MB!')
        return false
      }
      return true
    }

    const handleFaceChange = (file) => {
      if (beforeFaceUpload(file.raw)) {
        formData.faceImage = URL.createObjectURL(file.raw)
      }
    }

    return {
      dialogVisible,
      formRef,
      formData,
      formRules,
      Plus,
      handleClose,
      handleSubmit,
      beforeAvatarUpload,
      handleAvatarChange,
      beforeFaceUpload,
      handleFaceChange
    }
  }
}
</script>

<style scoped>
.user-form {
  padding: 20px 0;
}

.photo-upload-section {
  display: flex;
  gap: 40px;
  margin-bottom: 30px;
  justify-content: center;
}

.photo-item {
  text-align: center;
}

.photo-label {
  margin-bottom: 10px;
  font-weight: 600;
  color: #333;
}

.avatar-uploader, .face-uploader {
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover, .face-uploader:hover {
  border-color: #409eff;
}

.avatar, .face-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 4px;
}

.avatar-uploader-icon, .face-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-dialog__header) {
  background-color: #f8f9fa;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

:deep(.el-dialog__body) {
  padding: 0 24px;
}

:deep(.el-dialog__footer) {
  padding: 20px 24px;
  border-top: 1px solid #e9ecef;
  background-color: #f8f9fa;
}

/* 上传组件样式优化 */
:deep(.el-upload) {
  width: 100%;
  height: 100%;
}

:deep(.el-upload-dragger) {
  width: 120px;
  height: 120px;
  border-radius: 6px;
}
</style>