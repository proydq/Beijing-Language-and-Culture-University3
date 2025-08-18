<template>
  <div class="header">
    <div class="header-left">
      <div class="logo" @click="goToHome" style="cursor: pointer">
        <el-icon size="24"><home-filled /></el-icon>
      </div>
      <span class="title">房屋借用管理</span>
    </div>
    <div class="header-right">
      <div class="avatar">
        <el-avatar 
          v-if="userStore.userInfo.avatarUrl" 
          :src="userStore.userInfo.avatarUrl"
          :size="32"
          @error="handleAvatarError"
        >
          <el-icon v-if="avatarError" size="20"><user /></el-icon>
        </el-avatar>
        <el-icon v-else size="20"><user /></el-icon>
      </div>
      <span class="username">{{ userStore.displayName }}</span>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { HomeFilled, User, Grid } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

export default {
  name: 'RoomBookingHeader',
  components: {
    HomeFilled,
    User,
    Grid,
  },
  emits: ['profile', 'logout'],
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const avatarError = ref(false)
    
    // 处理头像加载错误
    const handleAvatarError = () => {
      avatarError.value = true
    }
    
    const goToHome = () => {
      router.push('/dashboard')
    }
    
    return {
      userStore,
      avatarError,
      handleAvatarError,
      goToHome
    }
  }
}
</script>

<style scoped>
.header {
  height: 60px;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
