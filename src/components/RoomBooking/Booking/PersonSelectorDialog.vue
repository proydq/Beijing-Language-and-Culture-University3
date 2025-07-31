<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="person-selector-container">
      <!-- 搜索栏 -->
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索姓名、工号或部门"
          clearable
          style="width: 300px; margin-right: 10px;"
          @keyup.enter="searchPersons"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="searchPersons">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>

      <!-- 已选人员展示 -->
      <div v-if="selectedPersons.length > 0" class="selected-section">
        <div class="selected-header">
          <span>已选人员 ({{ selectedPersons.length }})</span>
          <el-button type="text" @click="clearAll">清空</el-button>
        </div>
        <div class="selected-persons">
          <el-tag
            v-for="person in selectedPersons"
            :key="person.id"
            closable
            @close="removePerson(person)"
            class="person-tag"
          >
            {{ person.name }} - {{ person.department }}
          </el-tag>
        </div>
      </div>

      <!-- 人员列表 -->
      <div class="person-list-section">
        <el-table
          :data="personList"
          @selection-change="handleSelectionChange"
          style="width: 100%"
          max-height="400"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="avatar" label="头像" width="80">
            <template #default="{ row }">
              <el-avatar :size="40" :src="row.avatar">
                <span>{{ row.name.charAt(0) }}</span>
              </el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="jobNumber" label="工号" width="120" />
          <el-table-column prop="department" label="部门" />
          <el-table-column prop="position" label="职位" width="120" />
          <el-table-column prop="phone" label="联系电话" width="130" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm">
          确定 ({{ selectedPersons.length }})
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'PersonSelectorDialog',
  components: {
    Search
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '选择人员'
    },
    selectedValues: {
      type: Array,
      default: () => []
    },
    multiple: {
      type: Boolean,
      default: true
    },
    personType: {
      type: String,
      default: 'all', // all, approver, member
      validator: (value) => ['all', 'approver', 'member'].includes(value)
    }
  },
  emits: ['update:visible', 'confirm'],
  setup(props, { emit }) {
    const dialogVisible = computed({
      get: () => props.visible,
      set: (value) => emit('update:visible', value)
    })

    const dialogTitle = computed(() => {
      if (props.personType === 'approver') return '选择审批人'
      if (props.personType === 'member') return '选择参与人'
      return props.title
    })

    // 搜索相关
    const searchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    // 人员列表数据
    const personList = ref([])
    const selectedPersons = ref([])

    // 模拟人员数据
    const mockPersons = ref([
      {
        id: '001',
        name: '张三',
        jobNumber: 'EMP001',
        department: '技术部',
        position: '前端工程师',
        phone: '138****1234',
        avatar: '',
        type: 'member'
      },
      {
        id: '002',
        name: '李四',
        jobNumber: 'EMP002',
        department: '技术部',
        position: '后端工程师',
        phone: '138****5678',
        avatar: '',
        type: 'member'
      },
      {
        id: '003',
        name: '王五',
        jobNumber: 'EMP003',
        department: '产品部',
        position: '产品经理',
        phone: '138****9012',
        avatar: '',
        type: 'member'
      },
      {
        id: '004',
        name: '赵主管',
        jobNumber: 'MGR001',
        department: '管理层',
        position: '技术主管',
        phone: '138****3456',
        avatar: '',
        type: 'approver'
      },
      {
        id: '005',
        name: '钱经理',
        jobNumber: 'MGR002',
        department: '管理层',
        position: '部门经理',
        phone: '138****7890',
        avatar: '',
        type: 'approver'
      },
      {
        id: '006',
        name: '孙校长',
        jobNumber: 'DIR001',
        department: '管理层',
        position: '校长',
        phone: '138****2468',
        avatar: '',
        type: 'approver'
      },
      {
        id: '007',
        name: '周老师',
        jobNumber: 'TEA001',
        department: '教务部',
        position: '教师',
        phone: '138****1357',
        avatar: '',
        type: 'member'
      },
      {
        id: '008',
        name: '吴老师',
        jobNumber: 'TEA002',
        department: '教务部',
        position: '教师',
        phone: '138****9753',
        avatar: '',
        type: 'member'
      }
    ])

    // 加载人员列表
    const loadPersons = async () => {
      try {
        // 根据人员类型筛选
        let filteredPersons = mockPersons.value
        if (props.personType === 'approver') {
          filteredPersons = mockPersons.value.filter(p => p.type === 'approver')
        } else if (props.personType === 'member') {
          filteredPersons = mockPersons.value.filter(p => ['member', 'all'].includes(p.type))
        }

        // 搜索筛选
        if (searchKeyword.value) {
          const keyword = searchKeyword.value.toLowerCase()
          filteredPersons = filteredPersons.filter(person =>
            person.name.toLowerCase().includes(keyword) ||
            person.jobNumber.toLowerCase().includes(keyword) ||
            person.department.toLowerCase().includes(keyword)
          )
        }

        // 分页处理
        total.value = filteredPersons.length
        const start = (currentPage.value - 1) * pageSize.value
        const end = start + pageSize.value
        personList.value = filteredPersons.slice(start, end)

      } catch (error) {
        console.error('加载人员列表失败:', error)
        ElMessage.error('加载人员列表失败')
      }
    }

    // 搜索人员
    const searchPersons = () => {
      currentPage.value = 1
      loadPersons()
    }

    // 重置搜索
    const resetSearch = () => {
      searchKeyword.value = ''
      currentPage.value = 1
      loadPersons()
    }

    // 处理分页变化
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadPersons()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadPersons()
    }

    // 处理选择变化
    const handleSelectionChange = (selection) => {
      if (props.multiple) {
        // 多选模式：合并到已选列表，避免重复
        selection.forEach(person => {
          if (!selectedPersons.value.find(p => p.id === person.id)) {
            selectedPersons.value.push(person)
          }
        })
      } else {
        // 单选模式：替换已选列表
        selectedPersons.value = selection.slice(-1) // 只保留最后一个选择
      }
    }

    // 移除人员
    const removePerson = (person) => {
      const index = selectedPersons.value.findIndex(p => p.id === person.id)
      if (index > -1) {
        selectedPersons.value.splice(index, 1)
      }
    }

    // 清空所有选择
    const clearAll = () => {
      selectedPersons.value = []
    }

    // 关闭弹窗
    const handleClose = () => {
      dialogVisible.value = false
    }

    // 确认选择
    const handleConfirm = () => {
      if (selectedPersons.value.length === 0) {
        ElMessage.warning('请至少选择一个人员')
        return
      }

      emit('confirm', selectedPersons.value)
      dialogVisible.value = false
    }

    // 监听弹窗打开
    watch(() => props.visible, (newVal) => {
      if (newVal) {
        // 根据传入的已选值初始化
        if (props.selectedValues && props.selectedValues.length > 0) {
          selectedPersons.value = mockPersons.value.filter(person =>
            props.selectedValues.some(selected => 
              (typeof selected === 'string' ? selected === person.name : selected.id === person.id)
            )
          )
        } else {
          selectedPersons.value = []
        }
        
        // 重置搜索并加载数据
        searchKeyword.value = ''
        currentPage.value = 1
        loadPersons()
      }
    })

    onMounted(() => {
      loadPersons()
    })

    return {
      dialogVisible,
      dialogTitle,
      searchKeyword,
      currentPage,
      pageSize,
      total,
      personList,
      selectedPersons,
      loadPersons,
      searchPersons,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      handleSelectionChange,
      removePerson,
      clearAll,
      handleClose,
      handleConfirm
    }
  }
}
</script>

<style scoped>
.person-selector-container {
  min-height: 500px;
}

.search-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.selected-section {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 4px;
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  color: #333;
}

.selected-persons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.person-tag {
  margin: 0;
}

.person-list-section {
  flex: 1;
}

.pagination-container {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 表格样式调整 */
:deep(.el-table th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.el-table .cell) {
  padding: 8px;
}

/* 头像样式 */
:deep(.el-avatar) {
  border: 1px solid #e8e8e8;
}
</style>