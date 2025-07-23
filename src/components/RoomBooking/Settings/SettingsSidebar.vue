<template>
  <div class="settings-sidebar">
    <div class="sidebar-header">
      <h3>设置</h3>
    </div>
    <div class="sidebar-menu">
      <!-- 房屋类别权限设置分组 -->
      <div class="menu-group">
        <div 
          :class="['menu-group-title', { expanded: expandedGroups.includes('house_permission') }]"
          @click="toggleGroup('house_permission')"
        >
          <el-icon><key /></el-icon>
          <span>房屋类别权限设置</span>
          <el-icon class="expand-icon"><arrow-down /></el-icon>
        </div>
        <div v-show="expandedGroups.includes('house_permission')" class="submenu">
          <div 
            v-for="item in housePermissionSettings" 
            :key="item.key"
            :class="['submenu-item', { active: activeSettingType === item.key }]"
            @click="setActiveSettingType(item.key)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>

      <!-- 房屋管理分组 -->
      <div class="menu-group">
        <div 
          :class="['menu-group-title', { expanded: expandedGroups.includes('house_management') }]"
          @click="toggleGroup('house_management')"
        >
          <el-icon><cpu /></el-icon>
          <span>房屋管理</span>
          <el-icon class="expand-icon"><arrow-down /></el-icon>
        </div>
        <div v-show="expandedGroups.includes('house_management')" class="submenu">
          <div 
            v-for="item in houseManagementSettings" 
            :key="item.key"
            :class="['submenu-item', { active: activeSettingType === item.key }]"
            @click="setActiveSettingType(item.key)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>

      <!-- 违规设置分组 -->
      <div class="menu-group">
        <div 
          :class="['menu-group-title', { expanded: expandedGroups.includes('violation_settings') }]"
          @click="toggleGroup('violation_settings')"
        >
          <el-icon><warning /></el-icon>
          <span>违规设置</span>
          <el-icon class="expand-icon"><arrow-down /></el-icon>
        </div>
        <div v-show="expandedGroups.includes('violation_settings')" class="submenu">
          <div 
            v-for="item in violationSettings" 
            :key="item.key"
            :class="['submenu-item', { active: activeSettingType === item.key }]"
            @click="setActiveSettingType(item.key)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { 
  ArrowDown, 
  Key, 
  Cpu, 
  User, 
  Clock,
  Document,
  Bell,
  Warning
} from '@element-plus/icons-vue'

export default {
  name: 'SettingsSidebar',
  components: {
    ArrowDown,
    Key,
    Cpu,
    User,
    Clock,
    Document,
    Bell,
    Warning
  },
  props: {
    activeSettingType: {
      type: String,
      required: true
    },
    expandedGroups: {
      type: Array,
      required: true
    }
  },
  emits: ['update:activeSettingType', 'update:expandedGroups'],
  setup(props, { emit }) {
    // 设置菜单定义
    const housePermissionSettings = [
      { key: 'booking_personnel', label: '预约人员', icon: 'User' },
      { key: 'booking_time_settings', label: '预约时间设置', icon: 'Clock' },
      { key: 'continuous_booking', label: '连续预约设置', icon: 'Document' }
    ]

    const houseManagementSettings = [
      { key: 'scheme_management', label: '方案管理', icon: 'Document' },
      { key: 'recycle_bin', label: '回收站', icon: 'Delete' }
    ]

    const violationSettings = [
      { key: 'violation_config', label: '违规配置', icon: 'Warning' },
      { key: 'blacklist', label: '黑名单管理', icon: 'User' }
    ]

    const toggleGroup = (groupKey) => {
      const newExpandedGroups = [...props.expandedGroups]
      const index = newExpandedGroups.indexOf(groupKey)
      
      if (index > -1) {
        newExpandedGroups.splice(index, 1)
      } else {
        newExpandedGroups.push(groupKey)
      }
      
      emit('update:expandedGroups', newExpandedGroups)
    }

    const setActiveSettingType = (type) => {
      emit('update:activeSettingType', type)
    }

    return {
      housePermissionSettings,
      houseManagementSettings,
      violationSettings,
      toggleGroup,
      setActiveSettingType
    }
  }
}
</script>

<style scoped>
/* 设置左侧导航 */
.settings-sidebar {
  width: 200px;
  background: white;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.settings-sidebar .sidebar-header {
  padding: 15px 18px;
  border-bottom: 1px solid #e8e8e8;
  background: #4A90E2;
  color: white;
}

.settings-sidebar .sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.settings-sidebar .sidebar-menu {
  flex: 1;
  overflow-y: auto;
}

/* 设置菜单组样式 */
.settings-sidebar .menu-group {
  border-bottom: 1px solid #f0f0f0;
}

.settings-sidebar .menu-group-title {
  padding: 12px 18px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  color: #333;
  background: #fafafa;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.settings-sidebar .menu-group-title:hover {
  background: #f0f7ff;
  color: #4A90E2;
}

.settings-sidebar .menu-group-title.expanded {
  background: #e6f3ff;
  color: #4A90E2;
  border-left-color: #4A90E2;
}

.settings-sidebar .menu-group-title .expand-icon {
  margin-left: auto;
  transition: transform 0.3s;
  font-size: 14px;
}

.settings-sidebar .menu-group-title.expanded .expand-icon {
  transform: rotate(180deg);
}

/* 设置子菜单样式 */
.settings-sidebar .submenu {
  background: white;
}

.settings-sidebar .submenu-item {
  padding: 10px 18px 10px 35px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #666;
  font-size: 13px;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.settings-sidebar .submenu-item:hover {
  background: #f5f5f5;
  color: #333;
}

.settings-sidebar .submenu-item.active {
  background: #e6f3ff;
  color: #4A90E2;
  border-left-color: #4A90E2;
  font-weight: 500;
}

.settings-sidebar .submenu-item .el-icon {
  font-size: 13px;
}
</style>