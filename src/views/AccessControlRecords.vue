<template>
  <div class="access-control-records">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">门禁管理 - 通行记录</span>
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
        </el-dropdown>
      </div>
    </div>

    <div class="main-container">
      <!-- 左侧导航 -->
      <div class="sidebar">
        <div class="nav-item">
          <span>仪表板</span>
        </div>
        <div class="nav-item">
          <span>用户管理</span>
        </div>
        <div class="nav-item">
          <span>组织管理</span>
        </div>
        <div class="nav-item">
          <span>职务管理</span>
        </div>
        <div class="nav-item">
          <span>等级管理</span>
        </div>
        <div class="nav-item">
          <span>房屋管理</span>
        </div>
        <div class="nav-item active">
          <span>门禁管理</span>
        </div>
        <div class="nav-item">
          <span>角色管理</span>
        </div>
        <div class="nav-item">
          <span>管理员管理</span>
        </div>
        <div class="nav-item">
          <span>个人中心</span>
        </div>

        <!-- 保安室名称列表 -->
        <div class="room-list">
          <h4>保安室名称</h4>
          <div 
            v-for="room in roomList" 
            :key="room.id"
            :class="['room-item', { active: selectedRoom === room.id }]"
            @click="handleRoomSelect(room.id)"
          >
            <span>{{ room.name }}</span>
            <span class="room-count">({{ room.count }})</span>
          </div>
        </div>
      </div>

      <!-- 右侧主内容 -->
      <div class="main-content">
        <!-- 标签页 -->
        <el-tabs v-model="activeTab" class="tabs">
          <el-tab-pane label="首页" name="home"></el-tab-pane>
          <el-tab-pane label="通行记录" name="access-record"></el-tab-pane>
          <el-tab-pane label="校园分配" name="campus-allocation"></el-tab-pane>
          <el-tab-pane label="门禁控制管理" name="door-control"></el-tab-pane>
        </el-tabs>

        <!-- 子标签页 -->
        <el-tabs v-model="subActiveTab" class="sub-tabs">
          <el-tab-pane label="通行记录" name="access-record"></el-tab-pane>
          <el-tab-pane label="黑名单中通行记录" name="blacklist-record"></el-tab-pane>
        </el-tabs>

        <!-- 搜索筛选区域 -->
        <div class="search-area">
          <div class="search-form">
            <el-row :gutter="20">
              <el-col :span="4">
                <el-input 
                  v-model="searchForm.keyword" 
                  placeholder="请输入人员姓名"
                  class="search-input"
                />
              </el-col>
              <el-col :span="6">
                <div class="form-item">
                  <label>基础信息：</label>
                  <el-input 
                    v-model="searchForm.basicInfo" 
                    placeholder="请输入人员姓名或工号或联系方式"
                  />
                </div>
              </el-col>
              <el-col :span="4">
                <div class="form-item">
                  <label>开门方式：</label>
                  <el-select v-model="searchForm.openMethod" placeholder="全部">
                    <el-option label="全部" value=""></el-option>
                    <el-option label="人脸识别" value="face"></el-option>
                    <el-option label="刷卡" value="card"></el-option>
                    <el-option label="远程开门" value="remote"></el-option>
                  </el-select>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="form-item">
                  <label>通行类型：</label>
                  <el-select v-model="searchForm.accessType" placeholder="全部">
                    <el-option label="全部" value=""></el-option>
                    <el-option label="会议权限" value="meeting"></el-option>
                    <el-option label="临时权限" value="temporary"></el-option>
                    <el-option label="永久权限" value="permanent"></el-option>
                  </el-select>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-item">
                  <label>通行时间：</label>
                  <el-date-picker
                    v-model="searchForm.timeRange"
                    type="datetimerange"
                    range-separator="—"
                    start-placeholder="请选择开始时间"
                    end-placeholder="请选择结束时间"
                    format="YYYY-MM-DD HH:mm:ss"
                    value-format="YYYY-MM-DD HH:mm:ss"
                  />
                </div>
              </el-col>
            </el-row>
            <div class="search-buttons">
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
              <el-button type="primary" @click="handleExport">导出</el-button>
            </div>
          </div>
        </div>

        <!-- 数据表格 - 已优化解决右侧空白问题 -->
        <div class="table-area">
          <el-table 
            :data="tableData" 
            style="width: 100%" 
            stripe
            :header-cell-style="{ backgroundColor: '#fafafa', fontWeight: '600' }"
          >
            <!-- 姓名列 - 固定小宽度 -->
            <el-table-column prop="name" label="姓名" width="100" align="center" />
            
            <!-- 性别列 - 固定小宽度 -->
            <el-table-column prop="gender" label="性别" width="80" align="center" />
            
            <!-- 工号列 - 固定合适宽度 -->
            <el-table-column prop="employeeId" label="工号" width="120" align="center" />
            
            <!-- 联系方式列 - 固定合适宽度 -->
            <el-table-column prop="phone" label="联系方式" width="140" align="center" />
            
            <!-- 开门方式列 - 自适应宽度 -->
            <el-table-column prop="openMethod" label="开门方式" min-width="120" align="center">
              <template #default="scope">
                <el-tag 
                  :type="getOpenMethodType(scope.row.openMethod)"
                  size="small"
                >
                  {{ scope.row.openMethod }}
                </el-tag>
              </template>
            </el-table-column>
            
            <!-- 通行时间列 - 自适应宽度但设置最小宽度 -->
            <el-table-column prop="accessTime" label="通行时间" min-width="180" align="center" />
            
            <!-- 通行类型列 - 自适应宽度 -->
            <el-table-column prop="accessType" label="通行类型" min-width="120" align="center">
              <template #default="scope">
                <el-tag 
                  :type="getAccessTypeType(scope.row.accessType)"
                  size="small"
                >
                  {{ scope.row.accessType }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-area">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'

export default {
  name: 'AccessControlRecords',
  setup() {
    const activeTab = ref('access-record')
    const subActiveTab = ref('access-record')
    const selectedRoom = ref(1)

    // 搜索表单
    const searchForm = reactive({
      keyword: '',
      basicInfo: '',
      openMethod: '',
      accessType: '',
      timeRange: []
    })

    // 分页信息
    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 200
    })

    // 保安室列表
    const roomList = ref([
      { id: 1, name: '23.8寸竖屏办公室的智能联系亮测试设备', count: 140 },
      { id: 2, name: '23.8寸竖屏办公室的智能', count: 0 },
      { id: 3, name: '23.9寸竖屏办公室的智能', count: 0 },
      { id: 4, name: '23.8寸竖屏办公室的智能联系亮测试设备内容', count: 140 },
      { id: 5, name: '23.8寸竖屏办公室的智能联系亮测试', count: 0 },
      { id: 6, name: '23.8寸竖屏办公室的智能联系亮测试设备内容详情', count: 0 },
      { id: 7, name: '23.8寸竖屏办公室的智能', count: 140 },
      { id: 8, name: '23.9寸竖屏办公室的智能', count: 0 },
      { id: 9, name: '23.9寸竖屏办公室的智能', count: 0 }
    ])

    // 表格数据
    const tableData = ref([
      {
        name: '张1',
        gender: '男',
        employeeId: '12315461',
        phone: '13111311131',
        openMethod: '人脸识别',
        accessTime: '2024.06.27 10:23:23',
        accessType: '会议权限'
      },
      {
        name: '张2',
        gender: '女',
        employeeId: '12315462',
        phone: '13111311132',
        openMethod: '人脸识别',
        accessTime: '2024.06.27 10:23:23',
        accessType: '临时权限'
      },
      {
        name: '张3',
        gender: '男',
        employeeId: '12315463',
        phone: '13111311133',
        openMethod: '刷卡',
        accessTime: '2024.06.27 10:23:23',
        accessType: '永久权限'
      },
      {
        name: '张4',
        gender: '男',
        employeeId: '12315464',
        phone: '13111311134',
        openMethod: '刷卡',
        accessTime: '2024.06.27 10:23:23',
        accessType: '会议权限'
      },
      {
        name: 'administrator',
        gender: '/',
        employeeId: '/',
        phone: '/',
        openMethod: '远程开门',
        accessTime: '2024.06.27 10:23:23',
        accessType: '/'
      },
      {
        name: 'administrator',
        gender: '/',
        employeeId: '/',
        phone: '/',
        openMethod: '远程开门',
        accessTime: '2024.06.27 10:23:23',
        accessType: '/'
      },
      {
        name: '张7',
        gender: '男',
        employeeId: '12315467',
        phone: '13111311137',
        openMethod: '人脸识别',
        accessTime: '2024.06.27 10:23:23',
        accessType: '会议权限'
      },
      {
        name: '张8',
        gender: '女',
        employeeId: '12315468',
        phone: '13111311138',
        openMethod: '人脸识别',
        accessTime: '2024.06.27 10:23:23',
        accessType: '临时权限'
      },
      {
        name: '张9',
        gender: '女',
        employeeId: '12315469',
        phone: '13111311139',
        openMethod: '刷卡',
        accessTime: '2024.06.27 10:23:23',
        accessType: '永久权限'
      },
      {
        name: '张10',
        gender: '女',
        employeeId: '12315470',
        phone: '13111311140',
        openMethod: '刷卡',
        accessTime: '2024.06.27 10:23:23',
        accessType: '会议权限'
      }
    ])

    // 方法定义
    const handleRoomSelect = (roomId) => {
      selectedRoom.value = roomId
      console.log('选中房间:', roomId)
    }

    const handleSearch = () => {
      console.log('搜索:', searchForm)
    }

    const handleReset = () => {
      searchForm.keyword = ''
      searchForm.basicInfo = ''
      searchForm.openMethod = ''
      searchForm.accessType = ''
      searchForm.timeRange = []
    }

    const handleExport = () => {
      console.log('导出数据')
    }

    const handleSizeChange = (size) => {
      pagination.pageSize = size
    }

    const handleCurrentChange = (page) => {
      pagination.currentPage = page
    }

    const getOpenMethodType = (method) => {
      const typeMap = {
        '人脸识别': 'success',
        '刷卡': 'primary',
        '远程开门': 'warning'
      }
      return typeMap[method] || 'info'
    }

    const getAccessTypeType = (type) => {
      const typeMap = {
        '会议权限': 'success',
        '临时权限': 'warning',
        '永久权限': 'danger'
      }
      return typeMap[type] || 'info'
    }

    return {
      activeTab,
      subActiveTab,
      selectedRoom,
      searchForm,
      pagination,
      roomList,
      tableData,
      handleRoomSelect,
      handleSearch,
      handleReset,
      handleExport,
      handleSizeChange,
      handleCurrentChange,
      getOpenMethodType,
      getAccessTypeType
    }
  }
}
</script>

