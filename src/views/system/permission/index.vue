<template>
  <div class="permission-management">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <!-- 权限别名管理 -->
      <el-tab-pane label="权限别名管理" name="alias">
        <permission-alias />
      </el-tab-pane>
      
      <!-- URL权限映射 -->
      <el-tab-pane label="URL权限映射" name="url">
        <permission-url />
      </el-tab-pane>
      
      <!-- 审计日志 -->
      <el-tab-pane label="审计日志" name="audit">
        <audit-log />
      </el-tab-pane>
      
      <!-- 权限统计 -->
      <el-tab-pane label="权限统计" name="statistics">
        <permission-statistics />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import PermissionAlias from './components/PermissionAlias.vue'
import PermissionUrl from './components/PermissionUrl.vue'
import AuditLog from './components/AuditLog.vue'
import PermissionStatistics from './components/PermissionStatistics.vue'

const activeTab = ref('alias')

const handleTabClick = (tab) => {
  console.log('切换到标签页:', tab.props.name)
}
</script>

<style lang="scss" scoped>
.permission-management {
  padding: 20px;
  background-color: #fff;
  min-height: calc(100vh - 84px);
}
</style>