<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>预约人员权限列表</h2>
      </div>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入关键词搜索"
          style="width: 200px; margin-right: 10px"
          clearable
          @clear="loadPermissionList"
          @keyup.enter="loadPermissionList"
        />
        <el-button @click="loadPermissionList">搜索</el-button>
        <el-button type="primary" @click="addPersonnelPermission"> 新增 </el-button>
        <el-button @click="exportPersonnelList"> 导出 </el-button>
      </div>
    </div>

    <el-table :data="personnelPermissionData" style="width: 100%" border v-loading="loading">
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

    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
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
            <el-select v-model="userFilter.organizationId" class="filter-select" placeholder="选择组织机构">
              <el-option label="全部" value="" />
              <el-option
                v-for="org in organizationOptions"
                :key="org.value"
                :label="org.label"
                :value="org.value"
              />
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

    <!-- 用户选择弹窗 -->
    <el-dialog v-model="userSelectDialogVisible" title="选择用户" width="800px" destroy-on-close>
      <div class="select-dialog-content">
        <div class="search-section">
          <el-input
            v-model="userFilter.keyword"
            placeholder="请输入姓名/工号"
            style="width: 200px; margin-right: 10px"
            clearable
            @keyup.enter="loadAvailableUsers"
          />
          <el-button type="primary" @click="loadAvailableUsers">搜索</el-button>
        </div>

        <el-table
          :data="availableUsers"
          border
          style="width: 100%"
          v-loading="userSelectLoading"
          @selection-change="handleUserSelectChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="realName" label="姓名" width="120" />
          <el-table-column prop="jobNumber" label="工号" width="160" />
          <el-table-column prop="departmentName" label="所属部门" />
        </el-table>

        <div class="pagination-section">
          <el-pagination
            v-model:current-page="userSelectPagination.pageNum"
            v-model:page-size="userSelectPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="userSelectPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            small
            @size-change="handleUserSelectSizeChange"
            @current-change="handleUserSelectCurrentChange"
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="userSelectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAddUsers">确定添加</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 房间选择弹窗 -->
    <el-dialog v-model="roomSelectDialogVisible" title="选择房间" width="800px" destroy-on-close>
      <div class="select-dialog-content">
        <div class="search-section">
          <el-input
            v-model="roomFilter.keyword"
            placeholder="请输入房间名称"
            style="width: 200px; margin-right: 10px"
            clearable
            @keyup.enter="loadAvailableRooms"
          />
          <el-button type="primary" @click="loadAvailableRooms">搜索</el-button>
        </div>

        <el-table
          :data="availableRooms"
          border
          style="width: 100%"
          v-loading="roomSelectLoading"
          @selection-change="handleRoomSelectChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="roomName" label="房间名称" />
          <el-table-column prop="roomNo" label="房间号" width="120" />
          <el-table-column prop="roomAreaName" label="所属区域" />
        </el-table>

        <div class="pagination-section">
          <el-pagination
            v-model:current-page="roomSelectPagination.pageNum"
            v-model:page-size="roomSelectPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="roomSelectPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            small
            @size-change="handleRoomSelectSizeChange"
            @current-change="handleRoomSelectCurrentChange"
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roomSelectDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAddRooms">确定添加</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPermissionList,
  createPermission,
  updatePermission,
  deletePermission,
  exportPermissions,
  getAvailableUsers,
  getAvailableRooms
} from '@/api/bookingPersonnelPermission.js'
import { getOrganizationOptions } from '@/api/organization.js'