<style scoped>
.access-control-records {
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
  width: 100%;
}

.sidebar {
  width: 300px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 15px;
  flex-shrink: 0;
  overflow-y: auto;
}

.nav-item {
  padding: 12px 15px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s;
  color: #666;
}

.nav-item:hover {
  background-color: #f0f2f5;
  color: #4A90E2;
}

.nav-item.active {
  background-color: #4A90E2;
  color: white;
}

.room-list {
  margin-top: 20px;
}

.room-list h4 {
  margin-bottom: 15px;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.room-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  transition: all 0.3s;
  color: #666;
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-item:hover {
  background-color: #f0f2f5;
  color: #4A90E2;
}

.room-item.active {
  background-color: #e6f3ff;
  color: #4A90E2;
  font-weight: 500;
}

.room-count {
  font-size: 12px;
  color: #999;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  min-width: 0; /* 重要：允许flex项目收缩 */
}

.tabs {
  margin-bottom: 20px;
}

.sub-tabs {
  margin-bottom: 20px;
}

.search-area {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-form {
  width: 100%;
}

.search-input {
  width: 100%;
}

.form-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-item label {
  white-space: nowrap;
  font-size: 14px;
  color: #666;
  min-width: 80px;
}

.search-buttons {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

/* 关键样式：解决表格右侧空白问题 */
.table-area {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  width: 100%;
}

/* 重要：移除固定表格布局，改为自动布局 */
:deep(.el-table) {
  width: 100% !important;
  table-layout: auto !important; /* 关键：改为auto布局，解决右侧空白问题 */
}

:deep(.el-table__header-wrapper) {
  width: 100% !important;
}

:deep(.el-table__body-wrapper) {
  width: 100% !important;
}

:deep(.el-table th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table .cell) {
  padding: 0 12px;
  word-break: break-word;
}

.pagination-area {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  width: 100%;
}

/* Element Plus 样式覆盖 */
:deep(.el-tabs__item.is-active) {
  color: #4A90E2;
}

:deep(.el-tabs__active-bar) {
  background-color: #4A90E2;
}

:deep(.el-input) {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .sidebar {
    width: 250px;
  }
}

@media (max-width: 1200px) {
  .main-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid #e8e8e8;
  }
  
  .table-area {
    overflow-x: auto;
  }
}
</style>