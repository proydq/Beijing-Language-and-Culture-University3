<template>
  <div class="recycle-bin-container">
    <!-- 顶部搜索和操作区域 -->
    <div class="top-section">
      <div class="search-section">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入关键词搜索"
          class="search-input"
          clearable
        >
          <template #prefix>
            <el-icon><search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="action-section">
        <el-input
          v-model="teacherName"
          placeholder="请输入教室名称或房间号"
          class="teacher-input"
          clearable
        />
        <el-button type="primary" class="confirm-btn">确定恢复位置</el-button>
      </div>
    </div>

    <!-- 左侧导航 -->
     <div class="main-content">
       <div class="sidebar">
         <el-tree
           :data="treeData"
           :props="treeProps"
           node-key="id"
           :default-expanded-keys="['all']"
           :highlight-current="true"
           :default-current-key="'all'"
           @node-click="handleNodeClick"
           class="nav-tree"
         >
           <template #default="{ node, data }">
             <span class="tree-node">
               <span>{{ data.label }}</span>
             </span>
           </template>
         </el-tree>
       </div>

      <!-- 主要内容区域 -->
      <div class="content-area">
        <el-table :data="recycleData" style="width: 100%" class="recycle-table">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="roomName" label="教室" width="150" />
          <el-table-column prop="roomNumber" label="房间号" width="120" />
          <el-table-column prop="floor" label="所属楼层" width="120" />
          <el-table-column label="是否支持自动审核" width="150">
            <template #default="{ row }">
              <el-switch v-model="row.autoApproval" disabled />
            </template>
          </el-table-column>
          <el-table-column label="是否需要审核" width="150">
            <template #default="{ row }">
              <el-switch v-model="row.needApproval" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="description" label="备注" min-width="200">
            <template #default="{ row }">
              <div class="description-content">
                <div class="desc-line">第一负责人：{{ row.primaryContact }}</div>
                <div class="desc-line">第二负责人：{{ row.secondaryContact }}</div>
                <div class="desc-line">第三负责人：{{ row.thirdContact }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="restoreItem(row)">恢复设置</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="prev, pager, next, sizes, ->, total"
            class="pagination"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'RecycleBin',
  components: {
    Search
  },
  setup() {
    // 搜索关键词
    const searchKeyword = ref('')
    const teacherName = ref('')
    
    // 分页数据
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(201)

    // 树形菜单数据
    const treeData = ref([
      {
        id: 'all',
        label: '全部',
        children: [
          {
            id: 'dacai',
            label: '达才楼',
            children: [
              { id: 'dacai-b2', label: 'B2' },
              { id: 'dacai-b1', label: 'B1' },
              { id: 'dacai-1f', label: '1F' },
              { id: 'dacai-2f', label: '2F' },
              { id: 'dacai-3f', label: '3F' },
              { id: 'dacai-4f', label: '4F' },
              { id: 'dacai-5f', label: '5F' },
              { id: 'dacai-6f', label: '6F' }
            ]
          },
          {
            id: 'chengde',
            label: '成德楼',
            children: [
              { id: 'chengde-b2', label: 'B2' },
              { id: 'chengde-b1', label: 'B1' },
              { id: 'chengde-1f', label: '1F' },
              { id: 'chengde-2f', label: '2F' },
              { id: 'chengde-3f', label: '3F' },
              { id: 'chengde-4f', label: '4F' },
              { id: 'chengde-5f', label: '5F' },
              { id: 'chengde-6f', label: '6F' }
            ]
          }
        ]
      }
    ])

    const treeProps = {
      children: 'children',
      label: 'label'
    }

    const handleNodeClick = (data) => {
      console.log('选中节点:', data)
      // 这里可以根据选中的节点过滤数据
    }

    // 回收站数据 - 模拟教室数据
    const recycleData = ref([
      {
        id: 101,
        roomName: '多媒体教室',
        roomNumber: '101',
        floor: '科研楼',
        autoApproval: true,
        needApproval: true,
        primaryContact: '张三',
        secondaryContact: '李四',
        thirdContact: '王五'
      },
      {
        id: 102,
        roomName: '多媒体教室',
        roomNumber: '102',
        floor: '科研楼',
        autoApproval: true,
        needApproval: true,
        primaryContact: '张三',
        secondaryContact: '李四',
        thirdContact: '王五'
      },
      {
        id: 103,
        roomName: '多媒体教室',
        roomNumber: '103',
        floor: '科研楼',
        autoApproval: false,
        needApproval: false,
        primaryContact: '张三',
        secondaryContact: '李四',
        thirdContact: '王五'
      },
      {
        id: 104,
        roomName: '多媒体教室',
        roomNumber: '104',
        floor: '科研楼',
        autoApproval: true,
        needApproval: true,
        primaryContact: '张三',
        secondaryContact: '李四',
        thirdContact: '王五'
      },
      {
        id: 105,
        roomName: '多媒体教室',
        roomNumber: '105',
        floor: '科研楼',
        autoApproval: false,
        needApproval: false,
        primaryContact: '张三',
        secondaryContact: '李四',
        thirdContact: '王五'
      },
      {
        id: 106,
        roomName: '多媒体教室',
        roomNumber: '106',
        floor: '科研楼',
        autoApproval: false,
        needApproval: false,
        primaryContact: '张三',
        secondaryContact: '李四',
        thirdContact: '王五'
      }
    ])

    const restoreItem = (row) => {
      ElMessage.success(`已恢复教室: ${row.roomName} (${row.roomNumber})`)
    }

    return {
      searchKeyword,
      teacherName,
      currentPage,
      pageSize,
      total,
      treeData,
      treeProps,
      handleNodeClick,
      recycleData,
      restoreItem
    }
  }
}
</script>

