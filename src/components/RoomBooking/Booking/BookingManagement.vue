<template>
  <div class="booking-management">
    <div class="booking-layout">
      <!-- 左侧功能菜单 -->
      <Sidebar
        title="功能菜单"
        :menu-items="menuItems"
        :active-item="activeMenuItem"
        @menu-click="setActiveMenuItem"
      />

      <!-- 中间楼宇分类（仅在房间预约时显示） -->
      <div v-if="activeMenuItem === '房间预约'" class="middle-sidebar">
        <div class="category-header">
          <h4>楼宇分类</h4>
        </div>
        <div class="category-menu">
          <div 
            :class="['category-item', { active: activeCategory === '全部' }]"
            @click="setActiveCategory('全部')"
          >
            全部
          </div>
          <div 
            v-for="building in buildings" 
            :key="building"
            :class="['category-item', { active: activeCategory === building }]"
            @click="setActiveCategory(building)"
          >
            {{ building }}
          </div>
        </div>
        
        <!-- 楼层筛选 -->
        <div v-if="activeCategory !== '全部'" class="floor-filter">
          <h5>楼层</h5>
          <div class="floor-list">
            <div 
              v-for="floor in floors" 
              :key="floor"
              :class="['floor-item', { active: activeFloor === floor }]"
              @click="setActiveFloor(floor)"
            >
              {{ floor }}
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧主内容区域 -->
      <div class="main-content">
        <MyBookings 
          v-if="activeMenuItem === '我的预约'"
          :booking-data="bookingData"
          @edit="handleEdit"
          @approve="handleApprove"
        />
        
        <AllBookings 
          v-else-if="activeMenuItem === '全部借用'"
          :booking-data="allBookingData"
          @edit="handleEdit"
          @approve="handleApprove"
        />
        
        <RoomReservation 
          v-else-if="activeMenuItem === '房间预约'"
          :rooms="filteredRooms"
          :active-category="activeCategory"
          :active-floor="activeFloor"
          @book-room="handleBookRoom"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { Document } from '@element-plus/icons-vue'
import Sidebar from '../Layout/Sidebar.vue'
import MyBookings from './MyBookings.vue'
import AllBookings from './AllBookings.vue'
import RoomReservation from './RoomReservation.vue'

export default {
  name: 'BookingManagement',
  components: {
    Document,
    Sidebar,
    MyBookings,
    AllBookings,
    RoomReservation
  },
  props: {
    bookingData: {
      type: Array,
      default: () => []
    },
    allBookingData: {
      type: Array,
      default: () => []
    },
    rooms: {
      type: Array,
      default: () => []
    }
  },
  emits: ['edit', 'approve', 'book-room'],
  setup(props, { emit }) {
    const menuItems = ['我的预约', '全部借用', '房间预约']
    const buildings = ['达力楼', '明德楼', '博学楼']
    const floors = ['1F', '2F', '3F', '4F', '5F', '6F', 'B1', 'B2']
    
    const activeMenuItem = ref('我的预约')
    const activeCategory = ref('全部')
    const activeFloor = ref('')

    const setActiveMenuItem = (item) => {
      activeMenuItem.value = item
      // 切换菜单时重置分类和楼层
      activeCategory.value = '全部'
      activeFloor.value = ''
    }

    const setActiveCategory = (category) => {
      activeCategory.value = category
      activeFloor.value = ''
    }

    const setActiveFloor = (floor) => {
      activeFloor.value = floor
    }

    // 过滤房间
    const filteredRooms = computed(() => {
      let filtered = props.rooms

      if (activeCategory.value !== '全部') {
        filtered = filtered.filter(room => room.building === activeCategory.value)
      }

      if (activeFloor.value) {
        filtered = filtered.filter(room => room.floor === activeFloor.value)
      }

      return filtered
    })

    const handleEdit = (row) => {
      emit('edit', row)
    }

    const handleApprove = (row) => {
      emit('approve', row)
    }

    const handleBookRoom = (room) => {
      emit('book-room', room)
    }

    return {
      menuItems,
      buildings,
      floors,
      activeMenuItem,
      activeCategory,
      activeFloor,
      setActiveMenuItem,
      setActiveCategory,
      setActiveFloor,
      filteredRooms,
      handleEdit,
      handleApprove,
      handleBookRoom
    }
  }
}
</script>

<style scoped>
.booking-management {
  min-height: calc(100vh - 120px);
}

.booking-layout {
  display: flex;
  height: calc(100vh - 120px);
}

/* 左侧菜单样式已移到Sidebar组件中 */

/* 中间楼宇分类 */
.middle-sidebar {
  width: 180px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 15px;
  overflow-y: auto;
}

.category-header h4 {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.category-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: #666;
  font-size: 13px;
  margin-bottom: 2px;
}

.category-item:hover {
  background: #f5f5f5;
  color: #333;
}

.category-item.active {
  background: #4A90E2;
  color: white;
}

.floor-filter {
  margin-top: 20px;
}

.floor-filter h5 {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #333;
  font-weight: 600;
}

.floor-list {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.floor-item {
  padding: 4px 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: #666;
  font-size: 12px;
  border: 1px solid #e8e8e8;
  text-align: center;
  min-width: 35px;
}

.floor-item:hover {
  background: #f5f5f5;
  color: #333;
}

.floor-item.active {
  background: #4A90E2;
  color: white;
  border-color: #4A90E2;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  background: #f9f9f9;
  overflow-y: auto;
}
</style>