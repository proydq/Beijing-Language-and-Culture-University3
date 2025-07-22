<template>
  <div class="all-bookings">
    <!-- 搜索过滤器 -->
    <div class="search-filters">
      <div class="filter-row">
        <div class="filter-item">
          <label>预约名称</label>
          <el-input 
            v-model="filters.name" 
            placeholder="请输入预约名称" 
            clearable 
          />
        </div>
        <div class="filter-item">
          <label>申请人</label>
          <el-input 
            v-model="filters.applicant" 
            placeholder="请输入申请人姓名" 
            clearable 
          />
        </div>
        <div class="filter-item">
          <label>审核状态</label>
          <el-select 
            v-model="filters.auditType" 
            placeholder="请选择审核状态" 
            clearable
          >
            <el-option label="待审核" value="待审核" />
            <el-option label="通过" value="通过" />
            <el-option label="拒绝" value="拒绝" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>使用状态</label>
          <el-select 
            v-model="filters.useStatus" 
            placeholder="请选择使用状态" 
            clearable
          >
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
        </div>
        <div class="filter-actions">
          <el-button type="primary" @click="handleSearch">
            <el-icon><search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 借用表格 -->
    <div class="booking-table">
      <el-table :data="filteredData" border>
        <el-table-column prop="bookingName" label="预约名称" min-width="200" />
        <el-table-column prop="applicant" label="申请人" width="120" />
        <el-table-column prop="bookingTime" label="预约时间" min-width="250" />
        <el-table-column prop="roomName" label="房间名称" width="150" />
        <el-table-column prop="auditStatus" label="审核状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="getAuditStatusType(scope.row.auditStatus)" 
              size="small"
            >
              {{ scope.row.auditStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="useStatus" label="使用状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="getUseStatusType(scope.row.useStatus)" 
              size="small"
            >
              {{ scope.row.useStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button 
              type="success" 
              size="small" 
              @click="$emit('approve', scope.row)"
            >
              审核
            </el-button>
            <el-button 
              type="primary" 
              size="small" 
              @click="$emit('edit', scope.row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'AllBookings',
  components: {
    Search
  },
  props: {
    bookingData: {
      type: Array,
      default: () => []
    }
  },
  emits: ['edit', 'approve'],
  setup(props, { emit }) {
    const filters = reactive({
      name: '',
      applicant: '',
      auditType: '',
      useStatus: ''
    })

    const pagination = reactive({
      currentPage: 1,
      pageSize: 10,
      total: 0
    })

    // 过滤数据
    const filteredData = computed(() => {
      let data = props.bookingData

      if (filters.name) {
        data = data.filter(item => 
          item.bookingName.includes(filters.name)
        )
      }

      if (filters.applicant) {
        data = data.filter(item => 
          item.applicant.includes(filters.applicant)
        )
      }

      if (filters.auditType) {
        data = data.filter(item => 
          item.auditStatus === filters.auditType
        )
      }

      if (filters.useStatus) {
        data = data.filter(item => 
          item.useStatus === filters.useStatus
        )
      }

      pagination.total = data.length

      // 分页
      const start = (pagination.currentPage - 1) * pagination.pageSize
      const end = start + pagination.pageSize
      return data.slice(start, end)
    })

    const getAuditStatusType = (status) => {
      const statusMap = {
        '待审核': 'warning',
        '通过': 'success',
        '拒绝': 'danger'
      }
      return statusMap[status] || 'info'
    }

    const getUseStatusType = (status) => {
      const statusMap = {
        '未开始': 'info',
        '进行中': 'warning',
        '已结束': 'success'
      }
      return statusMap[status] || 'info'
    }

    const handleSearch = () => {
      pagination.currentPage = 1
      console.log('搜索:', filters)
    }

    const handleReset = () => {
      Object.keys(filters).forEach(key => {
        filters[key] = ''
      })
      pagination.currentPage = 1
    }

    const handleSizeChange = (val) => {
      pagination.pageSize = val
      pagination.currentPage = 1
    }

    const handleCurrentChange = (val) => {
      pagination.currentPage = val
    }

    return {
      filters,
      pagination,
      filteredData,
      getAuditStatusType,
      getUseStatusType,
      handleSearch,
      handleReset,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.all-bookings {
  flex: 1;
  padding: 20px;
  background: #f9f9f9;
}

.search-filters {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 200px;
}

.filter-item label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.filter-actions {
  display: flex;
  gap: 10px;
}

.booking-table {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.booking-table .el-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.booking-table .el-table th {
  background-color: #fafafa;
  font-weight: 600;
  color: #333;
}

.booking-table .el-table td {
  border-bottom: 1px solid #f0f0f0;
}

.booking-table .el-table tr:hover {
  background-color: #f5f7fa;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>