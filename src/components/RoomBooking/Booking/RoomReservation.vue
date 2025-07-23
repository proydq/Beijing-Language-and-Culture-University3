<template>
  <div class="room-reservation">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索房间名称..."
        clearable
        style="width: 300px;"
      >
        <template #prefix>
          <el-icon><search /></el-icon>
        </template>
      </el-input>
    </div>

    <!-- 房间网格 -->
    <div class="room-grid">
      <div
        v-for="room in filteredRooms"
        :key="room.id"
        :class="['room-card', { unavailable: !room.available }]"
      >
        <div class="room-header">
          <h4>{{ room.name }}</h4>
          <div class="room-status">
            <el-tag 
              :type="room.available ? 'success' : 'danger'" 
              size="small"
            >
              {{ room.available ? '可预约' : '不可用' }}
            </el-tag>
          </div>
        </div>
        
        <div class="room-info">
          <div class="info-item">
            <span class="label">容纳人数：</span>
            <span class="value">{{ room.capacity }}</span>
          </div>
          <div class="info-item">
            <span class="label">楼宇：</span>
            <span class="value">{{ room.building }}</span>
          </div>
          <div class="info-item">
            <span class="label">楼层：</span>
            <span class="value">{{ room.floor }}</span>
          </div>
        </div>

        <div class="room-actions">
          <el-button
            :type="room.available ? 'primary' : 'info'"
            :disabled="!room.available"
            @click="$emit('book-room', room)"
            style="width: 100%;"
          >
            {{ room.available ? '立即预约' : '暂不可用' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 无数据提示 -->
    <div v-if="filteredRooms.length === 0" class="no-data">
      <el-empty description="暂无符合条件的房间" />
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'RoomReservation',
  components: {
    Search
  },
  props: {
    rooms: {
      type: Array,
      default: () => []
    },
    activeCategory: {
      type: String,
      default: '全部'
    },
    activeFloor: {
      type: String,
      default: ''
    }
  },
  emits: ['book-room'],
  setup(props, { emit }) {
    const searchKeyword = ref('')

    // 过滤房间
    const filteredRooms = computed(() => {
      let filtered = props.rooms

      // 按楼宇筛选
      if (props.activeCategory !== '全部') {
        filtered = filtered.filter(room => room.building === props.activeCategory)
      }

      // 按楼层筛选
      if (props.activeFloor) {
        filtered = filtered.filter(room => room.floor === props.activeFloor)
      }

      // 按关键词搜索
      if (searchKeyword.value) {
        filtered = filtered.filter(room => 
          room.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
        )
      }

      return filtered
    })

    return {
      searchKeyword,
      filteredRooms
    }
  }
}
</script>

<style scoped>
.room-reservation {
  flex: 1;
  padding: 20px;
  background: #f9f9f9;
  overflow-y: auto;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.room-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  border: 2px solid transparent;
}

.room-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.room-card.unavailable {
  background: #fafafa;
  border-color: #f0f0f0;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.room-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.room-status {
  margin-left: 10px;
}

.room-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-item .label {
  color: #666;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

.room-actions {
  display: flex;
  justify-content: center;
}

.room-actions .el-button {
  border-radius: 20px;
  font-weight: 500;
}

.no-data {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
</style>