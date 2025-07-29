<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>教室连续预约设置</h2>
      </div>
    </div>

    <!-- 提示信息 -->
    <div class="tips-section">
      <p class="tips-text">
        <span class="tips-label">PS：</span>
        <span class="tips-content"> 表示连续预约时，房屋连续预约天数的限制规则； </span>
      </p>
      <p class="example-text">
        例如：设置可连续预约（16）天，可连续时间为：3月1日-3月16日，第17日起不可预约；单独预约的改：3月17日-4月1日，4月2日不可预约；
      </p>
    </div>

    <!-- 设置布局 -->
    <div class="settings-layout-horizontal">
      <!-- 左侧区域树形结构 -->
      <div class="area-tree-sidebar">
        <div class="search-box">
          <el-input v-model="areaSearchKeyword" placeholder="搜索区域" clearable />
        </div>
        <div class="area-tree">
          <el-tree
            ref="areaTreeRef"
            :data="areaTreeData"
            :props="treeProps"
            :filter-node-method="filterAreaNode"
            node-key="id"
            :expand-on-click-node="false"
            :highlight-current="true"
            @node-click="handleAreaNodeClick"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <el-icon v-if="data.type === 'building'" class="node-icon"><office-building /></el-icon>
                <el-icon v-else-if="data.type === 'floor'" class="node-icon"><grid /></el-icon>
                <el-icon v-else class="node-icon"><house /></el-icon>
                <span class="node-label">{{ node.label }}</span>
              </span>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧主要内容 -->
      <div class="main-content-area">
        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" @click="batchSetContinuousDays">
            <el-icon><plus /></el-icon>
            批量设置
          </el-button>
          <el-button @click="exportContinuousSettings">
            <el-icon><upload /></el-icon>
            导出设置
          </el-button>
        </div>

        <!-- 教室表格 -->
        <div class="classroom-table">
          <el-table
            v-loading="loading"
            :data="filteredClassrooms"
            style="width: 100%"
            border
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="roomName" label="预约教室" />
            <el-table-column prop="roomCode" label="房屋号" />
            <el-table-column prop="roomAreaName" label="所属楼" />
            <el-table-column prop="continuousDaysDisplay" label="可连续预约天数">
              <template #default="{ row }">
                <span v-if="row.continuousDaysDisplay === '不可连续预约'" class="unlimited-text">{{
                  row.continuousDaysDisplay
                }}</span>
                <span v-else-if="row.continuousDaysDisplay === '一天'" class="monthly-text">{{
                  row.continuousDaysDisplay
                }}</span>
                <span v-else-if="row.continuousDaysDisplay === '一年'" class="yearly-text">{{
                  row.continuousDaysDisplay
                }}</span>
                <span v-else-if="row.continuousDaysDisplay === '无限制预约'" class="no-limit-text">{{
                  row.continuousDaysDisplay
                }}</span>
                <span v-else>{{ row.continuousDaysDisplay }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="editContinuousDays(row)"
                  >编辑</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 分页 -->
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalClassrooms"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>

    <!-- 设置可连续预约天数弹出框 -->
    <el-dialog v-model="continuousDaysDialogVisible" title="设置可连续预约天数" width="500px">
      <div class="dialog-content">
        <el-radio-group v-model="continuousForm.settingType" class="radio-group">
          <el-radio value="no_continuous" class="radio-item">
            <div class="radio-content">
              <div class="radio-title">不可连续预约</div>
              <div class="radio-description">该教室不允许连续预约</div>
            </div>
          </el-radio>
          <el-radio value="limited" class="radio-item">
            <div class="radio-content">
              <div class="radio-title">限制连续预约</div>
              <div class="radio-description">设置最大连续预约天数</div>
              <div v-if="continuousForm.settingType === 'limited'" class="sub-options">
                <el-form-item label="连续天数">
                  <el-input-number
                    v-model="continuousForm.continuousDays"
                    :min="1"
                    :max="365"
                    controls-position="right"
                    style="width: 120px"
                  />
                </el-form-item>
                <el-form-item label="连续类型">
                  <el-select v-model="continuousForm.continuousType" style="width: 120px">
                    <el-option label="天" value="DAYS" />
                    <el-option label="周" value="WEEKS" />
                    <el-option label="月" value="MONTHS" />
                  </el-select>
                </el-form-item>
              </div>
            </div>
          </el-radio>
          <el-radio value="unlimited" class="radio-item">
            <div class="radio-content">
              <div class="radio-title">无限制预约</div>
              <div class="radio-description">该教室可以无限制连续预约</div>
            </div>
          </el-radio>
        </el-radio-group>
      </div>
      <template #footer>
         <span class="dialog-footer">
           <el-button @click="continuousDaysDialogVisible = false">取消</el-button>
           <el-button type="primary" :loading="saving" @click="confirmContinuousDays">确定</el-button>
         </span>
       </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Upload, OfficeBuilding, Grid, House } from '@element-plus/icons-vue'
