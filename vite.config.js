import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd())
  
  return {
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    // 开发服务器配置
    port: Number(env.VITE_APP_PORT) || 5173,
    host: true, // 监听所有地址
    proxy: {
      // 代理配置 - 带 /api 前缀的路径
      '/api': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
        rewrite: (path) => path,
      },
      // 认证相关接口代理
      '/authentication': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 区域管理接口代理
      '/area': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 黑名单管理接口代理
      '/blacklist': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 违规设置接口代理
      '/violation-settings': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 预约人员权限接口代理
      '/booking-personnel-permission': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 连续预约设置接口代理
      '/continuous-booking-settings': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 用户预约限制接口代理
      '/user-booking-limits': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 预约时间规则接口代理
      '/booking-time-rules': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 预约验证接口代理
      '/booking-validation': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 回收站接口代理
      '/recycle-bin': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 角色管理接口代理
      '/role-management': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 方案管理接口代理
      '/scheme-management': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      },
      // 系统用户接口代理
      '/system': {
        target: env.VITE_API_BASE_URL || 'http://localhost:10086',
        changeOrigin: true,
      }
    }
  }
  }
})
