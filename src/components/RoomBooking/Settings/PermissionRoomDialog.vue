<template>
  <el-dialog v-model="visible" title="可预约房屋/教室" width="600px" destroy-on-close>
    <div class="filter-area">
      <el-select v-model="selectedBuilding" style="width: 150px">
        <el-option label="全部" value="all" />
        <el-option v-for="b in buildingOptions" :key="b" :label="b" :value="b" />
      </el-select>
      <el-input v-model="searchKeyword" placeholder="按教室名称搜索" clearable style="width: 200px">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>
    <el-table :data="pagedRooms" style="width: 100%" border>
      <el-table-column prop="roomName" label="预约教室" />
      <el-table-column prop="roomCode" label="房间号" width="100" />
      <el-table-column prop="building" label="所属楼栋" />
    </el-table>
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredRooms.length"
        layout="prev, pager, next"
      />
    </div>
  </el-dialog>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'PermissionRoomDialog',
  components: { Search },
  props: {
    modelValue: Boolean,
    rooms: {
      type: Array,
      default: () => [],
    },
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const visible = ref(props.modelValue)
    watch(
      () => props.modelValue,
      (val) => {
        visible.value = val
      },
    )
    watch(visible, (val) => emit('update:modelValue', val))

    const selectedBuilding = ref('all')
    const searchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)

    const buildingOptions = computed(() => {
      const set = new Set(props.rooms.map((r) => r.building))
      return Array.from(set)
    })

    const filteredRooms = computed(() => {
      let list = props.rooms
      if (selectedBuilding.value !== 'all') {
        list = list.filter((r) => r.building === selectedBuilding.value)
      }
      if (searchKeyword.value) {
        list = list.filter((r) => r.roomName.includes(searchKeyword.value))
      }
      return list
    })

    const pagedRooms = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      return filteredRooms.value.slice(start, start + pageSize.value)
    })

    watch([selectedBuilding, searchKeyword, () => props.rooms], () => {
      currentPage.value = 1
    })

    return {
      visible,
      selectedBuilding,
      searchKeyword,
      currentPage,
      pageSize,
      buildingOptions,
      filteredRooms,
      pagedRooms,
    }
  },
}
</script>

<style scoped>
.filter-area {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.pagination-section {
  display: flex;
  justify-content: center;
  padding-top: 20px;
}
</style>
