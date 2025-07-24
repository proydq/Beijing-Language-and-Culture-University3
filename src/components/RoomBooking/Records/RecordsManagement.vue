<template>
  <div class="records-management">
    <div class="records-layout">
      <div class="records-sidebar">
        <div class="sidebar-header">
          <h3>数据记录</h3>
        </div>
        <el-menu :default-active="activeSubTab" class="sidebar-menu" @select="handleSubMenuClick">
          <el-sub-menu index="booking">
            <template #title>预约记录</template>
            <el-menu-item index="booking-records">教室借用记录</el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="access">
            <template #title>出入记录</template>
            <el-menu-item index="access-classroom">教室出入记录</el-menu-item>
            <el-menu-item index="access-remote">远程开门记录</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
      <div class="right-panel">
        <BookingRecords v-if="activeSubTab === 'booking-records'" />
        <AccessClassroomRecords v-else-if="activeSubTab === 'access-classroom'" />
        <AccessRemoteRecords v-else-if="activeSubTab === 'access-remote'" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import BookingRecords from './BookingRecords.vue'
import AccessClassroomRecords from './AccessClassroomRecords.vue'
import AccessRemoteRecords from './AccessRemoteRecords.vue'

const activeSubTab = ref('booking-records')

function handleSubMenuClick(index) {
  activeSubTab.value = index
}
</script>

<style scoped>
.records-management {
  min-height: calc(100vh - 120px);
  background: #f9f9f9;
}

.records-layout {
  display: flex;
  height: calc(100vh - 120px);
}

.records-sidebar {
  width: 200px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 15px 18px;
  border-bottom: 1px solid #e8e8e8;
  background: #4a90e2;
  color: #fff;
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
}

.right-panel {
  flex: 1;
  background: #f9f9f9;
  padding: 20px;
  overflow-y: auto;
}
</style>
