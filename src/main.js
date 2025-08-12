import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 权限相关
import permissionDirective, { roleDirective } from './directives/permission'
import permissionPlugin from './utils/permission'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 创建Pinia实例并使用（必须在使用store之前）
const pinia = createPinia()
app.use(pinia)

app.use(router)
app.use(ElementPlus)

// 注册权限指令
app.directive('permission', permissionDirective)
app.directive('role', roleDirective)

// 注册权限工具
app.use(permissionPlugin)

app.mount('#app')
