<template>
  <div class="user-status-test">
    <div class="header">
      <h2>用户状态测试页面</h2>
      <p>用于测试和验证用户状态相关功能</p>
    </div>

    <div class="test-sections">
      <!-- 状态分布 -->
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>用户状态分布</span>
            <el-button type="primary" @click="getStatusDistribution">刷新</el-button>
          </div>
        </template>
        
        <div v-if="statusDistribution" class="status-info">
          <div class="status-item">
            <label>活跃用户 (ACTIVE):</label>
            <span class="status-value">{{ statusDistribution.statusCounts.ACTIVE }}</span>
          </div>
          <div class="status-item">
            <label>禁用用户 (INACTIVE):</label>
            <span class="status-value">{{ statusDistribution.statusCounts.INACTIVE }}</span>
          </div>
          <div class="status-item">
            <label>总用户数:</label>
            <span class="status-value">{{ statusDistribution.statusCounts.TOTAL }}</span>
          </div>
          <div class="status-item">
            <label>状态为null的用户:</label>
            <span class="status-value" :class="{ 'error': statusDistribution.nullStatusCount > 0 }">
              {{ statusDistribution.nullStatusCount }}
            </span>
          </div>
          <div class="summary">
            {{ statusDistribution.summary }}
          </div>
        </div>
        <div v-else class="no-data">暂无数据</div>
      </el-card>

      <!-- 修复状态 -->
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>修复用户状态</span>
            <el-button type="warning" @click="fixUserStatus" :loading="fixLoading">执行修复</el-button>
          </div>
        </template>
        
        <div class="fix-info">
          <p>此功能将会：</p>
          <ul>
            <li>将状态为null的用户设置为ACTIVE</li>
            <li>修复无效的状态值</li>
            <li>统计修复结果</li>
          </ul>
          <div v-if="fixResult" class="fix-result">
            <el-alert :title="fixResult" type="success" show-icon />
          </div>
        </div>
      </el-card>

      <!-- 检查特定用户 -->
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>检查特定用户</span>
          </div>
        </template>
        
        <div class="user-check">
          <el-input 
            v-model="checkUsername" 
            placeholder="输入用户名" 
            style="width: 200px; margin-right: 10px;"
            @keyup.enter="checkUserStatus"
          />
          <el-button type="primary" @click="checkUserStatus" :loading="checkLoading">检查</el-button>
          
          <div v-if="userCheckResult" class="user-result" style="margin-top: 15px;">
            <div v-if="userCheckResult.found">
              <div class="user-info">
                <div class="info-item">
                  <label>用户ID:</label>
                  <span>{{ userCheckResult.id }}</span>
                </div>
                <div class="info-item">
                  <label>用户名:</label>
                  <span>{{ userCheckResult.username }}</span>
                </div>
                <div class="info-item">
                  <label>真实姓名:</label>
                  <span>{{ userCheckResult.realName }}</span>
                </div>
                <div class="info-item">
                  <label>状态:</label>
                  <span :class="getStatusClass(userCheckResult.status)">
                    {{ userCheckResult.status || 'null' }}
                  </span>
                </div>
                <div class="info-item">
                  <label>客户域ID:</label>
                  <span>{{ userCheckResult.customerId }}</span>
                </div>
                <div class="info-item">
                  <label>是否删除:</label>
                  <span>{{ userCheckResult.deleted }}</span>
                </div>
              </div>
            </div>
            <div v-else>
              <el-alert :title="userCheckResult.message" type="warning" show-icon />
            </div>
          </div>
        </div>
      </el-card>

      <!-- 测试状态枚举 -->
      <el-card class="test-card">
        <template #header>
          <div class="card-header">
            <span>测试状态枚举</span>
          </div>
        </template>
        
        <div class="enum-test">
          <el-input 
            v-model="testStatus" 
            placeholder="输入状态值 (ACTIVE, INACTIVE, NORMAL, DISABLED...)" 
            style="width: 300px; margin-right: 10px;"
            @keyup.enter="testStatusEnum"
          />
          <el-button type="primary" @click="testStatusEnum" :loading="enumLoading">测试</el-button>
          
          <div v-if="enumResult" class="enum-result" style="margin-top: 15px;">
            <div v-if="enumResult.valid">
              <el-alert title="状态值有效" type="success" show-icon />
              <div class="enum-info">
                <div class="info-item">
                  <label>状态:</label>
                  <span>{{ enumResult.status }}</span>
                </div>
                <div class="info-item">
                  <label>序号:</label>
                  <span>{{ enumResult.ordinal }}</span>
                </div>
              </div>
            </div>
            <div v-else>
              <el-alert :title="enumResult.error" type="error" show-icon />
              <div class="valid-values">
                <p>有效的状态值: {{ enumResult.validValues?.join(', ') }}</p>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { userStatusAPI } from '@/api/userStatus'

