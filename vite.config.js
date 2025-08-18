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
      // 统一处理所有 /api 前缀的请求
      '/api': {
        target: env.VITE_API_BASE_URL || 'http://localhost:12138',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // 去除 /api 前缀
        configure: (proxy, options) => {
          // 代理事件监听（用于调试）
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('[Proxy]', req.method, req.url, '->', options.target + proxyReq.path)
          })
        }
      }
    }
  }
  }
})
