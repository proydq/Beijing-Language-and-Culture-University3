import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useUserManagement() {
  const router = useRouter()
  
  // 基础数据
  const activeTab = ref('userList')
  const searchKeyword = ref('')
  const currentUser = ref(null)
  const detailDialogVisible = ref(false)
  const editDialogVisible = ref(false)
  const isEdit = ref(false)
  const editFormRef = ref()
  
  // 分页数据
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(100)
  
  // 表单数据
  const searchForm = reactive({
    realName: '',
    phone: '',
    jobNumber: '',
    status: ''
  })

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

  // 模拟数据
  const treeData = ref([
    {
      id: 1,
      name: '北京语言大学',
      type: 'company',
      count: 1200,
      children: [
        {
          id: 2,
          name: '技术部',
          type: 'department',
          count: 50,
          children: [
            { id: 3, name: '前端组', type: 'group', count: 20 },
            { id: 4, name: '后端组', type: 'group', count: 30 }
          ]
        },
        {
          id: 5,
          name: '行政部',
          type: 'department',
          count: 25,
          children: [
            { id: 6, name: '人事组', type: 'group', count: 15 },
            { id: 7, name: '财务组', type: 'group', count: 10 }
          ]
        }
      ]
    }
  ])

  const treeProps = {
    children: 'children',
    label: 'name'
  }

  const tableData = ref([
    {
      id: 1,
      realName: '张三',
      gender: '男',
      phone: '13800138001',
      jobNumber: 'JS001',
      department: '技术部',
      position: '前端工程师',
      jobTitle: '高级工程师',
      status: '正常',
      createTime: '2023-01-15 10:30:00'
    },
    {
      id: 2,
      realName: '李四',
      gender: '女',
      phone: '13800138002',
      jobNumber: 'JS002',
      department: '行政部',
      position: '人事专员',
      jobTitle: '中级专员',
      status: '正常',
      createTime: '2023-02-20 14:20:00'
    },
    {
      id: 3,
      realName: '王五',
      gender: '男',
      phone: '13800138003',
      jobNumber: 'JS003',
      department: '技术部',
      position: '后端工程师',
      jobTitle: '高级工程师',
      status: '禁用',
      createTime: '2023-03-10 09:15:00'
    }
  ])

  // 表单验证规则
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

  // 基础方法
  const goToHome = () => {
    router.push('/dashboard')
  }

  const goToPersonalCenter = () => {
    router.push('/personal-center')
  }

  const logout = () => {
    console.log('退出登录')
  }

  const handleTreeNodeClick = (data) => {
    console.log('选择部门:', data)
    // 根据选择的部门过滤用户数据
  }

  const handleTabClick = (tab) => {
    console.log('切换标签页:', tab.name)
  }

  // 用户管理方法
  const handleSearch = () => {
    console.log('搜索用户:', searchForm)
  }

  const handleReset = () => {
    Object.assign(searchForm, {
      realName: '',
      phone: '',
      jobNumber: '',
      status: ''
    })
  }

  const handleSync = () => {
    ElMessage.success('同步成功')
  }

  const handleImport = () => {
    console.log('导入用户')
  }

  const handleAdd = () => {
    isEdit.value = false
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
    editDialogVisible.value = true
  }

  const handleExport = () => {
    console.log('导出用户')
  }

  const handleView = (row) => {
    currentUser.value = row
    detailDialogVisible.value = true
  }

  const handleEdit = (row) => {
    isEdit.value = true
    Object.assign(editForm, row)
    editDialogVisible.value = true
  }

  const handleDelete = (row) => {
    ElMessageBox.confirm(`确定要删除用户"${row.realName}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      ElMessage.success('删除成功')
    })
  }

  const handleSaveUser = async () => {
    try {
      await editFormRef.value.validate()
      ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
      editDialogVisible.value = false
    } catch (error) {
      console.log('表单验证失败:', error)
    }
  }

  const handleSizeChange = (size) => {
    pageSize.value = size
  }

  const handleCurrentChange = (page) => {
    currentPage.value = page
  }

  return {
    // 基础数据
    activeTab,
    searchKeyword,
    currentUser,
    detailDialogVisible,
    editDialogVisible,
    isEdit,
    editFormRef,
    currentPage,
    pageSize,
    total,
    
    // 表单数据
    searchForm,
    editForm,
    
    // 模拟数据
    treeData,
    treeProps,
    tableData,
    
    // 表单验证规则
    editRules,
    
    // 基础方法
    goToHome,
    goToPersonalCenter,
    logout,
    handleTreeNodeClick,
    handleTabClick,
    
    // 用户管理方法
    handleSearch,
    handleReset,
    handleSync,
    handleImport,
    handleAdd,
    handleExport,
    handleView,
    handleEdit,
    handleDelete,
    handleSaveUser,
    handleSizeChange,
    handleCurrentChange
  }
}