export default {
  name: 'UserStatusTest',
  setup() {
    const statusDistribution = ref(null)
    const fixResult = ref('')
    const fixLoading = ref(false)
    
    const checkUsername = ref('')
    const userCheckResult = ref(null)
    const checkLoading = ref(false)
    
    const testStatus = ref('')
    const enumResult = ref(null)
    const enumLoading = ref(false)

    // 获取状态分布
    const getStatusDistribution = async () => {
      try {
        const response = await userStatusAPI.getStatusDistribution()
        if (response.code === '0') {
          statusDistribution.value = response.data
        } else {
          ElMessage.error(response.message || '获取状态分布失败')
        }
      } catch (error) {
        console.error('获取状态分布失败:', error)
        ElMessage.error('获取状态分布失败')
      }
    }

    // 修复用户状态
    const fixUserStatus = async () => {
      fixLoading.value = true
      try {
        const response = await userStatusAPI.fixUserStatus()
        if (response.code === '0') {
          fixResult.value = response.data || '状态修复完成'
          ElMessage.success('状态修复成功')
          // 刷新状态分布
          getStatusDistribution()
        } else {
          ElMessage.error(response.message || '状态修复失败')
        }
      } catch (error) {
        console.error('状态修复失败:', error)
        ElMessage.error('状态修复失败')
      } finally {
        fixLoading.value = false
      }
    }

    // 检查用户状态
    const checkUserStatus = async () => {
      if (!checkUsername.value.trim()) {
        ElMessage.warning('请输入用户名')
        return
      }
      
      checkLoading.value = true
      try {
        const response = await userStatusAPI.checkUserStatus(checkUsername.value.trim())
        if (response.code === '0') {
          userCheckResult.value = response.data
        } else {
          ElMessage.error(response.message || '检查用户状态失败')
        }
      } catch (error) {
        console.error('检查用户状态失败:', error)
        ElMessage.error('检查用户状态失败')
      } finally {
        checkLoading.value = false
      }
    }

    // 测试状态枚举
    const testStatusEnum = async () => {
      if (!testStatus.value.trim()) {
        ElMessage.warning('请输入状态值')
        return
      }
      
      enumLoading.value = true
      try {
        const response = await userStatusAPI.testStatusEnum(testStatus.value.trim())
        if (response.code === '0') {
          enumResult.value = response.data
        } else {
          ElMessage.error(response.message || '测试状态枚举失败')
        }
      } catch (error) {
        console.error('测试状态枚举失败:', error)
        ElMessage.error('测试状态枚举失败')
      } finally {
        enumLoading.value = false
      }
    }

    // 获取状态样式类
    const getStatusClass = (status) => {
      if (status === 'ACTIVE') return 'status-active'
      if (status === 'INACTIVE') return 'status-inactive'
      return 'status-null'
    }

    // 页面加载时获取状态分布
    getStatusDistribution()

    return {
      statusDistribution,
      getStatusDistribution,
      fixResult,
      fixLoading,
      fixUserStatus,
      checkUsername,
      userCheckResult,
      checkLoading,
      checkUserStatus,
      testStatus,
      enumResult,
      enumLoading,
      testStatusEnum,
      getStatusClass
    }
  }
}
</script>

<style scoped>
.user-status-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h2 {
  color: #333;
  margin-bottom: 8px;
}

.header p {
  color: #666;
  font-size: 14px;
}

.test-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
}

.test-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  padding: 10px 0;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.status-item label {
  font-weight: 500;
  color: #333;
}

.status-value {
  font-weight: 600;
  color: #4a90e2;
}

.status-value.error {
  color: #f56565;
}

.summary {
  margin-top: 15px;
  padding: 12px;
  background-color: #e8f4f8;
  border-radius: 6px;
  color: #2d3748;
  font-size: 14px;
}

.no-data {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.fix-info ul {
  margin: 10px 0;
  padding-left: 20px;
}

.fix-info li {
  margin-bottom: 5px;
  color: #666;
}

.fix-result {
  margin-top: 15px;
}

.user-info, .enum-info {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 6px;
  margin-top: 10px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.info-item label {
  font-weight: 500;
  color: #333;
  min-width: 100px;
}

.info-item span {
  color: #666;
}

.status-active {
  color: #67c23a;
  font-weight: 600;
}

.status-inactive {
  color: #f56565;
  font-weight: 600;
}

.status-null {
  color: #909399;
  font-weight: 600;
}

.valid-values {
  margin-top: 10px;
}

.valid-values p {
  color: #666;
  font-size: 14px;
}
</style>