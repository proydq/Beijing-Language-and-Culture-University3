import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useTitleManagement() {
  // 职称管理相关 ref
  const titleFormRef = ref()
  const titleSearchKeyword = ref('')
  const titleDialogVisible = ref(false)
  const titleDialogMode = ref('add') // 'add' or 'edit'
  const titleCurrentPage = ref(1)
  const titlePageSize = ref(10)
  const titleTotal = ref(30)

  // 职称数据
  const titleTableData = ref([
    {
      id: 1,
      name: '初级工程师',
      description: '入门级别的技术人员，负责基础开发工作',
      createTime: '2023-01-15 10:30:00'
    },
    {
      id: 2,
      name: '中级工程师',
      description: '有一定经验的技术人员，能独立完成开发任务',
      createTime: '2023-01-16 14:20:00'
    },
    {
      id: 3,
      name: '高级工程师',
      description: '资深技术人员，能指导团队和架构设计',
      createTime: '2023-01-17 09:15:00'
    },
    {
      id: 4,
      name: '技术专家',
      description: '在特定技术领域有深入研究的专业人员',
      createTime: '2023-01-18 11:45:00'
    }
  ])

  const titleFormData = reactive({
    name: '',
    description: ''
  })

  const titleFormRules = {
    name: [
      { required: true, message: '请输入职称名称', trigger: 'blur' },
      { min: 2, max: 20, message: '职称名称长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    description: [
      { required: true, message: '请输入职称描述', trigger: 'blur' },
      { min: 5, max: 200, message: '职称描述长度在 5 到 200 个字符', trigger: 'blur' }
    ]
  }

  // 职称管理方法
  const handleTitleSearch = () => {
    console.log('搜索职称:', titleSearchKeyword.value)
  }

  const handleAddTitle = () => {
    titleDialogMode.value = 'add'
    Object.assign(titleFormData, {
      name: '',
      description: ''
    })
    titleDialogVisible.value = true
  }

  const handleEditTitle = (row) => {
    titleDialogMode.value = 'edit'
    Object.assign(titleFormData, row)
    titleDialogVisible.value = true
  }

  const handleDeleteTitle = (row) => {
    ElMessageBox.confirm(`确定要删除职称"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      ElMessage.success('删除成功')
    })
  }

  const handleTitleSizeChange = (size) => {
    titlePageSize.value = size
  }

  const handleTitleCurrentChange = (page) => {
    titleCurrentPage.value = page
  }

  const resetTitleForm = () => {
    Object.assign(titleFormData, {
      name: '',
      description: ''
    })
  }

  const handleTitleDialogClose = () => {
    titleDialogVisible.value = false
    resetTitleForm()
  }

  const handleTitleSubmit = async () => {
    try {
      await titleFormRef.value.validate()
      ElMessage.success(titleDialogMode.value === 'add' ? '新增成功' : '编辑成功')
      handleTitleDialogClose()
    } catch (error) {
      console.log('表单验证失败:', error)
    }
  }

  return {
    titleFormRef,
    titleSearchKeyword,
    titleDialogVisible,
    titleDialogMode,
    titleCurrentPage,
    titlePageSize,
    titleTotal,
    titleTableData,
    titleFormData,
    titleFormRules,
    handleTitleSearch,
    handleAddTitle,
    handleEditTitle,
    handleDeleteTitle,
    handleTitleSizeChange,
    handleTitleCurrentChange,
    handleTitleDialogClose,
    handleTitleSubmit
  }
}