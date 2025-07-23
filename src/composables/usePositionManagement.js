import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export function usePositionManagement() {
  // 职务管理相关 ref
  const positionFormRef = ref()
  const positionSearchKeyword = ref('')
  const positionDialogVisible = ref(false)
  const positionDialogMode = ref('add') // 'add' or 'edit'
  const positionCurrentPage = ref(1)
  const positionPageSize = ref(10)
  const positionTotal = ref(50)

  // 职务数据
  const positionTableData = ref([
    {
      id: 1,
      name: '前端工程师',
      description: '负责前端页面开发和用户交互设计',
      createTime: '2023-01-15 10:30:00'
    },
    {
      id: 2,
      name: '后端工程师',
      description: '负责服务器端开发和数据库设计',
      createTime: '2023-01-16 14:20:00'
    },
    {
      id: 3,
      name: '产品经理',
      description: '负责产品规划和需求分析',
      createTime: '2023-01-17 09:15:00'
    }
  ])

  const positionFormData = reactive({
    name: '',
    description: ''
  })

  const positionFormRules = {
    name: [
      { required: true, message: '请输入职务名称', trigger: 'blur' },
      { min: 2, max: 20, message: '职务名称长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    description: [
      { required: true, message: '请输入职务描述', trigger: 'blur' },
      { min: 5, max: 200, message: '职务描述长度在 5 到 200 个字符', trigger: 'blur' }
    ]
  }

  // 职务管理方法
  const handlePositionSearch = () => {
    console.log('搜索职务:', positionSearchKeyword.value)
  }

  const handleAddPosition = () => {
    positionDialogMode.value = 'add'
    Object.assign(positionFormData, {
      name: '',
      description: ''
    })
    positionDialogVisible.value = true
  }

  const handleEditPosition = (row) => {
    positionDialogMode.value = 'edit'
    Object.assign(positionFormData, row)
    positionDialogVisible.value = true
  }

  const handleDeletePosition = (row) => {
    ElMessageBox.confirm(`确定要删除职务"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      ElMessage.success('删除成功')
    })
  }

  const handlePositionSizeChange = (size) => {
    positionPageSize.value = size
  }

  const handlePositionCurrentChange = (page) => {
    positionCurrentPage.value = page
  }

  const resetPositionForm = () => {
    Object.assign(positionFormData, {
      name: '',
      description: ''
    })
  }

  const handlePositionDialogClose = () => {
    positionDialogVisible.value = false
    resetPositionForm()
  }

  const handlePositionSubmit = async () => {
    try {
      await positionFormRef.value.validate()
      ElMessage.success(positionDialogMode.value === 'add' ? '新增成功' : '编辑成功')
      handlePositionDialogClose()
    } catch (error) {
      console.log('表单验证失败:', error)
    }
  }

  return {
    positionFormRef,
    positionSearchKeyword,
    positionDialogVisible,
    positionDialogMode,
    positionCurrentPage,
    positionPageSize,
    positionTotal,
    positionTableData,
    positionFormData,
    positionFormRules,
    handlePositionSearch,
    handleAddPosition,
    handleEditPosition,
    handleDeletePosition,
    handlePositionSizeChange,
    handlePositionCurrentChange,
    handlePositionDialogClose,
    handlePositionSubmit
  }
}