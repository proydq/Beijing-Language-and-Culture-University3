<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>预约人员权限列表</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="addPersonnelPermission"> 新增 </el-button>
        <el-button @click="exportPersonnelList"> 导出 </el-button>
      </div>
    </div>

    <el-table :data="personnelPermissionData" style="width: 100%" border>
      <el-table-column prop="subject" label="主题" width="200" />
      <el-table-column prop="authorizedPersonnel" label="权限人员" min-width="300">
        <template #default="{ row }">
          <div class="personnel-text">
            {{ row.authorizedPersonnel }}
            <el-button
              type="text"
              size="small"
              @click="viewPersonnelDetails(row)"
              style="color: #409eff; margin-left: 10px"
            >
              查看详情
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="bookingRooms" label="预约教室" min-width="350">
        <template #default="{ row }">
          <div class="rooms-text">
            {{ row.bookingRooms }}
            <el-button
              type="text"
              size="small"
              @click="viewRoomDetails(row)"
              style="color: #409eff; margin-left: 10px"
            >
              查看详情
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="creator" label="创建人" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="120" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="editPersonnelPermission(row)"
            >编辑</el-button
          >
          <el-button type="danger" size="small" @click="deletePersonnelPermission(row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <!-- 权限人员详情弹出框 -->
    <el-dialog v-model="personnelDetailDialogVisible" title="权限人员" width="600px" destroy-on-close>
      <div class="personnel-detail-container">
        <div class="search-section">
          <el-select v-model="personnelDetailFilter.type" class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="老师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
          <el-input
            v-model="personnelDetailFilter.keyword"
            placeholder="请输入姓名/工号"
            class="filter-input"
            clearable
          />
          <el-button type="primary" icon="Search">搜索</el-button>
        </div>

        <el-table :data="currentPersonnelDetails" border style="width: 100%" max-height="400">
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="jobNumber" label="工号" width="160" />
          <el-table-column prop="department" label="所属部门" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="removePersonnelFromDetail(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-section">
          <el-pagination
            v-model:current-page="personnelDetailPagination.currentPage"
            v-model:page-size="personnelDetailPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="personnelDetailPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            small
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="personnelDetailDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="personnelDetailDialogVisible = false">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 可预约房屋/教室详情弹出框 -->
    <el-dialog v-model="roomDetailDialogVisible" title="可预约房屋/教室" width="600px" destroy-on-close>
      <div class="room-detail-container">
        <div class="search-section">
          <el-select v-model="roomDetailFilter.type" class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="老师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
          <el-input
            v-model="roomDetailFilter.keyword"
            placeholder="请输入实验室"
            class="filter-input"
            clearable
          />
          <el-button type="primary" icon="Search">搜索</el-button>
        </div>

        <el-table :data="currentRoomDetails" border style="width: 100%" max-height="400">
          <el-table-column prop="roomName" label="预约教室" />
          <el-table-column prop="roomCode" label="房间号" width="120" />
          <el-table-column prop="building" label="所属楼栋" />
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="removeRoomFromDetail(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-section">
          <el-pagination
            v-model:current-page="roomDetailPagination.currentPage"
            v-model:page-size="roomDetailPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="roomDetailPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            small
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roomDetailDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="roomDetailDialogVisible = false">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 权限配置弹出框 -->
     <el-dialog v-model="editDialogVisible" :title="isEdit ? '编辑权限配置' : '新增权限配置'" width="900px" destroy-on-close>
       <!-- 主题输入框 -->
       <div class="subject-section">
         <label class="subject-label">主题：</label>
         <el-input
           v-model="subjectValue"
           placeholder="请输入主题"
           class="subject-input"
         />
       </div>

       <div class="permission-container">
        <!-- 左侧：权限人员名单 -->
        <div class="permission-side">
          <h3 class="side-title">权限人员名单</h3>
          <div class="search-bar">
            <el-select v-model="userFilter.type" class="filter-select">
              <el-option label="全部" value="all" />
              <el-option label="老师" value="teacher" />
              <el-option label="学生" value="student" />
            </el-select>
            <el-input
              v-model="userFilter.keyword"
              placeholder="请输入姓名/工号"
              class="filter-input"
              clearable
            />
            <el-button type="primary" @click="handleAddUser">添加</el-button>
          </div>
          <div class="action-bar">
            <el-button
              type="danger"
              :disabled="selectedUserRows.length === 0"
              @click="removeSelectedUsers"
            >批量移除</el-button>
          </div>
          <el-table
            :data="userData"
            border
            style="width: 100%"
            @selection-change="userSelectionChange"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="jobNumber" label="工号" width="160" />
            <el-table-column prop="department" label="所属部门" />
          </el-table>
          <div class="empty-state" v-if="userData.length === 0">
            <div class="empty-icon">👤</div>
            <div class="empty-text">暂无相关信息</div>
          </div>
        </div>

        <!-- 右侧：房屋名单 -->
        <div class="permission-side">
          <h3 class="side-title">房屋名单</h3>
          <div class="search-bar">
            <el-select v-model="roomFilter.type" class="filter-select">
              <el-option label="全部" value="all" />
              <el-option label="老师" value="teacher" />
              <el-option label="学生" value="student" />
            </el-select>
            <el-input
              v-model="roomFilter.keyword"
              placeholder="请输入实验室"
              class="filter-input"
              clearable
            />
            <el-button type="primary" @click="handleAddRoom">添加</el-button>
          </div>
          <div class="action-bar">
            <el-button
              type="danger"
              :disabled="selectedRoomRows.length === 0"
              @click="removeSelectedRooms"
            >批量移除</el-button>
          </div>
          <el-table
            :data="roomData"
            border
            style="width: 100%"
            @selection-change="roomSelectionChange"
          >
            <el-table-column type="selection" width="50" />
            <el-table-column prop="roomName" label="预约教室" />
            <el-table-column prop="roomCode" label="房间号" width="120" />
            <el-table-column prop="building" label="所属楼栋" />
          </el-table>
          <div class="empty-state" v-if="roomData.length === 0">
            <div class="empty-icon">🏠</div>
            <div class="empty-text">暂无相关信息</div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleTestSubmit">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'BookingPersonnelSettings',
  setup() {
    // 预约人员权限数据 - 根据截图的实际数据结构
    const personnelPermissionData = ref([
      {
        id: 1,
        subject: '物理实验室一层、二层、三层可预约人员',
        authorizedPersonnel:
          '杨超；郭辉；邓伯雯；赵芳；潘欣妍；张宇；吴俊杰；刘敏；孙辉；高颖；王丽；陈杰；周琼；赵诗雅；徐赠；王宇轩；黄斯；李鸥；荣意；贾永强；张良嘉；曹明红；郑子豪......',
        authorizedUsers: [
          { name: '张三', jobNumber: 'H0001234', department: '科技处' },
          { name: '李四', jobNumber: 'H0001235', department: '信息化办公室' },
        ],
        bookingRooms:
          '多媒体教室（101）；多媒体教室（102）；多媒体教室（103）；多媒体教室（104）；多媒体教室（105）；多媒体教室（106）；多媒体教室（107）；多媒体教室（108）；多媒体教室（109）；多媒体教室（110）......',
        roomList: [
          { roomName: '多媒体教室（101）', roomCode: '101', building: '科研楼' },
          { roomName: '多媒体教室（102）', roomCode: '102', building: '科研楼' },
        ],
        creator: '张三',
        createTime: '2024.03.08 09:16:26',
      },
      {
        id: 2,
        subject: '物理实验室四层可预约人员',
        authorizedPersonnel: '杨超；郭辉；邓伯雯；赵芳；潘欣妍；',
        authorizedUsers: [
          { name: '王五', jobNumber: 'H0001236', department: '后勤处' },
          { name: '赵六', jobNumber: 'H0001237', department: '资产管理处' },
        ],
        bookingRooms: '清洁间',
        roomList: [{ roomName: '清洁间', roomCode: '401', building: '达才楼' }],
        creator: '张三',
        createTime: '2024.03.08 09:16:26',
      },
    ])



    const editDialogVisible = ref(false)
    const isEdit = ref(false)
    const selectedUserData = ref([])
    const selectedRoomData = ref([])
    const subjectValue = ref('')
    const currentEditingRow = ref(null)

    // 权限人员详情弹出框相关
    const personnelDetailDialogVisible = ref(false)
    const currentPersonnelDetails = ref([])
    const personnelDetailFilter = ref({ type: 'all', keyword: '' })
    const personnelDetailPagination = ref({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    // 房间详情弹出框相关
    const roomDetailDialogVisible = ref(false)
    const currentRoomDetails = ref([])
    const roomDetailFilter = ref({ type: 'all', keyword: '' })
    const roomDetailPagination = ref({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    // 弹出框内的数据
    const userData = ref([])
    const roomData = ref([])
    const selectedUserRows = ref([])
    const selectedRoomRows = ref([])

    // 过滤条件
    const userFilter = ref({ type: 'all', keyword: '' })
    const roomFilter = ref({ type: 'all', keyword: '' })

    const addPersonnelPermission = () => {
      isEdit.value = false
      subjectValue.value = ''
      userData.value = []
      roomData.value = []
      selectedUserData.value = []
      selectedRoomData.value = []
      currentEditingRow.value = null
      editDialogVisible.value = true
    }

    const exportPersonnelList = () => {
      ElMessage.success('正在导出预约人员列表...')
    }

    const viewPersonnelDetails = (row) => {
      // 设置当前权限人员详情数据
      currentPersonnelDetails.value = row.authorizedUsers || []
      personnelDetailPagination.value.total = currentPersonnelDetails.value.length
      personnelDetailPagination.value.currentPage = 1
      personnelDetailDialogVisible.value = true
    }

    const removePersonnelFromDetail = (personnel) => {
      currentPersonnelDetails.value = currentPersonnelDetails.value.filter(
        item => item.jobNumber !== personnel.jobNumber
      )
      personnelDetailPagination.value.total = currentPersonnelDetails.value.length
      ElMessage.success('已移除人员')
    }

    const viewRoomDetails = (row) => {
      // 设置当前房间详情数据
      currentRoomDetails.value = row.roomList || []
      roomDetailPagination.value.total = currentRoomDetails.value.length
      roomDetailPagination.value.currentPage = 1
      roomDetailDialogVisible.value = true
    }

    const removeRoomFromDetail = (room) => {
      currentRoomDetails.value = currentRoomDetails.value.filter(
        item => item.roomCode !== room.roomCode
      )
      roomDetailPagination.value.total = currentRoomDetails.value.length
      ElMessage.success('已移除房间')
    }

    const editPersonnelPermission = (row) => {
      isEdit.value = true
      subjectValue.value = row.subject || ''
      userData.value = [...(row.authorizedUsers || [])]
      roomData.value = [...(row.roomList || [])]
      selectedUserData.value = row.authorizedUsers || []
      selectedRoomData.value = row.roomList || []
      currentEditingRow.value = row
      editDialogVisible.value = true
    }

    const deletePersonnelPermission = async (row) => {
      try {
        await ElMessageBox.confirm(`确认删除"${row.subject}"的权限设置吗？`, '删除确认')
        ElMessage.success('权限设置已删除')
      } catch {
        // 用户取消
      }
    }

    const handleTestSubmit = () => {
      if (!subjectValue.value.trim()) {
        ElMessage.warning('请输入主题')
        return
      }

      if (isEdit.value && currentEditingRow.value) {
        // 编辑模式：更新现有数据
        currentEditingRow.value.subject = subjectValue.value
        currentEditingRow.value.authorizedUsers = [...userData.value]
        currentEditingRow.value.roomList = [...roomData.value]
        ElMessage.success('权限配置已更新')
      } else {
        // 新增模式：添加新数据
        const newPermission = {
          id: Date.now(),
          subject: subjectValue.value,
          authorizedPersonnel: userData.value.map(u => u.name).join('；') || '暂无',
          authorizedUsers: [...userData.value],
          bookingRooms: roomData.value.map(r => r.roomName).join('；') || '暂无',
          roomList: [...roomData.value],
          creator: '当前用户',
          createTime: new Date().toLocaleString('zh-CN')
        }
        personnelPermissionData.value.push(newPermission)
        ElMessage.success('权限配置已添加')
      }

      editDialogVisible.value = false
    }

    // 弹出框相关方法
    const userSelectionChange = (val) => {
      selectedUserRows.value = val
    }

    const roomSelectionChange = (val) => {
      selectedRoomRows.value = val
    }

    const removeSelectedUsers = () => {
      userData.value = userData.value.filter(
        (item) => !selectedUserRows.value.includes(item)
      )
      selectedUserRows.value = []
      ElMessage.success('已移除选中的用户')
    }

    const removeSelectedRooms = () => {
      roomData.value = roomData.value.filter(
        (item) => !selectedRoomRows.value.includes(item)
      )
      selectedRoomRows.value = []
      ElMessage.success('已移除选中的房间')
    }

    const handleAddUser = () => {
      // 模拟添加用户
      const newUser = {
        id: Date.now(),
        name: '新用户' + Math.floor(Math.random() * 100),
        jobNumber: 'H' + Math.floor(Math.random() * 1000000),
        department: '测试部门'
      }
      userData.value.push(newUser)
      ElMessage.success('已添加用户')
    }

    const handleAddRoom = () => {
      // 模拟添加房间
      const newRoom = {
        id: Date.now(),
        roomName: '测试教室' + Math.floor(Math.random() * 100),
        roomCode: Math.floor(Math.random() * 999) + 1,
        building: '测试楼'
      }
      roomData.value.push(newRoom)
      ElMessage.success('已添加房间')
    }

    return {
      personnelPermissionData,
      addPersonnelPermission,
      exportPersonnelList,
      viewPersonnelDetails,
      viewRoomDetails,
      editPersonnelPermission,
      deletePersonnelPermission,
      handleTestSubmit,
      editDialogVisible,
      isEdit,
      selectedUserData,
      selectedRoomData,
      subjectValue,
      currentEditingRow,
      personnelDetailDialogVisible,
      currentPersonnelDetails,
      personnelDetailFilter,
      personnelDetailPagination,
      removePersonnelFromDetail,
      roomDetailDialogVisible,
      currentRoomDetails,
      roomDetailFilter,
      roomDetailPagination,
      removeRoomFromDetail,
      userData,
      roomData,
      selectedUserRows,
      selectedRoomRows,
      userFilter,
      roomFilter,
      userSelectionChange,
      roomSelectionChange,
      removeSelectedUsers,
      removeSelectedRooms,
      handleAddUser,
      handleAddRoom,
    }
  },
}
</script>

<style scoped>
.setting-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.personnel-text,
.rooms-text {
  line-height: 1.5;
  word-wrap: break-word;
  word-break: break-all;
}

/* 让表格内容可以换行显示 */
:deep(.el-table .cell) {
  white-space: normal !important;
  word-wrap: break-word;
  word-break: break-all;
  line-height: 1.5;
}

/* 调整表格行高 */
:deep(.el-table__row) {
  height: auto;
}

:deep(.el-table td) {
  padding: 12px 0;
}

/* 权限人员详情弹出框样式 */
.personnel-detail-container {
  padding: 10px 0;
}

.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.search-section .filter-select {
  width: 100px;
}

.search-section .filter-input {
  flex: 1;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 房间详情弹出框样式 */
.room-detail-container {
  padding: 10px 0;
}

.room-detail-container .search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  align-items: center;
}

.room-detail-container .search-section .filter-select {
  width: 100px;
}

.room-detail-container .search-section .filter-input {
  flex: 1;
}

.room-detail-container .pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 弹出框样式 */
.subject-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.subject-label {
  font-weight: 600;
  margin-right: 10px;
  min-width: 50px;
  color: #333;
}

.subject-input {
  flex: 1;
}

.permission-container {
  display: flex;
  gap: 20px;
  padding-top: 10px;
}

.permission-side {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.side-title {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 600;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.filter-select {
  width: 100px;
}

.filter-input {
  flex: 1;
}

.action-bar {
  margin-bottom: 10px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.empty-text {
  font-size: 14px;
}

.dialog-footer {
  text-align: right;
}
</style>
