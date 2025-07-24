<template>
  <el-dialog v-model="visible" title="权限人员" width="600px" destroy-on-close>
    <div class="filter-area">
      <el-select v-model="selectedType" style="width: 120px">
        <el-option label="全部" value="all" />
        <el-option label="老师" value="teacher" />
        <el-option label="学生" value="student" />
      </el-select>
      <el-input
        v-model="searchKeyword"
        placeholder="按姓名或工号搜索"
        style="width: 200px"
        clearable
      >
        <template #prefix>
          <el-icon><search /></el-icon>
        </template>
      </el-input>
    </div>
    <el-table :data="pagedUsers" style="width: 100%" border>
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="jobNumber" label="工号" width="160" />
      <el-table-column prop="department" label="所属部门" />
    </el-table>
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredUsers.length"
        layout="prev, pager, next"
      />
    </div>
  </el-dialog>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'

export default {
  name: 'AuthorityUserDialog',
  components: { Search },
  props: {
    modelValue: Boolean,
    users: {
      type: Array,
      default: () => []
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const visible = ref(props.modelValue)
    watch(
      () => props.modelValue,
      (val) => {
        visible.value = val
      }
    )
    watch(visible, (val) => emit('update:modelValue', val))

    const selectedType = ref('all')
    const searchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(10)

    const filteredUsers = computed(() => {
      let list = props.users
      if (selectedType.value !== 'all') {
        list = list.filter((u) => u.type === selectedType.value)
      }
      if (searchKeyword.value) {
        const kw = searchKeyword.value.toLowerCase()
        list = list.filter(
          (u) =>
            u.name.includes(searchKeyword.value) ||
            String(u.jobNumber).toLowerCase().includes(kw)
        )
      }
      return list
    })

    const pagedUsers = computed(() => {
      const start = (currentPage.value - 1) * pageSize.value
      return filteredUsers.value.slice(start, start + pageSize.value)
    })

    watch([selectedType, searchKeyword, () => props.users], () => {
      currentPage.value = 1
    })

    return {
      visible,
      selectedType,
      searchKeyword,
      currentPage,
      pageSize,
      filteredUsers,
      pagedUsers
    }
  }
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
