<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>回收站</h2>
        <p>管理已删除的方案和配置</p>
      </div>
    </div>

    <div class="recycle-management">
      <div class="recycle-actions">
        <el-button type="danger" @click="clearAll">
          <el-icon><delete /></el-icon>
          清空回收站
        </el-button>
        <el-button type="success" @click="batchRestore">
          <el-icon><refresh /></el-icon>
          批量恢复
        </el-button>
      </div>

      <el-table :data="recycleData" style="width: 100%" border class="recycle-table">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="项目名称" width="200" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deleteTime" label="删除时间" width="180" />
        <el-table-column prop="deleteBy" label="删除者" width="100" />
        <el-table-column prop="description" label="描述" min-width="250" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="restoreItem(row)">恢复</el-button>
            <el-button type="danger" size="small" @click="permanentDelete(row)">彻底删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'RecycleBin',
  components: {
    Delete,
    Refresh
  },
  setup() {
    // 回收站数据
    const recycleData = ref([
      {
        id: 1,
        name: '旧版教学方案',
        type: '预约方案',
        deleteTime: '2024-03-01 10:15:00',
        deleteBy: '管理员',
        description: '已废弃的旧版本教学预约方案'
      },
      {
        id: 2,
        name: '临时权限配置',
        type: '权限配置',
        deleteTime: '2024-02-28 16:30:00',
        deleteBy: '教务处',
        description: '临时活动期间使用的权限配置'
      },
      {
        id: 3,
        name: '实验室专用方案',
        type: '预约方案',
        deleteTime: '2024-02-25 14:20:00',
        deleteBy: '实验中心',
        description: '实验室专用的预约方案配置'
      }
    ])

    const getTypeColor = (type) => {
      const colorMap = {
        '预约方案': 'primary',
        '权限配置': 'success',
        '时间配置': 'warning',
        '其他': 'info'
      }
      return colorMap[type] || 'info'
    }

    const clearAll = async () => {
      try {
        await ElMessageBox.confirm('确认清空回收站吗？此操作不可恢复！', '清空确认')
        ElMessage.success('回收站已清空')
      } catch {
        // 用户取消
      }
    }

    const batchRestore = () => {
      ElMessage.success('批量恢复完成')
    }

    const restoreItem = (row) => {
      ElMessage.success(`已恢复: ${row.name}`)
    }

    const permanentDelete = async (row) => {
      try {
        await ElMessageBox.confirm(`确认彻底删除"${row.name}"吗？此操作不可恢复！`, '删除确认')
        ElMessage.success('已彻底删除')
      } catch {
        // 用户取消
      }
    }

    return {
      recycleData,
      getTypeColor,
      clearAll,
      batchRestore,
      restoreItem,
      permanentDelete
    }
  }
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

.recycle-management {
  margin-top: 20px;
}

.recycle-actions {
  margin-bottom: 16px;
  text-align: left;
}

.recycle-table {
  margin-top: 16px;
}
</style>