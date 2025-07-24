<template>
  <div class="search-area">
    <el-form :model="searchForm" inline>
      <el-form-item label="姓名:">
        <el-input 
          v-model="searchForm.realName" 
          placeholder="请输入姓名" 
          clearable 
          style="width: 180px;"
        />
      </el-form-item>
      <el-form-item label="手机号:">
        <el-input 
          v-model="searchForm.phone" 
          placeholder="请输入手机号" 
          clearable 
          style="width: 180px;"
        />
      </el-form-item>
      <el-form-item label="工号:">
        <el-input 
          v-model="searchForm.jobNumber" 
          placeholder="请输入工号" 
          clearable 
          style="width: 150px;"
        />
      </el-form-item>
      <el-form-item label="状态:">
        <el-select 
          v-model="searchForm.status" 
          placeholder="请选择状态" 
          clearable 
          style="width: 120px;"
        >
          <el-option label="正常" value="正常" />
          <el-option label="禁用" value="禁用" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { reactive } from 'vue'

export default {
  name: 'UserSearchForm',
  emits: ['search', 'reset'],
  setup(props, { emit }) {
    const searchForm = reactive({
      realName: '',
      phone: '',
      jobNumber: '',
      status: ''
    })

    const handleSearch = () => {
      emit('search', searchForm)
    }

    const handleReset = () => {
      Object.assign(searchForm, {
        realName: '',
        phone: '',
        jobNumber: '',
        status: ''
      })
      emit('reset')
    }

    return {
      searchForm,
      handleSearch,
      handleReset
    }
  }
}
</script>

<style scoped>
.search-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.search-area :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 20px;
}

.search-area :deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}
</style>