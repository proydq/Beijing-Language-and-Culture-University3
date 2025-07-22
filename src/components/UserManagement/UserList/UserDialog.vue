<template>
  <!-- 用户详情对话框 -->
  <el-dialog 
    :model-value="detailDialogVisible"
    @update:model-value="$emit('update:detailDialogVisible', $event)"
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

  <!-- 编辑/新增用户对话框 -->
  <el-dialog 
    :model-value="editDialogVisible"
    @update:model-value="$emit('update:editDialogVisible', $event)"
    :title="isEdit ? '编辑用户' : '新增用户'"
    width="600px"
    @close="handleEditDialogClose"
  >
    <el-form
      ref="editFormRef"
      :model="editForm"
      :rules="editRules"
      label-width="80px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="姓名:" prop="realName">
            <el-input v-model="editForm.realName" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别:" prop="gender">
            <el-radio-group v-model="editForm.gender">
              <el-radio value="男">男</el-radio>
              <el-radio value="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="手机号:" prop="phone">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="工号:" prop="jobNumber">
            <el-input v-model="editForm.jobNumber" placeholder="请输入工号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="部门:" prop="department">
            <el-select v-model="editForm.department" placeholder="请选择部门" style="width: 100%;">
              <el-option label="技术部" value="技术部" />
              <el-option label="行政部" value="行政部" />
              <el-option label="销售部" value="销售部" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职务:" prop="position">
            <el-select v-model="editForm.position" placeholder="请选择职务" style="width: 100%;">
              <el-option label="前端工程师" value="前端工程师" />
              <el-option label="后端工程师" value="后端工程师" />
              <el-option label="人事专员" value="人事专员" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="职称:" prop="jobTitle">
            <el-select v-model="editForm.jobTitle" placeholder="请选择职称" style="width: 100%;">
              <el-option label="初级工程师" value="初级工程师" />
              <el-option label="中级工程师" value="中级工程师" />
              <el-option label="高级工程师" value="高级工程师" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态:" prop="status">
            <el-radio-group v-model="editForm.status">
              <el-radio value="正常">正常</el-radio>
              <el-radio value="禁用">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleEditDialogClose">取消</el-button>
        <el-button type="primary" @click="handleSaveUser">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'UserDialog',
  props: {
    detailDialogVisible: {
      type: Boolean,
      default: false
    },
    editDialogVisible: {
      type: Boolean,
      default: false
    },
    currentUser: {
      type: Object,
      default: null
    },
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  emits: [
    'update:detailDialogVisible',
    'update:editDialogVisible',
    'save-user'
  ],
  setup(props, { emit }) {
    const editFormRef = ref()
    
    const editForm = reactive({
      realName: '',
      gender: '',
      phone: '',
      jobNumber: '',
      department: '',
      position: '',
      jobTitle: '',
      status: '正常'
    })

    const editRules = {
      realName: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      jobNumber: [
        { required: true, message: '请输入工号', trigger: 'blur' }
      ]
    }

    // 监听编辑用户数据变化
    watch(() => props.currentUser, (newUser) => {
      if (newUser && props.isEdit) {
        Object.assign(editForm, newUser)
      }
    }, { immediate: true })

    const handleEditDialogClose = () => {
      emit('update:editDialogVisible', false)
      // 重置表单
      Object.assign(editForm, {
        realName: '',
        gender: '',
        phone: '',
        jobNumber: '',
        department: '',
        position: '',
        jobTitle: '',
        status: '正常'
      })
      editFormRef.value?.resetFields()
    }

    const handleSaveUser = async () => {
      try {
        await editFormRef.value.validate()
        emit('save-user', { ...editForm })
        ElMessage.success(props.isEdit ? '编辑成功' : '新增成功')
        handleEditDialogClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    return {
      editFormRef,
      editForm,
      editRules,
      handleEditDialogClose,
      handleSaveUser
    }
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