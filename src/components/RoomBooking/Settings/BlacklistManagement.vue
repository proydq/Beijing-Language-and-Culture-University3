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
        :data="paginatedBlacklistData" 
        style="width: 100%" 
        border
        v-loading="loading"
        :header-cell-style="{ backgroundColor: '#fafafa', fontWeight: '600' }"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="序号" width="80" align="center">
          <template #default="{ $index }">
            {{ (currentPage - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="120" align="center" />
        <el-table-column prop="employeeId" label="工号" width="140" align="center" />
        <el-table-column prop="department" label="所属部门" min-width="150" align="center" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="removeFromBlacklist(row)">
              移除
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Plus, Upload, Search, Refresh } from '@element-plus/icons-vue'
import { searchBlacklist, removeFromBlacklist as removeFromBlacklistAPI, batchRemoveFromBlacklist, addToBlacklist } from '@/api/blacklist'

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
    const totalItems = ref(0)
    const loading = ref(false)

    // 搜索表单
    const searchForm = reactive({
      name: '',
      employeeId: '',
      department: ''
    })

    // 黑名单数据
    const blacklistData = ref([])

    // 过滤后的数据（后端已经处理搜索和分页，直接返回）
    const filteredBlacklistData = computed(() => {
      return blacklistData.value || []
    })

    // 分页数据（后端已经处理分页，直接返回）
    const paginatedBlacklistData = computed(() => {
      return blacklistData.value || []
    })

    // 加载黑名单数据
    const loadBlacklistData = async () => {
      try {
        loading.value = true
        
        const condition = {
          pageNumber: currentPage.value,
          pageSize: pageSize.value,
          name: searchForm.name || null,
          employeeId: searchForm.employeeId || null,
          department: searchForm.department || null
        }

        const response = await searchBlacklist(condition)
        
        if (response && response.code === 200) {
          blacklistData.value = response.data?.rows || []
          totalItems.value = response.data?.total || 0
        } else {
          console.warn('获取黑名单数据响应异常:', response)
          ElMessage.error((response && response.message) || '获取黑名单数据失败')
        }
      } catch (error) {
        console.error('获取黑名单数据失败:', error)
        ElMessage.error('获取黑名单数据失败')
        blacklistData.value = []
        totalItems.value = 0
      } finally {
        loading.value = false
      }
    }

    const addToBlacklist = () => {
      ElMessage.info('添加黑名单功能开发中...')
    }

    const exportBlacklist = () => {
      ElMessage.success('正在导出黑名单列表...')
    }

    const handleSearch = () => {
      currentPage.value = 1
      loadBlacklistData()
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        name: '',
        employeeId: '',
        department: ''
      })
      currentPage.value = 1
      loadBlacklistData()
    }

    const removeFromBlacklistAction = async (row) => {
      try {
        await ElMessageBox.confirm(`确认将 "${row.name}" 移出黑名单吗？`, '移除确认')
        
        const loadingInstance = ElLoading.service({ text: '移除中...' })
        
        const response = await removeFromBlacklistAPI(row.id)
        
        if (response && response.code === 200) {
          ElMessage.success(`已将 ${row.name} 移出黑名单`)
          // 刷新数据
          await loadBlacklistData()
        } else {
          ElMessage.error((response && response.message) || '移除失败')
        }
        
        loadingInstance.close()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('移除黑名单失败:', error)
          ElMessage.error('移除失败')
        }
      }
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadBlacklistData()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadBlacklistData()
    }

    // 页面初始化
    onMounted(() => {
      loadBlacklistData()
    })

    return {
      currentPage,
      pageSize,
      totalItems,
      loading,
      searchForm,
      blacklistData,
      filteredBlacklistData,
      paginatedBlacklistData,
      loadBlacklistData,
      addToBlacklist,
      exportBlacklist,
      handleSearch,
      handleReset,
      removeFromBlacklist: removeFromBlacklistAction,
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