<template>
  <div class="permission-demo">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>权限系统演示</span>
        </div>
      </template>

      <!-- 权限信息展示 -->
      <el-descriptions title="当前用户权限信息" :column="2" border>
        <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ userRoles.join(', ') }}</el-descriptions-item>
        <el-descriptions-item label="权限数量" :span="2">{{ userPermissions.length }}</el-descriptions-item>
      </el-descriptions>

      <!-- 权限测试 -->
      <el-divider />
      <h3>权限指令测试</h3>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <h4>旧权限码测试</h4>
          <el-space direction="vertical" alignment="left" :size="20">
            <div v-permission="'USER_VIEW'">
              <el-tag type="success">USER_VIEW - 用户查看权限</el-tag>
            </div>
            <div v-permission="'USER_ADD'">
              <el-tag type="success">USER_ADD - 用户添加权限</el-tag>
            </div>
            <div v-permission="'USER_EDIT'">
              <el-tag type="success">USER_EDIT - 用户编辑权限</el-tag>
            </div>
            <div v-permission="'USER_DELETE'">
              <el-tag type="danger">USER_DELETE - 用户删除权限</el-tag>
            </div>
            <div v-permission="['USER_ADD', 'USER_EDIT']">
              <el-tag type="warning">USER_ADD 或 USER_EDIT - 任意权限</el-tag>
            </div>
            <div v-permission.all="['USER_VIEW', 'USER_ADD']">
              <el-tag type="info">USER_VIEW 且 USER_ADD - 全部权限</el-tag>
            </div>
          </el-space>
        </el-col>

        <el-col :span="12">
          <h4>新权限码测试</h4>
          <el-space direction="vertical" alignment="left" :size="20">
            <div v-permission="'system:user:view'">
              <el-tag type="success">system:user:view - 用户查看权限</el-tag>
            </div>
            <div v-permission="'system:user:create'">
              <el-tag type="success">system:user:create - 用户创建权限</el-tag>
            </div>
            <div v-permission="'system:user:update'">
              <el-tag type="success">system:user:update - 用户更新权限</el-tag>
            </div>
            <div v-permission="'system:user:delete'">
              <el-tag type="danger">system:user:delete - 用户删除权限</el-tag>
            </div>
            <div v-permission="'system:user:manage'">
              <el-tag type="primary">system:user:manage - 用户管理权限（包含所有子权限）</el-tag>
            </div>
          </el-space>
        </el-col>
      </el-row>

      <!-- 函数测试 -->
      <el-divider />
      <h3>权限函数测试</h3>
      
      <el-table :data="permissionTests" stripe>
        <el-table-column prop="permission" label="权限码" width="300" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === '新' ? 'success' : 'info'" size="small">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="检查结果" width="100">
          <template #default="scope">
            <el-icon v-if="scope.row.result" color="green"><CircleCheck /></el-icon>
            <el-icon v-else color="red"><CircleClose /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因说明" />
      </el-table>

      <!-- Manage权限推理测试 -->
      <el-divider />
      <h3>Manage权限推理测试</h3>
      
      <el-alert
        title="Manage权限说明"
        type="info"
        description="拥有manage权限将自动获得该资源的所有操作权限。例如：拥有 system:user:manage 将自动获得 system:user:view/create/update/delete 等权限。"
        show-icon
        :closable="false"
      />
      
      <el-table :data="managePermissionTests" stripe style="margin-top: 20px">
        <el-table-column prop="manage" label="Manage权限" width="200" />
        <el-table-column prop="inferred" label="推理权限" width="200" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="scope">
            <el-icon v-if="scope.row.result" color="green"><CircleCheck /></el-icon>
            <el-icon v-else color="red"><CircleClose /></el-icon>
          </template>
        </el-table-column>
      </el-table>

      <!-- 操作按钮测试 -->
      <el-divider />
      <h3>操作按钮权限控制</h3>
      
      <el-space>
        <el-button type="primary" v-permission="'USER_ADD'">添加用户（旧）</el-button>
        <el-button type="primary" v-permission="'system:user:create'">添加用户（新）</el-button>
        <el-button type="warning" v-permission="'USER_EDIT'">编辑用户（旧）</el-button>
        <el-button type="warning" v-permission="'system:user:update'">编辑用户（新）</el-button>
        <el-button type="danger" v-permission="'USER_DELETE'">删除用户（旧）</el-button>
        <el-button type="danger" v-permission="'system:user:delete'">删除用户（新）</el-button>
        <el-button type="success" v-permission="'system:permission:refresh'">刷新权限缓存</el-button>
      </el-space>

      <!-- 刷新权限缓存 -->
      <el-divider />
      <h3>权限缓存管理</h3>
      
      <el-space>
        <el-button @click="refreshAllPermissions" :loading="refreshing">
          刷新所有权限缓存
        </el-button>
        <el-button @click="getCacheStatus" :loading="loadingStatus">
          获取缓存状态
        </el-button>
      </el-space>
      
      <el-descriptions 
        v-if="cacheStatus" 
        title="缓存状态" 
        :column="2" 
        border 
        style="margin-top: 20px"
      >
        <el-descriptions-item label="别名缓存数量">{{ cacheStatus.aliasCount }}</el-descriptions-item>
        <el-descriptions-item label="URL权限数量">{{ cacheStatus.urlPermissionCount }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">{{ cacheStatus.message }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { hasPermission } from '@/utils/permission'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import request from '@/utils/request'

const userStore = useUserStore()

// 用户信息
const userInfo = computed(() => userStore.userInfo || {})
const userRoles = computed(() => userStore.roles || [])
const userPermissions = computed(() => userStore.permissions || [])

// 权限测试数据
const permissionTests = ref([])
const managePermissionTests = ref([])

// 缓存状态
const cacheStatus = ref(null)
const refreshing = ref(false)
const loadingStatus = ref(false)

// 测试权限列表
const testPermissions = [
  // 旧权限码
  { permission: 'USER_VIEW', type: '旧' },
  { permission: 'USER_ADD', type: '旧' },
  { permission: 'USER_EDIT', type: '旧' },
  { permission: 'USER_DELETE', type: '旧' },
  { permission: 'ROLE_MANAGE', type: '旧' },
  // 新权限码
  { permission: 'system:user:view', type: '新' },
  { permission: 'system:user:create', type: '新' },
  { permission: 'system:user:update', type: '新' },
  { permission: 'system:user:delete', type: '新' },
  { permission: 'system:user:manage', type: '新' },
  { permission: 'system:role:view', type: '新' },
  { permission: 'system:permission:refresh', type: '新' }
]

// Manage权限推理测试
const manageTests = [
  { manage: 'system:user:manage', inferred: 'system:user:view' },
  { manage: 'system:user:manage', inferred: 'system:user:create' },
  { manage: 'system:user:manage', inferred: 'system:user:update' },
  { manage: 'system:user:manage', inferred: 'system:user:delete' },
  { manage: 'system:manage', inferred: 'system:user:view' },
  { manage: 'system:manage', inferred: 'system:role:create' }
]

// 初始化权限测试
const initPermissionTests = () => {
  permissionTests.value = testPermissions.map(test => {
    const result = hasPermission(test.permission)
    let reason = ''
    
    if (result) {
      if (userPermissions.value.includes(test.permission)) {
        reason = '直接拥有该权限'
      } else if (test.type === '旧') {
        reason = '通过新权限码映射获得'
      } else {
        reason = '通过manage权限或域权限推理获得'
      }
    } else {
      reason = '无此权限'
    }
    
    return {
      ...test,
      result,
      reason
    }
  })
}

// 初始化Manage权限测试
const initManageTests = () => {
  // 模拟用户拥有 system:user:manage 权限
  const hasManagePermission = hasPermission('system:user:manage')
  
  managePermissionTests.value = manageTests.map(test => ({
    ...test,
    result: hasManagePermission && hasPermission(test.inferred)
  }))
}

// 刷新所有权限缓存
const refreshAllPermissions = async () => {
  refreshing.value = true
  try {
    await request.post('/api/permission/refresh/all')
    ElMessage.success('权限缓存刷新成功')
    await getCacheStatus()
  } catch (error) {
    ElMessage.error('刷新权限缓存失败：' + (error.message || '未知错误'))
  } finally {
    refreshing.value = false
  }
}

// 获取缓存状态
const getCacheStatus = async () => {
  loadingStatus.value = true
  try {
    const response = await request.get('/api/permission/refresh/status')
    cacheStatus.value = response.data
  } catch (error) {
    ElMessage.error('获取缓存状态失败：' + (error.message || '未知错误'))
  } finally {
    loadingStatus.value = false
  }
}

onMounted(() => {
  initPermissionTests()
  initManageTests()
})
</script>

<style lang="scss" scoped>
.permission-demo {
  padding: 20px;
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  h3 {
    margin: 20px 0 15px 0;
    color: #303133;
  }
  
  h4 {
    margin-bottom: 15px;
    color: #606266;
  }
  
  .el-tag {
    margin: 5px 0;
  }
}</style>