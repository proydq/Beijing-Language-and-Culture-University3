<template>
  <div class="settings-management">
    <div class="settings-layout">
      <!-- 左侧设置菜单 -->
      <SettingsSidebar
        v-model:activeSettingType="activeSettingType"
        v-model:expandedGroups="expandedGroups"
      />

      <!-- 右侧主要内容区域 -->
      <div class="settings-main-content">
        <div class="settings-page">
          <!-- 预约人员设置 -->
          <BookingPersonnelSettings 
            v-if="activeSettingType === 'booking_personnel'"
            @save-settings="handleSaveSettings"
          />

          <!-- 预约时间设置 -->
          <BookingTimeSettings 
            v-else-if="activeSettingType === 'booking_time_settings'"
            @save-settings="handleSaveSettings"
          />

          <!-- 连续预约设置 -->
          <ContinuousBookingSettings 
            v-else-if="activeSettingType === 'continuous_booking'"
            @save-settings="handleSaveSettings"
          />

          <!-- 方案管理 -->
          <SchemeManagement 
            v-else-if="activeSettingType === 'scheme_management'"
            @save-settings="handleSaveSettings"
          />

          <!-- 回收站 -->
          <RecycleBin 
            v-else-if="activeSettingType === 'recycle_bin'"
            @save-settings="handleSaveSettings"
          />

          <!-- 违规配置 -->
          <ViolationSettings 
            v-else-if="activeSettingType === 'violation_config'"
            @save-settings="handleSaveSettings"
          />

          <!-- 黑名单管理 -->
          <BlacklistManagement 
            v-else-if="activeSettingType === 'blacklist_management'"
            @save-settings="handleSaveSettings"
          />

          <!-- 默认占位符 -->
          <div v-else class="setting-section">
            <div class="content-placeholder">
              <el-icon size="64" color="#ccc"><setting /></el-icon>
              <h3>请从左侧菜单选择要配置的功能模块</h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Setting } from '@element-plus/icons-vue'

// 导入拆分后的组件
import SettingsSidebar from './SettingsSidebar.vue'
import BookingPersonnelSettings from './BookingPersonnelSettings.vue'
import BookingTimeSettings from './BookingTimeSettings.vue'
import ContinuousBookingSettings from './ContinuousBookingSettings.vue'
import SchemeManagement from './SchemeManagement.vue'
import RecycleBin from './RecycleBin.vue'
import ViolationSettings from './ViolationSettings.vue'
import BlacklistManagement from './BlacklistManagement.vue'

export default {
  name: 'SettingsManagement',
  components: {
    Setting,
    SettingsSidebar,
    BookingPersonnelSettings,
    BookingTimeSettings,
    ContinuousBookingSettings,
    SchemeManagement,
    RecycleBin,
    ViolationSettings,
    BlacklistManagement
  },
  props: {
    settingsData: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['save-settings'],
  setup(props, { emit }) {
    const activeSettingType = ref('booking_personnel')
    const expandedGroups = ref(['house_permission'])

    const handleSaveSettings = (settingsData) => {
      emit('save-settings', settingsData)
      ElMessage.success('设置保存成功')
    }

    return {
      activeSettingType,
      expandedGroups,
      handleSaveSettings
    }
  }
}
</script>

<style scoped>
.settings-management {
  min-height: calc(100vh - 120px);
  background: #f9f9f9;
}

.settings-layout {
  display: flex;
  height: calc(100vh - 120px);
}

/* 主要内容区域 */
.settings-main-content {
  flex: 1;
  background: #f9f9f9;
  overflow-y: auto;
}

.settings-page {
  padding: 20px;
  min-height: 100%;
}

.setting-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.content-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-layout {
    flex-direction: column;
  }
}
</style>