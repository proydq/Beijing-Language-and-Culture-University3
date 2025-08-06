<template>
  <div class="access-control-records">
    <!-- 顶部导航 -->
    <div class="page-navigation">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>教室管理</el-breadcrumb-item>
        <el-breadcrumb-item>出入记录</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 功能选项卡 -->
    <div class="function-tabs">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="出入记录管理" name="records">
          <AccessRecordsManagement />
        </el-tab-pane>
        <el-tab-pane label="教室状态监控" name="monitor">
          <RoomStatusMonitor @view-all-records="switchToRecordsTab" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AccessRecordsManagement from '@/components/RoomBooking/AccessRecords/AccessRecordsManagement.vue'
import RoomStatusMonitor from '@/components/RoomBooking/AccessRecords/RoomStatusMonitor.vue'

// 响应式数据
const activeTab = ref('records')

// 处理选项卡切换
const handleTabChange = (tabName) => {
  console.log('切换到选项卡:', tabName)
}

// 从状态监控页面跳转到记录页面
const switchToRecordsTab = (room) => {
  activeTab.value = 'records'
  // 这里可以传递教室ID给记录页面进行筛选
  console.log('查看教室记录:', room)
}
</script>

<style scoped>
.access-control-records {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.page-navigation {
  background: white;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 0;
}

.function-tabs {
  background: white;
  min-height: calc(100vh - 60px);
}

:deep(.el-tabs__header) {
  margin: 0;
  padding: 0 20px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
}

:deep(.el-tabs__nav-wrap) {
  padding: 0;
}

:deep(.el-tabs__item) {
  padding: 0 20px;
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff;
  font-weight: 600;
}

:deep(.el-tabs__content) {
  padding: 0;
}

:deep(.el-tab-pane) {
  background: #f5f5f5;
  min-height: calc(100vh - 110px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-navigation {
    padding: 12px 16px;
  }

  :deep(.el-tabs__header) {
    padding: 0 16px;
  }

  :deep(.el-tabs__item) {
    padding: 0 12px;
    font-size: 13px;
  }
}
</style>