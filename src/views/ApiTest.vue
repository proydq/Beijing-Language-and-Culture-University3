<template>
  <div class="api-test-page">
    <div class="test-container">
      <h1>前后端联调测试页面</h1>
      
      <!-- 登录测试区域 -->
      <div class="test-section">
        <h2>登录API测试</h2>
        <div class="form-group">
          <label>用户名:</label>
          <input v-model="loginData.username" placeholder="请输入用户名" />
        </div>
        <div class="form-group">
          <label>密码:</label>
          <input v-model="loginData.password" type="password" placeholder="请输入密码" />
        </div>
        <button @click="testLogin" :disabled="loginLoading">
          {{ loginLoading ? '登录中...' : '测试登录' }}
        </button>
        <div v-if="loginResult" class="result">
          <h3>登录结果:</h3>
          <pre>{{ JSON.stringify(loginResult, null, 2) }}</pre>
        </div>
      </div>

      <!-- 用户查询测试区域 -->
      <div class="test-section">
        <h2>用户查询API测试</h2>
        <button @click="testUserSearch" :disabled="userLoading">
          {{ userLoading ? '查询中...' : '测试用户查询' }}
        </button>
        <div v-if="userResult" class="result">
          <h3>用户查询结果:</h3>
          <pre>{{ JSON.stringify(userResult, null, 2) }}</pre>
        </div>
      </div>

      <!-- 当前登录状态 -->
      <div class="test-section">
        <h2>当前登录状态</h2>
        <div class="status-info">
          <p><strong>Token:</strong> {{ currentToken || '未登录' }}</p>
          <p><strong>用户信息:</strong></p>
          <pre v-if="currentUser">{{ JSON.stringify(currentUser, null, 2) }}</pre>
          <p v-else>未登录</p>
        </div>
        <button @click="clearLoginInfo">清除登录信息</button>
      </div>

      <!-- 错误信息显示 -->
      <div v-if="errorMessage" class="error-message">
        <h3>错误信息:</h3>
        <pre>{{ errorMessage }}</pre>
      </div>
    </div>
  </div>
</template>

<script>
import { authAPI } from '@/api/auth'
import { userAPI } from '@/api/user'

export default {
  name: 'ApiTest',
  data() {
    return {
      // 登录测试数据
      loginData: {
        username: 'admin',
        password: '123456'
      },
      loginLoading: false,
      loginResult: null,
      
      // 用户查询测试
      userLoading: false,
      userResult: null,
      
      // 错误信息
      errorMessage: null
    }
  },
  
  computed: {
    currentToken() {
      return localStorage.getItem('userToken')
    },
    currentUser() {
      const userInfo = localStorage.getItem('userInfo')
      return userInfo ? JSON.parse(userInfo) : null
    }
  },
  
  methods: {
    // 测试登录API
    async testLogin() {
      this.loginLoading = true
      this.errorMessage = null
      this.loginResult = null
      
      try {
        console.log('发送登录请求:', this.loginData)
        const response = await authAPI.login(this.loginData)
        console.log('登录响应:', response)
        
        this.loginResult = response
        
        // 如果登录成功，保存token和用户信息
        if (response.success && response.data) {
          localStorage.setItem('userToken', response.data.token)
          localStorage.setItem('userInfo', JSON.stringify({
            id: response.data.userId,
            username: response.data.username,
            realName: response.data.realName,
            customerId: response.data.customerId
          }))
        }
      } catch (error) {
        console.error('登录测试失败:', error)
        this.errorMessage = error.response?.data || error.message || '登录测试失败'
      } finally {
        this.loginLoading = false
      }
    },
    
    // 测试用户查询API
    async testUserSearch() {
      this.userLoading = true
      this.errorMessage = null
      this.userResult = null
      
      try {
        const searchCondition = {
          pageNum: 1,
          pageSize: 10,
          status: 'NORMAL'
        }
        
        console.log('发送用户查询请求:', searchCondition)
        const response = await userAPI.searchUsers(searchCondition)
        console.log('用户查询响应:', response)
        
        this.userResult = response
      } catch (error) {
        console.error('用户查询测试失败:', error)
        this.errorMessage = error.response?.data || error.message || '用户查询测试失败'
      } finally {
        this.userLoading = false
      }
    },
    
    // 清除登录信息
    clearLoginInfo() {
      localStorage.removeItem('userToken')
      localStorage.removeItem('userInfo')
      this.loginResult = null
      this.userResult = null
      this.errorMessage = null
    }
  }
}
</script>

<style scoped>
.api-test-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-container {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.test-section {
  margin-bottom: 40px;
  padding: 20px;
  border: 1px solid #e1e5e9;
  border-radius: 6px;
}

.test-section h2 {
  color: #333;
  margin-bottom: 20px;
  border-bottom: 2px solid #667eea;
  padding-bottom: 10px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 300px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

button {
  background: #667eea;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 10px;
}

button:hover:not(:disabled) {
  background: #5a6fd8;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.result {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #28a745;
}

.error-message {
  margin-top: 20px;
  padding: 15px;
  background: #fff5f5;
  border-radius: 4px;
  border-left: 4px solid #dc3545;
  color: #721c24;
}

.status-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 15px;
}

pre {
  background: #f1f3f4;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  line-height: 1.4;
}

h1 {
  color: #333;
  text-align: center;
  margin-bottom: 30px;
}

h3 {
  color: #333;
  margin-bottom: 10px;
}
</style>