import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

export function useOrganizationManagement() {
  // 组织架构管理相关 ref
  const orgTreeRef = ref()
  const orgFormRef = ref()
  const orgDialogFormRef = ref()
  const orgSearchKeyword = ref('')
  const selectedOrgNode = ref(null)
  const orgEditMode = ref(false)
  const orgDialogVisible = ref(false)
  const orgDialogMode = ref('add') // 'add' or 'edit'
  const orgActionType = ref('')

  const departmentOptions = ref([])
  const departmentLoading = ref(false)

  const fetchDepartmentOptions = async () => {
    departmentLoading.value = true
    try {
      const { code, message, data } = await request.get('/api/organization/all')
      if (code === 200) {
        departmentOptions.value = data || []
      } else {
        ElMessage.error(message || '获取部门下拉失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取部门下拉失败')
    } finally {
      departmentLoading.value = false
    }
  }

  // 组织架构数据
  const orgTreeData = ref([
    {
      id: 1,
      name: '总公司',
      code: 'ROOT001',
      description: '总公司',
      children: [
        {
          id: 2,
          name: '技术部',
          code: 'TECH001',
          description: '技术研发部门',
          children: [
            { id: 3, name: '前端组', code: 'FE001', description: '前端开发组' },
            { id: 4, name: '后端组', code: 'BE001', description: '后端开发组' }
          ]
        },
        {
          id: 5,
          name: '行政部',
          code: 'ADMIN001',
          description: '行政管理部门',
          children: [
            { id: 6, name: '人事组', code: 'HR001', description: '人力资源组' },
            { id: 7, name: '财务组', code: 'FIN001', description: '财务管理组' }
          ]
        }
      ]
    }
  ])

  const orgFormData = reactive({
    name: '',
    code: '',
    description: ''
  })

  const orgDialogFormData = reactive({
    name: '',
    code: '',
    parentId: null,
    description: ''
  })

  const orgMembersData = ref([
    {
      id: 1,
      realName: '张三',
      jobNumber: 'JS001',
      position: '前端开发工程师',
      phone: '13800138001'
    },
    {
      id: 2,
      realName: '李四',
      jobNumber: 'JS002',
      position: '后端开发工程师',
      phone: '13800138002'
    }
  ])

  // 组织表单验证规则
  const orgFormRules = {
    name: [
      { required: true, message: '请输入组织名称', trigger: 'blur' }
    ],
    code: [
      { required: true, message: '请输入组织编码', trigger: 'blur' }
    ]
  }

  const orgDialogFormRules = {
    name: [
      { required: true, message: '请输入组织名称', trigger: 'blur' }
    ],
    code: [
      { required: true, message: '请输入组织编码', trigger: 'blur' }
    ]
  }

  // ====================== 后端请求 ======================
  const fetchOrganizationTree = async () => {
    try {
      const { code, message, data } = await request.get('/api/organization/tree')
      if (code === 200) {
        orgTreeData.value = data || []
      } else {
        ElMessage.error(message || '获取组织树失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取组织树失败')
    }
  }

  const loadOrganizationDetail = async (id) => {
    try {
      const { code, message, data } = await request.get(`/api/organization/${id}`)
      if (code === 200) {
        Object.assign(orgFormData, {
          name: data.name || '',
          code: data.code || '',
          description: data.description || ''
        })
        if (orgDialogMode.value === 'edit') {
          Object.assign(orgDialogFormData, {
            name: data.name || '',
            code: data.code || '',
            parentId: data.parentId || null,
            description: data.description || ''
          })
        }
      } else {
        ElMessage.error(message || '获取组织详情失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '获取组织详情失败')
    }
  }

  const createOrganization = async () => {
    try {
      const { code, message } = await request.post('/api/organization', orgDialogFormData)
      if (code === 200) {
        ElMessage.success('新增成功')
        handleOrgDialogClose()
        fetchOrganizationTree()
      } else {
        ElMessage.error(message || '新增失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '新增失败')
    }
  }

  const updateOrganization = async () => {
    try {
      const id = selectedOrgNode.value?.id
      const { code, message } = await request.put(`/api/organization/${id}`, orgDialogFormData)
      if (code === 200) {
        ElMessage.success('编辑成功')
        handleOrgDialogClose()
        fetchOrganizationTree()
        if (id) {
          loadOrganizationDetail(id)
        }
      } else {
        ElMessage.error(message || '编辑失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '编辑失败')
    }
  }

  const deleteOrganization = async (id) => {
    try {
      const { code, message } = await request.delete(`/api/organization/${id}`)
      if (code === 200) {
        ElMessage.success('删除成功')
        fetchOrganizationTree()
        selectedOrgNode.value = null
      } else {
        ElMessage.error(message || '删除失败')
      }
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  }

  // 组织架构管理方法
  const handleOrgSearch = () => {
    console.log('搜索组织:', orgSearchKeyword.value)
  }

  const handleOrgTreeNodeClick = (data) => {
    selectedOrgNode.value = data
    orgEditMode.value = false
    loadOrganizationDetail(data.id)
  }

  const handleAddOrgParent = () => {
    orgActionType.value = 'addParent'
    orgDialogMode.value = 'add'
    Object.assign(orgDialogFormData, {
      name: '',
      code: '',
      parentId: null,
      description: ''
    })
    orgDialogVisible.value = true
  }

  const handleAddOrgChild = () => {
    if (!selectedOrgNode.value) {
      ElMessage.warning('请先选择一个组织节点')
      return
    }
    orgActionType.value = 'addChild'
    orgDialogMode.value = 'add'
    Object.assign(orgDialogFormData, {
      name: '',
      code: '',
      parentId: selectedOrgNode.value.id,
      description: ''
    })
    orgDialogVisible.value = true
  }

  const handleDeleteOrg = () => {
    if (!selectedOrgNode.value) {
      ElMessage.warning('请先选择一个组织节点')
      return
    }
    ElMessageBox.confirm(`确定要删除组织"${selectedOrgNode.value.name}"吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      deleteOrganization(selectedOrgNode.value.id)
    })
  }

  const handleEditOrg = async () => {
    if (!selectedOrgNode.value) {
      ElMessage.warning('请先选择一个组织节点')
      return
    }
    orgActionType.value = 'edit'
    orgDialogMode.value = 'edit'
    await loadOrganizationDetail(selectedOrgNode.value.id)
    orgDialogVisible.value = true
  }

  const handleCancelOrgEdit = () => {
    orgEditMode.value = false
    // 重置表单数据
    if (selectedOrgNode.value) {
      Object.assign(orgFormData, {
        name: selectedOrgNode.value.name,
        code: selectedOrgNode.value.code,
        description: selectedOrgNode.value.description
      })
    }
  }

  const handleSaveOrg = async () => {
    try {
      await orgFormRef.value.validate()
      orgEditMode.value = false
      ElMessage.success('保存成功')
      // 这里实现保存逻辑
    } catch (error) {
      console.log('表单验证失败:', error)
    }
  }

  const handleAddOrgMember = () => {
    console.log('添加组织成员')
    // 这里可以打开添加成员的弹窗
  }

  const handleRemoveOrgMember = (row) => {
    ElMessageBox.confirm(`确定要移除成员"${row.realName}"吗？`, '确认移除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      const index = orgMembersData.value.findIndex(item => item.id === row.id)
      if (index !== -1) {
        orgMembersData.value.splice(index, 1)
      }
      ElMessage.success('移除成功')
    })
  }

  const resetOrgDialogForm = () => {
    Object.assign(orgDialogFormData, {
      name: '',
      code: '',
      parentId: null,
      description: ''
    })
  }

  const handleOrgDialogClose = () => {
    orgDialogVisible.value = false
    resetOrgDialogForm()
  }

  const handleOrgDialogSubmit = async () => {
    try {
      await orgDialogFormRef.value.validate()
      if (orgDialogMode.value === 'add') {
        await createOrganization()
      } else {
        await updateOrganization()
      }
    } catch (error) {
      console.log('表单验证失败:', error)
    }
  }

  onMounted(() => {
    fetchOrganizationTree()
    fetchDepartmentOptions()
  })

  return {
    // 组织架构管理相关数据
    orgTreeRef,
    orgFormRef,
    orgDialogFormRef,
    orgSearchKeyword,
    selectedOrgNode,
    orgEditMode,
    orgDialogVisible,
    orgDialogMode,
    orgActionType,
    orgTreeData,
    orgFormData,
    orgDialogFormData,
    orgMembersData,
    departmentOptions,
    departmentLoading,
    fetchDepartmentOptions,
    orgFormRules,
    orgDialogFormRules,
    
    // 组织架构管理方法
    handleOrgSearch,
    handleOrgTreeNodeClick,
    handleAddOrgParent,
    handleAddOrgChild,
    handleDeleteOrg,
    handleEditOrg,
    handleCancelOrgEdit,
    handleSaveOrg,
    handleAddOrgMember,
    handleRemoveOrgMember,
    handleOrgDialogClose,
    handleOrgDialogSubmit
  }
}