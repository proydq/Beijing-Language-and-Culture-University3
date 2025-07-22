<template>
  <div class="header">
    <div class="header-left">
      <div class="logo" @click="goToHome">
        <el-icon><house /></el-icon>
      </div>
      <h1 class="title">用户管理系统</h1>
    </div>
    <div class="header-right">
      <div class="avatar" @click="goToPersonalCenter">
        <el-icon><user /></el-icon>
      </div>
      <span class="username">管理员</span>
      <el-button type="primary" plain @click="logout">退出</el-button>
    </div>
  </div>
</template>

<script>
import { House, User } from '@element-plus/icons-vue'

export default {
  name: 'UserManagementHeader',
  components: {
    House,
    User
  },
  emits: ['go-home', 'go-personal-center', 'logout'],
  setup(props, { emit }) {
    const goToHome = () => {
      emit('go-home')
    }

    const goToPersonalCenter = () => {
      emit('go-personal-center')
    }

    const logout = () => {
      emit('logout')
    }

    return {
      goToHome,
      goToPersonalCenter,
      logout
    }
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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
  background: white;
  color: #4A90E2;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.05);
}

.title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.avatar:hover {
  background: rgba(255, 255, 255, 0.3);
}

.username {
  font-size: 14px;
  font-weight: 500;
}
</style>