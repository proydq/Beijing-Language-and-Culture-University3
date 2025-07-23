<template>
  <div class="classroom-borrow">
    <el-row>
      <el-col :span="6" class="sidebar">
        <el-tree
          :data="buildingTree"
          node-key="label"
          default-expand-all
          @node-click="handleTreeClick"
        />
      </el-col>
      <el-col :span="18" class="content">
        <div class="search-bar">
          <el-form :model="searchForm" inline>
            <el-form-item label="">
              <el-input v-model="searchForm.keyword" placeholder="请输入教室名称" />
            </el-form-item>
            <el-form-item label="">
              <el-select v-model="searchForm.sort" placeholder="排序方式">
                <el-option
                  v-for="item in sortOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary">导出当前页</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="success">导出全部页</el-button>
            </el-form-item>
          </el-form>
        </div>
        <el-table :data="sortedData" stripe style="width: 100%">
          <el-table-column prop="name" label="预约教室" />
          <el-table-column prop="times" label="预约次数（次）" />
          <el-table-column prop="duration" label="预约累计时长（分）" />
          <el-table-column prop="people" label="累计预约人数（人）" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="primary" size="small" @click="showDetail(scope.row)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>

    <el-dialog v-model="detailVisible" title="教室预约详情" width="500px">
      <el-descriptions v-if="selectedRoom" :column="1" border>
        <el-descriptions-item label="教室名称">
          {{ selectedRoom.name }}
        </el-descriptions-item>
        <el-descriptions-item label="所属楼栋">
          {{ selectedRoom.building }}
        </el-descriptions-item>
        <el-descriptions-item label="总预约次数">
          {{ selectedRoom.times }}
        </el-descriptions-item>
        <el-descriptions-item label="累计时长">
          {{ selectedRoom.duration }} 分
        </el-descriptions-item>
        <el-descriptions-item label="累计人数"> {{ selectedRoom.people }} 人 </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const buildingTree = [
  {
    label: '达才楼',
    children: [
      { label: 'B2' },
      { label: 'B1' },
      { label: '1F' },
      { label: '2F' },
      { label: '3F' },
      { label: '4F' },
      { label: '5F' },
      { label: '6F' },
    ],
  },
  { label: '成彦楼' },
  { label: '笃行楼' },
  { label: '博雅楼' },
  { label: '正芯楼' },
  { label: '学华楼' },
]

const tableData = ref([
  {
    id: 1,
    name: '达才楼101',
    building: '达才楼',
    times: 12,
    duration: 300,
    people: 260,
  },
  {
    id: 2,
    name: '达才楼102',
    building: '达才楼',
    times: 8,
    duration: 200,
    people: 160,
  },
  {
    id: 3,
    name: '成彦楼201',
    building: '成彦楼',
    times: 5,
    duration: 150,
    people: 120,
  },
  {
    id: 4,
    name: '笃行楼301',
    building: '笃行楼',
    times: 10,
    duration: 240,
    people: 180,
  },
  {
    id: 5,
    name: '博雅楼401',
    building: '博雅楼',
    times: 7,
    duration: 210,
    people: 140,
  },
  {
    id: 6,
    name: '正芯楼501',
    building: '正芯楼',
    times: 9,
    duration: 190,
    people: 170,
  },
  {
    id: 7,
    name: '学华楼601',
    building: '学华楼',
    times: 6,
    duration: 160,
    people: 110,
  },
  {
    id: 8,
    name: '达才楼103',
    building: '达才楼',
    times: 3,
    duration: 90,
    people: 50,
  },
  {
    id: 9,
    name: '笃行楼302',
    building: '笃行楼',
    times: 4,
    duration: 110,
    people: 80,
  },
  {
    id: 10,
    name: '博雅楼402',
    building: '博雅楼',
    times: 11,
    duration: 260,
    people: 230,
  },
])

const searchForm = ref({
  keyword: '',
  sort: 'times',
})

const sortOptions = [
  { label: '预约次数倒序', value: 'times' },
  { label: '累计时长倒序', value: 'duration' },
  { label: '累计人数倒序', value: 'people' },
]

const selectedBuilding = ref('')

function handleTreeClick(data) {
  selectedBuilding.value = data.label
}

const filteredData = computed(() => {
  return tableData.value.filter((item) => {
    const matchBuilding = selectedBuilding.value
      ? item.building.includes(selectedBuilding.value)
      : true
    const matchName = searchForm.value.keyword ? item.name.includes(searchForm.value.keyword) : true
    return matchBuilding && matchName
  })
})

const sortedData = computed(() => {
  return [...filteredData.value].sort((a, b) => b[searchForm.value.sort] - a[searchForm.value.sort])
})

const detailVisible = ref(false)
const selectedRoom = ref(null)

function showDetail(row) {
  selectedRoom.value = row
  detailVisible.value = true
}
</script>

<style scoped>
.classroom-borrow {
  padding: 20px;
}
.sidebar {
  max-width: 250px;
}
.content {
  padding-left: 20px;
}
.search-bar {
  margin-bottom: 16px;
}
</style>
