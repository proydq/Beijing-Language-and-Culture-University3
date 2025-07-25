import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveAs } from 'file-saver'
import request from '@/utils/request'

export function usePositionManagement() {
  const positionFormRef = ref()
  const positionSearchKeyword = ref('')
  const positionDialogVisible = ref(false)
  const positionDialogMode = ref('add')
  const positionCurrentPage = ref(1)
  const positionPageSize = ref(10)
  const positionTotal = ref(0)

  const positionTableData = ref([])

  const positionFormData = reactive({
    id: null,
    name: '',
    description: ''
  })

  const positionOptions = ref([])
  const positionOptionsLoading = ref(false)

  const fetchPositionOptions = async () => {
    positionOptionsLoading.value = true
    try {
      const { code, message, data } = await request.get('/api/position/all')
      if (code === 200) {
        positionOptions.value = data || []
      } else {
        ElMessage.error(message || '获取职务下拉失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取职务下拉失败')
    } finally {
      positionOptionsLoading.value = false
    }
  }

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

  const fetchPositionList = async () => {
    try {
      const { code, message, data } = await request.post('/api/position/search', {
        name: positionSearchKeyword.value,
        pageNumber: positionCurrentPage.value,
        pageSize: positionPageSize.value
      })
      if (code === 200) {
        positionTableData.value = data?.rows || []
        positionTotal.value = data?.total || 0
      } else {
        ElMessage.error(message || '获取职务列表失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取职务列表失败')
    }
  }

  const handleAddPosition = () => {
    positionDialogMode.value = 'add'
    Object.assign(positionFormData, { id: null, name: '', description: '' })
    positionDialogVisible.value = true
  }

  const handleEditPosition = (row) => {
    positionDialogMode.value = 'edit'
    Object.assign(positionFormData, { id: row.id, name: row.name, description: row.description })
    positionDialogVisible.value = true
  }

  const handleDeletePosition = (row) => {
    ElMessageBox.confirm(`确定要删除职务"${row.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        const { code, message } = await request.delete(`/api/position/${row.id}`)
        if (code === 200) {
          ElMessage.success('删除成功')
          fetchPositionList()
        } else {
          ElMessage.error(message || '删除失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '删除失败')
      }
    })
  }

  const handlePositionSizeChange = (size) => {
    positionPageSize.value = size
    fetchPositionList()
  }

  const handlePositionCurrentChange = (page) => {
    positionCurrentPage.value = page
    fetchPositionList()
  }

  const resetPositionForm = () => {
    Object.assign(positionFormData, { id: null, name: '', description: '' })
  }

  const handlePositionDialogClose = () => {
    positionDialogVisible.value = false
    resetPositionForm()
  }

  const handlePositionSubmit = async () => {
    try {
      await positionFormRef.value.validate()
      if (positionDialogMode.value === 'add') {
        const { code, message } = await request.post('/api/position', {
          name: positionFormData.name,
          description: positionFormData.description
        })
        if (code === 200) {
          ElMessage.success('新增成功')
          handlePositionDialogClose()
          fetchPositionList()
        } else {
          ElMessage.error(message || '新增失败')
        }
      } else {
        const { code, message } = await request.put(`/api/position/${positionFormData.id}`, {
          name: positionFormData.name,
          description: positionFormData.description
        })
        if (code === 200) {
          ElMessage.success('编辑成功')
          handlePositionDialogClose()
          fetchPositionList()
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
      const blob = await request.get('/api/position/export', { responseType: 'blob' })
      saveAs(blob, 'positions.xlsx')
      ElMessage.success('导出成功')
    } catch (error) {
      ElMessage.error(error.message || '导出失败')
    }
  }

  const handleImport = async (file) => {
    const formData = new FormData()
    formData.append('file', file.raw || file)
    try {
      const { code, message } = await request.post('/api/position/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      if (code === 200) {
        ElMessage.success('导入成功')
        fetchPositionList()
      } else {
        ElMessage.error(message || '导入失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '导入失败')
    }
  }

  onMounted(() => {
    fetchPositionList()
    fetchPositionOptions()
  })

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
    fetchPositionList,
    handleAddPosition,
    handleEditPosition,
    handleDeletePosition,
    handlePositionSizeChange,
    handlePositionCurrentChange,
    handlePositionDialogClose,
    handlePositionSubmit,
    handleExport,
    handleImport,
    positionOptions,
    positionOptionsLoading,
    fetchPositionOptions
  }
}
