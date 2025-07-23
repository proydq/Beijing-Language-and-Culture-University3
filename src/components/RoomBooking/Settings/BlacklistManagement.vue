<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>黑名单管理</h2>
        <p>管理被禁止预约的用户</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="addToBlacklist">
          <el-icon><plus /></el-icon>
          添加黑名单
        </el-button>
        <el-button @click="exportBlacklist">
          <el-icon><upload /></el-icon>
          导出列表
        </el-button>
      </div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" class="search-form">
        <el-form-item label="姓名：">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入姓名"
            style="width: 200px;"
            clearable
          />
        </el-form-item>
        <el-form-item label="工号：">
          <el-input
            v-model="searchForm.employeeId"
            placeholder="请输入工号"
            style="width: 200px;"
            clearable
          />
        </el-form-item>
        <el-form-item label="所属部门：">
          <el-select
            v-model="searchForm.department"
            placeholder="请选择部门"
            style="width: 200px;"
            clearable
          >
            <el-option label="全部部门" value="" />
            <el-option label="教务处" value="教务处" />
            <el-option label="学工处" value="学工处" />
            <el-option label="实验中心" value="实验中心" />
            <el-option label="图书馆" value="图书馆" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-area">
      <el-table 
        :data="filteredBlacklistData" 
        style="width: 100%" 
        border
        stripe
        :header-cell-style="{ backgroundColor: '#fafafa', fontWeight: '600' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="姓名" width="120" align="center" />
        <el-table-column prop="employeeId" label="工号" width="140" align="center" />
        <el-table-column prop="department" label="所属部门" min-width="150" align="center" />
        <el-table-column prop="violationReason" label="违规原因" min-width="200" />
        <el-table-column prop="violationCount" label="违规次数" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getViolationCountType(row.violationCount)" size="small">
              {{ row.violationCount }}次
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="addTime" label="加入时间" width="180" align="center" />
        <el-table-column prop="expireTime" label="解除时间" width="180" align="center">
          <template #default="{ row }">
            <span :class="{ 'permanent-ban': row.expireTime === '永久' }">
              {{ row.expireTime }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="removeFromBlacklist(row)">
              解除
            </el-button>
            <el-button type="danger" size="small" @click="deleteBlacklistItem(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="totalItems"
        layout="prev, pager, next, jumper, total, sizes"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
      <div class="pagination-info">
        共 {{ totalItems }} 条
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Search, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'BlacklistManagement',
  components: {
    Plus,
    Upload,
    Search,
    Refresh
  },
  setup() {
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalItems = ref(15)

    // 搜索表单
    const searchForm = reactive({
      name: '',
      employeeId: '',
      department: ''
    })

    // 黑名单数据
    const blacklistData = ref([
      {
        id: 1,
        name: '张三',
        employeeId: 'T001',
        department: '教务处',
        violationReason: '连续3次无故缺席预约，影响教室正常使用',
        violationCount: 3,
        addTime: '2024-03-15 14:30:00',
        expireTime: '2024-04-15 14:30:00'
      },
      {
        id: 2,
        name: '李四',
        employeeId: 'S002',
        department: '学工处',
        violationReason: '恶意占用教室资源，不按时归还',
        violationCount: 5,
        addTime: '2024-03-10 09:20:00',
        expireTime: '永久'
      },
      {
        id: 3,
        name: '王五',
        employeeId: 'L003',
        department: '图书馆',
        violationReason: '预约后经常迟到，影响其他用户使用',
        violationCount: 2,
        addTime: '2024-03-20 16:45:00',
        expireTime: '2024-05-20 16:45:00'
      },
      {
        id: 4,
        name: '赵六',
        employeeId: 'E004',
        department: '实验中心',
        violationReason: '多次违反教室使用规定',
        violationCount: 4,
        addTime: '2024-03-18 11:30:00',
        expireTime: '2024-06-18 11:30:00'
      },
      {
        id: 5,
        name: '孙七',
        employeeId: 'T005',
        department: '教务处',
        violationReason: '重复预约多个教室但只使用其中一个',
        violationCount: 1,
        addTime: '2024-03-25 10:15:00',
        expireTime: '2024-04-25 10:15:00'
      }
    ])

    // 过滤后的数据
    const filteredBlacklistData = computed(() => {
      let filtered = blacklistData.value

      if (searchForm.name) {
        filtered = filtered.filter(item => 
          item.name.includes(searchForm.name)
        )
      }

      if (searchForm.employeeId) {
        filtered = filtered.filter(item => 
          item.employeeId.includes(searchForm.employeeId)
        )
      }

      if (searchForm.department) {
        filtered = filtered.filter(item => 
          item.department === searchForm.department
        )
      }

      totalItems.value = filtered.length
      return filtered
    })

    // 获取违规次数标签类型
    const getViolationCountType = (count) => {
      if (count >= 5) return 'danger'
      if (count >= 3) return 'warning'
      return 'info'
    }

    const addToBlacklist = () => {
      ElMessage.info('添加黑名单功能开发中...')
    }

    const exportBlacklist = () => {
      ElMessage.success('正在导出黑名单列表...')
    }

    const handleSearch = () => {
      currentPage.value = 1
      ElMessage.success('搜索完成')
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        name: '',
        employeeId: '',
        department: ''
      })
      currentPage.value = 1
      ElMessage.info('搜索条件已重置')
    }

    const removeFromBlacklist = async (row) => {
      try {
        await ElMessageBox.confirm(`确认将 "${row.name}" 移出黑名单吗？`, '解除确认')
        ElMessage.success(`已将 ${row.name} 移出黑名单`)
      } catch {
        // 用户取消
      }
    }

    const deleteBlacklistItem = async (row) => {
      try {
        await ElMessageBox.confirm(`确认删除 "${row.name}" 的黑名单记录吗？此操作不可恢复！`, '删除确认')
        ElMessage.success(`已删除 ${row.name} 的黑名单记录`)
      } catch {
        // 用户取消
      }
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
    }

    return {
      currentPage,
      pageSize,
      totalItems,
      searchForm,
      blacklistData,
      filteredBlacklistData,
      getViolationCountType,
      addToBlacklist,
      exportBlacklist,
      handleSearch,
      handleReset,
      removeFromBlacklist,
      deleteBlacklistItem,
      handleSizeChange,
      handleCurrentChange
    }
  }
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

.section-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 搜索区域 */
.search-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.search-form {
  width: 100%;
}

/* 表格区域 - 解决右侧空白问题 */
.table-area {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
  margin-bottom: 20px;
}

/* 表格样式优化 */
:deep(.el-table) {
  width: 100% !important;
  table-layout: auto !important;
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

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  width: 100%;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

/* 特殊样式 */
.permanent-ban {
  color: #e74c3c;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-area {
    overflow-x: auto;
  }
  
  .table-area {
    overflow-x: auto;
  }
}
</style>