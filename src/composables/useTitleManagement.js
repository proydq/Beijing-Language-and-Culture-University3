import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveAs } from 'file-saver'
import request from '@/utils/request'

export function useTitleManagement() {
  // 职称管理相关 ref
  const titleFormRef = ref()
  const titleSearchKeyword = ref('')
  const titleDialogVisible = ref(false)
  const titleDialogMode = ref('add') // 'add' or 'edit'
  const titleCurrentPage = ref(1)
  const titlePageSize = ref(10)
  const titleTotal = ref(0)

  // 职称数据
  const titleTableData = ref([])

  const titleFormData = reactive({
    id: null,
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

  const fetchTitleList = async () => {
    try {
      const { code, message, data } = await request.post('/api/title/search', {
        name: titleSearchKeyword.value,
        pageNumber: titleCurrentPage.value,
        pageSize: titlePageSize.value
      })
      if (code === 200) {
        titleTableData.value = data?.rows || []
        titleTotal.value = data?.total || 0
      } else {
        ElMessage.error(message || '获取职称列表失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取职称列表失败')
    }
  }

  // 职称管理方法
  const handleTitleSearch = () => {
    titleCurrentPage.value = 1
    fetchTitleList()
  }

  const handleAddTitle = () => {
    titleDialogMode.value = 'add'
    Object.assign(titleFormData, {
      id: null,
      name: '',
      description: ''
    })
    titleDialogVisible.value = true
  }

  const handleEditTitle = (row) => {
    titleDialogMode.value = 'edit'
    Object.assign(titleFormData, {
      id: row.id,
      name: row.name,
      description: row.description
    })
    titleDialogVisible.value = true
  }

  const handleDeleteTitle = (row) => {
    ElMessageBox.confirm(`确定要删除职称"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        const { code, message } = await request.delete(`/api/title/${row.id}`)
        if (code === 200) {
          ElMessage.success('删除成功')
          fetchTitleList()
        } else {
          ElMessage.error(message || '删除失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
      }
    })
  }

  const handleTitleSizeChange = (size) => {
    titlePageSize.value = size
    fetchTitleList()
  }

  const handleTitleCurrentChange = (page) => {
    titleCurrentPage.value = page
    fetchTitleList()
  }

  const resetTitleForm = () => {
    Object.assign(titleFormData, {
      id: null,
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
      if (titleDialogMode.value === 'add') {
        const { code, message } = await request.post('/api/title', {
          name: titleFormData.name,
          description: titleFormData.description
        })
        if (code === 200) {
          ElMessage.success('新增成功')
          handleTitleDialogClose()
          fetchTitleList()
        } else {
          ElMessage.error(message || '新增失败')
        }
      } else {
        const { code, message } = await request.put(`/api/title/${titleFormData.id}`, {
          name: titleFormData.name,
          description: titleFormData.description
        })
        if (code === 200) {
          ElMessage.success('编辑成功')
          handleTitleDialogClose()
          fetchTitleList()
        } else {
          ElMessage.error(message || '编辑失败')
        }
      }
    } catch (error) {
      ElMessage.error(error.message || '提交失败')
    }
  }

  const handleExport = async () => {
    try {
      const blob = await request.get('/api/title/export', { responseType: 'blob' })
      saveAs(blob, 'titles.xlsx')
      ElMessage.success('导出成功')
    } catch (error) {
      ElMessage.error(error.message || '导出失败')
    }
  }

  const handleImport = async (file) => {
    const formData = new FormData()
    formData.append('file', file.raw || file)
    try {
      const { code, message } = await request.post('/api/title/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      if (code === 200) {
        ElMessage.success('导入成功')
        fetchTitleList()
      } else {
        ElMessage.error(message || '导入失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '导入失败')
    }
  }

  onMounted(fetchTitleList)

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
    fetchTitleList,
    handleTitleSearch,
    handleAddTitle,
    handleEditTitle,
    handleDeleteTitle,
    handleTitleSizeChange,
    handleTitleCurrentChange,
    handleTitleDialogClose,
    handleTitleSubmit,
    handleExport,
    handleImport
  }
}
