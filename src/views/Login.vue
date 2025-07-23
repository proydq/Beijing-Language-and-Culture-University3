<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>欢迎登录</h1>
        <p>请输入您的账号信息</p>
      </div>

      <!-- 错误提示 -->
      <div v-if="showError" class="error-message">
        {{ errorMessage }}
      </div>

      <!-- 成功提示 -->
      <div v-if="showSuccess" class="success-message">
        {{ successMessage }}
      </div>

      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">用户名/邮箱</label>
          <input
            type="text"
            id="username"
            v-model="loginForm.username"
            placeholder="请输入用户名或邮箱"
            required
            :disabled="loading"
          />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="loginForm.password"
            placeholder="请输入密码"
            required
            :disabled="loading"
          />
        </div>

        <div class="form-options">
          <div class="remember-me">
            <input
              type="checkbox"
              id="remember"
              v-model="loginForm.remember"
            />
            <label for="remember">记住我</label>
          </div>
          <a href="#" class="forgot-password" @click.prevent="handleForgotPassword">
            忘记密码？
          </a>
        </div>

        <button type="submit" class="login-button" :disabled="loading">
          <span v-if="loading" class="loading-spinner"></span>
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>


    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return {
      // 表单数据
      loginForm: {
        username: '',
        password: '',
        remember: false
      },
      // 状态控制
      loading: false,
      showError: false,
      showSuccess: false,
      errorMessage: '',
      successMessage: ''
    }
  },
  methods: {
    // 处理登录
    async handleLogin() {
      // 隐藏之前的提示
      this.hideMessages();
      
      // 基本验证
      if (!this.loginForm.username || !this.loginForm.password) {
        this.showErrorMessage('请填写完整的登录信息');
        return;
      }

      this.loading = true;

      try {
        // 模拟API调用
        const result = await this.simulateLogin();
        
        if (result.success) {
          this.showSuccessMessage('登录成功！正在跳转...');
          
          // 如果选择了记住我，保存到本地存储
          if (this.loginForm.remember) {
            localStorage.setItem('rememberedUser', this.loginForm.username);
          }
          
          // 2秒后跳转
          setTimeout(() => {
            this.handleLoginSuccess(result.data);
          }, 2000);
        } else {
          this.showErrorMessage(result.message || '登录失败，请重试');
        }
      } catch (error) {
        this.showErrorMessage('网络错误，请检查网络连接');
      } finally {
        this.loading = false;
      }
    },

    // 模拟登录API（您需要替换为真实的API调用）
    simulateLogin() {
      return new Promise((resolve) => {
        setTimeout(() => {
          // 这里是模拟的验证逻辑
          if (this.loginForm.username === 'admin' && this.loginForm.password === '123456') {
            resolve({
              success: true,
              data: {
                token: 'mock-jwt-token',
                user: {
                  id: 1,
                  username: this.loginForm.username,
                  email: 'admin@example.com'
                }
              }
            });
          } else {
            resolve({
              success: false,
              message: '用户名或密码错误'
            });
          }
        }, 1000); // 模拟网络延迟
      });
    },

    // 登录成功处理
    handleLoginSuccess(userData) {
      // 保存用户信息到vuex或本地存储
      localStorage.setItem('userToken', userData.token);
      localStorage.setItem('userInfo', JSON.stringify(userData.user));
      
      // 发送登录成功事件给父组件
      this.$emit('loginSuccess', userData);
      
      // 跳转到首页（根据您的路由配置修改）
      this.$router.push('/dashboard');
    },

    // 处理忘记密码
    handleForgotPassword() {
      // 显示提示信息，因为没有忘记密码页面
      alert('请联系系统管理员重置密码');
    },



    // 显示错误信息
    showErrorMessage(message) {
      this.errorMessage = message;
      this.showError = true;
      setTimeout(() => {
        this.showError = false;
      }, 5000);
    },

    // 显示成功信息
    showSuccessMessage(message) {
      this.successMessage = message;
      this.showSuccess = true;
    },

    // 隐藏所有提示信息
    hideMessages() {
      this.showError = false;
      this.showSuccess = false;
    }
  },

  // 组件挂载时检查是否有记住的用户名
  mounted() {
    const rememberedUser = localStorage.getItem('rememberedUser');
    if (rememberedUser) {
      this.loginForm.username = rememberedUser;
      this.loginForm.remember = true;
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  background: white;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  animation: fadeIn 0.5s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #333;
  font-size: 28px;
  margin-bottom: 10px;
  font-weight: 600;
}

.login-header p {
  color: #666;
  font-size: 14px;
}

.error-message {
  background: #fee;
  color: #c33;
  padding: 12px;
  border-radius: 5px;
  margin-bottom: 20px;
  border-left: 4px solid #c33;
  animation: slideIn 0.3s ease;
}

.success-message {
  background: #efe;
  color: #393;
  padding: 12px;
  border-radius: 5px;
  margin-bottom: 20px;
  border-left: 4px solid #393;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e1e5e9;
  border-radius: 5px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remember-me input[type="checkbox"] {
  width: auto;
  margin: 0;
}

.remember-me label {
  margin: 0;
  font-size: 14px;
  cursor: pointer;
}

.forgot-password {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}



/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    padding: 30px 20px;
  }
  
  .login-header h1 {
    font-size: 24px;
  }
  
  .form-options {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
}
</style>