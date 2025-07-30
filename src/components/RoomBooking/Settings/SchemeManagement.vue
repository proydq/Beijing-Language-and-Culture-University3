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
            />
          </div>
          <div class="filter-item">
            <label>是否需要审批:</label>
            <el-select v-model="approvalFilter" placeholder="全部" style="width: 120px">
              <el-option label="全部" value="all"></el-option>
              <el-option label="需要" value="yes"></el-option>
              <el-option label="不需要" value="no"></el-option>
            </el-select>
          </div>
        </div>

        <div class="action-buttons">
          <el-button type="warning" @click="exportData">导出</el-button>
          <el-button type="success" @click="importData">导入</el-button>
          <el-button type="danger" @click="deleteSelected">删除</el-button>
          <el-button type="primary" @click="showAddRoomDialog">添加教室</el-button>
          <el-button type="primary" @click="viewDetails">手动同步</el-button>
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
          <el-table-column prop="roomNumber" label="房间号" width="100" />
          <el-table-column prop="location" label="所属楼" width="100" />
          <el-table-column label="是否交换" width="120">
            <template #default="{ row }">
              <el-select v-model="row.isExchange" size="small">
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
      title="基本信息"
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
              <el-form-item label="教室名称:" prop="classroomName">
                <el-input v-model="addRoomForm.classroomName" placeholder="请输入教室名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="房间号:" prop="roomNumber">
                <el-input v-model="addRoomForm.roomNumber" placeholder="请输入房间号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="教室编号:">
                <el-input v-model="addRoomForm.classroomCode" placeholder="自动生成" disabled />
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
          <el-form-item label="是否需要审批:" prop="needApproval">
            <el-radio-group v-model="addRoomForm.needApproval">
              <el-radio label="yes">是</el-radio>
              <el-radio label="no">否</el-radio>
            </el-radio-group>
          </el-form-item>

          <div v-if="addRoomForm.needApproval === 'yes'" class="approval-settings">
            <div class="approval-note">
              <el-text type="info" size="small">注：第一级审批人由预约人在发起预约申请时自行选择。</el-text>
            </div>
            
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
                    v-if="index === approvalLevels.length - 1 && index < 2" 
                    type="primary" 
                    size="small" 
                    @click="addApprovalLevel"
                    class="add-level-btn"
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
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmitRoom">确认</el-button>
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
            <el-button type="primary" @click="searchPersonnel">查询</el-button>
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
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, User } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area'

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
    const addRoomForm = ref({
      classroomName: '',
      roomNumber: '',
      classroomCode: '',
      building: '',
      remarks: '',
      needApproval: 'no'
    })

    // 审批级别管理
    const approvalLevels = ref([
      { level: 2, approvers: '' } // 默认从第二级开始，第一级由预约人选择
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
    
    // 人员列表数据（模拟数据，实际应该从后端获取）
    const personnelList = ref([
      { id: 1, name: '张三', department: '信息技术部', position: '部门经理', email: 'zhangsan@example.com', phone: '13800138001' },
      { id: 2, name: '李四', department: '人力资源部', position: '主管', email: 'lisi@example.com', phone: '13800138002' },
      { id: 3, name: '王五', department: '财务部', position: '会计', email: 'wangwu@example.com', phone: '13800138003' },
      { id: 4, name: '赵六', department: '市场部', position: '经理', email: 'zhaoliu@example.com', phone: '13800138004' },
      { id: 5, name: '孙七', department: '技术部', position: '工程师', email: 'sunqi@example.com', phone: '13800138005' },
      { id: 6, name: '周八', department: '运营部', position: '专员', email: 'zhouba@example.com', phone: '13800138006' },
      { id: 7, name: '吴九', department: '产品部', position: '产品经理', email: 'wujiu@example.com', phone: '13800138007' },
      { id: 8, name: '郑十', department: '设计部', position: '设计师', email: 'zhengshi@example.com', phone: '13800138008' },
      { id: 9, name: '刘备', department: '管理层', position: '总经理', email: 'liubei@example.com', phone: '13800138009' },
      { id: 10, name: '关羽', department: '管理层', position: '副总经理', email: 'guanyu@example.com', phone: '13800138010' }
    ])

    // 表单验证规则
    const addRoomRules = ref({
      classroomName: [
        { required: true, message: '请输入教室名称', trigger: 'blur' }
      ],
      roomNumber: [
        { required: true, message: '请输入房间号', trigger: 'blur' }
      ],
      building: [
        { required: true, message: '请选择所属楼', trigger: 'change' }
      ],
      needApproval: [
        { required: true, message: '请选择是否需要审批', trigger: 'change' }
      ]
    })

    // 房间数据
    const roomData = ref([
      {
        id: 1,
        roomName: '多媒体教室',
        roomNumber: '101',
        location: '科研楼',
        isExchange: 'yes',
        needApproval: 'yes',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 2,
        roomName: '多媒体教室',
        roomNumber: '102',
        location: '科研楼',
        isExchange: 'yes',
        needApproval: 'yes',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 3,
        roomName: '多媒体教室',
        roomNumber: '103',
        location: '科研楼',
        isExchange: 'no',
        needApproval: 'no',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 4,
        roomName: '多媒体教室',
        roomNumber: '104',
        location: '科研楼',
        isExchange: 'yes',
        needApproval: 'yes',
        firstApprover: '张三、李四、王五',
        secondApprover: '赵六、孙七、周八、吴九、郑十',
        thirdApprover: '刘备、关羽、张飞',
      },
      {
        id: 5,
        roomName: '多媒体教室',
        roomNumber: '105',
        location: '科研楼',
        isExchange: 'no',
        needApproval: 'no',
        firstApprover: '',
        secondApprover: '',
        thirdApprover: '',
      },
      {
        id: 6,
        roomName: '多媒体教室',
        roomNumber: '106',
        location: '科研楼',
        isExchange: 'no',
        needApproval: 'no',
        firstApprover: '',
        secondApprover: '',
        thirdApprover: '',
      },
    ])

    const handleSelectionChange = (selection) => {
      selectedRows.value = selection
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
    }

    const managePersonnel = (row) => {
      ElMessage.info(`管理人员: ${row.roomName} ${row.roomNumber}`)
    }

    const exportData = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要导出的数据')
        return
      }
      ElMessage.success(`正在导出 ${selectedRows.value.length} 条数据`)
    }

    const importData = () => {
      ElMessage.info('导入功能开发中')
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
        ElMessage.success('删除成功')
        selectedRows.value = []
      } catch {
        // 用户取消
      }
    }

    const viewDetails = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要查看的数据')
        return
      }
      ElMessage.info('查看详情功能开发中')
    }

    // 显示添加教室弹出框
    const showAddRoomDialog = () => {
      addRoomDialogVisible.value = true
      // 重置表单
      resetAddRoomForm()
    }



    // 自动生成教室编号
    const generateClassroomCode = () => {
      const { classroomName, building } = addRoomForm.value
      if (classroomName && building) {
        // 生成格式：楼栋-教室名称简码-时间戳
        const nameCode = classroomName.substring(0, 2) // 取教室名称前两个字符
        const timestamp = Date.now().toString().slice(-4) // 取时间戳后4位
        addRoomForm.value.classroomCode = `${building}-${nameCode}${timestamp}`
      }
    }

    // 添加审批级别
    const addApprovalLevel = () => {
      if (approvalLevels.value.length < 3) {
        const maxLevel = Math.max(...approvalLevels.value.map(item => item.level))
        approvalLevels.value.push({
          level: maxLevel + 1,
          approvers: ''
        })
      }
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

    // 搜索人员
    const searchPersonnel = () => {
      // 这里应该调用后端API进行搜索
      // 目前使用模拟数据过滤
      const { name, department } = personnelSearchForm.value
      let filteredList = [...personnelList.value]
      
      if (name) {
        filteredList = filteredList.filter(person => person.name.includes(name))
      }
      if (department) {
        filteredList = filteredList.filter(person => person.department.includes(department))
      }
      
      // 更新分页信息
      personnelPagination.value.total = filteredList.length
      console.log('搜索人员:', personnelSearchForm.value)
    }

    // 重置人员搜索
    const resetPersonnelSearch = () => {
      personnelSearchForm.value = {
        name: '',
        department: ''
      }
      searchPersonnel()
    }

    // 处理人员选择变化
    const handlePersonnelSelectionChange = (selection) => {
      selectedPersonnel.value = selection
    }

    // 确认选择人员
    const confirmPersonnelSelection = () => {
      const approverNames = selectedPersonnel.value.map(person => person.name).join('、')
      approvalLevels.value[currentApprovalLevelIndex.value].approvers = approverNames
      personnelDialogVisible.value = false
    }

    // 关闭人员选择弹出框
    const handlePersonnelDialogClose = () => {
      personnelDialogVisible.value = false
      selectedPersonnel.value = []
    }

    // 处理人员分页变化
    const handlePersonnelCurrentPageChange = (page) => {
      personnelPagination.value.currentPage = page
      searchPersonnel()
    }

    // 处理人员分页大小变化
    const handlePersonnelPageSizeChange = (size) => {
      personnelPagination.value.pageSize = size
      personnelPagination.value.currentPage = 1
      searchPersonnel()
    }

    // 监听表单变化，自动生成教室编号
    watch(
      () => [addRoomForm.value.classroomName, addRoomForm.value.building],
      () => {
        generateClassroomCode()
      },
      { deep: true }
    )

    // 重置添加教室表单
    const resetAddRoomForm = () => {
      addRoomForm.value = {
        classroomName: '',
        roomNumber: '',
        classroomCode: '',
        building: '',
        remarks: '',
        needApproval: 'no'
      }
      // 重置审批级别
      approvalLevels.value = [
        { level: 2, approvers: '' }
      ]
      // 清除表单验证
      if (addRoomFormRef.value) {
        addRoomFormRef.value.clearValidate()
      }
    }

    // 关闭弹出框
    const handleCloseDialog = () => {
      addRoomDialogVisible.value = false
      resetAddRoomForm()
    }

    // 提交添加教室表单
    const handleSubmitRoom = async () => {
      if (!addRoomFormRef.value) return

      try {
        await addRoomFormRef.value.validate()

        // 构造新的房间数据
        const newRoom = {
          id: Date.now(), // 临时ID，实际应该由后端生成
          classroomName: addRoomForm.value.classroomName,
          roomNumber: addRoomForm.value.roomNumber,
          classroomCode: addRoomForm.value.classroomCode,
          building: addRoomForm.value.building,
          remarks: addRoomForm.value.remarks,
          needApproval: addRoomForm.value.needApproval,
          firstApprover: addRoomForm.value.firstApprover,
          secondApprover: addRoomForm.value.secondApprover,
          thirdApprover: addRoomForm.value.thirdApprover
        }

        // 添加到房间列表
        roomData.value.unshift(newRoom)
        total.value += 1

        ElMessage.success('添加教室成功')
        handleCloseDialog()

        // TODO: 这里应该调用后端API保存数据
        // await addRoomAPI(newRoom)

      } catch (error) {
        console.error('表单验证失败:', error)
      }
    }

    const batchSetPermissions = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要设置权限的数据')
        return
      }
      ElMessage.info('批量设置权限功能开发中')
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
      // 这里可以根据选中的区域过滤房间数据
      // 例如：根据区域ID过滤roomData
    }

    // 页面初始化
    onMounted(() => {
      loadBuildingTree()
    })

    return {
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
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange,
      managePersonnel,
      exportData,
      importData,
      deleteSelected,
      viewDetails,
      batchSetPermissions,
      handleBuildingNodeClick,
      loadBuildingTree,
      // 添加教室相关
      addRoomDialogVisible,
      addRoomFormRef,
      addRoomForm,
      addRoomRules,
      showAddRoomDialog,
      handleCloseDialog,
      handleSubmitRoom,
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
      searchPersonnel,
      resetPersonnelSearch,
      handlePersonnelSelectionChange,
      confirmPersonnelSelection,
      handlePersonnelDialogClose,
      handlePersonnelCurrentPageChange,
      handlePersonnelPageSizeChange
    }
  },
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
</style>