import { getAreaTree } from '@/api/area.js'
import {
  getContinuousBookingSettings,
  updateContinuousBookingSetting,
  batchUpdateContinuousBookingSettings,
  CONTINUOUS_TYPE_LABELS
} from '@/api/continuousBooking.js'

export default {
  name: 'ContinuousBookingSettings',
  components: {
    Plus,
    Upload,
    OfficeBuilding,
    Grid,
    House,
  },
  setup() {
    // 响应式变量
    const selectedAreaId = ref('')
    const selectedAreaName = ref('')
    const areaSearchKeyword = ref('')
    const currentPage = ref(1)
    const pageSize = ref(20)
    const totalClassrooms = ref(0)
    const loading = ref(false)
    const saving = ref(false)

    // 区域树相关
    const areaTreeRef = ref(null)
    const areaTreeData = ref([])
    const treeProps = {
      children: 'children',
      label: 'label'
    }

    // 弹出框相关变量
    const continuousDaysDialogVisible = ref(false)
    const currentEditingRow = ref(null)
    const isBatchMode = ref(false)
    const selectedRows = ref([])

    // 连续预约设置表单
    const continuousForm = ref({
      settingType: 'limited', // no_continuous, limited, unlimited
      continuousDays: 7,
      continuousType: 'DAYS'
    })

    // 教室数据
    const classroomsData = ref([])

    // 格式化连续预约天数显示
    const formatContinuousDays = (setting) => {
      if (!setting.canContinuous) {
        return '不可连续预约'
      }
      if (setting.isUnlimited) {
        return '无限制预约'
      }
      const typeLabel = CONTINUOUS_TYPE_LABELS[setting.continuousType] || '天'
      return `${setting.continuousDays}${typeLabel}`
    }

    // 过滤后的教室数据
    const filteredClassrooms = computed(() => {
      return classroomsData.value.map(item => ({
        ...item,
        continuousDaysDisplay: formatContinuousDays(item)
      }))
    })

    // 加载区域树数据
    const loadAreaTree = async () => {
      try {
        const response = await getAreaTree()
        if (response.code === 200) {
          // 将后端返回的区域树数据转换为前端需要的格式
          const formatTreeData = (nodes) => {
            return nodes.map(node => ({
              label: node.areaName,
              id: node.id,
              type: node.type,
              areaName: node.areaName,
              children: node.children && node.children.length > 0 ? formatTreeData(node.children) : undefined
            }))
          }
          areaTreeData.value = formatTreeData(response.data || [])
        } else {
          ElMessage.error(response.message || '获取区域树失败')
        }
      } catch (error) {
        console.error('获取区域树失败:', error)
        ElMessage.error('获取区域树失败')
      }
    }

    // 加载连续预约设置列表
    const loadContinuousBookingSettings = async () => {
      try {
        loading.value = true
        const params = {
          areaId: selectedAreaId.value,
          pageNumber: currentPage.value,
          pageSize: pageSize.value
        }
        const response = await getContinuousBookingSettings(params)
         if (response.code === 200) {
           const pageData = response.data
           if (pageData) {
             classroomsData.value = pageData.rows || []
             totalClassrooms.value = pageData.total || 0
           } else {
             classroomsData.value = []
             totalClassrooms.value = 0
           }
         } else {
           ElMessage.error('获取连续预约设置失败')
         }
      } catch (error) {
        console.error('加载连续预约设置失败:', error)
        ElMessage.error('加载连续预约设置失败')
      } finally {
        loading.value = false
      }
    }

    // 区域树节点点击
    const handleAreaNodeClick = (data) => {
      selectedAreaId.value = data.id
      selectedAreaName.value = data.areaName || data.label
      currentPage.value = 1
      loadContinuousBookingSettings()
    }

    // 区域树搜索过滤
    const filterAreaNode = (value, data) => {
      if (!value) return true
      return data.label.includes(value)
    }

    const batchSetContinuousDays = () => {
      if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要批量设置的教室')
        return
      }
      isBatchMode.value = true
      currentEditingRow.value = null
      continuousForm.value = {
        settingType: 'limited',
        continuousDays: 7,
        continuousType: 'DAYS'
      }
      continuousDaysDialogVisible.value = true
    }

    const exportContinuousSettings = () => {
      ElMessage.success('正在导出教室设置...')
    }

    const editContinuousDays = (row) => {
      console.log('编辑连续预约设置 - row对象:', row)
      console.log('roomId:', row.roomId)
      
      isBatchMode.value = false
      currentEditingRow.value = row

      // 根据当前设置初始化表单
      if (!row.canContinuous) {
        continuousForm.value.settingType = 'no_continuous'
      } else if (row.isUnlimited) {
        continuousForm.value.settingType = 'unlimited'
      } else {
        continuousForm.value.settingType = 'limited'
        continuousForm.value.continuousDays = row.continuousDays
        continuousForm.value.continuousType = row.continuousType
      }

      continuousDaysDialogVisible.value = true
    }

    const confirmContinuousDays = async () => {
      try {
        saving.value = true

        // 构建设置数据
        const settingData = {
          canContinuous: continuousForm.value.settingType !== 'no_continuous',
          isUnlimited: continuousForm.value.settingType === 'unlimited',
          continuousDays: continuousForm.value.settingType === 'limited' ? continuousForm.value.continuousDays : null,
          continuousType: continuousForm.value.settingType === 'limited' ? continuousForm.value.continuousType : 'DAYS'
        }

        if (isBatchMode.value) {
          // 批量更新
          const batchData = {
            roomIds: selectedRows.value.map(row => row.roomId),
            ...settingData
          }
          await batchUpdateContinuousBookingSettings(batchData)
          ElMessage.success('批量设置连续预约天数成功')
        } else {
          // 单个更新
          console.log('单个更新模式 - currentEditingRow:', currentEditingRow.value)
          console.log('准备调用API的roomId:', currentEditingRow.value?.roomId)
          console.log('settingData:', settingData)
          
          await updateContinuousBookingSetting(currentEditingRow.value.roomId, settingData)
          ElMessage.success(`已更新教室 ${currentEditingRow.value.roomName} 的连续预约天数`)
        }

        continuousDaysDialogVisible.value = false
        loadContinuousBookingSettings() // 重新加载数据
      } catch (error) {
        console.error('设置连续预约天数失败:', error)
        ElMessage.error('设置连续预约天数失败')
      } finally {
        saving.value = false
      }
    }

    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
      loadContinuousBookingSettings()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadContinuousBookingSettings()
    }

    // 表格选择变化
    const handleSelectionChange = (selection) => {
      selectedRows.value = selection
    }

    // 监听区域搜索关键词变化
    watch(areaSearchKeyword, (val) => {
      areaTreeRef.value?.filter(val)
    })

    // 组件挂载时加载数据
    onMounted(() => {
      loadAreaTree()
      loadContinuousBookingSettings()
    })

    return {
      // 区域相关
      selectedAreaId,
      areaSearchKeyword,
      areaTreeRef,
      areaTreeData,
      treeProps,
      handleAreaNodeClick,
      filterAreaNode,

      // 状态
      loading,
      saving,

      // 分页
      currentPage,
      pageSize,
      totalClassrooms,

      // 教室数据
      classroomsData,
      filteredClassrooms,
      selectedRows,
      handleSelectionChange,

      // 弹出框
      continuousDaysDialogVisible,
      continuousForm,
      isBatchMode,
      currentEditingRow,

      // 方法
      batchSetContinuousDays,
      exportContinuousSettings,
      editContinuousDays,
      confirmContinuousDays,
      handleSizeChange,
      handleCurrentChange,
      formatContinuousDays,

      // 常量
      CONTINUOUS_TYPE_LABELS,

      // 图标
      OfficeBuilding,
      Grid,
      House
    }
  },
}
</script>