export default {
  name: 'BookingPersonnelSettings',
  setup() {
    // 数据定义
    const personnelPermissionData = ref([])
    const loading = ref(false)
    const searchKeyword = ref('')

    // 分页数据
    const pagination = ref({
      pageNum: 1,
      pageSize: 10,
      total: 0
    })



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
    const userFilter = ref({ organizationId: '', keyword: '' })
    const roomFilter = ref({ type: 'all', keyword: '' })

    // 组织机构选项
    const organizationOptions = ref([])

    // 加载权限列表
    const loadPermissionList = async () => {
      try {
        loading.value = true
        const params = {
          pageNum: pagination.value.pageNum,
          pageSize: pagination.value.pageSize,
          keyword: searchKeyword.value
        }
        const response = await getPermissionList(params)
        if (response.code === 200) {
          personnelPermissionData.value = response.data.rows || []
          pagination.value.total = response.data.total || 0
        } else {
          ElMessage.error(response.message || '获取权限列表失败')
        }
      } catch (error) {
        console.error('获取权限列表失败:', error)
        ElMessage.error('获取权限列表失败')
      } finally {
        loading.value = false
      }
    }

    // 加载组织机构选项
    const loadOrganizationOptions = async () => {
      try {
        const response = await getOrganizationOptions()
        if (response && response.code === 200) {
          organizationOptions.value = response.data || []
        } else {
          console.error('获取组织机构选项失败:', response)
        }
      } catch (error) {
        console.error('获取组织机构选项失败:', error)
      }
    }

    // 分页处理
    const handleSizeChange = (val) => {
      pagination.value.pageSize = val
      pagination.value.pageNum = 1
      loadPermissionList()
    }

    const handleCurrentChange = (val) => {
      pagination.value.pageNum = val
      loadPermissionList()
    }

    const addPersonnelPermission = () => {
      isEdit.value = false
      subjectValue.value = ''
      userData.value = []
      roomData.value = []
      selectedUserData.value = []
      selectedRoomData.value = []
      currentEditingRow.value = null
      editDialogVisible.value = true

      // 预加载可选用户和房间列表
      loadAvailableUsers()
      loadAvailableRooms()
    }

    const exportPersonnelList = async () => {
      try {
        ElMessage.info('正在导出预约人员列表...')
        const params = {
          keyword: searchKeyword.value
        }
        const response = await exportPermissions(params)

        // 创建下载链接
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = `预约人员权限列表_${new Date().toLocaleDateString()}.xlsx`
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

      // 预加载可选用户和房间列表
      loadAvailableUsers()
      loadAvailableRooms()
    }

    const deletePersonnelPermission = async (row) => {
      try {
        await ElMessageBox.confirm(`确认删除"${row.subject}"的权限设置吗？`, '删除确认')

        const response = await deletePermission(row.id)
        if (response.code === 200) {
          ElMessage.success('权限设置已删除')
          loadPermissionList() // 刷新列表
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除失败:', error)
          ElMessage.error('删除失败')
        }
      }
    }

    const handleTestSubmit = async () => {
      if (!subjectValue.value.trim()) {
        ElMessage.warning('请输入主题')
        return
      }

      if (userData.value.length === 0) {
        ElMessage.warning('请至少添加一个权限人员')
        return
      }

      if (roomData.value.length === 0) {
        ElMessage.warning('请至少添加一个房间')
        return
      }

      try {
        const requestData = {
          subject: subjectValue.value,
          userIds: userData.value.map(user => user.id),
          roomIds: roomData.value.map(room => room.id)
        }

        let response
        if (isEdit.value && currentEditingRow.value) {
          // 编辑模式
          response = await updatePermission(currentEditingRow.value.id, requestData)
        } else {
          // 新增模式
          response = await createPermission(requestData)
        }

        if (response.code === 200) {
          ElMessage.success(isEdit.value ? '权限配置已更新' : '权限配置已添加')
          editDialogVisible.value = false
          loadPermissionList() // 刷新列表
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      }
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

    // 用户选择弹窗相关
    const userSelectDialogVisible = ref(false)
    const availableUsers = ref([])
    const userSelectLoading = ref(false)
    const userSelectPagination = ref({ pageNum: 1, pageSize: 10, total: 0 })

    // 房间选择弹窗相关
    const roomSelectDialogVisible = ref(false)
    const availableRooms = ref([])
    const roomSelectLoading = ref(false)
    const roomSelectPagination = ref({ pageNum: 1, pageSize: 10, total: 0 })

    const handleAddUser = () => {
      userSelectDialogVisible.value = true
      loadAvailableUsers()
    }

    const loadAvailableUsers = async () => {
      try {
        userSelectLoading.value = true
        const params = {
          pageNum: userSelectPagination.value.pageNum,
          pageSize: userSelectPagination.value.pageSize,
          realNameAndJobNumber: userFilter.value.keyword || '',
          organizationId: userFilter.value.organizationId || ''
        }
        console.log('=== 用户接口调试信息 ===')
        console.log('请求URL: /api/user/available')
        console.log('请求参数:', params)
        console.log('当前用户分页状态:', userSelectPagination.value)
        
        const response = await getAvailableUsers(params)
        console.log('原始响应:', response)
        console.log('响应类型:', typeof response)
        console.log('响应结构:', Object.keys(response || {}))
        
        if (response && response.code === 200 && response.data) {
          availableUsers.value = response.data.rows || []
          userSelectPagination.value.total = response.data.total || 0
          console.log('解析后用户列表:', availableUsers.value)
          console.log('用户总数:', userSelectPagination.value.total)
          console.log('用户列表长度:', availableUsers.value.length)
          if (availableUsers.value.length === 0) {
            console.warn('⚠️ 用户列表为空，请检查后端数据')
            ElMessage.warning('暂无可选用户数据')
          }
        } else {
          console.error('❌ 用户接口响应异常:', response)
          ElMessage.error(response?.message || '获取可选用户失败')
        }
      } catch (error) {
        console.error('❌ 获取可选用户失败:', error)
        console.error('错误详情:', error.response || error)
        ElMessage.error('获取可选用户失败: ' + (error.message || '网络错误'))
      } finally {
        userSelectLoading.value = false
        console.log('=== 用户接口调试结束 ===')
      }
    }

    const handleAddRoom = () => {
      roomSelectDialogVisible.value = true
      loadAvailableRooms()
    }

    const loadAvailableRooms = async () => {
      try {
        roomSelectLoading.value = true
        const params = {
          pageNum: roomSelectPagination.value.pageNum,
          pageSize: roomSelectPagination.value.pageSize,
          roomName: roomFilter.value.keyword || ''
        }
        console.log('=== 房间接口调试信息 ===')
        console.log('请求URL: /api/room/available')
        console.log('请求参数:', params)
        console.log('当前房间分页状态:', roomSelectPagination.value)
        
        const response = await getAvailableRooms(params)
        console.log('原始响应:', response)
        console.log('响应类型:', typeof response)
        console.log('响应结构:', Object.keys(response || {}))
        
        if (response && response.code === 200 && response.data) {
          availableRooms.value = response.data.rows || []
          roomSelectPagination.value.total = response.data.total || 0
          console.log('解析后房间列表:', availableRooms.value)
          console.log('房间总数:', roomSelectPagination.value.total)
          console.log('房间列表长度:', availableRooms.value.length)
          if (availableRooms.value.length === 0) {
            console.warn('⚠️ 房间列表为空，请检查后端数据')
            ElMessage.warning('暂无可选房间数据')
          }
        } else {
          console.error('❌ 房间接口响应异常:', response)
          ElMessage.error(response?.message || '获取可选房间失败')
        }
      } catch (error) {
        console.error('❌ 获取可选房间失败:', error)
        console.error('错误详情:', error.response || error)
        ElMessage.error('获取可选房间失败: ' + (error.message || '网络错误'))
      } finally {
        roomSelectLoading.value = false
        console.log('=== 房间接口调试结束 ===')
      }
    }

    // 用户选择变化处理
    const selectedUsers = ref([])
    const handleUserSelectChange = (selection) => {
      selectedUsers.value = selection
    }

    // 房间选择变化处理
    const selectedRooms = ref([])
    const handleRoomSelectChange = (selection) => {
      selectedRooms.value = selection
    }

    // 用户选择分页处理
    const handleUserSelectSizeChange = (size) => {
      userSelectPagination.value.pageSize = size
      userSelectPagination.value.pageNum = 1
      loadAvailableUsers()
    }

    const handleUserSelectCurrentChange = (page) => {
      userSelectPagination.value.pageNum = page
      loadAvailableUsers()
    }

    // 房间选择分页处理
    const handleRoomSelectSizeChange = (size) => {
      roomSelectPagination.value.pageSize = size
      roomSelectPagination.value.pageNum = 1
      loadAvailableRooms()
    }

    const handleRoomSelectCurrentChange = (page) => {
      roomSelectPagination.value.pageNum = page
      loadAvailableRooms()
    }

    // 确认添加用户
    const confirmAddUsers = () => {
      if (selectedUsers.value.length === 0) {
        ElMessage.warning('请选择要添加的用户')
        return
      }

      selectedUsers.value.forEach(user => {
        const exists = userData.value.some(p => p.id === user.id)
        if (!exists) {
          userData.value.push({
            id: user.id,
            name: user.realName,
            jobNumber: user.jobNumber,
            department: user.departmentName
          })
        }
      })

      userSelectDialogVisible.value = false
      const addedCount = selectedUsers.value.length
      selectedUsers.value = []
      ElMessage.success(`成功添加 ${addedCount} 个用户`)
    }

    // 确认添加房间
    const confirmAddRooms = () => {
      if (selectedRooms.value.length === 0) {
        ElMessage.warning('请选择要添加的房间')
        return
      }

      selectedRooms.value.forEach(room => {
        const exists = roomData.value.some(r => r.id === room.id)
        if (!exists) {
          roomData.value.push({
            id: room.id,
            roomName: room.roomName,
            roomCode: room.roomNo || room.roomCode,
            building: room.roomAreaName || room.areaName
          })
        }
      })

      roomSelectDialogVisible.value = false
      const addedCount = selectedRooms.value.length
      selectedRooms.value = []
      ElMessage.success(`成功添加 ${addedCount} 个房间`)
    }

    // 组件挂载时初始化
    onMounted(() => {
      loadPermissionList()
      loadOrganizationOptions()
    })

    return {
      // 数据
      personnelPermissionData,
      loading,
      searchKeyword,
      pagination,
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
      roomDetailDialogVisible,
      currentRoomDetails,
      roomDetailFilter,
      roomDetailPagination,
      userData,
      roomData,
      selectedUserRows,
      selectedRoomRows,
      userFilter,
      roomFilter,
      organizationOptions,
      userSelectDialogVisible,
      availableUsers,
      userSelectLoading,
      userSelectPagination,
      roomSelectDialogVisible,
      availableRooms,
      roomSelectLoading,
      roomSelectPagination,
      selectedUsers,
      selectedRooms,
      // 方法
      loadPermissionList,
      handleSizeChange,
      handleCurrentChange,
      addPersonnelPermission,
      exportPersonnelList,
      viewPersonnelDetails,
      viewRoomDetails,
      editPersonnelPermission,
      deletePersonnelPermission,
      handleTestSubmit,
      removePersonnelFromDetail,
      removeRoomFromDetail,
      userSelectionChange,
      roomSelectionChange,
      removeSelectedUsers,
      removeSelectedRooms,
      handleAddUser,
      handleAddRoom,
      loadAvailableUsers,
      loadAvailableRooms,
      handleUserSelectChange,
      handleRoomSelectChange,
      handleUserSelectSizeChange,
      handleUserSelectCurrentChange,
      handleRoomSelectSizeChange,
      handleRoomSelectCurrentChange,
      confirmAddUsers,
      confirmAddRooms
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 选择弹窗样式 */
.select-dialog-content {
  padding: 10px 0;
}

.search-section {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
