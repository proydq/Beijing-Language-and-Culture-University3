<template>
  <div class="classroom-records-management">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo" @click="goToHome" style="cursor: pointer;">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">教室借用记录管理</span>
      </div>
      <div class="header-right">
        <div class="avatar">
          <el-icon size="20"><user /></el-icon>
        </div>
        <span class="username">系统管理员</span>
        <el-dropdown>
          <span class="dropdown-link">
            <el-icon><grid /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToPersonalCenter">个人中心</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="main-container">
      <!-- 右侧主内容 -->
      <div class="main-content">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item @click="goToHome" style="cursor: pointer; color: #4A90E2;">首页</el-breadcrumb-item>
            <el-breadcrumb-item>教室借用记录管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 标签页 -->
        <el-tabs v-model="activeTab" class="management-tabs">
          <el-tab-pane label="借用记录" name="records">
            <AccessClassroomRecords />
          </el-tab-pane>
          
          <el-tab-pane label="统计信息" name="stats">
            <AccessStatsCard />
          </el-tab-pane>
          
          <el-tab-pane label="实时状态" name="status">
            <RoomStatusCard />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, User, Grid } from '@element-plus/icons-vue'
import AccessClassroomRecords from '@/components/RoomBooking/Records/AccessClassroomRecords.vue'
import AccessStatsCard from '@/components/RoomBooking/Stats/AccessStatsCard.vue'
import RoomStatusCard from '@/components/RoomBooking/Status/RoomStatusCard.vue'

const router = useRouter()
const activeTab = ref('records')

// 导航方法
const goToHome = () => {
  router.push('/dashboard')
}

const goToPersonalCenter = () => {
  ElMessage.info('个人中心功能开发中')
}

const logout = () => {
  ElMessage.info('退出登录功能开发中')
}
</script>

<style scoped>
.classroom-records-management {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo {
  width: 40px;
  height: 40px;
  background: #4A90E2;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 12px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 32px;
  height: 32px;
  background: #e8f4fd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4A90E2;
}

.username {
  font-size: 14px;
  color: #606266;
}

.dropdown-link {
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.dropdown-link:hover {
  background: #f5f7fa;
}

.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.breadcrumb {
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.management-tabs {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.management-tabs :deep(.el-tabs__content) {
  flex: 1;
  padding: 0;
  overflow: hidden;
}

.management-tabs :deep(.el-tab-pane) {
  height: 100%;
  overflow: auto;
}

.management-tabs :deep(.el-tabs__header) {
  margin: 0;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.management-tabs :deep(.el-tabs__nav-wrap) {
  padding: 12px 0;
}

.management-tabs :deep(.el-tabs__item) {
  font-size: 14px;
  font-weight: 500;
}

.management-tabs :deep(.el-tabs__item.is-active) {
  color: #4A90E2;
}

.management-tabs :deep(.el-tabs__active-bar) {
  background-color: #4A90E2;
}
</style>