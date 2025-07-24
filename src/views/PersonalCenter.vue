<template>
  <div class="personal-center">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo" @click="goToHome" style="cursor: pointer;">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">个人中心</span>
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
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="main-container">
      <!-- 左侧导航 -->
      <div class="sidebar">
        <!-- 添加首页导航 -->
        <div class="nav-item" @click="goToHome">
          <el-icon><home /></el-icon>
          <span>首页</span>
        </div>
        
        <div class="nav-item" :class="{ active: activeMenu === 'profile' }" @click="setActiveMenu('profile')">
          <el-icon><user /></el-icon>
          <span>个人信息</span>
        </div>
        <div class="nav-item" :class="{ active: activeMenu === 'password' }" @click="setActiveMenu('password')">
          <el-icon><lock /></el-icon>
          <span>修改密码</span>
        </div>
        <div class="nav-item" :class="{ active: activeMenu === 'settings' }" @click="setActiveMenu('settings')">
          <el-icon><setting /></el-icon>
          <span>系统设置</span>
        </div>
      </div>

      <!-- 右侧内容区域 -->
      <div class="main-content">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item @click="goToHome" style="cursor: pointer; color: #4A90E2;">首页</el-breadcrumb-item>
            <el-breadcrumb-item>个人中心</el-breadcrumb-item>
            <el-breadcrumb-item>{{ getMenuTitle(activeMenu) }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 个人信息页面 -->
        <div v-if="activeMenu === 'profile'" class="profile-section">
          <div class="section-header">
            <h2>个人信息</h2>
            <el-button type="primary" @click="toggleEdit">
              {{ editMode ? '取消编辑' : '编辑信息' }}
            </el-button>
          </div>

          <div class="profile-content">
            <div class="avatar-section">
              <div class="avatar-upload">
                <img :src="profileForm.avatar || '/default-avatar.png'" alt="头像" class="avatar-img" />
                <div v-if="editMode" class="avatar-overlay" @click="handleAvatarUpload">
                  <el-icon><camera /></el-icon>
                  <span>更换头像</span>
                </div>
              </div>
            </div>

            <div class="form-section">
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="120px">
                <el-row :gutter="20">
                  <el-col :span="12">
                    <el-form-item label="用户名:" prop="username">
                      <el-input v-model="profileForm.username" :disabled="true" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="姓名:" prop="realName">
                      <el-input v-model="profileForm.realName" :disabled="!editMode" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="邮箱:" prop="email">
                      <el-input v-model="profileForm.email" :disabled="!editMode" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="手机号:" prop="phone">
                      <el-input v-model="profileForm.phone" :disabled="!editMode" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="部门:" prop="department">
                      <el-input v-model="profileForm.department" :disabled="!editMode" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="12">
                    <el-form-item label="职位:" prop="position">
                      <el-input v-model="profileForm.position" :disabled="!editMode" />
                    </el-form-item>
                  </el-col>
                  <el-col :span="24">
                    <el-form-item label="个人简介:" prop="bio">
                      <el-input 
                        v-model="profileForm.bio" 
                        type="textarea" 
                        :rows="4"
                        :disabled="!editMode" 
                        placeholder="请输入个人简介"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>

                <!-- 编辑模式下的操作按钮 -->
                <div v-if="editMode" class="form-actions">
                  <el-button @click="cancelEdit">取消</el-button>
                  <el-button type="primary" @click="saveProfile">保存</el-button>
                </div>
              </el-form>
            </div>
          </div>
        </div>

        <!-- 修改密码页面 -->
        <div v-if="activeMenu === 'password'" class="password-section">
          <div class="section-header">
            <h2>修改密码</h2>
          </div>

          <div class="password-content">
            <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="120px">
              <el-form-item label="当前密码:" prop="currentPassword">
                <el-input 
                  v-model="passwordForm.currentPassword" 
                  type="password" 
                  show-password
                  placeholder="请输入当前密码" 
                />
              </el-form-item>
              <el-form-item label="新密码:" prop="newPassword">
                <el-input 
                  v-model="passwordForm.newPassword" 
                  type="password" 
                  show-password
                  placeholder="请输入新密码" 
                />
              </el-form-item>
              <el-form-item label="确认密码:" prop="confirmPassword">
                <el-input 
                  v-model="passwordForm.confirmPassword" 
                  type="password" 
                  show-password
                  placeholder="请再次输入新密码" 
                />
              </el-form-item>
              
              <div class="form-actions">
                <el-button @click="resetPasswordForm">重置</el-button>
                <el-button type="primary" @click="changePassword">修改密码</el-button>
              </div>
            </el-form>
          </div>
        </div>

        <!-- 系统设置页面 -->
        <div v-if="activeMenu === 'settings'" class="settings-section">
          <div class="section-header">
            <h2>系统设置</h2>
          </div>

          <div class="settings-content">
            <div class="settings-group">
              <h3>界面设置</h3>
              <div class="setting-item">
                <div class="setting-label">
                  <span>主题模式</span>
                  <small>选择您喜欢的界面主题</small>
                </div>
                <el-select v-model="settingsForm.theme" placeholder="请选择主题">
                  <el-option label="浅色主题" value="light" />
                  <el-option label="深色主题" value="dark" />
                  <el-option label="自动" value="auto" />
                </el-select>
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <span>语言设置</span>
                  <small>选择界面显示语言</small>
                </div>
                <el-select v-model="settingsForm.language" placeholder="请选择语言">
                  <el-option label="简体中文" value="zh-CN" />
                  <el-option label="English" value="en-US" />
                </el-select>
              </div>
            </div>

            <div class="settings-group">
              <h3>通知设置</h3>
              <div class="setting-item">
                <div class="setting-label">
                  <span>邮件通知</span>
                  <small>接收系统邮件通知</small>
                </div>
                <el-switch v-model="settingsForm.emailNotification" />
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <span>短信通知</span>
                  <small>接收系统短信通知</small>
                </div>
                <el-switch v-model="settingsForm.smsNotification" />
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <span>桌面通知</span>
                  <small>接收浏览器桌面通知</small>
                </div>
                <el-switch v-model="settingsForm.desktopNotification" />
              </div>
            </div>

            <div class="settings-group">
              <h3>安全设置</h3>
              <div class="setting-item">
                <div class="setting-label">
                  <span>自动锁屏</span>
                  <small>设置自动锁屏时间</small>
                </div>
                <el-select v-model="settingsForm.autoLock" placeholder="请选择时间">
                  <el-option label="从不" value="never" />
                  <el-option label="5分钟" value="5min" />
                  <el-option label="15分钟" value="15min" />
                  <el-option label="30分钟" value="30min" />
                  <el-option label="1小时" value="1hour" />
                </el-select>
              </div>
              <div class="setting-item">
                <div class="setting-label">
                  <span>登录日志</span>
                  <small>记录登录活动</small>
                </div>
                <el-switch v-model="settingsForm.loginLog" />
              </div>
            </div>

            <div class="form-actions">
              <el-button @click="resetSettings">重置设置</el-button>
              <el-button type="primary" @click="saveSettings">保存设置</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'PersonalCenter',
  setup() {
    const router = useRouter()
    const activeMenu = ref('profile')
    const editMode = ref(false)
    const profileFormRef = ref()
    const passwordFormRef = ref()

    // 个人信息表单
    const profileForm = reactive({
      username: 'admin',
      realName: '系统管理员',
      email: 'admin@example.com',
      phone: '13800138000',
      department: '技术部',
      position: '系统管理员',
      bio: '负责系统的整体管理和维护工作',
      avatar: ''
    })

    // 密码修改表单
    const passwordForm = reactive({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    // 系统设置表单
    const settingsForm = reactive({
      theme: 'light',
      language: 'zh-CN',
      emailNotification: true,
      smsNotification: false,
      desktopNotification: true,
      autoLock: '30min',
      loginLog: true
    })

    // 个人信息验证规则
    const profileRules = {
      realName: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ]
    }

    // 密码验证规则
    const passwordRules = {
      currentPassword: [
        { required: true, message: '请输入当前密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度应为6-20个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== passwordForm.newPassword) {
              callback(new Error('两次输入的密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }

    // 返回首页
    const goToHome = () => {
      router.push('/dashboard')
    }

    const logout = () => {
      console.log('退出登录')
    }

    // 菜单相关方法
    const setActiveMenu = (menu) => {
      activeMenu.value = menu
    }

    const getMenuTitle = (menu) => {
      const titleMap = {
        'profile': '个人信息',
        'password': '修改密码',
        'settings': '系统设置'
      }
      return titleMap[menu] || ''
    }

    // 个人信息相关方法
    const toggleEdit = () => {
      editMode.value = !editMode.value
      if (!editMode.value) {
        // 取消编辑时重置表单
        cancelEdit()
      }
    }

    const cancelEdit = () => {
      editMode.value = false
      // 这里可以重置表单数据到原始状态
    }

    const saveProfile = async () => {
      try {
        await profileFormRef.value.validate()
        // 保存个人信息
        editMode.value = false
        ElMessage.success('个人信息保存成功')
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    const handleAvatarUpload = () => {
      console.log('上传头像')
      // 这里可以实现头像上传逻辑
    }

    // 密码相关方法
    const changePassword = async () => {
      try {
        await passwordFormRef.value.validate()
        // 修改密码
        resetPasswordForm()
        ElMessage.success('密码修改成功')
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    const resetPasswordForm = () => {
      Object.assign(passwordForm, {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
    }

    // 设置相关方法
    const saveSettings = () => {
      // 保存系统设置
      ElMessage.success('设置保存成功')
    }

    const resetSettings = () => {
      Object.assign(settingsForm, {
        theme: 'light',
        language: 'zh-CN',
        emailNotification: true,
        smsNotification: false,
        desktopNotification: true,
        autoLock: '30min',
        loginLog: true
      })
      ElMessage.success('设置已重置')
    }

    return {
      activeMenu,
      editMode,
      profileFormRef,
      passwordFormRef,
      profileForm,
      passwordForm,
      settingsForm,
      profileRules,
      passwordRules,
      goToHome,  // 新增返回首页方法
      logout,
      setActiveMenu,
      getMenuTitle,
      toggleEdit,
      cancelEdit,
      saveProfile,
      handleAvatarUpload,
      changePassword,
      resetPasswordForm,
      saveSettings,
      resetSettings
    }
  }
}
</script>

<style scoped>
.personal-center {
  min-height: 100vh;
  background-color: #f5f5f5;
}

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
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.05);
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.username {
  font-size: 16px;
  font-weight: 500;
}

.dropdown-link {
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-container {
  display: flex;
  height: calc(100vh - 70px);
}

.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 15px;
  flex-shrink: 0;
}

.nav-item {
  padding: 12px 15px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item:hover {
  background-color: #f0f2f5;
  color: #4A90E2;
}

.nav-item.active {
  background-color: #4A90E2;
  color: white;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.breadcrumb {
  margin-bottom: 20px;
  padding: 15px 0;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child)) {
  cursor: pointer;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child):hover) {
  color: #357ABD;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.profile-section,
.password-section,
.settings-section {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-content {
  display: flex;
  gap: 40px;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-upload {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #e8e8e8;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-upload:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay span {
  font-size: 12px;
  margin-top: 5px;
}

.form-section {
  flex: 1;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.password-content {
  max-width: 500px;
}

.settings-content {
  max-width: 600px;
}

.settings-group {
  margin-bottom: 40px;
}

.settings-group h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #4A90E2;
  padding-bottom: 10px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-label {
  flex: 1;
}

.setting-label span {
  display: block;
  font-size: 16px;
  color: #333;
  margin-bottom: 5px;
}

.setting-label small {
  color: #999;
  font-size: 13px;
}
</style>