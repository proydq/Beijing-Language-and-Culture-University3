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
      <!-- 左侧导航 -->
      <div class="sidebar">
        <!-- 添加首页导航 -->
        <div class="nav-item" @click="goToHome">
          <el-icon><home /></el-icon>
          <span>首页</span>
        </div>
      </div>

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
            <!-- 搜索筛选区域 -->
            <div class="search-area">
              <el-form :model="searchForm" :inline="true">
                <el-form-item label="房间号码:">
                  <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号码" />
                </el-form-item>
                <el-form-item label="房屋名称:">
                  <el-input v-model="searchForm.roomName" placeholder="请输入房屋名称" />
                </el-form-item>
                <el-form-item label="房屋类型:">
                  <el-select v-model="searchForm.roomType" placeholder="请选择房屋类型">
                    <el-option label="全部" value="" />
                    <el-option label="教室" value="教室" />
                    <el-option label="会议室" value="会议室" />
                    <el-option label="实验室" value="实验室" />
                    <el-option label="办公室" value="办公室" />
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
              <el-table :data="houseTableData" style="width: 100%" stripe>
                <el-table-column prop="roomCode" label="房间编码" width="120" />
                <el-table-column prop="roomName" label="房屋名称" />
                <el-table-column prop="roomNumber" label="房间号码" width="100" />
                <el-table-column prop="roomType" label="房屋类型" width="100">
                  <template #default="scope">
                    <el-tag :type="getRoomTypeTagType(scope.row.roomType)">
                      {{ scope.row.roomType }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="roomArea" label="房间面积(㎡)" width="120" />
                <el-table-column prop="capacity" label="容纳人数" width="100" />
                <el-table-column prop="updateTime" label="更新时间" width="150" />
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
                    <el-icon v-if="data.type === 'root'"><office-building /></el-icon>
                    <el-icon v-else-if="data.type === 'building'"><office-building /></el-icon>
                    <el-icon v-else-if="data.type === 'floor'"><menu /></el-icon>
                    <el-icon v-else><home /></el-icon>
                    <span class="node-text">{{ node.label }}</span>
                    <span class="node-count" v-if="data.count !== undefined">[{{ data.count }}]</span>
                  </span>
                </template>
              </el-tree>
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
            <el-form-item label="房间号码:" prop="roomNumber">
              <el-input v-model="roomForm.roomNumber" placeholder="请输入房间号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋类型:" prop="roomType">
              <el-select v-model="roomForm.roomType" placeholder="请选择房屋类型">
                <el-option label="教室" value="教室" />
                <el-option label="会议室" value="会议室" />
                <el-option label="实验室" value="实验室" />
                <el-option label="办公室" value="办公室" />
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
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

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
      roomNumber: '',
      roomName: '',
      roomType: ''
    })

    // 房间表单
    const roomForm = reactive({
      roomCode: '',
      roomName: '',
      roomNumber: '',
      roomType: '教室',
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
      roomNumber: [
        { required: true, message: '请输入房间号码', trigger: 'blur' }
      ],
      roomType: [
        { required: true, message: '请选择房屋类型', trigger: 'change' }
      ]
    }

    const formRules = {
      name: [
        { required: true, message: '请输入名称', trigger: 'blur' }
      ]
    }

    // 楼宇树形数据
    const treeData = ref([
      {
        id: 1,
        label: '总部大楼',
        type: 'root',
        count: 156,
        children: [
          {
            id: 2,
            label: '1楼',
            type: 'floor',
            count: 45,
            children: [
              {
                id: 3,
                label: '101',
                type: 'room',
                count: 0
              },
              {
                id: 4,
                label: '102',
                type: 'room',
                count: 0
              }
            ]
          },
          {
            id: 5,
            label: '2楼',
            type: 'floor',
            count: 32
          }
        ]
      }
    ])

    const treeProps = {
      children: 'children',
      label: 'label'
    }

    // 房屋数据
    const houseTableData = ref([
      {
        id: 1,
        roomCode: '010CL010101',
        roomName: '房屋名称1',
        roomNumber: '101',
        roomType: '会议室',
        roomArea: '500',
        capacity: '100',
        updateTime: '2021.12.10 13:34'
      },
      {
        id: 2,
        roomCode: '010CL010102',
        roomName: '房屋名称2',
        roomNumber: '102',
        roomType: '实验室',
        roomArea: '500',
        capacity: '100',
        updateTime: '2021.12.10 13:34'
      },
      {
        id: 3,
        roomCode: '010CL010103',
        roomName: '房屋名称3',
        roomNumber: '103',
        roomType: '教室',
        roomArea: '500',
        capacity: '100',
        updateTime: '2021.12.10 13:34'
      },
      {
        id: 4,
        roomCode: '010CL010104',
        roomName: '房屋名称4',
        roomNumber: '104',
        roomType: '办公室',
        roomArea: '500',
        capacity: '100',
        updateTime: '2021.12.10 13:34'
      },
      {
        id: 5,
        roomCode: '010CL010105',
        roomName: '房屋名称5',
        roomNumber: '105',
        roomType: '会议室',
        roomArea: '500',
        capacity: '100',
        updateTime: '2021.12.10 13:34'
      }
    ])

    // 计算属性
    const dialogTitle = computed(() => {
      if (actionType.value === 'parent') {
        return selectedNode.value ? `在"${selectedNode.value.label}"同级添加主题` : '添加主题'
      } else {
        return selectedNode.value ? `在"${selectedNode.value.label}"下添加子主题` : '添加子主题'
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

    // 搜索相关方法
    const handleSearch = () => {
      console.log('搜索房屋:', searchForm)
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        roomNumber: '',
        roomName: '',
        roomType: ''
      })
    }

    // 房屋管理相关方法
    const handleAddRoom = () => {
      isEditMode.value = false
      resetRoomForm()
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
      }).then(() => {
        const index = houseTableData.value.findIndex(item => item.id === row.id)
        if (index !== -1) {
          houseTableData.value.splice(index, 1)
          housePagination.total -= 1
        }
        ElMessage.success('删除成功')
      })
    }

    const handleImport = () => {
      console.log('导入房间')
    }

    const handleExport = () => {
      console.log('导出房间')
    }

    const handleHouseSizeChange = (size) => {
      housePagination.pageSize = size
    }

    const handleHouseCurrentChange = (page) => {
      housePagination.currentPage = page
    }

    const resetRoomForm = () => {
      Object.assign(roomForm, {
        roomCode: '',
        roomName: '',
        roomNumber: '',
        roomType: '教室',
        roomArea: '',
        capacity: ''
      })
    }

    const handleRoomDialogClose = () => {
      roomDialogVisible.value = false
      resetRoomForm()
    }

    const handleRoomSubmit = async () => {
      try {
        await roomFormRef.value?.validate()
        
        if (isEditMode.value) {
          // 编辑房间
          const index = houseTableData.value.findIndex(item => item.id === currentRoomData.value.id)
          if (index !== -1) {
            houseTableData.value[index] = {
              ...houseTableData.value[index],
              ...roomForm,
              updateTime: new Date().toISOString().slice(0, 16).replace('T', ' ')
            }
          }
          ElMessage.success('房间信息更新成功')
        } else {
          // 新增房间
          const newRoom = {
            id: houseTableData.value.length + 1,
            ...roomForm,
            roomCode: roomForm.roomCode || `010CL0101${(houseTableData.value.length + 1).toString().padStart(2, '0')}`,
            updateTime: new Date().toISOString().slice(0, 16).replace('T', ' ')
          }
          houseTableData.value.unshift(newRoom)
          housePagination.total += 1
          ElMessage.success('房间添加成功')
        }
        
        handleRoomDialogClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    // 楼宇管理相关方法
    const handleNodeClick = (data, node) => {
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
      ElMessageBox.confirm(`确定要删除"${selectedNode.value.label}"吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
      })
    }

    const handleDialogClose = () => {
      dialogVisible.value = false
      formData.name = ''
    }

    const handleSubmit = async () => {
      try {
        await formRef.value?.validate()
        ElMessage.success('添加成功')
        handleDialogClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

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
      dialogTitle,
      Search,
      goToHome,  // 新增返回首页方法
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
      handleNodeClick,
      handleAddParent,
      handleAddChild,
      handleDelete,
      handleDialogClose,
      handleSubmit
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

.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 15px;
  flex-shrink: 0;
}

.nav-item {
  padding: 12px 15px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item:hover {
  background-color: #f0f2f5;
  color: #4A90E2;
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