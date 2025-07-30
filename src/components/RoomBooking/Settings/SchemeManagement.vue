<template>
  <div class="scheme-management-container">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>楼栋架构</h3>
      </div>
      <div class="sidebar-content">
        <el-input
          v-model="sidebarSearch"
          placeholder="搜索楼栋"
          :prefix-icon="Search"
          clearable
          class="search-input"
        />
        <el-tree
          :data="buildingTreeData"
          default-expand-all
          class="structure-tree"
          @node-click="handleBuildingNodeClick"
        />
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 隐藏的文件上传输入框 -->
      <input
        ref="uploadRef"
        type="file"
        style="display: none"
        accept=".xlsx,.xls"
        @change="handleFileUpload"
      />

      <!-- 顶部操作栏 -->
      <div class="header-section">
        <div class="search-filters">
          <div class="filter-item">
            <label>房间名称:</label>
            <el-input
              v-model="roomNameSearch"
              placeholder="请输入房间名称或房间号"
              style="width: 200px"
              clearable
              @keyup.enter="searchRooms"
            />
          </div>
          <div class="filter-item">
            <label>是否需要审批:</label>
            <el-select v-model="approvalFilter" placeholder="全部" style="width: 120px" @change="searchRooms">
              <el-option label="全部" value="all"></el-option>
              <el-option label="需要" value="yes"></el-option>
              <el-option label="不需要" value="no"></el-option>
            </el-select>
          </div>
          <div class="filter-item">
            <el-button type="primary" @click="searchRooms">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="warning" @click="exportData">导出</el-button>
          <el-button type="success" @click="importData">导入</el-button>
          <el-button type="danger" @click="deleteSelected">删除</el-button>
          <el-button type="primary" @click="showAddRoomDialog">添加教室</el-button>
          <el-button type="primary" @click="syncRooms">手动同步</el-button>
          <el-button type="info" @click="batchSetPermissions">批量配置审批权限</el-button>
        </div>
      </div>

      <!-- 表格区域 -->
      <div class="table-section">
        <el-table
          :data="roomData"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="roomName" label="教室" width="150" />
          <el-table-column prop="roomNo" label="房间号" width="100" />
          <el-table-column prop="roomAreaName" label="所属楼" width="100" />
          <el-table-column label="是否支持自选审批人" width="160">
            <template #default="{ row }">
              <el-select v-model="row.allowBookerSelectApprover" size="small">
                <el-option label="是" value="yes"></el-option>
                <el-option label="否" value="no"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="是否需要审批" width="140">
            <template #default="{ row }">
              <el-select v-model="row.needApproval" size="small">
                <el-option label="是" value="yes"></el-option>
                <el-option label="否" value="no"></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="details" label="申请人" min-width="300">
            <template #default="{ row }">
              <div class="details-content">
                <div class="detail-line">第一级审批：{{ row.firstApprover }}</div>
                <div class="detail-line">第二级审批：{{ row.secondApprover }}</div>
                <div class="detail-line">第三级审批：{{ row.thirdApprover }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="managePersonnel(row)"
              >审批人设置</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页区域 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加教室弹出框 -->
    <el-dialog
      v-model="addRoomDialogVisible"
      :title="isEditMode ? '编辑教室信息' : '基本信息'"
      width="600px"
      :before-close="handleCloseDialog"
    >
      <el-form
        ref="addRoomFormRef"
        :model="addRoomForm"
        :rules="addRoomRules"
        label-width="120px"
        class="add-room-form"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <h4 class="section-title">基本信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="教室编号:">
                <el-input v-model="addRoomForm.classroomCode" placeholder="新增后自动生成" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="教室名称:" prop="classroomName">
                <el-input v-model="addRoomForm.classroomName" placeholder="请输入教室名称" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="房间号:">
                <el-input v-model="addRoomForm.roomNumber" placeholder="请输入房间号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属楼:" prop="building">
                 <el-tree-select
                   v-model="addRoomForm.building"
                   :data="buildingTreeData"
                   :props="{ label: 'label', value: 'id', children: 'children' }"
                   placeholder="请选择所属楼"
                   style="width: 100%"
                   check-strictly
                   :render-after-expand="false"
                 />
               </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
             <el-col :span="24">
               <el-form-item label="备注信息:">
                 <el-input v-model="addRoomForm.remarks" placeholder="请输入备注信息" />
               </el-form-item>
             </el-col>
           </el-row>
        </div>

        <!-- 设置审批信息 -->
        <div class="form-section">
          <h4 class="section-title">设置审批信息</h4>
          <div class="approval-settings">
            <!-- 是否需要预约自选人员审批 -->
            <el-form-item label="是否需要预约自选人员审批:" style="margin-bottom: 20px;">
              <el-radio-group v-model="addRoomForm.allowBookerSelectApprover">
                <el-radio label="yes">是</el-radio>
                <el-radio label="no">否</el-radio>
              </el-radio-group>
            </el-form-item>

            <div class="approval-note">
                <el-text v-if="addRoomForm.allowBookerSelectApprover === 'yes'" type="success" size="small">
                  注：第一级审批人由预约人在发起预约申请时自行选择。下方可设置第二级及以上审批人。
                </el-text>
                <el-text v-else-if="approvalLevels.some(level => level.approvers)" type="info" size="small">
                  注：已设置审批人，预约申请将按照设置的审批层级进行审批。
                </el-text>
                <el-text v-else type="warning" size="small">
                  注：未设置审批人，系统认为该教室无需审批。
                </el-text>
              </div>

            <!-- 审批级别设置 -->
            <div>
              <div v-for="(level, index) in approvalLevels" :key="index" class="approval-level">
                <el-form-item :label="`第${level.level}级审批人:`">
                  <div class="approval-input-group">
                    <el-input
                      v-model="level.approvers"
                      placeholder="点击选择审批人"
                      readonly
                      @click="showPersonnelDialog(index)"
                      class="approver-input"
                    >
                      <template #suffix>
                        <el-icon class="cursor-pointer"><User /></el-icon>
                      </template>
                    </el-input>
                    <el-button
                      v-if="index === approvalLevels.length - 1"
                      type="success"
                      size="small"
                      @click="addApprovalLevel"
                      class="add-level-btn-inline"
                    >
                      下一级
                    </el-button>
                    <el-button
                      v-if="index > 0"
                      type="danger"
                      size="small"
                      @click="removeApprovalLevel(index)"
                      class="remove-level-btn"
                    >
                      删除
                    </el-button>
                  </div>
                </el-form-item>
              </div>
            </div>
          </div>
        </div>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitRoom">{{ isEditMode ? '更新' : '确认' }}</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 人员选择弹出框 -->
    <el-dialog
      v-model="personnelDialogVisible"
      title="选择审批人"
      width="800px"
      :before-close="handlePersonnelDialogClose"
    >
      <!-- 搜索区域 -->
      <div class="personnel-search">
        <el-form :inline="true" class="search-form">
          <el-form-item label="姓名:">
            <el-input
              v-model="personnelSearchForm.name"
              placeholder="请输入姓名"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="部门:">
            <el-input
              v-model="personnelSearchForm.department"
              placeholder="请输入部门"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchPersonnelData">查询</el-button>
            <el-button @click="resetPersonnelSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 人员列表 -->
      <div class="personnel-table">
        <el-table
          ref="personnelTableRef"
          :data="personnelList"
          @selection-change="handlePersonnelSelectionChange"
          style="width: 100%"
          border
          max-height="400px"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="department" label="部门" width="150" />
          <el-table-column prop="position" label="职位" width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="phone" label="电话" width="130" />
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="personnel-pagination">
        <el-pagination
          v-model:current-page="personnelPagination.currentPage"
          v-model:page-size="personnelPagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="personnelPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePersonnelPageSizeChange"
          @current-change="handlePersonnelCurrentPageChange"
        />
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handlePersonnelDialogClose">取消</el-button>
          <el-button type="primary" @click="confirmPersonnelSelection">确认选择</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量配置审批权限弹出框 -->
    <el-dialog
      v-model="batchPermissionDialogVisible"
      title="批量配置审批权限"
      width="800px"
      :before-close="handleCloseBatchPermissionDialog"
    >
      <div class="batch-permission-content">
        <!-- 基础信息 -->
        <div class="basic-info">
          <h4>基础信息</h4>
          <div class="selected-count">
            已选择教室数量：<span class="count-number">{{ selectedRows.length }}</span> (间)
          </div>
          <div class="info-text">
            批量设置审批人员，选择的教室审批人员为一致，如需要不同教室设置不同审批权限，请单独设置
          </div>
        </div>

        <!-- 设置审批信息 -->
        <div class="approval-settings">
          <h4>设置审批信息</h4>
          <div class="approval-tip">
            Ps：本设置审批人员，系统默认设置"无审批"。
          </div>

          <div class="approval-option">
            <label>是否需要预约自选人员审批：</label>
            <el-radio-group v-model="batchPermissionForm.allowBookerSelectApprover">
              <el-radio label="yes">是</el-radio>
              <el-radio label="no">否</el-radio>
            </el-radio-group>
          </div>

          <!-- 审批级别设置 -->
          <div class="approval-levels">
            <div
              v-for="(level, index) in batchApprovalLevels"
              :key="index"
              class="approval-level-item"
            >
              <div class="level-header">
                <span class="level-title">第{{ level.level === 1 ? '一' : level.level === 2 ? '二' : '三' }}级审批人：</span>
                <div class="level-actions">
                  <el-button
                    type="primary"
                    size="small"
                    @click="showBatchPersonnelDialog(index)"
                  >
                    选择审批人员
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="removeBatchApprovalLevel(index)"
                    :disabled="batchApprovalLevels.length <= 1"
                  >
                    删除
                  </el-button>
                </div>
              </div>

              <div class="approvers-display">
                <div class="approver-tags">
                  <template v-if="level.approvers">
                    <el-tag
                      v-for="(approver, approverIndex) in level.approvers.split('、')"
                      :key="approverIndex"
                      class="approver-tag"
                      closable
                      @close="removeApproverFromBatchLevel(index, approverIndex)"
                    >
                      <el-avatar :size="20" class="approver-avatar">
                        <el-icon><User /></el-icon>
                      </el-avatar>
                      {{ approver }}
                    </el-tag>
                  </template>
                  <span v-else class="no-approver">暂无审批人员</span>
                </div>
              </div>
            </div>

            <!-- 添加审批级别按钮 -->
            <div class="add-level-btn">
              <el-button
                type="success"
                @click="addBatchApprovalLevel"
                class="add-level-btn-primary"
              >
                添加审批级别
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseBatchPermissionDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitBatchPermission">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area'
import { searchClassrooms, addClassroom, updateClassroom, batchDeleteClassrooms, batchSetApproval, syncClassroomsFromRoom, searchPersonnel, exportClassrooms, importClassrooms, getClassroomDetail } from '@/api/schemeManagement'

export default {
  name: 'SchemeManagement',
  components: {
    Search,
    User,
  },
  setup() {
    // 搜索和筛选
    const sidebarSearch = ref('')
    const roomNameSearch = ref('')
    const approvalFilter = ref('all')

    // 分页
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(201)

    // 选中的行
    const selectedRows = ref([])

    // 楼栋结构树数据
    const buildingTreeData = ref([])

    // 当前选中的楼栋区域信息
    const selectedBuildingArea = ref(null)

    // 添加教室弹出框相关
    const addRoomDialogVisible = ref(false)
    const addRoomFormRef = ref(null)
    const isEditMode = ref(false) // 是否为编辑模式
    const currentEditingRoom = ref(null) // 当前编辑的房间数据
    const addRoomForm = ref({
      classroomName: '',
      roomNumber: '',
      classroomCode: '',
      building: '',
      remarks: '',

      allowBookerSelectApprover: 'yes'
    })

    // 审批级别管理
    const approvalLevels = ref([
      { level: 2, approvers: '', approverIds: '' }
    ])

    // 人员选择弹出框相关
    const personnelDialogVisible = ref(false)
    const personnelTableRef = ref(null)
    const currentApprovalLevelIndex = ref(0) // 当前正在设置的审批级别索引
    const selectedPersonnel = ref([]) // 当前选中的人员

    // 人员搜索表单
    const personnelSearchForm = ref({
      name: '',
      department: ''
    })

    // 人员分页
    const personnelPagination = ref({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    // 人员列表数据
    const personnelList = ref([])

    // 表单验证规则
    const addRoomRules = ref({
      classroomName: [
        { required: true, message: '请输入教室名称', trigger: 'blur' }
      ],
      building: [
        { required: true, message: '请选择所属楼', trigger: 'change' }
      ],

    })

    // 房间数据
    const roomData = ref([])

    const handleSelectionChange = (selection) => {
      selectedRows.value = selection
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      loadRoomData()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadRoomData()
    }

    // 搜索教室
    const searchRooms = () => {
      currentPage.value = 1
      loadRoomData()
    }

    // 重置搜索
    const resetSearch = () => {
      roomNameSearch.value = ''
      approvalFilter.value = 'all'
      selectedBuildingArea.value = null
      currentPage.value = 1
      loadRoomData()
    }

    const managePersonnel = async (row) => {
      // 设置为编辑模式
      isEditMode.value = true
      currentEditingRoom.value = row

      const loading = ElLoading.service({
        lock: true,
        text: '加载教室详情...',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      try {
        // 调用详情接口获取完整数据
        const response = await getClassroomDetail(row.id)
        const classroomDetail = response.data || response

        // 预填充表单数据
        addRoomForm.value = {
          classroomName: classroomDetail.classroomName || row.roomName,
          roomNumber: classroomDetail.roomNumber || row.roomNo,
          classroomCode: classroomDetail.classroomCode || row.roomCode || row.id,
          building: classroomDetail.roomAreaId || row.roomAreaId,
          remarks: classroomDetail.remarks || row.remark || '',
          allowBookerSelectApprover: classroomDetail.allowBookerSelectApprover || row.allowBookerSelectApprover || 'yes'
        }

        // 解析审批级别数据，优先使用详情接口返回的数据
        const approvalData = []
        // 如果详情接口返回了审批级别数据
        if (classroomDetail.approvalLevels && classroomDetail.approvalLevels.length > 0) {
          classroomDetail.approvalLevels.forEach(levelData => {
            if (levelData.approvers) {
              approvalData.push({
                level: levelData.level,
                approvers: levelData.approvers,
                approverIds: levelData.approverIds || ''
              })
            }
          })
        } else {
          // 如果没有审批级别数据，使用表格数据
          if (classroomDetail.firstApprover || row.firstApprover) {
            approvalData.push({
              level: 1,
              approvers: classroomDetail.firstApprover || row.firstApprover,
              approverIds: classroomDetail.firstApproverIds || ''
            })
          }
          if (classroomDetail.secondApprover || row.secondApprover) {
            approvalData.push({
              level: 2,
              approvers: classroomDetail.secondApprover || row.secondApprover,
              approverIds: classroomDetail.secondApproverIds || ''
            })
          }
          if (classroomDetail.thirdApprover || row.thirdApprover) {
            approvalData.push({
              level: 3,
              approvers: classroomDetail.thirdApprover || row.thirdApprover,
              approverIds: classroomDetail.thirdApproverIds || ''
            })
          }
        }

        // 根据后端返回的配置来设置界面状态
        const allowBookerSelect = classroomDetail.allowBookerSelectApprover === 'yes'
        addRoomForm.value.allowBookerSelectApprover = allowBookerSelect ? 'yes' : 'no'

        if (!allowBookerSelect) {
          // 不需要预约自选人员审批 - 显示所有审批级别（包括第一级）
          approvalLevels.value = approvalData.length > 0 ? approvalData : [{ level: 1, approvers: '', approverIds: '' }]
        } else {
          // 需要预约自选人员审批 - 只显示第二级和第三级审批人
          const filteredApprovalData = approvalData.filter(item => item.level > 1)
          approvalLevels.value = filteredApprovalData.length > 0 ? filteredApprovalData : [{ level: 2, approvers: '', approverIds: '' }]
        }

        // 显示弹出框
        addRoomDialogVisible.value = true

      } catch (error) {
        console.error('获取教室详情失败:', error)
        ElMessage.error('获取教室详情失败，使用列表数据')

        // 如果接口调用失败，使用原来的逻辑作为兜底
        addRoomForm.value = {
          classroomName: row.roomName,
          roomNumber: row.roomNo,
          classroomCode: row.roomCode || row.id,
          building: row.roomAreaId,
          remarks: row.remark || '',
          allowBookerSelectApprover: row.allowBookerSelectApprover || 'yes'
        }

        // 解析并设置审批级别数据
        const approvalData = []
        if (row.firstApprover) {
          approvalData.push({ level: 1, approvers: row.firstApprover, approverIds: '' })
        }
        if (row.secondApprover) {
          approvalData.push({ level: 2, approvers: row.secondApprover, approverIds: '' })
        }
        if (row.thirdApprover) {
          approvalData.push({ level: 3, approvers: row.thirdApprover, approverIds: '' })
        }

        // 使用表格数据的默认逻辑（兜底逻辑）
        const allowBookerSelect = (row.allowBookerSelectApprover === 'yes')
        addRoomForm.value.allowBookerSelectApprover = allowBookerSelect ? 'yes' : 'no'

        if (!allowBookerSelect) {
          // 不需要预约自选人员审批 - 显示所有审批级别（包括第一级）
          approvalLevels.value = approvalData.length > 0 ? approvalData : [{ level: 1, approvers: '', approverIds: '' }]
        } else {
          // 需要预约自选人员审批 - 只显示第二级和第三级审批人
          const filteredApprovalData = approvalData.filter(item => item.level > 1)
          approvalLevels.value = filteredApprovalData.length > 0 ? filteredApprovalData : [{ level: 2, approvers: '', approverIds: '' }]
        }

        // 显示弹出框
        addRoomDialogVisible.value = true
      } finally {
        loading.close()
      }
    }

    const exportData = async () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要导出的数据')
        return
      }

      const loading = ElLoading.service({
        lock: true,
        text: '导出中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      try {
        // 获取所有选中行的ID
        const ids = selectedRows.value.map(row => row.id)
        // 调用导出接口
        const response = await exportClassrooms(ids)

        // 创建下载链接
        const blob = new Blob([response], { type: 'application/vnd.ms-excel' })
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = `教室数据_${new Date().toLocaleDateString()}.xlsx`
        link.click()
        URL.revokeObjectURL(link.href)

        ElMessage.success(`成功导出 ${selectedRows.value.length} 条数据`)
      } catch (error) {
        console.error('导出数据失败:', error)
        ElMessage.error('导出数据失败')
      } finally {
        loading.close()
      }
    }

    // 文件上传引用
    const uploadRef = ref(null)

    const importData = () => {
      // 触发文件选择
      uploadRef.value.click()
    }

    // 处理文件上传
    const handleFileUpload = async (event) => {
      const file = event.target.files[0]
      if (!file) return

      // 检查文件类型
      if (!file.name.endsWith('.xlsx') && !file.name.endsWith('.xls')) {
        ElMessage.error('请上传Excel文件(.xlsx或.xls)')
        return
      }

      const loading = ElLoading.service({
        lock: true,
        text: '导入中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      try {
        const formData = new FormData()
        formData.append('file', file)

        // 调用导入接口
        const response = await importClassrooms(formData)

        if (response.success) {
          ElMessage.success(`导入成功: 共导入${response.data.successCount}条数据，失败${response.data.failCount}条`)
          // 重新加载数据
          loadRoomData()
        } else {
          ElMessage.error(`导入失败: ${response.message}`)
        }
      } catch (error) {
        console.error('导入数据失败:', error)
        ElMessage.error('导入数据失败')
      } finally {
        loading.close()
        // 清空文件选择
        event.target.value = ''
      }
    }

    const deleteSelected = async () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要删除的数据')
        return
      }
      try {
        await ElMessageBox.confirm(
          `确认删除选中的 ${selectedRows.value.length} 条数据吗？`,
          '删除确认',
        )

        const loading = ElLoading.service({
          lock: true,
          text: '删除中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          // 获取所有选中行的ID
          const ids = selectedRows.value.map(row => row.id)
          // 调用批量删除接口
          await batchDeleteClassrooms(ids)
          ElMessage.success(`已将 ${selectedRows.value.length} 个教室移动到回收站`)
          selectedRows.value = []
          // 重新加载数据
          loadRoomData()
        } catch (error) {
          console.error('删除教室失败:', error)
          ElMessage.error('删除教室失败')
        } finally {
          loading.close()
        }
      } catch {
        // 用户取消
      }
    }

    // 同步教室数据
    const syncRooms = async () => {
      try {
        const loading = ElLoading.service({
          lock: true,
          text: '同步中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        // 调用同步接口
        const response = await syncClassroomsFromRoom()

        if (response.success) {
          ElMessage.success(`同步成功: 共同步${response.data}条数据`)
          // 重新加载数据
          loadRoomData()
        } else {
          ElMessage.error(`同步失败: ${response.message}`)
        }

        loading.close()
      } catch (error) {
        console.error('同步数据失败:', error)
        ElMessage.error('同步数据失败')
      }
    }

    // 显示添加教室弹出框
    const showAddRoomDialog = () => {
      isEditMode.value = false
      currentEditingRoom.value = null
      addRoomDialogVisible.value = true
      // 重置表单
      resetAddRoomForm()
    }



    // 根据区域ID获取区域名称
    const getBuildingNameById = (buildingId) => {
      if (!buildingId || !buildingTreeData.value) return ''

      const findBuildingName = (nodes) => {
        for (const node of nodes) {
          if (node.id === buildingId) {
            return node.label
          }
          if (node.children && node.children.length > 0) {
            const found = findBuildingName(node.children)
            if (found) return found
          }
        }
        return null
      }

      return findBuildingName(buildingTreeData.value) || ''
    }

    // 自动生成教室编号
    const generateClassroomCode = () => {
      const { classroomName, building } = addRoomForm.value
      if (classroomName && building) {
        // 生成格式：楼栋-教室名称简码-时间戳
        const nameCode = classroomName.substring(0, 2) // 取教室名称前两个字符
        const timestamp = Date.now().toString().slice(-4) // 取时间戳后4位
        const buildingName = getBuildingNameById(building)
        addRoomForm.value.classroomCode = `${buildingName}-${nameCode}${timestamp}`
      }
    }

    // 添加审批级别
    const addApprovalLevel = () => {
      const maxLevel = Math.max(...approvalLevels.value.map(item => item.level))
      approvalLevels.value.push({
        level: maxLevel + 1,
        approvers: '',
        approverIds: ''
      })
    }

    // 删除审批级别
    const removeApprovalLevel = (index) => {
      if (approvalLevels.value.length > 1) {
        approvalLevels.value.splice(index, 1)
      }
    }

    // 显示人员选择弹出框
    const showPersonnelDialog = (levelIndex) => {
      currentApprovalLevelIndex.value = levelIndex
      // 解析当前已选择的人员
      const currentApprovers = approvalLevels.value[levelIndex].approvers
      if (currentApprovers) {
        const approverNames = currentApprovers.split('、')
        // 保存已选择的人员名称，等待数据加载后再设置选中状态
        selectedPersonnel.value = approverNames.map(name => ({ name }))
      } else {
        selectedPersonnel.value = []
      }

      // 重置分页和搜索条件
      personnelPagination.value.currentPage = 1
      personnelSearchForm.value = {
        name: '',
        department: ''
      }

      // 加载人员数据
      searchPersonnelData()

      personnelDialogVisible.value = true
    }

    // 搜索人员
    const searchPersonnelData = async () => {
      try {
        const params = {
          pageNumber: personnelPagination.value.currentPage,
          pageSize: personnelPagination.value.pageSize,
          name: personnelSearchForm.value.name || '',
          department: personnelSearchForm.value.department || '',
          position: ''
        }

        const loading = ElLoading.service({
          lock: true,
          text: '搜索中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          const res = await searchPersonnel(params)
          personnelList.value = res.data.rows || []
          personnelPagination.value.total = res.data.total || 0

          // 设置表格选中状态
          setTimeout(() => {
            if (personnelTableRef.value) {
              selectedPersonnel.value.forEach(person => {
                const matchedPerson = personnelList.value.find(p => p.id === person.id)
                if (matchedPerson) {
                  personnelTableRef.value.toggleRowSelection(matchedPerson, true)
                }
              })
            }
          }, 100)
        } catch (error) {
          console.error('搜索人员失败:', error)
          ElMessage.error('搜索人员失败')
        } finally {
          loading.close()
        }
      } catch (error) {
        console.error('搜索人员参数错误:', error)
      }
    }

    // 重置人员搜索
    const resetPersonnelSearch = () => {
      personnelSearchForm.value = {
        name: '',
        department: ''
      }
      personnelPagination.value.currentPage = 1
      searchPersonnelData()
    }


    const handlePersonnelCurrentPageChange = (val) => {
      personnelPagination.value.currentPage = val
      searchPersonnelData()
    }

    // 处理人员选择变化
    const handlePersonnelSelectionChange = (selection) => {
      selectedPersonnel.value = selection
    }

    // 确认选择人员
    const confirmPersonnelSelection = () => {
      if (selectedPersonnel.value.length === 0) {
        ElMessage.warning('请选择至少一个人员')
        return
      }

      const approverNames = selectedPersonnel.value.map(person => person.name).join('、')
      const approverIds = selectedPersonnel.value.map(person => person.id).join(',')

      // 检查是否是批量配置模式
      if (batchPermissionDialogVisible.value) {
        // 批量配置模式下更新批量审批级别
        batchApprovalLevels.value[currentApprovalLevelIndex.value].approvers = approverNames
        batchApprovalLevels.value[currentApprovalLevelIndex.value].approverIds = approverIds
      } else {
        // 普通模式下更新审批级别
        approvalLevels.value[currentApprovalLevelIndex.value].approvers = approverNames
        approvalLevels.value[currentApprovalLevelIndex.value].approverIds = approverIds
      }

      personnelDialogVisible.value = false
      selectedPersonnel.value = []
    }

    // 关闭人员选择弹出框
    const handlePersonnelDialogClose = () => {
      personnelDialogVisible.value = false
      selectedPersonnel.value = []
    }


    // 处理人员分页大小变化
    const handlePersonnelPageSizeChange = (size) => {
      personnelPagination.value.pageSize = size
      personnelPagination.value.currentPage = 1
      searchPersonnelData()
    }

    // 监听表单变化，自动生成教室编号
    watch(
      () => [addRoomForm.value.classroomName, addRoomForm.value.building],
      () => {
        generateClassroomCode()
      },
      { deep: true }
    )

    // 监听是否需要预约自选人员审批的变化
    watch(() => addRoomForm.value.allowBookerSelectApprover, (newValue) => {
      if (approvalLevels.value.length > 0) return // ✅ 已有数据就不改动

      if (newValue === 'yes') {
        approvalLevels.value = [
          { level: 2, approvers: '', approverIds: '' }
        ]
      } else {
        approvalLevels.value = [
          { level: 1, approvers: '', approverIds: '' }
        ]
      }
    })


    // 重置添加教室表单
    const resetAddRoomForm = () => {
      addRoomForm.value = {
        classroomName: '',
        roomNumber: '',
        classroomCode: '',
        building: '',
        remarks: '',
        allowBookerSelectApprover: 'yes'
      }
      // 重置审批级别
      approvalLevels.value = [
        { level: 2, approvers: '', approverIds: '' }
      ]
      // 清除表单验证
      if (addRoomFormRef.value) {
        addRoomFormRef.value.clearValidate()
      }
    }

    // 关闭弹出框
    const handleCloseDialog = () => {
      addRoomDialogVisible.value = false
      isEditMode.value = false
      currentEditingRoom.value = null
      resetAddRoomForm()
    }

    // 提交添加教室表单
    const handleSubmitRoom = async () => {
      if (!addRoomFormRef.value) return

      try {
        await addRoomFormRef.value.validate()

        // 处理审批级别数据
        const approvalLevelsData = approvalLevels.value.map(level => ({
          level: level.level,
          approvers: level.approvers || '',
          approverIds: level.approverIds || '' // 使用存储的审批人ID
        }))

        // 构建提交的数据
        const submitData = {
          classroomName: addRoomForm.value.classroomName, // 教室名称对应roomName
          roomNumber: addRoomForm.value.roomNumber, // 房间号对应roomNo
          roomAreaName: getBuildingNameById(addRoomForm.value.building), // 根据ID获取名称
          roomAreaId: addRoomForm.value.building, // 区域ID
          remarks: addRoomForm.value.remarks,
          roomVolume: 0,
          roomArea: '',
          allowBookerSelectApprover: addRoomForm.value.allowBookerSelectApprover,
          approvalLevels: approvalLevelsData
        }

        // 编辑模式下需要传入ID
        if (isEditMode.value) {
          submitData.id = currentEditingRoom.value.id
        }

        const loading = ElLoading.service({
          lock: true,
          text: '提交中...',
          background: 'rgba(0, 0, 0, 0.7)'
        })

        try {
          if (isEditMode.value) {
            // 编辑模式，调用更新接口
            await updateClassroom(submitData)
            ElMessage.success('更新教室信息成功')
          } else {
            // 添加模式，调用添加接口
            await addClassroom(submitData)
            ElMessage.success('添加教室成功')
          }

          handleCloseDialog()
          // 重新加载数据
          loadRoomData()
        } catch (error) {
          console.error(isEditMode.value ? '修改教室失败:' : '添加教室失败:', error)
          ElMessage.error(isEditMode.value ? '修改教室失败' : '添加教室失败')
        } finally {
          loading.close()
        }
      } catch (error) {
        console.error('表单验证失败:', error)
      }
    }

    // 批量配置审批权限弹出框相关
    const batchPermissionDialogVisible = ref(false)
    const batchPermissionForm = ref({
      allowBookerSelectApprover: 'yes'
    })

    // 批量审批级别管理
    const batchApprovalLevels = ref([
      { level: 2, approvers: '', approverIds: '' }
    ])

    const batchSetPermissions = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要设置权限的数据')
        return
      }
      // 重置批量配置表单
      batchPermissionForm.value = {
        allowBookerSelectApprover: 'yes'
      }
      batchApprovalLevels.value = [
        { level: 2, approvers: '', approverIds: '' }
      ]
      batchPermissionDialogVisible.value = true
    }

    // 关闭批量配置弹出框
    const handleCloseBatchPermissionDialog = () => {
      batchPermissionDialogVisible.value = false
    }

    // 提交批量配置
    const handleSubmitBatchPermission = async () => {
      const loading = ElLoading.service({
        lock: true,
        text: '提交中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      try {
        // 处理审批级别数据
        const approvalLevelsData = batchApprovalLevels.value.map(level => ({
          level: level.level,
          approvers: level.approvers || '',
          approverIds: level.approverIds || ''
        }))

        // 构建提交的数据
        const submitData = {
          roomIds: selectedRows.value.map(room => room.id),
          allowBookerSelectApprover: batchPermissionForm.value.allowBookerSelectApprover,
          approvalLevels: approvalLevelsData
        }

        // 调用批量设置审批接口
        await batchSetApproval(submitData)
        ElMessage.success(`已成功为 ${selectedRows.value.length} 个教室配置审批权限`)
        batchPermissionDialogVisible.value = false
        selectedRows.value = []
        // 重新加载数据
        loadRoomData()
      } catch (error) {
        console.error('批量设置审批权限失败:', error)
        ElMessage.error('批量设置审批权限失败')
      } finally {
        loading.close()
      }
    }

    // 批量添加审批级别
    const addBatchApprovalLevel = () => {
      const maxLevel = Math.max(...batchApprovalLevels.value.map(item => item.level))
      batchApprovalLevels.value.push({
        level: maxLevel + 1,
        approvers: '',
        approverIds: ''
      })
    }

    // 批量删除审批级别
    const removeBatchApprovalLevel = (index) => {
      if (batchApprovalLevels.value.length > 1) {
        batchApprovalLevels.value.splice(index, 1)
      }
    }

    // 批量显示人员选择弹出框
    const showBatchPersonnelDialog = (levelIndex) => {
      currentApprovalLevelIndex.value = levelIndex
      // 解析当前已选择的人员
      const currentApprovers = batchApprovalLevels.value[levelIndex].approvers
      if (currentApprovers) {
        const approverNames = currentApprovers.split('、')
        selectedPersonnel.value = personnelList.value.filter(person =>
          approverNames.includes(person.name)
        )
        // 设置表格选中状态
        setTimeout(() => {
          if (personnelTableRef.value) {
            selectedPersonnel.value.forEach(person => {
              personnelTableRef.value.toggleRowSelection(person, true)
            })
          }
        }, 100)
      } else {
        selectedPersonnel.value = []
      }
      personnelDialogVisible.value = true
    }

    // 监听批量配置中是否需要预约自选人员审批的变化
     watch(() => batchPermissionForm.value.allowBookerSelectApprover, (newValue) => {
       if (newValue === 'yes') {
         // 选择"是"时，从第二级开始设置审批
         batchApprovalLevels.value = [
           { level: 2, approvers: '', approverIds: '' }
         ]
       } else {
         // 选择"否"时，从第一级开始设置审批
         batchApprovalLevels.value = [
           { level: 1, approvers: '', approverIds: '' }
         ]
       }
     })

     // 从批量审批级别中删除指定审批人
     const removeApproverFromBatchLevel = (levelIndex, approverIndex) => {
       const level = batchApprovalLevels.value[levelIndex]
       const approvers = level.approvers.split('、')
       approvers.splice(approverIndex, 1)
       level.approvers = approvers.join('、')
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


    // 处理楼栋架构树节点点击事件
    const handleBuildingNodeClick = (data) => {
      // 保存选中的区域信息
      selectedBuildingArea.value = {
        id: data.id,
        name: data.label
      }
      ElMessage.success(`已选择区域：${data.label}`)
      // 根据选中的区域过滤房间数据
      loadRoomData()
    }

    // 加载教室数据
    const loadRoomData = async () => {
      const loading = ElLoading.service({
        lock: true,
        text: '加载中...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      try {
        const params = {
          pageNumber: currentPage.value,
          pageSize: pageSize.value,
          roomName: roomNameSearch.value || '',
          approvalFilter: approvalFilter.value === 'all' ? '' : approvalFilter.value,
          roomAreaId: selectedBuildingArea.value ? selectedBuildingArea.value.id : '',
          roomNo: '',
          roomCode: ''
        }
        const res = await searchClassrooms(params)
        roomData.value = res.data.rows || []
        total.value = res.data.total || 0
      } catch (error) {
        console.error('加载教室数据失败:', error)
        ElMessage.error('加载教室数据失败')
      } finally {
        loading.close()
      }
    }

    // 页面初始化
    onMounted(() => {
      loadBuildingTree()
      // 加载教室数据
      loadRoomData()
    })

    return {
      // 基础数据
      sidebarSearch,
      roomNameSearch,
      approvalFilter,
      currentPage,
      pageSize,
      total,
      selectedRows,
      roomData,
      buildingTreeData,
      selectedBuildingArea,
      // 基础方法
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      searchRooms,
      resetSearch,
      managePersonnel,
      exportData,
      uploadRef,
      handleFileUpload,
      importData,
      deleteSelected,
      syncRooms,
      batchSetPermissions,
      handleBuildingNodeClick,
      loadBuildingTree,
      loadRoomData,
      // 添加教室相关
      addRoomDialogVisible,
      addRoomFormRef,
      addRoomForm,
      addRoomRules,
      isEditMode,
      currentEditingRoom,
      showAddRoomDialog,
      handleCloseDialog,
      handleSubmitRoom,
      getBuildingNameById,
      generateClassroomCode,
      // 审批级别管理
      approvalLevels,
      addApprovalLevel,
      removeApprovalLevel,
      // 人员选择相关
      personnelDialogVisible,
      personnelTableRef,
      currentApprovalLevelIndex,
      selectedPersonnel,
      personnelSearchForm,
      personnelPagination,
      personnelList,
      showPersonnelDialog,
      searchPersonnelData,
      resetPersonnelSearch,
      handlePersonnelSelectionChange,
      confirmPersonnelSelection,
      handlePersonnelDialogClose,
      handlePersonnelCurrentPageChange,
      handlePersonnelPageSizeChange,
      // 批量配置相关
      batchPermissionDialogVisible,
      batchPermissionForm,
      batchApprovalLevels,
      handleCloseBatchPermissionDialog,
      handleSubmitBatchPermission,
      addBatchApprovalLevel,
      removeBatchApprovalLevel,
      showBatchPersonnelDialog,
      removeApproverFromBatchLevel
    }
  }
}
</script>

<style scoped>
.scheme-management-container {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
}

/* 左侧导航栏 */
.sidebar {
  width: 250px;
  background-color: white;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.sidebar-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.search-input {
  margin-bottom: 15px;
}

.structure-tree {
  margin-top: 10px;
}

.structure-tree :deep(.el-tree-node__content) {
  height: 32px;
  line-height: 32px;
}

.structure-tree :deep(.el-tree-node__label) {
  font-size: 14px;
  color: #606266;
}

.structure-tree :deep(.el-tree-node:focus > .el-tree-node__content) {
  background-color: #f5f7fa;
}

.structure-tree :deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  margin: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 顶部操作栏 */
.header-section {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafbfc;
}

.search-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

/* 表格区域 */
.table-section {
  flex: 1;
  padding: 0 20px;
  overflow: auto;
}

.details-content {
  line-height: 1.6;
}

.detail-line {
  margin-bottom: 4px;
  font-size: 13px;
  color: #606266;
}

.detail-line:last-child {
  margin-bottom: 0;
}

/* 分页区域 */
.pagination-section {
  padding: 16px 20px;
  border-top: 1px solid #e4e7ed;
  background: #fafbfc;
  display: flex;
  justify-content: center;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-select) {
  width: 100%;
}

/* 添加教室弹出框样式 */
.add-room-form {
  padding: 20px 0;
}

.form-section {
  margin-bottom: 30px;
}

.section-title {
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.approval-settings {
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.approval-note {
  margin: 15px 0;
  padding: 12px;
  background-color: #f0f9ff;
  border-left: 4px solid #409eff;
  border-radius: 4px;
}

.approval-level {
  margin-bottom: 20px;
  padding: 15px;
  background-color: white;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
}

.approval-input-group {
  display: flex;
  gap: 10px;
  align-items: center;
}

.approver-input {
  flex: 1;
  cursor: pointer;
}

.add-level-btn,
.remove-level-btn {
  flex-shrink: 0;
}

.add-level-btn-inline {
  margin-left: 8px;
  background: linear-gradient(45deg, #67c23a, #85ce61);
  border: none;
  box-shadow: 0 2px 4px rgba(103, 194, 58, 0.3);
  font-weight: bold;
}

.add-level-btn-inline:hover {
  background: linear-gradient(45deg, #5daf34, #7bc652);
  box-shadow: 0 4px 8px rgba(103, 194, 58, 0.4);
  transform: translateY(-1px);
}

/* 人员选择弹出框样式 */
.personnel-search {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.personnel-table {
  margin-bottom: 20px;
}

.personnel-pagination {
  margin-top: 20px;
  text-align: center;
}

/* 批量配置审批权限弹出框样式 */
.batch-permission-content {
  padding: 20px 0;
}

.basic-info {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.basic-info h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.selected-count {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.count-number {
  color: #e74c3c;
  font-weight: 600;
  font-size: 16px;
}

.info-text {
  font-size: 13px;
  color: #888;
  line-height: 1.5;
}

.approval-settings {
  padding: 0 20px;
}

.approval-settings h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.approval-tip {
  margin-bottom: 20px;
  padding: 10px;
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  color: #856404;
  font-size: 13px;
}

.approval-option {
  margin-bottom: 25px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.approval-option label {
  display: inline-block;
  margin-right: 15px;
  font-weight: 500;
  color: #333;
}

.approval-levels {
  margin-top: 20px;
}

.approval-level-item {
  margin-bottom: 25px;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fafafa;
}

.level-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.level-title {
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.level-actions {
  display: flex;
  gap: 8px;
}

.level-actions .el-button {
  padding: 5px 12px;
  font-size: 12px;
}

.approvers-display {
  min-height: 40px;
}

.approver-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.approver-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background-color: #e3f2fd;
  border: 1px solid #2196f3;
  border-radius: 20px;
  color: #1976d2;
}

.approver-avatar {
  background-color: #2196f3;
  color: white;
}

.no-approver {
  color: #999;
  font-style: italic;
  font-size: 13px;
}

.add-level-btn {
  text-align: center;
  margin-top: 20px;
  padding: 20px;
  border: 2px dashed #67c23a;
  border-radius: 8px;
  background-color: #f5faf5;
  transition: all 0.3s ease;
}

.add-level-btn:hover {
  border-color: #5daf34;
  background-color: #eef8ee;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);
}

.add-level-btn-primary {
  background: linear-gradient(45deg, #67c23a, #85ce61) !important;
  border: none !important;
  box-shadow: 0 3px 6px rgba(103, 194, 58, 0.3);
  font-weight: bold;
  padding: 10px 20px;
  font-size: 14px;
}

.add-level-btn-primary:hover {
  background: linear-gradient(45deg, #5daf34, #7bc652) !important;
  box-shadow: 0 5px 10px rgba(103, 194, 58, 0.4);
  transform: translateY(-2px);
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>