<style scoped>
.setting-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.section-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 教室连续预约设置布局 */
.settings-layout-horizontal {
  display: flex;
  gap: 20px;
  min-height: 600px;
}

.floor-filter-sidebar {
  width: 200px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  height: fit-content;
}

.floor-filter-sidebar .search-box {
  margin-bottom: 16px;
}

.floor-filter-sidebar .floor-list {
  max-height: 400px;
  overflow-y: auto;
}

.floor-filter-sidebar .floor-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  color: #666;
  font-size: 14px;
  margin-bottom: 4px;
}

.floor-filter-sidebar .floor-item:hover {
  background: #f5f5f5;
  color: #333;
}

.floor-filter-sidebar .floor-item.active {
  background: #4a90e2;
  color: white;
}

.main-content-area {
  flex: 1;
}

/* 提示信息 */
.tips-section {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  border-left: 4px solid #4a90e2;
}

.tips-text {
  margin: 0 0 8px 0;
  font-size: 14px;
}

.tips-label {
  color: #e74c3c;
  font-weight: 600;
}

.tips-content {
  color: #333;
}

.example-text {
  margin: 0;
  color: #e74c3c;
  font-size: 13px;
  line-height: 1.5;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-bottom: 16px;
}

/* 教室表格 */
.classroom-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.unlimited-text {
  color: #e74c3c;
}

.monthly-text {
  color: #f39c12;
}

.yearly-text {
  color: #27ae60;
}

.no-limit-text {
  color: #27ae60;
}

/* 分页 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 弹出框样式 */
.continuous-days-options {
  padding: 20px 0;
}

.continuous-days-options .el-radio {
  display: block;
  margin-bottom: 16px;
  line-height: 32px;
}

.continuous-days-options .el-radio:last-child {
  margin-bottom: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
