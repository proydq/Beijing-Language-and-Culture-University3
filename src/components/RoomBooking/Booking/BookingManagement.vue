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
import { getMyBookings, getAllBookings, getAvailableRooms, createBooking } from '@/api/roomBookingManagement'
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
    // Remove props since we'll fetch data via API
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

    // Data storage
    const bookingData = ref([])
    const allBookingData = ref([])
    const rooms = ref([])
    const loading = ref(false)

    const treeProps = {
      children: 'children',
      label: 'label'
    }

    const setActiveMenuItem = async (item) => {
      activeMenuItem.value = item
      // 切换菜单时重置分类和楼层
      activeCategory.value = '全部'
      activeFloor.value = ''
      // 根据菜单项加载对应数据
      await loadDataForMenuItem(item)
    }

    const setActiveCategory = (category) => {
      activeCategory.value = category
      activeFloor.value = ''
    }

    const setActiveFloor = (floor) => {
      activeFloor.value = floor
    }

    // 根据菜单项加载数据
    const loadDataForMenuItem = async (menuItem) => {
      loading.value = true
      try {
        switch (menuItem) {
          case '我的预约':
            await loadMyBookings()
            break
          case '全部借用':
            await loadAllBookings()
            break
          case '房间预约':
            await loadAvailableRooms()
            break
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        ElMessage.error('加载数据失败')
      } finally {
        loading.value = false
      }
    }

    // 加载我的预约
    const loadMyBookings = async () => {
      try {
        const response = await getMyBookings({
          pageNumber: 1,
          pageSize: 50
        })
        if (response.code === 200) {
          bookingData.value = response.data.rows || []
        } else {
          ElMessage.error('获取我的预约列表失败')
        }
      } catch (error) {
        console.error('获取我的预约失败:', error)
        ElMessage.error('获取我的预约失败')
      }
    }

    // 加载全部借用
    const loadAllBookings = async () => {
      try {
        const response = await getAllBookings({
          pageNumber: 1,
          pageSize: 50
        })
        if (response.code === 200) {
          allBookingData.value = response.data.rows || []
        } else {
          ElMessage.error('获取全部借用列表失败')
        }
      } catch (error) {
        console.error('获取全部借用失败:', error)
        ElMessage.error('获取全部借用失败')
      }
    }

    // 加载可预约房间
    const loadAvailableRooms = async () => {
      try {
        const params = {}
        // 只有选择了具体区域时才传递 roomAreaId 参数
        if (selectedBuildingArea.value?.id && selectedBuildingArea.value.id !== 'all') {
          params.roomAreaId = selectedBuildingArea.value.id
        }
        
        const response = await getAvailableRooms(params)
        if (response.code === 200) {
          rooms.value = response.data || []
        } else {
          ElMessage.error('获取可预约房间失败')
        }
      } catch (error) {
        console.error('获取可预约房间失败:', error)
        ElMessage.error('获取可预约房间失败')
      }
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
          
          // 添加"全部"选项在树的顶部
          const formattedTreeData = formatTreeData(response.data || [])
          treeData.value = [
            {
              label: '全部区域',
              id: 'all',
              type: 'all',
              children: undefined
            },
            ...formattedTreeData
          ]
        } else {
          ElMessage.error(response.message || '获取楼栋架构失败')
        }
      } catch (error) {
        console.error('获取楼栋架构失败:', error)
        ElMessage.error('获取楼栋架构失败')
      }
    }

    // 处理树节点点击
    const handleNodeClick = async (data) => {
      // 保存选中的区域信息
      if (data.id === 'all') {
        // 选择全部区域时，清空选中区域
        selectedBuildingArea.value = null
        activeCategory.value = '全部'
        activeFloor.value = ''
      } else {
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
      
      // 如果当前在房间预约页面，重新加载房间数据
      if (activeMenuItem.value === '房间预约') {
        await loadAvailableRooms()
      }
    }

    // 过滤房间
    const filteredRooms = computed(() => {
      let filtered = rooms.value

      if (activeCategory.value !== '全部') {
        filtered = filtered.filter(room => room.building === activeCategory.value)
      }

      if (activeFloor.value) {
        filtered = filtered.filter(room => room.floor === activeFloor.value)
      }

      // 按可预约状态排序：可预约的在前面，不可预约的在后面
      filtered = filtered.sort((a, b) => {
        // 判断房间是否可预约（假设 available 或 status 字段表示可预约状态）
        const aAvailable = a.available !== false && a.status !== 'UNAVAILABLE' && a.status !== 'OCCUPIED'
        const bAvailable = b.available !== false && b.status !== 'UNAVAILABLE' && b.status !== 'OCCUPIED'
        
        // 可预约的排在前面
        if (aAvailable && !bAvailable) return -1
        if (!aAvailable && bAvailable) return 1
        
        // 如果可预约状态相同，按房间名称排序
        return (a.name || a.roomName || '').localeCompare(b.name || b.roomName || '')
      })

      return filtered
    })

    const handleEdit = (row) => {
      emit('edit', row)
    }

    const handleApprove = (row) => {
      emit('approve', row)
    }

    const handleBookRoom = async (bookingData) => {
      try {
        loading.value = true

        // 转换前端数据格式为后端需要的格式
        const createBookingRequest = {
          // 房间信息
          roomId: bookingData.room?.id || '',
          roomName: bookingData.room?.name || '',

          // 申请人信息
          applicant: bookingData.applicant || '',
          applicantId: '', // 后端会从SecurityUtil.getCurrentUserId()获取
          applicantType: 'TEACHER', // 默认为教师，可根据实际情况调整
          applicantPhone: '', // 如果需要可以添加到表单中
          applicantDepartment: '', // 如果需要可以添加到表单中

          // 预约信息
          bookingName: bookingData.bookingName || '',
          borrowTime: bookingData.borrowTime || '',

          // 时间信息 - 从borrowTime解析或使用默认值
          bookingStartTime: parseBookingStartTime(bookingData.borrowTime),
          bookingEndTime: parseBookingEndTime(bookingData.borrowTime),

          // 描述信息
          description: bookingData.description || '',
          reason: bookingData.remark || bookingData.description || '', // 申请理由使用备注详情

          // 人员信息
          participants: bookingData.participants || [],
          remark: bookingData.remark || '',
          approvers: bookingData.approvers || [],

          // 详细人员信息
          participantDetails: bookingData.participantDetails || [],
          approverDetails: bookingData.approverDetails || [],

          // 紧急程度
          urgency: 'NORMAL'
        }

        const response = await createBooking(createBookingRequest)
        if (response.code === 200) {
          ElMessage.success('预约申请提交成功')
          // 刷新当前数据
          await loadDataForMenuItem(activeMenuItem.value)
        } else {
          ElMessage.error(response.message || '预约申请提交失败')
        }
      } catch (error) {
        console.error('提交预约失败:', error)
        ElMessage.error('预约申请提交失败')
      } finally {
        loading.value = false
      }
    }

    // 解析借用时间获取开始时间
    const parseBookingStartTime = (borrowTime) => {
      if (!borrowTime) return null

      try {
        // 从borrowTime格式 "2025-03-03 08:00:00-08:45:00; 2025-03-03 08:55:00-09:40:00" 解析
        const firstTimeSlot = borrowTime.split(';')[0].trim()
        const [datePart, timePart] = firstTimeSlot.split(' ')
        const startTime = timePart.split('-')[0]
        // 转换为ISO 8601格式：yyyy-MM-ddTHH:mm:ss
        return `${datePart}T${startTime}`
      } catch (error) {
        console.warn('解析开始时间失败:', error)
        return null
      }
    }

    // 解析借用时间获取结束时间
    const parseBookingEndTime = (borrowTime) => {
      if (!borrowTime) return null

      try {
        // 从borrowTime格式解析最后一个时间段的结束时间
        const timeSlots = borrowTime.split(';').map(slot => slot.trim())
        const lastTimeSlot = timeSlots[timeSlots.length - 1]
        const [datePart, timePart] = lastTimeSlot.split(' ')
        const endTime = timePart.split('-')[1]
        // 转换为ISO 8601格式：yyyy-MM-ddTHH:mm:ss
        return `${datePart}T${endTime}`
      } catch (error) {
        console.warn('解析结束时间失败:', error)
        return null
      }
    }

    // 页面初始化
    onMounted(async () => {
      await loadBuildingTree()
      await loadDataForMenuItem(activeMenuItem.value)
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
      bookingData,
      allBookingData,
      rooms,
      loading,
      setActiveMenuItem,
      setActiveCategory,
      setActiveFloor,
      loadBuildingTree,
      loadDataForMenuItem,
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
