<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑权限配置' : '新增权限配置'"
    width="900px"
    destroy-on-close
  >
    <div class="edit-container">
      <!-- 左侧权限人员列表 -->
      <div class="pane">
        <div class="pane-header">
          <div class="filters">
            <el-select v-model="userFilter.type" size="small" style="width: 100px">
              <el-option label="全部" value="all" />
              <el-option label="老师" value="teacher" />
              <el-option label="学生" value="student" />
            </el-select>
            <el-input
              v-model="userFilter.keyword"
              size="small"
              placeholder="搜索姓名或工号"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="actions">
            <el-button v-if="userSelections.length" size="small" @click="removeSelectedUsers">批量移除</el-button>
            <el-button type="primary" size="small" @click="addUser">添加</el-button>
          </div>
        </div>
        <el-table
          :data="pagedUsers"
          style="width: 100%"
          border
          @selection-change="userSelections = $event"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="jobNumber" label="工号" width="160" />
          <el-table-column prop="department" label="所属部门" />
        </el-table>
        <el-pagination
          class="pager"
          v-model:current-page="userPage"
          v-model:page-size="userPageSize"
          :total="filteredUsers.length"
          layout="prev, pager, next"
        />
      </div>

      <!-- 右侧房屋列表 -->
      <div class="pane">
        <div class="pane-header">
          <div class="filters">
            <el-select v-model="roomFilter.building" size="small" style="width: 120px">
              <el-option label="全部" value="all" />
              <el-option
                v-for="b in buildingOptions"
                :key="b"
                :label="b"
                :value="b"
              />
            </el-select>
            <el-input
              v-model="roomFilter.keyword"
              size="small"
              placeholder="搜索房屋"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          <div class="actions">
            <el-button v-if="roomSelections.length" size="small" @click="removeSelectedRooms">批量移除</el-button>
            <el-button type="primary" size="small" @click="addRoom">添加</el-button>
          </div>
        </div>
        <el-table
          :data="pagedRooms"
          style="width: 100%"
          border
          @selection-change="roomSelections = $event"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="roomName" label="预约教室" />
          <el-table-column prop="roomCode" label="房间号" width="100" />
          <el-table-column prop="buildingName" label="所属楼" />
        </el-table>
        <el-pagination
          class="pager"
          v-model:current-page="roomPage"
          v-model:page-size="roomPageSize"
          :total="filteredRooms.length"
          layout="prev, pager, next"
        />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
import { ref, reactive, watch, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'PermissionEditDialog',
  components: { Search },
  props: {
    visible: Boolean,
    isEdit: Boolean,
    editData: {
      type: Object,
      default: () => ({})
    },
    allUsers: {
      type: Array,
      default: () => []
    },
    allRooms: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:visible', 'submit'],
  setup(props, { emit }) {
    const dialogVisible = ref(false)

    const selectedUsers = ref([])
    const selectedRooms = ref([])

    const userSelections = ref([])
    const roomSelections = ref([])

    watch(
      () => props.visible,
      (val) => {
        dialogVisible.value = val
        if (val) {
          selectedUsers.value = props.isEdit && props.editData?.users ? [...props.editData.users] : []
          selectedRooms.value = props.isEdit && props.editData?.rooms ? [...props.editData.rooms] : []
        }
      }
    )
    watch(dialogVisible, (val) => emit('update:visible', val))

    const userFilter = reactive({ type: 'all', keyword: '' })
    const roomFilter = reactive({ building: 'all', keyword: '' })

    const userPage = ref(1)
    const userPageSize = ref(5)
    const roomPage = ref(1)
    const roomPageSize = ref(5)

    const filteredUsers = computed(() => {
      let list = selectedUsers.value
      if (userFilter.type !== 'all') {
        list = list.filter((u) => u.type === userFilter.type)
      }
      if (userFilter.keyword) {
        const kw = userFilter.keyword.toLowerCase()
        list = list.filter(
          (u) =>
            u.name.includes(userFilter.keyword) ||
            String(u.jobNumber).toLowerCase().includes(kw)
        )
      }
      return list
    })

    const pagedUsers = computed(() => {
      const start = (userPage.value - 1) * userPageSize.value
      return filteredUsers.value.slice(start, start + userPageSize.value)
    })

    const buildingOptions = computed(() => {
      const set = new Set(selectedRooms.value.map((r) => r.buildingName))
      return Array.from(set)
    })

    const filteredRooms = computed(() => {
      let list = selectedRooms.value
      if (roomFilter.building !== 'all') {
        list = list.filter((r) => r.buildingName === roomFilter.building)
      }
      if (roomFilter.keyword) {
        list = list.filter((r) => r.roomName.includes(roomFilter.keyword))
      }
      return list
    })

    const pagedRooms = computed(() => {
      const start = (roomPage.value - 1) * roomPageSize.value
      return filteredRooms.value.slice(start, start + roomPageSize.value)
    })

    watch([selectedUsers, () => userFilter.type, () => userFilter.keyword], () => {
      userPage.value = 1
    })
    watch([selectedRooms, () => roomFilter.building, () => roomFilter.keyword], () => {
      roomPage.value = 1
    })

    const addUser = () => {
      const candidates = props.allUsers.filter(
        (u) => !selectedUsers.value.some((s) => s.jobNumber === u.jobNumber)
      )
      if (candidates.length) {
        selectedUsers.value.push(candidates[0])
      }
    }

    const addRoom = () => {
      const candidates = props.allRooms.filter(
        (r) => !selectedRooms.value.some((s) => s.roomCode === r.roomCode)
      )
      if (candidates.length) {
        selectedRooms.value.push(candidates[0])
      }
    }

    const removeSelectedUsers = () => {
      selectedUsers.value = selectedUsers.value.filter(
        (u) => !userSelections.value.includes(u)
      )
      userSelections.value = []
    }
    const removeSelectedRooms = () => {
      selectedRooms.value = selectedRooms.value.filter(
        (r) => !roomSelections.value.includes(r)
      )
      roomSelections.value = []
    }

    const handleCancel = () => {
      dialogVisible.value = false
    }

    const handleSubmit = () => {
      emit('submit', { users: selectedUsers.value, rooms: selectedRooms.value })
      dialogVisible.value = false
    }

    return {
      dialogVisible,
      userFilter,
      roomFilter,
      userPage,
      userPageSize,
      roomPage,
      roomPageSize,
      pagedUsers,
      pagedRooms,
      buildingOptions,
      userSelections,
      roomSelections,
      addUser,
      addRoom,
      removeSelectedUsers,
      removeSelectedRooms,
      handleCancel,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.edit-container {
  display: flex;
  gap: 20px;
}
.pane {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.pane-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.filters {
  display: flex;
  gap: 8px;
}
.actions {
  display: flex;
  gap: 8px;
  align-items: center;
}
.pager {
  margin-top: 10px;
  display: flex;
  justify-content: center;
}
</style>
