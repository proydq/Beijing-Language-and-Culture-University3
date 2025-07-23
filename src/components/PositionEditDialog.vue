<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑职务' : '添加新职务'"
    width="600px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="position-form"
    >
      <el-form-item label="职务名称:" prop="positionName">
        <el-input 
          v-model="formData.positionName" 
          placeholder="请输入职务名称" 
          style="width: 100%"
        />
      </el-form-item>
      
      <el-form-item label="职务描述:" prop="positionDesc">
        <el-input 
          v-model="formData.positionDesc" 
          type="textarea"
          :rows="4"
          placeholder="请输入职务描述" 
          style="width: 100%"
        />
      </el-form-item>
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
import { ElMessage } from 'element-plus'

export default {
  name: 'PositionEditDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    positionData: {
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
      positionName: '',
      positionDesc: ''
    })

    const formRules = {
      positionName: [
        { required: true, message: '请输入职务名称', trigger: 'blur' },
        { min: 2, max: 20, message: '职务名称长度应为2-20个字符', trigger: 'blur' }
      ],
      positionDesc: [
        { required: true, message: '请输入职务描述', trigger: 'blur' },
        { min: 5, max: 200, message: '职务描述长度应为5-200个字符', trigger: 'blur' }
      ]
    }

    // 监听弹窗显示状态
    watch(() => props.visible, (newVal) => {
      dialogVisible.value = newVal
      if (newVal && props.isEdit && props.positionData) {
        // 编辑模式，填充数据
        Object.assign(formData, {
          positionName: props.positionData.positionName || '',
          positionDesc: props.positionData.positionDesc || ''
        })
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
      formData.positionName = ''
      formData.positionDesc = ''
    }

    const handleClose = () => {
      dialogVisible.value = false
      resetForm()
    }

    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        emit('submit', { ...formData })
        ElMessage.success(props.isEdit ? '职务信息更新成功' : '职务添加成功')
        handleClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    return {
      dialogVisible,
      formRef,
      formData,
      formRules,
      handleClose,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.position-form {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
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

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
  resize: none;
}
</style>