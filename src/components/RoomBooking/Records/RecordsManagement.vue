<template>
  <div class="records-management">
    <el-menu
      class="records-menu"
      :default-active="activeSubTab"
      @select="handleSubMenuClick"
    >
      <el-sub-menu index="booking">
        <template #title>预约记录</template>
        <el-menu-item index="booking-records">数据借用记录</el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="access">
        <template #title>出入记录</template>
        <el-menu-item index="access-classroom">教室出入记录</el-menu-item>
        <el-menu-item index="access-remote">远程开门记录</el-menu-item>
      </el-sub-menu>
    </el-menu>

    <div class="right-panel">
      <BookingRecords v-if="activeSubTab === 'booking-records'" />
      <AccessClassroomRecords v-else-if="activeSubTab === 'access-classroom'" />
      <AccessRemoteRecords v-else-if="activeSubTab === 'access-remote'" />
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
  display: flex;
  height: 100%;
}

.records-menu {
  width: 200px;
  border-right: 1px solid #e5e5e5;
}

.right-panel {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
