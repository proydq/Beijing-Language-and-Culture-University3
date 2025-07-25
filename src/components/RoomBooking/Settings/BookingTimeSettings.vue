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
        <el-table-column prop="timeSlot" label="预约时间段" width="200" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="bookingDays" label="预约天数" width="120">
          <template #default="{ row }">
            <span v-if="row.bookingDays === '不限制'" class="no-limit-text">{{
              row.bookingDays
            }}</span>
            <span v-else-if="row.bookingDays === '全天预约'" class="all-day-text">{{
              row.bookingDays
            }}</span>
            <span v-else class="days-text">{{ row.bookingDays }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <span style="display: inline-flex; gap: 8px; align-items: center; white-space: nowrap">
              <el-button type="warning" size="small" @click="managePersonnel(row)"
                >管理人员</el-button
              >
              <el-button type="primary" size="small" @click="editTimeSlot(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteTimeSlot(row)">删除</el-button>
            </span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑时间段对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="设置可预约时间天数"
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
            <span>1/29 条</span>
            <div class="search-box">
              <el-select
                v-model="selectedDepartment"
                placeholder="全部"
                style="width: 120px; margin-right: 10px"
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
              >
                <template #suffix>
                  <el-icon><Search /></el-icon>
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
            <span>1 条</span>
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
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, ArrowRight, ArrowLeft } from '@element-plus/icons-vue'

export default {
  name: 'BookingTimeSettings',
  setup() {
    // 预约时间段数据
    const timeSlotData = ref([
      {
        id: 1,
        sequence: 1,
        timeSlot: '不限制',
        remark:
          '表示预约时间时，可预约的天数限制规则，可预约时间为：3月1日-3月16日，3月16日后不可预约；单独预约的改：3月17日-4月1日，4月2日不可预约；',
        bookingDays: 3000,
      },
      {
        id: 2,
        sequence: 2,
        timeSlot: '10',
        remark: '全天预约时间',
        bookingDays: 20,
      },
      {
        id: 3,
        sequence: 3,
        timeSlot: '30',
        remark: '全天预约时间',
        bookingDays: 10,
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

    const addTimeSlot = () => {
      ElMessage.info('新增时间段功能开发中...')
    }

    const editTimeSlot = (row) => {
      currentEditingRow.value = row
      editForm.value.days = row.bookingDays
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

    const confirmEdit = () => {
      if (!editForm.value.days) {
        ElMessage.warning('请输入预约天数')
        return
      }

      // 更新数据
      if (currentEditingRow.value) {
        const index = timeSlotData.value.findIndex((item) => item.id === currentEditingRow.value.id)
        if (index > -1) {
          timeSlotData.value[index].bookingDays = parseInt(editForm.value.days)
          timeSlotData.value[index].remark = editForm.value.remark
        }
      }

      ElMessage.success('修改成功')
      handleClose()
    }

    const managePersonnel = (row) => {
      personnelDialogVisible.value = true
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
          const index = availablePersonnel.value.findIndex((p) => p.id === person.id)
          if (index > -1) {
            availablePersonnel.value.splice(index, 1)
          }
        }
      })
      selectedAvailable.value = []
      assignedTotal.value = assignedPersonnel.value.length
      availableTotal.value = availablePersonnel.value.length
    }

    const removePersonnel = () => {
      selectedAssigned.value.forEach((person) => {
        const exists = availablePersonnel.value.find((p) => p.id === person.id)
        if (!exists) {
          availablePersonnel.value.push({ ...person })
          const index = assignedPersonnel.value.findIndex((p) => p.id === person.id)
          if (index > -1) {
            assignedPersonnel.value.splice(index, 1)
          }
        }
      })
      selectedAssigned.value = []
      assignedTotal.value = assignedPersonnel.value.length
      availableTotal.value = availablePersonnel.value.length
    }

    const confirmPersonnel = () => {
      ElMessage.success('人员设置成功')
      handlePersonnelClose()
    }

    const deleteTimeSlot = (row) => {
      ElMessageBox.confirm(`确定要删除时间段 "${row.timeSlot}" 吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          const index = timeSlotData.value.findIndex((item) => item.id === row.id)
          if (index > -1) {
            timeSlotData.value.splice(index, 1)
            ElMessage.success('删除成功')
          }
        })
        .catch(() => {
          ElMessage.info('已取消删除')
        })
    }

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