<style scoped>
.recycle-bin-container {
  background: #f5f5f5;
  min-height: 100vh;
  padding: 0;
}

.top-section {
  background: white;
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-section {
  flex: 1;
  max-width: 300px;
}

.search-input {
  width: 100%;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.teacher-input {
  width: 250px;
}

.confirm-btn {
  background: #5b9bd5;
  border-color: #5b9bd5;
}

.main-content {
  display: flex;
  height: calc(100vh - 80px);
}

.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 0;
}

.nav-tree {
  padding: 8px 0;
}

.nav-tree :deep(.el-tree-node__content) {
  height: 40px;
  padding-left: 16px;
  border-bottom: 1px solid #f0f0f0;
  color: #666;
  font-size: 14px;
  transition: all 0.3s;
}

.nav-tree :deep(.el-tree-node__content:hover) {
  background: #f5f5f5;
  color: #333;
}

.nav-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: #e6f3ff;
  color: #1890ff;
  border-right: 3px solid #1890ff;
}

.nav-tree :deep(.el-tree-node__expand-icon) {
  color: #666;
  font-size: 12px;
}

.nav-tree :deep(.el-tree-node__expand-icon.expanded) {
  transform: rotate(90deg);
}

.nav-tree :deep(.el-tree-node__children) {
  background: #fafafa;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__content) {
  padding-left: 32px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__content:hover) {
  background: #f0f0f0;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__children .el-tree-node__content) {
  padding-left: 48px;
  background: #f5f5f5;
  border-bottom: 1px solid #f0f0f0;
}

.nav-tree :deep(.el-tree-node__children .el-tree-node__children .el-tree-node__content:hover) {
  background: #eeeeee;
}

.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
}

.content-area {
  flex: 1;
  background: white;
  padding: 24px;
  overflow: auto;
}

.recycle-table {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.recycle-table :deep(.el-table__header) {
  background: #fafafa;
}

.recycle-table :deep(.el-table__header th) {
  background: #fafafa;
  color: #333;
  font-weight: 600;
  border-bottom: 1px solid #e8e8e8;
}

.recycle-table :deep(.el-table__body tr:hover) {
  background: #f5f5f5;
}

.description-content {
  line-height: 1.4;
}

.desc-line {
  margin-bottom: 4px;
  font-size: 12px;
  color: #666;
}

.desc-line:last-child {
  margin-bottom: 0;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.pagination {
  background: white;
}

.pagination :deep(.el-pagination__total) {
  color: #666;
}

.pagination :deep(.el-pagination__sizes) {
  color: #666;
}

.pagination :deep(.el-pager li) {
  background: white;
  border: 1px solid #ddd;
  margin: 0 2px;
}

.pagination :deep(.el-pager li.active) {
  background: #5b9bd5;
  border-color: #5b9bd5;
  color: white;
}

.pagination :deep(.btn-prev),
.pagination :deep(.btn-next) {
  background: white;
  border: 1px solid #ddd;
}

.pagination :deep(.btn-prev:hover),
.pagination :deep(.btn-next:hover) {
  color: #5b9bd5;
}
</style>