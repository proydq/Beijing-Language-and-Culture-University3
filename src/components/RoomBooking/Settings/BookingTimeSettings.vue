<template>
  <div class="setting-section">
    <!-- 提示信息 -->
    <div class="tips-section">
      <p class="tips-text">
        <span class="tips-label">PS：</span>
        <span class="tips-content"> 表示预约时间时，可预约的天数限制规则； </span>
      </p>
      <p class="example-text">
        例如：设置可预约时间（16）天，可预约时间为：3月1日-3月16日，3月16日后不可预约；单独预约的改：3月17日-4月1日，4月2日不可预约；
      </p>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="addTimeSlot"> 新增 </el-button>
    </div>

    <!-- 预约时间表格 -->
    <div class="time-table">
      <el-table :data="timeSlotData" style="width: 100%" border>
        <el-table-column prop="sequence" label="序号" width="80" />
        <el-table-column prop="advanceDays" label="预约时间（天）" width="150" >
          <template #default="{ row }">
            <span v-if="row.advanceDays === 0 || row.advanceDays === '不限制'">不限制</span>
            <span v-else>{{ row.advanceDays }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="bindingUserCount" label="绑定人数" width="120">
          <template #default="{ row }">
            <span>{{ row.bindingUserCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <span style="display: inline-flex; gap: 8px; align-items: center; white-space: nowrap">
              <el-button type="warning" size="small" @click="managePersonnel(row)"
                >管理人员</el-button
              >
              <el-button v-if="row.id !== 'default'" type="primary" size="small" @click="editTimeSlot(row)">编辑</el-button>
              <el-button v-if="row.id !== 'default'" type="danger" size="small" @click="deleteTimeSlot(row)">删除</el-button>
            </span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑时间段对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="currentEditingRow ? '编辑预约时间天数' : '新增预约时间天数'"
      width="500px"
      :before-close="handleClose"
    >
      <div class="edit-form">
        <div class="form-item">
          <label class="form-label">预约时间:</label>
          <div class="input-group">
            <el-input
              v-model="editForm.days"
              type="number"
              placeholder="请输入天数"
              style="width: 100px"
            />
            <span class="unit-text">天</span>
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">备注信息:</label>
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注信息"
            style="width: 100%"
          />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="confirmEdit">确认</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 管理人员对话框 -->
    <el-dialog
      v-model="personnelDialogVisible"
      title="选择人员"
      width="800px"
      :before-close="handlePersonnelClose"
    >
      <div class="personnel-tips">
        <span class="tips-label">PS：</span>
        <span class="tips-content">请从人员表中选择，仅支持单选预约时间段的预约人员</span>
      </div>

      <div class="personnel-container">
        <!-- 左侧可选人员 -->
        <div class="personnel-section">
          <div class="section-header">
            <span>{{ availablePersonnel.length }}/{{ availableTotal }} 条</span>
            <div class="search-box">
              <el-select
                v-model="selectedDepartment"
                placeholder="全部"
                style="width: 120px; margin-right: 10px"
                @change="handleDepartmentChange"
              >
                <el-option label="全部" value="all"></el-option>
                <el-option label="部门1" value="dept1"></el-option>
                <el-option label="部门2" value="dept2"></el-option>
              </el-select>
              <el-input
                v-model="searchKeyword"
                placeholder="请输入姓名/工号"
                style="width: 200px"
                clearable
                @keyup.enter="handleSearch"
                @clear="handleSearch"
              >
                <template #suffix>
                  <el-icon @click="handleSearch" style="cursor: pointer"><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </div>

          <el-table
            :data="availablePersonnel"
            height="300"
            @selection-change="handleAvailableSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="workId" label="工号" width="100" />
            <el-table-column prop="name" label="姓名" />
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="availablePage"
              :page-size="10"
              :total="availableTotal"
              layout="prev, pager, next"
              small
              @current-change="handleAvailablePageChange"
            />
          </div>
        </div>

        <!-- 中间操作按钮 -->
        <div class="operation-buttons">
          <el-button
            type="primary"
            :icon="ArrowRight"
            @click="addPersonnel"
            :disabled="selectedAvailable.length === 0"
          ></el-button>
          <el-button
            type="primary"
            :icon="ArrowLeft"
            @click="removePersonnel"
            :disabled="selectedAssigned.length === 0"
          ></el-button>
        </div>

        <!-- 右侧已选人员 -->
        <div class="personnel-section">
          <div class="section-header">
            <span>{{ assignedPersonnel.length }} 条</span>
            <div class="search-box">
              <el-input
                v-model="assignedSearchKeyword"
                placeholder="请输入姓名/工号"
                style="width: 200px"
                clearable
              >
                <template #suffix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </div>

          <el-table
            :data="assignedPersonnel"
            height="300"
            @selection-change="handleAssignedSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="workId" label="工号" width="100" />
            <el-table-column prop="name" label="姓名" />
          </el-table>

          <div class="pagination">
            <el-pagination
              v-model:current-page="assignedPage"
              :page-size="10"
              :total="assignedTotal"
              layout="prev, pager, next"
              small
              @current-change="handleAssignedPageChange"
            />
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handlePersonnelClose">取消</el-button>
          <el-button type="primary" @click="confirmPersonnel">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowRight, ArrowLeft } from '@element-plus/icons-vue'
// 导入预约相关API
import {
  getUserBookingLimit,
  setUserBookingLimit,
  batchSetUserBookingLimits,
  deleteUserBookingLimit,
  getAllUserBookingLimits,
  getBookingTimeRuleList,
  getBookingTimeRuleById,
  createBookingTimeRule,
  updateBookingTimeRule,
  deleteBookingTimeRule,
  getBookingTimeRulesByUserId,
  checkBookingTime,
  getAvailableBookingTimes
} from '@/api/booking'
// 导入用户相关API
import { userAPI } from '@/api/user'

export default {
  name: 'BookingTimeSettings',
  setup() {
    // 预约时间段数据
    const timeSlotData = ref([
      {
        id: 'default',
        sequence: 1,
        advanceDays: '不限制',
        remark: '当前时间为默认时间，所有可预约人均默认在此时间周期内，如需要更改请新建提前时间，进行授权',
        bindingUserCount: '未更改的用户数',
      },
    ])

    // 编辑对话框相关
    const editDialogVisible = ref(false)
    const editForm = ref({
      days: '',
      remark: '',
    })
    const currentEditingRow = ref(null)

    // 管理人员对话框相关
    const personnelDialogVisible = ref(false)
    const selectedDepartment = ref('all')
    const searchKeyword = ref('')
    const assignedSearchKeyword = ref('')
    const availablePage = ref(1)
    const assignedPage = ref(1)
    const availableTotal = ref(29)
    const assignedTotal = ref(1)
    const selectedAvailable = ref([])
    const selectedAssigned = ref([])

    // 可选人员数据
    const availablePersonnel = ref([
      { id: 1, workId: 'A219213', name: '张三' },
      { id: 2, workId: 'A219214', name: '李四' },
      { id: 3, workId: 'A219215', name: '王五' },
      { id: 4, workId: 'A219216', name: '赵六' },
      { id: 5, workId: 'A219217', name: '孙七' },
      { id: 6, workId: 'A219218', name: '周八' },
      { id: 7, workId: 'A219219', name: '吴九' },
      { id: 8, workId: 'A219220', name: '郑十' },
      { id: 9, workId: 'A219221', name: '钱十一' },
      { id: 10, workId: 'A219222', name: '孙十二' },
    ])

    // 已选人员数据
    const assignedPersonnel = ref([{ id: 11, workId: 'A219214', name: '李四' }])

    // 加载预约时间规则数据
    const loadTimeRules = async () => {
      try {
        const params = {
          pageNum: 1,
          pageSize: 100
        }
        const response = await getBookingTimeRuleList(params)

        if (response.data && response.data.rows) {
          // 保留默认数据，将从后端获取的数据追加到默认数据之后
          const defaultData = timeSlotData.value.find(item => item.id === 'default')
          const backendData = response.data.rows.map((rule, index) => ({
            id: rule.id,
            sequence: index + 2, // 从2开始，因为默认数据占用序号1
            advanceDays: rule.advanceBookingDays || '不限制',
            remark: rule.ruleName || '',
            bindingUserCount: rule.bindingUserCount || 0
          }))

          timeSlotData.value = defaultData ? [defaultData, ...backendData] : backendData
        }
      } catch (error) {
        console.error('加载预约时间规则失败:', error)
        ElMessage.error('加载预约时间规则失败: ' + (error.message || '未知错误'))
      }
    }

    // 加载用户预约限制数据
    const loadUserBookingLimits = async () => {
      try {
        const response = await getAllUserBookingLimits()
        if (response.data) {
          console.log('用户预约限制数据:', response.data)
        }
      } catch (error) {
        console.error('加载用户预约限制失败:', error)
        ElMessage.error('加载用户预约限制失败: ' + (error.message || '未知错误'))
      }
    }

    // 加载可选人员列表
    const loadAvailablePersonnel = async () => {
      try {
        const searchCondition = {
          pageNumber: availablePage.value,
          pageSize: 10,
          name: searchKeyword.value,
          departmentId: selectedDepartment.value === 'all' ? '' : selectedDepartment.value
        }

        const response = await userAPI.searchUsers(searchCondition)
        if (response.data && response.data.rows) {
          // 过滤掉已分配的人员
          const assignedIds = assignedPersonnel.value.map(p => p.id)
          availablePersonnel.value = response.data.rows
            .filter(user => !assignedIds.includes(user.id))
            .map(user => ({
              id: user.id,
              workId: user.jobNumber || user.username,
              name: user.realName || user.username
            }))
          availableTotal.value = response.data.total || 0
        }
      } catch (error) {
        console.error('加载可选人员失败:', error)
        ElMessage.error('加载可选人员失败: ' + (error.message || '未知错误'))
      }
    }

    // 加载当前时间段对应的人员
    const loadAssignedPersonnel = async (row) => {
      try {
        console.log('加载已分配人员，当前行数据:', row)

        // 获取所有用户预约限制
        const response = await getAllUserBookingLimits()
        console.log('获取到的用户预约限制数据:', response.data)

        if (response.data) {
          // 筛选出与当前时间段匹配的用户
          const matchedLimits = response.data.filter(limit => {
            if (row.id === 'default') {
              // 默认数据：显示没有特定限制的用户或限制值为0的用户
              return !limit.limitValue || limit.limitValue === 0 || limit.limitValue === '0'
            } else {
              // 具体时间段：显示有对应限制的用户
              const rowAdvanceDays = typeof row.advanceDays === 'string' && row.advanceDays === '不限制' ? 0 : parseInt(row.advanceDays)
              const limitValue = parseInt(limit.limitValue)
              return limitValue === rowAdvanceDays && limit.limitType === 'ADVANCE_BOOKING'
            }
          })

          console.log('筛选后的匹配限制:', matchedLimits)

          // 根据用户ID获取用户详细信息
          const assignedUsers = []
          for (const limit of matchedLimits) {
            try {
              const userResponse = await userAPI.getUserById(limit.userId)
              if (userResponse.data) {
                assignedUsers.push({
                  id: userResponse.data.id,
                  workId: userResponse.data.jobNumber || userResponse.data.username,
                  name: userResponse.data.name || userResponse.data.username
                })
              }
            } catch (userError) {
              console.warn('获取用户信息失败:', limit.userId, userError)
            }
          }

          console.log('最终的已分配用户:', assignedUsers)
          assignedPersonnel.value = assignedUsers
          assignedTotal.value = assignedUsers.length
        } else {
          // 如果没有数据，清空已分配人员
          assignedPersonnel.value = []
          assignedTotal.value = 0
        }
      } catch (error) {
        console.error('加载已分配人员失败:', error)
        ElMessage.error('加载已分配人员失败: ' + (error.message || '未知错误'))
        // 出错时也要清空数据
        assignedPersonnel.value = []
        assignedTotal.value = 0
      }
    }

    // 通过接口加载当前时间段对应的人员
    const loadAssignedPersonnelFromAPI = async (row) => {
      try {
        console.log('通过接口加载已分配人员，当前行数据:', row)

        // 如果是默认数据，清空已分配人员
        if (row.id === 'default') {
          assignedPersonnel.value = []
          assignedTotal.value = 0
          return
        }

        // 调用根据ID获取预约时间规则详情接口
        const response = await getBookingTimeRuleById(row.id)
        console.log('获取到的预约时间规则详情:', response.data)

        if (response.data && response.data.applicableUsers) {
          // 转换用户数据格式
          const assignedUsers = response.data.applicableUsers.map(user => ({
            id: user.userId,
            workId: user.jobNumber || user.userId,
            name: user.userName
          }))

          console.log('转换后的已分配用户:', assignedUsers)
          assignedPersonnel.value = assignedUsers
          assignedTotal.value = assignedUsers.length
        } else {
          // 如果没有数据，清空已分配人员
          assignedPersonnel.value = []
          assignedTotal.value = 0
        }
      } catch (error) {
        console.error('通过接口加载已分配人员失败:', error)
        ElMessage.error('加载已分配人员失败: ' + (error.message || '未知错误'))
        // 出错时也要清空数据
        assignedPersonnel.value = []
        assignedTotal.value = 0
      }
    }

    const addTimeSlot = () => {
      // 重置表单
      editForm.value = {
        days: '',
        remark: ''
      }
      currentEditingRow.value = null
      editDialogVisible.value = true
    }

    const editTimeSlot = (row) => {
      currentEditingRow.value = row
      editForm.value.days = row.advanceDays === '不限制' ? '' : row.advanceDays
      editForm.value.remark = row.remark
      editDialogVisible.value = true
    }

    const handleClose = () => {
      editDialogVisible.value = false
      editForm.value = {
        days: '',
        remark: '',
      }
      currentEditingRow.value = null
    }

    const confirmEdit = async () => {
      if (!editForm.value.days) {
        ElMessage.warning('请输入预约天数')
        return
      }

      try {
        if (currentEditingRow.value) {
          // 编辑模式：更新预约时间规则
          const updateData = {
            advanceBookingDays: parseInt(editForm.value.days),
            ruleName: editForm.value.remark,
            isActive: true
          }

          await updateBookingTimeRule(currentEditingRow.value.id, updateData)

          // 更新本地数据
          const index = timeSlotData.value.findIndex((item) => item.id === currentEditingRow.value.id)
          if (index > -1) {
            timeSlotData.value[index].advanceDays = parseInt(editForm.value.days)
            timeSlotData.value[index].remark = editForm.value.remark
          }

          ElMessage.success('修改成功')
        } else {
          // 新增模式：创建新的预约时间规则
          const newRule = {
            ruleName: editForm.value.remark || `预约时间规则-${editForm.value.days}天`,
            ruleType: 'ADVANCE_BOOKING',
            advanceBookingDays: parseInt(editForm.value.days),
            isActive: true
          }

          await createBookingTimeRule(newRule)
          ElMessage.success('新增时间段成功')

          // 重新加载数据
          await loadTimeRules()
        }

        handleClose()
      } catch (error) {
        console.error(currentEditingRow.value ? '修改失败:' : '新增失败:', error)
        ElMessage.error((currentEditingRow.value ? '修改失败: ' : '新增失败: ') + (error.message || '未知错误'))
      }
    }

    const managePersonnel = async (row) => {
      currentEditingRow.value = row
      personnelDialogVisible.value = true

      // 加载可选人员列表
      await loadAvailablePersonnel()

      // 通过接口获取当前时间段对应的人员
      await loadAssignedPersonnelFromAPI(row)
    }

    const handlePersonnelClose = () => {
      personnelDialogVisible.value = false
      selectedAvailable.value = []
      selectedAssigned.value = []
    }

    const handleAvailableSelectionChange = (selection) => {
      selectedAvailable.value = selection
    }

    const handleAssignedSelectionChange = (selection) => {
      selectedAssigned.value = selection
    }

    const addPersonnel = () => {
      selectedAvailable.value.forEach((person) => {
        const exists = assignedPersonnel.value.find((p) => p.id === person.id)
        if (!exists) {
          assignedPersonnel.value.push({ ...person })
        }
      })
      selectedAvailable.value = []
      assignedTotal.value = assignedPersonnel.value.length
      // 重新加载可选人员列表以更新过滤
      loadAvailablePersonnel()
    }

    const removePersonnel = () => {
      selectedAssigned.value.forEach((person) => {
        const index = assignedPersonnel.value.findIndex((p) => p.id === person.id)
        if (index > -1) {
          assignedPersonnel.value.splice(index, 1)
        }
      })
      selectedAssigned.value = []
      assignedTotal.value = assignedPersonnel.value.length
      // 重新加载可选人员列表以更新过滤
      loadAvailablePersonnel()
    }

    const confirmPersonnel = async () => {
      try {
        // 如果是默认数据，不允许设置人员
        if (currentEditingRow.value.id === 'default') {
          ElMessage.warning('默认时间段不支持设置人员')
          return
        }

        console.log('设置人员，当前行:', currentEditingRow.value)
        console.log('已分配人员:', assignedPersonnel.value)

        // 准备更新数据
        const updateData = {
          ruleName: currentEditingRow.value.remark || currentEditingRow.value.ruleName,
          advanceBookingDays: currentEditingRow.value.advanceDays,
          applicableUserIds: assignedPersonnel.value.map(person => person.id),
          isActive: true
        }

        console.log('准备更新的规则数据:', updateData)

        // 调用编辑预约时间规则接口
        await updateBookingTimeRule(currentEditingRow.value.id, updateData)

        // 更新本地数据中的绑定人数
        const index = timeSlotData.value.findIndex(item => item.id === currentEditingRow.value.id)
        if (index > -1) {
          timeSlotData.value[index].bindingUserCount = assignedPersonnel.value.length
        }

        ElMessage.success('人员设置成功')
        handlePersonnelClose()
      } catch (error) {
        console.error('人员设置失败:', error)
        ElMessage.error('人员设置失败: ' + (error.message || '未知错误'))
      }
    }

    const deleteTimeSlot = (row) => {
      ElMessageBox.confirm(`确定要删除预约时间 "${row.advanceDays === 0 ? '不限制' : row.advanceDays + '天'}" 吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(async () => {
          try {
            // 调用删除API
            await deleteBookingTimeRule(row.id)

            // 更新本地数据
            const index = timeSlotData.value.findIndex((item) => item.id === row.id)
            if (index > -1) {
              timeSlotData.value.splice(index, 1)
              ElMessage.success('删除成功')
            }
          } catch (error) {
            console.error('删除失败:', error)
            ElMessage.error('删除失败: ' + (error.message || '未知错误'))
          }
        })
        .catch(() => {
          ElMessage.info('已取消删除')
        })
    }

    // 搜索功能
    const handleSearch = () => {
      availablePage.value = 1
      loadAvailablePersonnel()
    }

    // 分页变化处理
    const handleAvailablePageChange = (page) => {
      availablePage.value = page
      loadAvailablePersonnel()
    }

    const handleAssignedPageChange = (page) => {
      assignedPage.value = page
      // 已分配人员暂时不需要分页，因为数据量较小
    }

    // 部门选择变化处理
    const handleDepartmentChange = () => {
      availablePage.value = 1
      loadAvailablePersonnel()
    }

    // 组件挂载时加载数据
    onMounted(() => {
      loadTimeRules()
      loadUserBookingLimits()
    })

    return {
      timeSlotData,
      addTimeSlot,
      editTimeSlot,
      deleteTimeSlot,
      editDialogVisible,
      editForm,
      currentEditingRow,
      handleClose,
      confirmEdit,
      managePersonnel,
      personnelDialogVisible,
      selectedDepartment,
      searchKeyword,
      assignedSearchKeyword,
      availablePage,
      assignedPage,
      availableTotal,
      assignedTotal,
      selectedAvailable,
      selectedAssigned,
      availablePersonnel,
      assignedPersonnel,
      handlePersonnelClose,
      handleAvailableSelectionChange,
      handleAssignedSelectionChange,
      addPersonnel,
      removePersonnel,
      confirmPersonnel,
      handleSearch,
      handleAvailablePageChange,
      handleAssignedPageChange,
      handleDepartmentChange,
      Search,
      ArrowRight,
      ArrowLeft,
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

/* 提示信息 */
.tips-section {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border-left: 4px solid #4a90e2;
}

.tips-text {
  margin: 0 0 8px 0;
  font-size: 14px;
}

.tips-label {
  color: #e74c3c;
  font-weight: 600;
}

.tips-content {
  color: #333;
}

.example-text {
  margin: 0;
  color: #e74c3c;
  font-size: 13px;
  line-height: 1.5;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 16px;
}

/* 表格样式 */
.time-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.no-limit-text {
  color: #27ae60;
}

.all-day-text {
  color: #f39c12;
}

.days-text {
  color: #333;
}

/* 编辑对话框样式 */
.edit-form {
  padding: 20px 0;
}

.form-item {
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.form-label {
  min-width: 80px;
  font-size: 14px;
  color: #333;
  line-height: 32px;
  font-weight: 500;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.unit-text {
  font-size: 14px;
  color: #666;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 管理人员对话框样式 */
.personnel-tips {
  background: #f8f9fa;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  border-left: 4px solid #4a90e2;
  font-size: 14px;
}

.personnel-container {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.personnel-section {
  flex: 1;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.section-header {
  background: #f5f7fa;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.search-box {
  display: flex;
  align-items: center;
}

.operation-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-self: center;
  margin: 0 10px;
}

.operation-buttons .el-button {
  width: 40px;
  height: 40px;
  padding: 0;
  border-radius: 50%;
}

.pagination {
  padding: 12px 16px;
  border-top: 1px solid #e4e7ed;
  background: #fafafa;
  display: flex;
  justify-content: center;
}
.el-button + .el-button {
  margin-left: 0px;
}
</style>
