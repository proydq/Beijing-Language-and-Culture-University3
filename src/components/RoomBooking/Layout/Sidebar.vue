<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <h3>{{ title }}</h3>
    </div>
    <div class="sidebar-menu">
      <div 
        v-for="item in menuItems" 
        :key="item.key || item"
        :class="['menu-item', { active: isActive(item) }]"
        @click="handleMenuClick(item)"
      >
        <el-icon v-if="item.icon">
          <component :is="item.icon" />
        </el-icon>
        <el-icon v-else><document /></el-icon>
        <span>{{ item.label || item }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { Document } from '@element-plus/icons-vue'

export default {
  name: 'Sidebar',
  components: {
    Document
  },
  props: {
    title: {
      type: String,
      default: '菜单'
    },
    menuItems: {
      type: Array,
      default: () => []
    },
    activeItem: {
      type: [String, Object],
      default: ''
    }
  },
  emits: ['menu-click'],
  setup(props, { emit }) {
    const isActive = (item) => {
      if (typeof item === 'string') {
        return props.activeItem === item
      }
      if (typeof item === 'object' && item.key) {
        return props.activeItem === item.key || 
               (typeof props.activeItem === 'object' && props.activeItem.key === item.key)
      }
      return false
    }

    const handleMenuClick = (item) => {
      emit('menu-click', item)
    }

    return {
      isActive,
      handleMenuClick
    }
  }
}
</script>

<style scoped>
.sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.sidebar-header {
  padding: 15px 18px;
  border-bottom: 1px solid #e8e8e8;
  background: #4A90E2;
  color: white;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
}

.menu-item {
  padding: 12px 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #666;
  font-size: 14px;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background: #f5f5f5;
  color: #333;
}

.menu-item.active {
  background: #e6f3ff;
  color: #4A90E2;
  border-left-color: #4A90E2;
  font-weight: 500;
}

.menu-item .el-icon {
  font-size: 14px;
}
</style>