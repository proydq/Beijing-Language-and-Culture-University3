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
        <div class="sidebar-header">
          <h3>楼栋架构</h3>
        </div>
        <div class="sidebar-content">
          <el-input
            v-model="sidebarSearch"
            placeholder="搜索楼栋"
            :prefix-icon="Search"
            clearable
            class="search-input"
          />
          <el-tree
            :data="treeData"
            :props="treeProps"
            node-key="id"
            :default-expanded-keys="['all']"
            :highlight-current="true"
            @node-click="handleNodeClick"
            class="structure-tree"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ data.label }}</span>
              </span>
            </template>
          </el-tree>
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
import { ref, computed, onMounted } from 'vue'
import { Document, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAreaTree } from '@/api/area'
import Sidebar from '../Layout/Sidebar.vue'
import MyBookings from './MyBookings.vue'
import AllBookings from './AllBookings.vue'
import RoomReservation from './RoomReservation.vue'

export default {
  name: 'BookingManagement',
  components: {
    Document,
    Search,
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
    const sidebarSearch = ref('')
    const treeData = ref([])
    const selectedBuildingArea = ref(null)
    
    const treeProps = {
      children: 'children',
      label: 'label'
    }

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
    
    // 加载楼栋架构数据
    const loadBuildingTree = async () => {
      try {
        const response = await getAreaTree()
        if (response.code === 200) {
          // 将后端返回的区域树数据转换为前端需要的格式
          const formatTreeData = (nodes) => {
            return nodes.map(node => ({
              label: node.areaName,
              id: node.id,
              type: node.type,
              children: node.children && node.children.length > 0 ? formatTreeData(node.children) : undefined
            }))
          }
          treeData.value = formatTreeData(response.data || [])
        } else {
          ElMessage.error(response.message || '获取楼栋架构失败')
        }
      } catch (error) {
        console.error('获取楼栋架构失败:', error)
        ElMessage.error('获取楼栋架构失败')
      }
    }
    
    // 处理树节点点击
    const handleNodeClick = (data) => {
      // 保存选中的区域信息
      selectedBuildingArea.value = {
        id: data.id,
        name: data.label
      }
      // 更新activeCategory和activeFloor
      if (data.type === 'building') {
        activeCategory.value = data.label
        activeFloor.value = ''
      } else if (data.type === 'floor') {
        // 如果点击的是楼层，需要设置楼栋和楼层
        // 这里需要根据实际的树结构来获取父节点信息
        activeFloor.value = data.label
      }
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
    
    // 页面初始化
    onMounted(() => {
      loadBuildingTree()
    })

    return {
      menuItems,
      buildings,
      floors,
      activeMenuItem,
      activeCategory,
      activeFloor,
      sidebarSearch,
      treeData,
      treeProps,
      selectedBuildingArea,
      setActiveMenuItem,
      setActiveCategory,
      setActiveFloor,
      loadBuildingTree,
      handleNodeClick,
      filteredRooms,
      handleEdit,
      handleApprove,
      handleBookRoom,
      Search
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
  width: 200px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-right: 20px;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.sidebar-content {
  padding: 15px;
}

.search-input {
  margin-bottom: 10px;
}

.structure-tree {
  font-size: 14px;
}

.structure-tree :deep(.el-tree-node__content) {
  height: 36px;
  padding: 0 15px;
}

.structure-tree :deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

.structure-tree :deep(.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
  color: #409eff;
}

.tree-node {
  display: flex;
  align-items: center;
  width: 100%;
  font-size: 14px;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  background: #f9f9f9;
  overflow-y: auto;
}
</style>