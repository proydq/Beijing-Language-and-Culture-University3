<template>
  <div class="main-nav">
    <div class="nav-tabs">
      <div 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="['nav-tab', { active: modelValue === tab.key }]"
        @click="$emit('update:modelValue', tab.key)"
      >
        <el-icon>
          <component :is="tab.icon" />
        </el-icon>
        <span>{{ tab.label }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { DataLine, Management, DocumentChecked, FolderOpened, Setting } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

export default {
  name: 'MainNavigation',
  components: {
    DataLine,
    Management,
    DocumentChecked,
    FolderOpened,
    Setting
  },
  props: {
    modelValue: {
      type: String,
      default: 'dashboard'
    }
  },
  emits: ['update:modelValue'],
  setup() {
    const userStore = useUserStore()
    
    // 检查用户是否有特定权限
    const hasPermission = (permissionCode) => {
      return userStore.permissions.includes(permissionCode)
    }
    
    // 所有可能的标签
    const allTabs = [
      { key: 'dashboard', label: '数据看板', icon: 'DataLine', permission: 'BOOKING_VIEW' },
      { key: 'booking', label: '借用管理', icon: 'Management', permission: 'BOOKING_MANAGE' },
      { key: 'approval', label: '审批管理', icon: 'DocumentChecked', permission: 'BOOKING_APPROVE' },
      { key: 'records', label: '数据记录', icon: 'FolderOpened', permission: 'BOOKING_VIEW' },
      { key: 'settings', label: '设置', icon: 'Setting', permission: 'BOOKING_CONFIG' }
    ]
    
    // 根据权限过滤标签
    const tabs = computed(() => {
      return allTabs.filter(tab => {
        // 数据看板和借用管理对普通用户开放
        if (tab.key === 'dashboard' || tab.key === 'booking') {
          return hasPermission('BOOKING_VIEW') || hasPermission('BOOKING_MANAGE')
        }
        // 审批管理需要审批权限
        if (tab.key === 'approval') {
          return hasPermission('BOOKING_APPROVE')
        }
        // 数据记录需要管理权限或特定的记录查看权限
        if (tab.key === 'records') {
          return hasPermission('BOOKING_MANAGE') && hasPermission('BOOKING_APPROVE')
        }
        // 设置需要配置权限
        if (tab.key === 'settings') {
          return hasPermission('BOOKING_CONFIG')
        }
        // 其他标签需要特定权限
        return hasPermission(tab.permission)
      })
    })

    return {
      tabs
    }
  }
}
</script>

<style scoped>
.main-nav {
  background: white;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.nav-tabs {
  display: flex;
  padding: 0 20px;
}

.nav-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 15px 25px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  font-weight: 500;
  border-bottom: 3px solid transparent;
  position: relative;
}

.nav-tab:hover {
  color: #4A90E2;
  background-color: #f8f9fa;
}

.nav-tab.active {
  color: #4A90E2;
  border-bottom-color: #4A90E2;
  background-color: #f0f7ff;
}
</style>