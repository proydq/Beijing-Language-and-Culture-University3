<template>
  <div class="house-management">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo" @click="goToHome" style="cursor: pointer;">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">房屋管理</span>
      </div>
      <div class="header-right">
        <div class="avatar">
          <el-icon size="20"><user /></el-icon>
        </div>
        <span class="username">系统管理员</span>
        <el-dropdown>
          <span class="dropdown-link">
            <el-icon><grid /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToPersonalCenter">个人中心</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="main-container">
      <!-- 右侧主内容 -->
      <div class="main-content">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item @click="goToHome" style="cursor: pointer; color: #4A90E2;">首页</el-breadcrumb-item>
            <el-breadcrumb-item>房屋管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 标签页 -->
        <el-tabs v-model="activeTab" class="management-tabs">
          <el-tab-pane label="房屋列表" name="house">
            <div class="house-page-wrapper">
              <div class="building-panel">
                <h3 class="panel-title">楼栋架构</h3>
                <el-input v-model="buildingKeyword" placeholder="请输入关键字" class="tree-search" clearable>
                  <template #suffix>
                    <el-icon><search /></el-icon>
                  </template>
                </el-input>
                <el-tree :data="buildingTreeData" default-expand-all class="structure-tree" @node-click="handleBuildingNodeClick" />
              </div>
              <div class="house-page-main">
                <!-- 搜索筛选区域 -->
                <div class="search-area">
                  <el-form :model="searchForm" :inline="true">
                    <el-form-item label="房间号码:">
                      <el-input v-model="searchForm.roomNo" placeholder="请输入房间号码" />
                    </el-form-item>
                    <el-form-item label="房屋名称:">
                      <el-input v-model="searchForm.roomName" placeholder="请输入房屋名称" />
                    </el-form-item>
                    <el-form-item label="房屋类型:">
                      <el-select v-model="searchForm.roomTypeName" placeholder="请选择房屋类型">
                        <el-option label="全部" value="" />
                        <el-option 
                          v-for="roomType in roomTypeOptions" 
                          :key="roomType.id" 
                          :label="roomType.typeName" 
                          :value="roomType.typeName" 
                        />
                      </el-select>
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" @click="handleSearch">
                        <el-icon><search /></el-icon>
                        搜索
                      </el-button>
                      <el-button @click="handleReset">重置</el-button>
                    </el-form-item>
                  </el-form>
                </div>

                <!-- 操作按钮区域 -->
                <div class="action-area">
                  <div class="left-actions">
                    <el-button type="primary" @click="handleAddRoom">添加房间</el-button>
                    <el-button @click="handleImport">导入房间</el-button>
                    <el-button @click="handleExport">导出房间</el-button>
                  </div>
                </div>

                <!-- 房屋列表表格 -->
                <div class="house-table">
                  <el-table :data="houseTableData" style="width: 100%" stripe v-loading="loading" element-loading-text="加载中...">
                    <el-table-column prop="roomCode" label="房间编码" width="120" />
                    <el-table-column prop="roomName" label="房屋名称" />
                    <el-table-column prop="roomNo" label="房间号码" width="100" />
                    <el-table-column prop="roomTypeName" label="房屋类型" width="100">
                      <template #default="scope">
                        <el-tag :type="getRoomTypeTagType(scope.row.roomTypeName)">
                          {{ scope.row.roomTypeName }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="roomArea" label="房间面积(㎡)" width="120" />
                    <el-table-column prop="capacity" label="容纳人数" width="100" />
                    <el-table-column prop="lastUpdateTime" label="更新时间" width="150" />
                    <el-table-column label="操作" width="180" fixed="right">
                      <template #default="scope">
                        <el-button type="primary" size="small" @click="handleEditRoom(scope.row)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDeleteRoom(scope.row)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>

                  <!-- 分页 -->
                  <div class="pagination-area">
                    <el-pagination
                      v-model:current-page="housePagination.currentPage"
                      v-model:page-size="housePagination.pageSize"
                      :page-sizes="[10, 20, 50, 100]"
                      :total="housePagination.total"
                      layout="total, sizes, prev, pager, next, jumper"
                      @size-change="handleHouseSizeChange"
                      @current-change="handleHouseCurrentChange"
                    />
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="楼宇管理" name="building">
            <!-- 操作按钮区域 -->
            <div class="action-area">
              <el-button type="primary" @click="handleAddParent" :disabled="!selectedNode">主题</el-button>
              <el-button type="primary" @click="handleAddChild" :disabled="!selectedNode">子主题</el-button>
              <el-button type="danger" @click="handleDelete" :disabled="!selectedNode || selectedNode.id === 1">删除</el-button>
            </div>

            <!-- 楼宇树形结构 -->
            <div class="tree-area">
              <div v-loading="treeLoading" element-loading-text="加载中...">
                <el-tree
                  ref="treeRef"
                  :data="treeData"
                  :props="treeProps"
                  node-key="id"
                  default-expand-all
                  highlight-current
                  @node-click="handleNodeClick"
                >
                  <template #default="{ node, data }">
                    <span class="tree-node">
                      <el-icon v-if="data.type === 'building'"><office-building /></el-icon>
                      <el-icon v-else-if="data.type === 'floor'"><menu /></el-icon>
                      <el-icon v-else><home /></el-icon>
                      <span class="node-text">{{ node.label }}</span>
                      <span class="node-count" v-if="data.count !== undefined">[{{ data.count }}]</span>
                    </span>
                  </template>
                </el-tree>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 房屋信息编辑弹窗 -->
    <el-dialog
      v-model="roomDialogVisible"
      :title="isEditMode ? '编辑房间' : '添加房间'"
      width="800px"
      @close="handleRoomDialogClose"
    >
      <el-form ref="roomFormRef" :model="roomForm" :rules="roomRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间编码:" prop="roomCode">
              <el-input v-model="roomForm.roomCode" placeholder="请输入房间编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋名称:" prop="roomName">
              <el-input v-model="roomForm.roomName" placeholder="请输入房屋名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号码:" prop="roomNo">
              <el-input v-model="roomForm.roomNo" placeholder="请输入房间号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋类型:" prop="roomTypeName">
              <el-select v-model="roomForm.roomTypeName" placeholder="请选择房屋类型" @change="handleRoomTypeChange">
                <el-option 
                  v-for="roomType in roomTypeOptions" 
                  :key="roomType.id" 
                  :label="roomType.typeName" 
                  :value="roomType.typeName" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间面积:" prop="roomArea">
              <el-input v-model="roomForm.roomArea" placeholder="请输入房间面积(㎡)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="容纳人数:" prop="capacity">
              <el-input v-model="roomForm.capacity" placeholder="请输入容纳人数" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 注意事项 -->
        <div class="notice-section">
          <h4>重要提示</h4>
          <div class="notice-content">
            <p><strong>一、房屋信息录入规范</strong></p>
            <p>1. <strong>核心字段必填：</strong>添加房间时，"房屋类型" 为关键必选字段，请确保准确填写。</p>
            <p>2. <strong>唯一性要求：</strong>房屋名称与房间号的组合必须具有唯一性，重复信息将无法成录入。</p>

            <p><strong>二、业务关联与数据同步</strong></p>
            <p>1. <strong>跨系统数据同步：</strong>房屋添加成功后，如用于工会议预约、实验室预约、教室借用等业务，会在对应业务系统中生成同步房屋信息。</p>

            <p><strong>三、房屋删除须知</strong></p>
            <p style="color: red;"><strong>若在房屋管理系统中删除房屋操作，所有关联业务系统中的该房屋信息将同步删除，请谨慎操作。</strong></p>
          </div>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleRoomDialogClose">取消</el-button>
          <el-button type="primary" @click="handleRoomSubmit">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 添加节点弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="名称:" prop="name">
          <el-input v-model="formData.name" placeholder="请输入名称" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import {
  getAreaTree,
  getAreaById,
  addArea,
  updateArea,
  deleteArea,
  cascadeDeleteArea,
  searchArea,
  AREA_TYPES,
  AREA_TYPE_LABELS
} from '@/api/area.js'
import {
  searchRooms,
  getRoomById,
  addRoom,
  updateRoom,
  deleteRoom,
  batchDeleteRooms,
  getRoomTypes,
  checkRoomUniqueness,
  importRooms,
  exportRooms,
  ROOM_TYPES,
  ROOM_TYPE_LABELS
} from '@/api/room.js'

export default {
  name: 'HouseManagement',
  setup() {
    const router = useRouter()
    const activeTab = ref('house')
    const treeRef = ref()
    const formRef = ref()
    const roomFormRef = ref()
    const dialogVisible = ref(false)
    const roomDialogVisible = ref(false)
    const isEditMode = ref(false)
    const currentRoomData = ref({})
    const selectedNode = ref(null)
    const actionType = ref('')

    // 搜索表单
    const searchForm = reactive({
      roomNo: '',
      roomName: '',
      roomTypeName: '',
      roomAreaId: '',
      roomAreaName: '',
      roomCode: ''
    })

    // 房间表单
    const roomForm = reactive({
      id: '',
      roomName: '',
      roomAreaId: '',
      roomAreaName: '',
      roomNo: '',
      roomTypeId: '',
      roomTypeName: '教室',
      roomVolume: '',
      remark: '',
      roomCode: '',
      extend2: '',
      roomArea: '',
      capacity: ''
    })

    // 添加节点表单
    const formData = reactive({
      name: ''
    })

    // 分页数据
    const housePagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 100
    })

    // 表单验证规则
    const roomRules = {
      roomName: [
        { required: true, message: '请输入房屋名称', trigger: 'blur' }
      ],
      roomNo: [
        { required: true, message: '请输入房间号码', trigger: 'blur' }
      ],
      roomTypeName: [
        { required: true, message: '请选择房屋类型', trigger: 'change' }
      ]
    }

    const formRules = {
      name: [
        { required: true, message: '请输入名称', trigger: 'blur' }
      ]
    }

    // 楼栋结构树及搜索关键字
    const buildingKeyword = ref('')
    const buildingTreeData = ref([])
    
    // 当前选中的楼栋区域信息
    const selectedBuildingArea = ref(null)

    // 楼宇树形数据
    const treeData = ref([])

    const treeProps = {
      children: 'children',
      label: 'areaName'
    }

    // 房屋数据
    const houseTableData = ref([])
    
    // 房间类型选项
    const roomTypeOptions = ref([])

    // 加载状态
    const loading = ref(false)
    const treeLoading = ref(false)

    // 计算属性
    const dialogTitle = computed(() => {
      if (actionType.value === 'parent') {
        return selectedNode.value ? `在"${selectedNode.value.areaName}"同级添加主题` : '添加主题'
      } else {
        return selectedNode.value ? `在"${selectedNode.value.areaName}"下添加子主题` : '添加子主题'
      }
    })

    // 返回首页
    const goToHome = () => {
      router.push('/dashboard')
    }

    const goToPersonalCenter = () => {
      router.push('/personal-center')
    }

    const logout = () => {
      console.log('退出登录')
    }

    // 获取房屋类型标签类型
    const getRoomTypeTagType = (type) => {
      const typeMap = {
        '教室': 'primary',
        '会议室': 'success',
        '实验室': 'warning',
        '办公室': 'info'
      }
      return typeMap[type] || 'default'
    }

    // API调用方法
    const loadAreaTree = async () => {
      try {
        treeLoading.value = true
        const response = await getAreaTree()
        if (response.code === 200) {
          treeData.value = response.data || []
        } else {
          ElMessage.error(response.message || '获取区域树失败')
        }
      } catch (error) {
        console.error('获取区域树失败:', error)
        ElMessage.error('获取区域树失败')
      } finally {
        treeLoading.value = false
      }
    }

    // 加载楼栋架构数据
    const loadBuildingTree = async () => {
      try {
        const response = await getAreaTree()
        if (response.code === 200) {
          // 将后端返回的区域树数据转换为前端需要的格式
          const formatTreeData = (nodes) => {
            return nodes.map(node => ({
              label: node.areaName,
              id: node.id,
              type: node.type,
              children: node.children && node.children.length > 0 ? formatTreeData(node.children) : undefined
            }))
          }
          buildingTreeData.value = formatTreeData(response.data || [])
        } else {
          ElMessage.error(response.message || '获取楼栋架构失败')
        }
      } catch (error) {
        console.error('获取楼栋架构失败:', error)
        ElMessage.error('获取楼栋架构失败')
      }
    }

    const loadRoomList = async () => {
      try {
        loading.value = true
        const condition = {
          roomName: searchForm.roomName,
          roomNo: searchForm.roomNo,
          roomTypeName: searchForm.roomTypeName,
          roomAreaId: searchForm.roomAreaId,
          roomAreaName: searchForm.roomAreaName,
          roomCode: searchForm.roomCode,
          pageNumber: housePagination.currentPage,
          pageSize: housePagination.pageSize
        }
        const response = await searchRooms(condition)
        if (response.code === 200) {
          const pageData = response.data
          houseTableData.value = pageData.rows || []
          housePagination.total = pageData.total || 0
        } else {
          ElMessage.error(response.message || '获取房间列表失败')
        }
      } catch (error) {
        console.error('获取房间列表失败:', error)
        ElMessage.error('获取房间列表失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索相关方法
    const handleSearch = () => {
      housePagination.currentPage = 1
      loadRoomList()
    }

    // 加载房间类型列表
    const loadRoomTypes = async () => {
      try {
        const response = await getRoomTypes()
        if (response.code === 200) {
          roomTypeOptions.value = response.data || []
        } else {
          ElMessage.error(response.message || '获取房间类型失败')
        }
      } catch (error) {
        console.error('获取房间类型失败:', error)
        ElMessage.error('获取房间类型失败')
      }
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        roomNo: '',
        roomName: '',
        roomTypeName: '',
        roomAreaId: '',
        roomAreaName: '',
        roomCode: ''
      })
      handleSearch()
    }

    // 房屋管理相关方法
    const handleAddRoom = () => {
      isEditMode.value = false
      resetRoomForm()
      
      // 如果有选中的楼栋区域，自动填充到房间表单中
      if (selectedBuildingArea.value) {
        roomForm.roomAreaId = selectedBuildingArea.value.id
        roomForm.roomAreaName = selectedBuildingArea.value.name
      }
      
      roomDialogVisible.value = true
    }

    const handleEditRoom = (row) => {
      isEditMode.value = true
      currentRoomData.value = { ...row }
      Object.assign(roomForm, row)
      roomDialogVisible.value = true
    }

    const handleDeleteRoom = (row) => {
      ElMessageBox.confirm(`确定要删除房间"${row.roomName}"吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await deleteRoom(row.id)
          if (response.code === 200) {
            ElMessage.success('删除成功')
            await loadRoomList()
          } else {
            ElMessage.error(response.message || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      })
    }

    const handleImport = () => {
      // 创建文件输入元素
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = '.xlsx,.xls'
      input.onchange = async (event) => {
        const file = event.target.files[0]
        if (!file) return
        
        const formData = new FormData()
        formData.append('file', file)
        
        try {
          const response = await importRooms(formData)
          if (response.code === 200) {
            ElMessage.success(response.data || '导入成功')
            await loadRoomList()
          } else {
            ElMessage.error(response.message || '导入失败')
          }
        } catch (error) {
          console.error('导入失败:', error)
          ElMessage.error('导入失败')
        }
      }
      input.click()
    }

    const handleExport = async () => {
      try {
        const condition = {
          roomName: searchForm.roomName,
          roomNo: searchForm.roomNo,
          roomTypeName: searchForm.roomTypeName,
          roomAreaId: searchForm.roomAreaId,
          roomAreaName: searchForm.roomAreaName,
          roomCode: searchForm.roomCode
        }
        
        const response = await exportRooms(condition)
        
        // 创建下载链接
        const blob = new Blob([response], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `房间数据_${new Date().toISOString().slice(0, 10)}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        
        ElMessage.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        ElMessage.error('导出失败')
      }
    }

    const handleHouseSizeChange = (size) => {
      housePagination.pageSize = size
      loadRoomList()
    }

    const handleHouseCurrentChange = (page) => {
      housePagination.currentPage = page
      loadRoomList()
    }

    const resetRoomForm = () => {
      Object.assign(roomForm, {
        id: '',
        roomName: '',
        roomAreaId: '',
        roomAreaName: '',
        roomNo: '',
        roomTypeId: '',
        roomTypeName: '教室',
        roomVolume: '',
        remark: '',
        roomCode: '',
        extend2: '',
        roomArea: '',
        capacity: ''
      })
    }

    const handleRoomDialogClose = () => {
      roomDialogVisible.value = false
      resetRoomForm()
    }

    // 房间类型变更处理
    const handleRoomTypeChange = (typeName) => {
      const selectedType = roomTypeOptions.value.find(type => type.typeName === typeName)
      if (selectedType) {
        roomForm.roomTypeId = selectedType.id
      }
    }

    const handleRoomSubmit = async () => {
      try {
        await roomFormRef.value?.validate()

        // 检查房间唯一性
        const uniqueResponse = await checkRoomUniqueness(
          roomForm.roomName, 
          roomForm.roomNo, 
          isEditMode.value ? roomForm.id : null
        )
        if (uniqueResponse.code === 200 && !uniqueResponse.data) {
          ElMessage.error('房屋名称和房间号码组合已存在')
          return
        }

        const roomData = {
          roomName: roomForm.roomName,
          roomAreaId: roomForm.roomAreaId,
          roomAreaName: roomForm.roomAreaName,
          roomNo: roomForm.roomNo,
          roomTypeId: roomForm.roomTypeId,
          roomTypeName: roomForm.roomTypeName,
          roomVolume: roomForm.capacity ? parseInt(roomForm.capacity) : null,
          remark: roomForm.remark,
          roomCode: roomForm.roomCode,
          extend2: roomForm.extend2,
          roomArea: roomForm.roomArea,
          capacity: roomForm.capacity
        }

        if (isEditMode.value) {
          // 编辑房间
          const response = await updateRoom(roomForm.id, roomData)
          if (response.code === 200) {
            ElMessage.success('房间信息更新成功')
            handleRoomDialogClose()
            await loadRoomList()
          } else {
            ElMessage.error(response.message || '更新失败')
          }
        } else {
          // 新增房间
          const response = await addRoom(roomData)
          if (response.code === 200) {
            ElMessage.success('房间添加成功')
            handleRoomDialogClose()
            await loadRoomList()
          } else {
            ElMessage.error(response.message || '添加失败')
          }
        }
      } catch (error) {
        console.error('房间操作失败:', error)
        ElMessage.error('操作失败')
      }
    }

    // 楼宇管理相关方法
    const handleNodeClick = data => {
      selectedNode.value = data
      console.log('选中节点:', data)
    }

    const handleAddParent = () => {
      if (!selectedNode.value) {
        ElMessage.warning('请先选择节点')
        return
      }
      actionType.value = 'parent'
      formData.name = ''
      dialogVisible.value = true
    }

    const handleAddChild = () => {
      if (!selectedNode.value) {
        ElMessage.warning('请先选择节点')
        return
      }
      actionType.value = 'child'
      formData.name = ''
      dialogVisible.value = true
    }

    const handleDelete = () => {
      if (!selectedNode.value) {
        ElMessage.warning('请先选择节点')
        return
      }
      ElMessageBox.confirm(`确定要删除"${selectedNode.value.areaName}"吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await cascadeDeleteArea(selectedNode.value.id)
          if (response.code === 200) {
            ElMessage.success('删除成功')
            selectedNode.value = null
            await loadAreaTree()
          } else {
            ElMessage.error(response.message || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      })
    }

    const handleDialogClose = () => {
      dialogVisible.value = false
      formData.name = ''
    }

    const handleSubmit = async () => {
      try {
        await formRef.value?.validate()

        const areaData = {
          areaName: formData.name,
          parentId: actionType.value === 'child' ? selectedNode.value.id : selectedNode.value.parentId,
          type: actionType.value === 'child' ? getChildType(selectedNode.value.type) : selectedNode.value.type
        }

        const response = await addArea(areaData)
        if (response.code === 200) {
          ElMessage.success('添加成功')
          handleDialogClose()
          await loadAreaTree()
        } else {
          ElMessage.error(response.message || '添加失败')
        }
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error('添加失败')
      }
    }

    // 根据父节点类型确定子节点类型
    const getChildType = (parentType) => {
      if (parentType === AREA_TYPES.BUILDING) {
        return AREA_TYPES.FLOOR
      } else if (parentType === AREA_TYPES.FLOOR) {
        return AREA_TYPES.ROOM
      }
      return AREA_TYPES.ROOM
    }

    // 处理楼栋架构树节点点击事件
    const handleBuildingNodeClick = (data) => {
      // 保存选中的区域信息
      selectedBuildingArea.value = {
        id: data.id,
        name: data.label
      }
      // 设置区域查询条件
      searchForm.roomAreaId = data.id
      searchForm.roomAreaName = data.label
      // 重置分页并查询
      housePagination.currentPage = 1
      loadRoomList()
      ElMessage.success(`已选择区域：${data.label}`)
    }

    // 页面初始化
    onMounted(() => {
      loadAreaTree()
      loadBuildingTree()
      loadRoomTypes()
      loadRoomList()
    })

    return {
      activeTab,
      treeRef,
      formRef,
      roomFormRef,
      dialogVisible,
      roomDialogVisible,
      isEditMode,
      currentRoomData,
      selectedNode,
      actionType,
      searchForm,
      roomForm,
      formData,
      housePagination,
      roomRules,
      formRules,
      treeData,
      treeProps,
      houseTableData,
      roomTypeOptions,
      buildingKeyword,
      buildingTreeData,
      selectedBuildingArea,
      dialogTitle,
      loading,
      treeLoading,
      Search,
      goToHome,
      goToPersonalCenter,
      logout,
      getRoomTypeTagType,
      handleSearch,
      handleReset,
      handleAddRoom,
      handleEditRoom,
      handleDeleteRoom,
      handleImport,
      handleExport,
      handleHouseSizeChange,
      handleHouseCurrentChange,
      handleRoomDialogClose,
      handleRoomSubmit,
      handleRoomTypeChange,
      handleNodeClick,
      handleAddParent,
      handleAddChild,
      handleDelete,
      handleDialogClose,
      handleSubmit,
      handleBuildingNodeClick,
      loadRoomTypes,
      loadBuildingTree,
      AREA_TYPES,
      AREA_TYPE_LABELS,
      ROOM_TYPES,
      ROOM_TYPE_LABELS
    }
  }
}
</script>

<style scoped>
.house-management {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: white;
  color: #4A90E2;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.05);
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.username {
  font-size: 16px;
  font-weight: 500;
}

.dropdown-link {
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-container {
  display: flex;
  height: calc(100vh - 70px);
}


.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.breadcrumb {
  margin-bottom: 20px;
  padding: 15px 0;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child)) {
  cursor: pointer;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child):hover) {
  color: #357ABD;
}

.management-tabs {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.action-area {
  margin-bottom: 20px;
}

.left-actions {
  display: flex;
  gap: 10px;
}

.house-table {
  margin-bottom: 20px;
}

.pagination-area {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.house-page-wrapper {
  display: flex;
  gap: 20px;
}

.building-panel {
  width: 260px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  flex-shrink: 0;
}

.panel-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.tree-search {
  margin-bottom: 15px;
}

.structure-tree {
  min-height: 400px;
}

.house-page-main {
  flex: 1;
}

.tree-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  min-height: 400px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-text {
  font-size: 14px;
}

.node-count {
  color: #999;
  font-size: 12px;
}

.notice-section {
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 6px;
}

.notice-section h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-weight: 600;
}

.notice-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.notice-content p {
  margin: 8px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
