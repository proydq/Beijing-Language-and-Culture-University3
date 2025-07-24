<template>
  <el-dialog
    v-model:visible="visible"
    :title="isEdit ? '编辑权限配置' : '新增权限配置'"
    width="900px"
    destroy-on-close
    :before-close="handleClose"
  >
    <div class="permission-container">
      <!-- Left: user list -->
      <div class="permission-side">
        <h3 class="side-title">权限人员名单</h3>
        <div class="search-bar">
          <el-select v-model="userFilter.type" class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="老师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
          <el-input
            v-model="userFilter.keyword"
            placeholder="请输入姓名/工号"
            class="filter-input"
            @keyup.enter="searchUsers"
            clearable
          >
            <template #suffix>
              <el-button
                :icon="Search"
                text
                @click="searchUsers"
              />
            </template>
          </el-input>
          <el-button type="primary" @click="handleAddUser">添加</el-button>
        </div>
        <div class="action-bar">
          <el-button
            type="danger"
            :disabled="selectedUserRows.length === 0"
            @click="removeSelectedUsers"
          >批量移除</el-button>
        </div>
        <el-table
          :data="pagedUsers"
          border
          style="width: 100%"
          @selection-change="userSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="jobNumber" label="工号" width="160" />
          <el-table-column prop="department" label="所属部门" />
        </el-table>
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="userPage.currentPage"
            v-model:page-size="userPage.pageSize"
            :total="filteredUsers.length"
            layout="prev, pager, next"
          />
        </div>
      </div>

      <!-- Right: room list -->
      <div class="permission-side">
        <h3 class="side-title">房屋名单</h3>
        <div class="search-bar">
          <el-select v-model="roomFilter.type" class="filter-select">
            <el-option label="全部" value="all" />
            <el-option label="老师" value="teacher" />
            <el-option label="学生" value="student" />
          </el-select>
          <el-input
            v-model="roomFilter.keyword"
            placeholder="请输入实验室"
            class="filter-input"
            @keyup.enter="searchRooms"
            clearable
          >
            <template #suffix>
              <el-button
                :icon="Search"
                text
                @click="searchRooms"
              />
            </template>
          </el-input>
          <el-button type="primary" @click="handleAddRoom">添加</el-button>
        </div>
        <div class="action-bar">
          <el-button
            type="danger"
            :disabled="selectedRoomRows.length === 0"
            @click="removeSelectedRooms"
          >批量移除</el-button>
        </div>
        <el-table
          :data="pagedRooms"
          border
          style="width: 100%"
          @selection-change="roomSelectionChange"
        >
          <el-table-column type="selection" width="50" />
          <el-table-column prop="roomName" label="预约教室" />
          <el-table-column prop="roomCode" label="房间号" width="120" />
          <el-table-column prop="building" label="所属楼栋" />
        </el-table>
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="roomPage.currentPage"
            v-model:page-size="roomPage.pageSize"
            :total="filteredRooms.length"
            layout="prev, pager, next"
          />
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  visible: Boolean,
  isEdit: Boolean,
  userList: {
    type: Array,
    default: () => []
  },
  roomList: {
    type: Array,
    default: () => []
  },
  selectedUsers: {
    type: Array,
    default: () => []
  },
  selectedRooms: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:visible', 'submit'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const userFilter = reactive({
  type: 'all',
  keyword: ''
})
const roomFilter = reactive({
  type: 'all',
  keyword: ''
})

const userPage = reactive({
  currentPage: 1,
  pageSize: 10
})
const roomPage = reactive({
  currentPage: 1,
  pageSize: 10
})

const userData = ref([])
const roomData = ref([])

const selectedUserRows = ref([])
const selectedRoomRows = ref([])

// watch visible prop
watch(
  () => props.visible,
  (val) => {
    if (val) {
      userData.value = [...props.selectedUsers]
      roomData.value = [...props.selectedRooms]
    }
  }
)

const filterListByTypeAndKeyword = (list, filter) => {
  let result = list
  if (filter.type !== 'all') {
    result = result.filter((item) => item.type === filter.type)
  }
  if (filter.keyword) {
    const kw = filter.keyword.toLowerCase()
    result = result.filter(
      (item) =>
        (item.name && item.name.includes(filter.keyword)) ||
        (item.jobNumber && String(item.jobNumber).toLowerCase().includes(kw)) ||
        (item.roomName && item.roomName.includes(filter.keyword)) ||
        (item.roomCode && String(item.roomCode).toLowerCase().includes(kw)) ||
        (item.building && item.building.includes(filter.keyword))
    )
  }
  return result
}

const filteredUsers = computed(() =>
  filterListByTypeAndKeyword(userData.value, userFilter)
)
const filteredRooms = computed(() =>
  filterListByTypeAndKeyword(roomData.value, roomFilter)
)

const pagedUsers = computed(() => {
  const start = (userPage.currentPage - 1) * userPage.pageSize
  return filteredUsers.value.slice(start, start + userPage.pageSize)
})

const pagedRooms = computed(() => {
  const start = (roomPage.currentPage - 1) * roomPage.pageSize
  return filteredRooms.value.slice(start, start + roomPage.pageSize)
})

const searchUsers = () => {
  userPage.currentPage = 1
}
const searchRooms = () => {
  roomPage.currentPage = 1
}

const userSelectionChange = (val) => {
  selectedUserRows.value = val
}
const roomSelectionChange = (val) => {
  selectedRoomRows.value = val
}

const removeSelectedUsers = () => {
  userData.value = userData.value.filter(
    (item) => !selectedUserRows.value.includes(item)
  )
  selectedUserRows.value = []
}

const removeSelectedRooms = () => {
  roomData.value = roomData.value.filter(
    (item) => !selectedRoomRows.value.includes(item)
  )
  selectedRoomRows.value = []
}

const handleAddUser = () => {
  const existingIds = new Set(userData.value.map((u) => u.id))
  const candidate = props.userList.find((u) => !existingIds.has(u.id))
  if (candidate) {
    userData.value.push(candidate)
  }
}

const handleAddRoom = () => {
  const existingIds = new Set(roomData.value.map((r) => r.id))
  const candidate = props.roomList.find((r) => !existingIds.has(r.id))
  if (candidate) {
    roomData.value.push(candidate)
  }
}

const handleClose = () => {
  emit('update:visible', false)
}

const handleSubmit = () => {
  emit('submit', {
    users: userData.value,
    rooms: roomData.value
  })
  handleClose()
}
</script>

<style scoped>
.permission-container {
  display: flex;
  gap: 20px;
  padding-top: 10px;
}
.permission-side {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.side-title {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: 600;
}
.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}
.filter-select {
  width: 100px;
}
.filter-input {
  flex: 1;
}
.action-bar {
  margin-bottom: 10px;
}
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 10px 0;
}
.dialog-footer {
  text-align: right;
}
</style>
