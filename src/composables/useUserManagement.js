import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useFileUploadEnhanced } from '@/composables/useFileUploadEnhanced'
import { userAPI } from '@/api/user'
import { useUserStore } from '@/stores/user'

export function useUserManagement() {
  const router = useRouter()
  const userStore = useUserStore()
  
  // 初始化增强文件上传
  const avatarUpload = useFileUploadEnhanced({
    maxSize: 5 * 1024 * 1024, // 5MB
    allowedTypes: ['image/jpeg', 'image/png', 'image/gif'],
    category: 'avatar',
    onSuccess: (result) => {
      userForm.avatar = result.url
      avatarList.value = [{ url: result.url, name: result.fileInfo?.fileName || 'avatar' }]
    },
    onError: (error) => {
      ElMessage.error('头像上传失败: ' + (error.message || '未知错误'))
    }
  })
  
  const faceUpload = useFileUploadEnhanced({
    maxSize: 5 * 1024 * 1024, // 5MB
    allowedTypes: ['image/jpeg', 'image/png'],
    category: 'face',
    onSuccess: (result) => {
      userForm.faceImage = result.url
      faceList.value = [{ url: result.url, name: result.fileInfo?.fileName || 'face' }]
    },
    onError: (error) => {
      ElMessage.error('人脸照片上传失败: ' + (error.message || '未知错误'))
    }
  })

  // 基础数据
  const activeTab = ref('userList')
  const searchKeyword = ref('')
  const currentUser = ref(null)
  const detailDialogVisible = ref(false)
  const editDialogVisible = ref(false)
  const isEdit = ref(false)
  const formRef = ref()
  const avatarList = ref([])
  const faceList = ref([])

  // 分页数据
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)

  // 表单数据
  const searchForm = reactive({
    realName: '',
    phone: '',
    jobNumber: '',
    status: '',
    // 当前选中组织ID，用于组织树筛选
    organizationId: null
  })

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
    attendanceNumber: '',
    userType: '教师',
    status: '正常'
  })

  const departments = ref([])
  const departmentsLoading = ref(false)
  const positions = ref([])
  const positionsLoading = ref(false)
  const titles = ref([])
  const titlesLoading = ref(false)
  const userTypes = ref([])
  const userTypesLoading = ref(false)

  const fetchDepartments = async () => {
    departmentsLoading.value = true
    try {
      const { code, message, data } = await request.get('/api/organization/all')
      if (code === 200) {
        departments.value = data || []
      } else {
        ElMessage.error(message || '获取部门失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取部门失败')
    } finally {
      departmentsLoading.value = false
    }
  }

  const fetchPositions = async () => {
    positionsLoading.value = true
    try {
      const { code, message, data } = await request.get('/api/position/all')
      if (code === 200) {
        positions.value = data || []
      } else {
        ElMessage.error(message || '获取职务失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取职务失败')
    } finally {
      positionsLoading.value = false
    }
  }

  const fetchTitles = async () => {
    titlesLoading.value = true
    try {
      const { code, message, data } = await request.get('/api/title/all')
      if (code === 200) {
        titles.value = data || []
      } else {
        ElMessage.error(message || '获取职称失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取职称失败')
    } finally {
      titlesLoading.value = false
    }
  }

  const fetchUserTypes = async () => {
    userTypesLoading.value = true
    try {
      const response = await userAPI.getUserTypes()
      if (response.code === 200 || response.code === '0' || response.code === 0) {
        userTypes.value = response.data || []
      } else {
        // 如果后端没有返回数据，使用默认值
        userTypes.value = [
          { value: '教师', label: '教师' },
          { value: '学生', label: '学生' }
        ]
      }
    } catch (error) {
      console.error('获取用户类型失败:', error)
      // 发生错误时使用默认值
      userTypes.value = [
        { value: '教师', label: '教师' },
        { value: '学生', label: '学生' }
      ]
    } finally {
      userTypesLoading.value = false
    }
  }

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

  // 用户表格数据
  const tableData = ref([])

  // 获取用户列表
  const fetchUserList = async () => {
    try {
      console.log('开始获取用户列表，参数：', {
        ...searchForm,
        pageNumber: currentPage.value,
        pageSize: pageSize.value
      })
      
      const response = await userAPI.searchUsers({
        ...searchForm,
        pageNumber: currentPage.value,
        pageSize: pageSize.value
      })
      
      console.log('用户列表API响应：', response)
      
      if (response.code === '0' || response.code === 0 || response.code === 200) {
        tableData.value = response.data?.rows || []
        total.value = response.data?.total || 0
        console.log('用户列表加载成功，数据量：', tableData.value.length)
      } else {
        console.error('用户列表API返回错误：', response)
        ElMessage.error(response.message || '查询失败')
      }
    } catch (error) {
      console.error('用户列表API调用失败：', error)
      ElMessage.error(error.message || '查询失败')
      
      // 提供模拟数据以便测试界面
      console.log('使用模拟数据')
      tableData.value = [
        {
          id: '1',
          realName: '张教师',
          gender: '男',
          phone: '13800138001',
          jobNumber: 'T001',
          departmentName: '计算机学院',
          positionName: '教授',
          titleName: '高级职称',
                status: 'ACTIVE',
          createTime: '2024-01-15 10:00:00',
          avatarUrl: '',
          faceImageUrl: ''
        },
        {
          id: '2',
          realName: '李学生',
          gender: '女',
          phone: '13800138002',
          jobNumber: 'S001',
          departmentName: '计算机学院',
          positionName: '学生',
          titleName: '本科生',
          status: 'ACTIVE',
          createTime: '2024-02-20 14:00:00',
          avatarUrl: '',
          faceImageUrl: ''
        },
        {
          id: '3',
          realName: '王老师',
          gender: '男',
          phone: '13800138003',
          jobNumber: 'T002',
          departmentName: '外语学院',
          positionName: '副教授',
          titleName: '中级职称',
                status: 'ACTIVE',
          createTime: '2024-03-10 09:00:00',
          avatarUrl: '',
          faceImageUrl: ''
        }
      ]
      total.value = 3
    }
  }

  // 同步相关数据
  const syncDialogVisible = ref(false)
  const syncProgress = ref(0)
  const syncDuration = ref(0)
  const syncSummary = ref([
    { object: '用户信息', auto: '否', lastTime: '2023-07-20 10:00:00' }
  ])
  const syncLogDialogVisible = ref(false)
  const syncLogData = ref([
    {
      id: 1,
      object: '用户信息',
      status: '成功',
      reason: '',
      startTime: '2023-07-20 10:00:00',
      endTime: '2023-07-20 10:00:30',
      duration: 0.5
    },
    {
      id: 2,
      object: '用户信息',
      status: '失败',
      reason: '网络异常',
      startTime: '2023-07-19 09:00:00',
      endTime: '2023-07-19 09:00:20',
      duration: 0.33
    },
    {
      id: 3,
      object: '用户信息',
      status: '成功',
      reason: '',
      startTime: '2023-07-18 08:00:00',
      endTime: '2023-07-18 08:00:25',
      duration: 0.42
    }
  ])
  const logCurrentPage = ref(1)
  const logPageSize = ref(10)
  const logTotal = ref(3)

  // 表单验证规则
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
    attendanceNumber: [
      { required: true, message: '请输入无感考勤编号', trigger: 'blur' }
    ],
    userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
  }

  // 基础方法
  const goToHome = () => {
    router.push('/dashboard')
  }

  const goToPersonalCenter = () => {
    router.push('/personal-center')
  }

  const logout = () => {
    // 清除用户数据
    userStore.clearUserData()
    
    // 显示退出成功提示
    ElMessage.success('退出登录成功')
    
    // 跳转到登录页
    router.push('/login')
  }

  const handleTreeNodeClick = (data) => {
    // 组织树节点点击，设置组织ID并查询
    searchForm.organizationId = data.id
    currentPage.value = 1
    fetchUserList()
  }

  const handleTabClick = (tab) => {
    console.log('切换标签页:', tab.name)
  }

  // 用户管理方法
  const handleSearch = () => {
    currentPage.value = 1
    fetchUserList()
  }

  const handleReset = () => {
    Object.assign(searchForm, {
      realName: '',
      phone: '',
      jobNumber: '',
      status: '',
      // 组织ID 不重置
      organizationId: searchForm.organizationId
    })
    currentPage.value = 1
    fetchUserList()
  }

  let syncTimer = null
  const startSync = () => {
    syncProgress.value = 0
    syncDuration.value = 0
    syncDialogVisible.value = true
    if (syncTimer) clearInterval(syncTimer)
    syncTimer = setInterval(() => {
      if (syncProgress.value < 100) {
        syncProgress.value += 20
        syncDuration.value += 1
      } else {
        clearInterval(syncTimer)
      }
    }, 1000)
  }

  const showSyncLogs = () => {
    syncLogDialogVisible.value = true
  }

  const handleSyncCommand = (cmd) => {
    if (cmd === 'syncNow') {
      startSync()
    } else if (cmd === 'viewLogs') {
      showSyncLogs()
    }
  }

  const handleImport = () => {
    console.log('导入用户')
  }

  const resetForm = () => {
    // 重置表单到初始状态
    Object.assign(userForm, {
      id: undefined,
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
      attendanceNumber: '',
      userType: '教师',
      status: '正常'
    })
    avatarList.value = []
    faceList.value = []
    formRef.value?.resetFields()
    
    // 清空文件上传状态
    avatarUpload.clearFile()
    faceUpload.clearFile()
  }

  const handleAdd = () => {
    isEdit.value = false
    // 先关闭对话框，确保清理干净
    editDialogVisible.value = false
    // 使用 nextTick 确保 DOM 更新后再重置表单和打开对话框
    nextTick(() => {
      resetForm()
      fetchUserTypes()
      editDialogVisible.value = true
    })
  }

  const handleExport = () => {
    console.log('导出用户')
  }

  const handleView = (row) => {
    currentUser.value = row
    detailDialogVisible.value = true
  }

  const handleEdit = async (row) => {
    try {
      isEdit.value = true
      fetchUserTypes()
      // 调用后端接口获取用户详情
      const res = await userAPI.getUserById(row.id)
      if (res.code === '0' || res.code === 0 || res.code === 200) {
        // 使用后端返回的完整数据填充表单
        Object.assign(userForm, {
          id: res.data.id,
          realName: res.data.realName,
          gender: res.data.gender,
          phone: res.data.phone,
          jobNumber: res.data.jobNumber,
          department: res.data.departmentId,
          position: res.data.positionId,
          jobTitle: res.data.titleId,
          avatar: res.data.avatarUrl,
          faceImage: res.data.faceImageUrl,
          cardNumber: res.data.cardNumber,
          attendanceNumber: res.data.attendanceNumber,
          userType: res.data.userType || '教师',
          status: res.data.status
        })
        // 设置头像和人脸识别图片预览
        avatarList.value = userForm.avatar ? [{ url: userForm.avatar }] : []
        faceList.value = userForm.faceImage ? [{ url: userForm.faceImage }] : []
        editDialogVisible.value = true
      } else {
        ElMessage.error(res.message || '获取用户详情失败')
      }
    } catch (error) {
      ElMessage.error('获取用户详情失败')
      console.error('获取用户详情失败:', error)
    }
  }

  const handleDelete = async (row) => {
    try {
      await ElMessageBox.confirm(`确定要删除用户"${row.realName}"吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      // 调用后端删除接口
      const res = await request.delete(`/api/user/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        // 刷新用户列表
        fetchUserList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
        console.error('删除用户失败:', error)
      }
    }
  }

  const handleDialogClose = () => {
    editDialogVisible.value = false
    resetForm()
  }

  const handleSaveUser = async () => {
    try {
      await formRef.value.validate()
      const payload = {
        id: userForm.id,
        realName: userForm.realName,
        gender: userForm.gender,
        phone: userForm.phone,
        jobNumber: userForm.jobNumber,
        departmentId: userForm.department,
        positionId: userForm.position,
        titleId: userForm.jobTitle,
        avatarUrl: userForm.avatar,
        faceImageUrl: userForm.faceImage,
        cardNumber: userForm.cardNumber,
        attendanceNumber: userForm.attendanceNumber,
        userType: userForm.userType,
        status: userForm.status
      }
      let res
      if (isEdit.value && userForm.id) {
        res = await userAPI.updateUser(payload)
      } else {
        res = await userAPI.saveUser(payload)
      }
      if (res.code === '0' || res.code === 0 || res.code === 200) {
        ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
        handleDialogClose()
        fetchUserList()
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '保存失败')
    }
  }

  const handleAvatarChange = async (file, fileList) => {
    avatarList.value = fileList.slice(-1)
    if (!file.raw) return
    
    try {
      // 使用增强的文件上传，支持文件去重和秒传
      await avatarUpload.handleUpload(file.raw)
    } catch (e) {
      console.error('头像上传失败:', e)
    }
  }

  const handleFaceChange = async (file, fileList) => {
    faceList.value = fileList.slice(-1)
    if (!file.raw) return
    
    try {
      // 使用增强的文件上传，支持文件去重和秒传
      await faceUpload.handleUpload(file.raw)
    } catch (e) {
      console.error('人脸照片上传失败:', e)
    }
  }

  const handleSizeChange = (size) => {
    pageSize.value = size
    fetchUserList()
  }

  const handleCurrentChange = (page) => {
    currentPage.value = page
    fetchUserList()
  }

  const handleLogSizeChange = (size) => {
    logPageSize.value = size
  }

  const handleLogCurrentChange = (page) => {
    logCurrentPage.value = page
  }


  onMounted(() => {
    fetchUserList()
    fetchDepartments()
    fetchPositions()
    fetchTitles()
  })

  return {
    // 基础数据
    activeTab,
    searchKeyword,
    currentUser,
    detailDialogVisible,
    editDialogVisible,
    isEdit,
    formRef,
    avatarList,
    faceList,
    currentPage,
    pageSize,
    total,

    // 表单数据
    searchForm,
    userForm,

    // 模拟数据
    treeData,
    treeProps,
    tableData,

    // 表单验证规则
    formRules,
    departments,
    departmentsLoading,
    positions,
    positionsLoading,
    titles,
    titlesLoading,
    userTypes,
    userTypesLoading,
    fetchDepartments,
    fetchPositions,
    fetchTitles,
    fetchUserTypes,

    // 基础方法
    goToHome,
    goToPersonalCenter,
    logout,
    handleTreeNodeClick,
    handleTabClick,

    // 用户管理方法
    handleSearch,
    handleReset,
    syncDialogVisible,
    syncProgress,
    syncDuration,
    syncSummary,
    syncLogDialogVisible,
    syncLogData,
    logCurrentPage,
    logPageSize,
    logTotal,
    handleSyncCommand,
    handleLogSizeChange,
    handleLogCurrentChange,
    handleImport,
    handleAdd,
    handleExport,
    handleView,
    handleEdit,
    handleDelete,
    handleDialogClose,
    handleAvatarChange,
    handleFaceChange,
    handleSaveUser,
    handleSizeChange,
    handleCurrentChange,
    fetchUserList,
    
    // 文件上传状态
    avatarUploadProgress: avatarUpload.uploadProgress,
    avatarCalculating: avatarUpload.calculating,
    faceUploadProgress: faceUpload.uploadProgress,
    faceCalculating: faceUpload.calculating
  }
}
