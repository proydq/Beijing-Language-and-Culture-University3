<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑职级' : '添加新职级'"
    width="600px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="level-form"
    >
      <el-form-item label="职级名称:" prop="levelName">
        <el-input 
          v-model="formData.levelName" 
          placeholder="请输入中文" 
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
  name: 'LevelEditDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    levelData: {
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
      levelName: ''
    })

    const formRules = {
      levelName: [
        { required: true, message: '请输入职级名称', trigger: 'blur' },
        { min: 2, max: 20, message: '职级名称长度应为2-20个字符', trigger: 'blur' },
        { pattern: /^[\u4e00-\u9fa5]+$/, message: '请输入中文字符', trigger: 'blur' }
      ]
    }

    // 监听弹窗显示状态
    watch(() => props.visible, (newVal) => {
      dialogVisible.value = newVal
      if (newVal && props.isEdit && props.levelData) {
        // 编辑模式，填充数据
        formData.levelName = props.levelData.levelName || ''
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
      formData.levelName = ''
    }

    const handleClose = () => {
      dialogVisible.value = false
      resetForm()
    }

    const handleSubmit = async () => {
      try {
        await formRef.value.validate()
        emit('submit', { ...formData })
        ElMessage.success(props.isEdit ? '职级信息更新成功' : '职级添加成功')
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
.level-form {
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
</style>