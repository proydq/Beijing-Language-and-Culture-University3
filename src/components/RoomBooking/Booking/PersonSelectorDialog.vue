<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="800px"
    :close-on-click-modal="false"
    @close="handleClose"
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
            @keyup.enter="searchPersonnelData"
          />
        </el-form-item>
        <el-form-item label="部门:">
          <el-input
            v-model="personnelSearchForm.department"
            placeholder="请输入部门"
            clearable
            style="width: 200px"
            @keyup.enter="searchPersonnelData"
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
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="confirmPersonnelSelection">确认选择</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { searchPersonnel } from '@/api/schemeManagement'

export default {
  name: 'PersonSelectorDialog',
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

    // 人员选择弹窗相关
    const personnelTableRef = ref(null)
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

      emit('confirm', selectedPersonnel.value)
      dialogVisible.value = false
      selectedPersonnel.value = []
    }

    // 关闭人员选择弹出框
    const handleClose = () => {
      dialogVisible.value = false
      selectedPersonnel.value = []
    }

    // 处理人员分页大小变化
    const handlePersonnelPageSizeChange = (size) => {
      personnelPagination.value.pageSize = size
      personnelPagination.value.currentPage = 1
      searchPersonnelData()
    }

    // 监听弹窗打开
    watch(() => props.visible, (newVal) => {
      if (newVal) {
        // 根据传入的已选值初始化
        if (props.selectedValues && props.selectedValues.length > 0) {
          selectedPersonnel.value = [...props.selectedValues]
        } else {
          selectedPersonnel.value = []
        }
        
        // 重置搜索并加载数据
        personnelSearchForm.value = {
          name: '',
          department: ''
        }
        personnelPagination.value.currentPage = 1
        searchPersonnelData()
      }
    })

    onMounted(() => {
      searchPersonnelData()
    })

    return {
      dialogVisible,
      dialogTitle,
      personnelTableRef,
      selectedPersonnel,
      personnelSearchForm,
      personnelPagination,
      personnelList,
      searchPersonnelData,
      resetPersonnelSearch,
      handlePersonnelSelectionChange,
      confirmPersonnelSelection,
      handleClose,
      handlePersonnelCurrentPageChange,
      handlePersonnelPageSizeChange
    }
  }
}
</script>

<style scoped>
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
</style>