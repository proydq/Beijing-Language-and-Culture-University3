<template>
  <div class="header">
    <div class="header-left">
      <div class="logo">
        <el-icon size="24"><home-filled /></el-icon>
      </div>
      <span class="title">房屋借用管理</span>
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
            <el-dropdown-item @click="$emit('profile')">个人中心</el-dropdown-item>
            <el-dropdown-item @click="$emit('logout')">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { HomeFilled, User, Grid } from '@element-plus/icons-vue'

export default {
  name: 'RoomBookingHeader',
  components: {
    HomeFilled,
    User,
    Grid
  },
  emits: ['profile', 'logout']
}
</script>

<style scoped>
.header {
  height: 60px;
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-left .title {
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

.username {
  font-size: 14px;
}

.dropdown-link {
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.3s;
}

.dropdown-link:hover {
  background: rgba(255, 255, 255, 0.1);
}
</style>