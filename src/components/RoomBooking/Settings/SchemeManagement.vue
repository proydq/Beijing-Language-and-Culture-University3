<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>方案管理</h2>
        <p>管理预约方案和配置模板</p>
      </div>
    </div>

    <div class="scheme-management">
      <div class="scheme-actions">
        <el-button type="primary" @click="addScheme">
          <el-icon><plus /></el-icon>
          新建方案
        </el-button>
        <el-button @click="importScheme">
          <el-icon><upload /></el-icon>
          导入方案
        </el-button>
      </div>

      <el-table :data="schemeData" style="width: 100%" border class="scheme-table">
        <el-table-column prop="name" label="方案名称" width="200" />
        <el-table-column prop="description" label="方案描述" min-width="300" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="creator" label="创建者" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editScheme(row)">编辑</el-button>
            <el-button type="success" size="small" @click="copyScheme(row)">复制</el-button>
            <el-button type="info" size="small" @click="exportScheme(row)">导出</el-button>
            <el-button type="danger" size="small" @click="deleteScheme(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'

export default {
  name: 'SchemeManagement',
  components: {
    Plus,
    Upload
  },
  setup() {
    // 方案数据
    const schemeData = ref([
      {
        id: 1,
        name: '标准教学方案',
        description: '适用于日常教学活动的标准预约方案，包含基本的预约规则和权限设置',
        createTime: '2024-01-15 10:30:00',
        creator: '管理员',
        status: '启用'
      },
      {
        id: 2,
        name: '考试专用方案',
        description: '专门用于考试期间的预约方案，具有更严格的预约限制和审核流程',
        createTime: '2024-02-20 14:20:00',
        creator: '教务处',
        status: '停用'
      },
      {
        id: 3,
        name: '活动预约方案',
        description: '用于学生社团活动和大型会议的预约方案，支持批量预约和特殊权限',
        createTime: '2024-03-10 09:15:00',
        creator: '学工处',
        status: '启用'
      }
    ])

    const addScheme = () => {
      ElMessage.info('新建方案功能开发中...')
    }

    const importScheme = () => {
      ElMessage.info('导入方案功能开发中...')
    }

    const editScheme = (row) => {
      ElMessage.info(`编辑方案: ${row.name}`)
    }

    const copyScheme = (row) => {
      ElMessage.success(`已复制方案: ${row.name}`)
    }

    const exportScheme = (row) => {
      ElMessage.success(`正在导出方案: ${row.name}`)
    }

    const deleteScheme = async (row) => {
      try {
        await ElMessageBox.confirm(`确认删除方案"${row.name}"吗？`, '删除确认')
        ElMessage.success('方案已删除')
      } catch {
        // 用户取消
      }
    }

    return {
      schemeData,
      addScheme,
      importScheme,
      editScheme,
      copyScheme,
      exportScheme,
      deleteScheme
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

.scheme-management {
  margin-top: 20px;
}

.scheme-actions {
  margin-bottom: 16px;
  text-align: left;
}

.scheme-table {
  margin-top: 16px;
}
</style>