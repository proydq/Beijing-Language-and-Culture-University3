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
    const tabs = [
      { key: 'dashboard', label: '数据看板', icon: 'DataLine' },
      { key: 'booking', label: '借用管理', icon: 'Management' },
      { key: 'approval', label: '审批管理', icon: 'DocumentChecked' },
      { key: 'records', label: '数据记录', icon: 'FolderOpened' },
      { key: 'settings', label: '设置', icon: 'Setting' }
    ]

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