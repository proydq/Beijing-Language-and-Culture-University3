<template>
  <div class="setting-section">
    <div class="section-header">
      <div>
        <h2>预约人员权限列表</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="addPersonnelPermission">
          新增
        </el-button>
        <el-button @click="exportPersonnelList">
          导出
        </el-button>
      </div>
    </div>

    <el-table :data="personnelPermissionData" style="width: 100%" border>
      <el-table-column prop="subject" label="主题" width="200" />
      <el-table-column prop="authorizedPersonnel" label="权限人员" min-width="300">
        <template #default="{ row }">
          <div class="personnel-text">
            {{ row.authorizedPersonnel }}
            <el-button 
              type="text" 
              size="small" 
              @click="viewPersonnelDetails(row)"
              style="color: #409EFF; margin-left: 10px;"
            >
              查看详情
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="bookingRooms" label="预约教室" min-width="350">
        <template #default="{ row }">
          <div class="rooms-text">
            {{ row.bookingRooms }}
            <el-button 
              type="text" 
              size="small" 
              @click="viewRoomDetails(row)"
              style="color: #409EFF; margin-left: 10px;"
            >
              查看详情
            </el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="creator" label="创建人" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="120" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="editPersonnelPermission(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deletePersonnelPermission(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'BookingPersonnelSettings',
  setup() {
    // 预约人员权限数据 - 根据截图的实际数据结构
    const personnelPermissionData = ref([
      {
        id: 1,
        subject: '物理实验室一层、二层、三层可预约人员',
        authorizedPersonnel: '杨超；郭辉；邓伯雯；赵芳；潘欣妍；张宇；吴俊杰；刘敏；孙辉；高颖；王丽；陈杰；周琼；赵诗雅；徐赠；王宇轩；黄斯；李鸥；荣意；贾永强；张良嘉；曹明红；郑子豪......',
        bookingRooms: '多媒体教室（101）；多媒体教室（102）；多媒体教室（103）；多媒体教室（104）；多媒体教室（105）；多媒体教室（106）；多媒体教室（107）；多媒体教室（108）；多媒体教室（109）；多媒体教室（110）......',
        creator: '张三',
        createTime: '2024.03.08 09:16:26'
      },
      {
        id: 2,
        subject: '物理实验室四层可预约人员',
        authorizedPersonnel: '杨超；郭辉；邓伯雯；赵芳；潘欣妍；',
        bookingRooms: '清洁间',
        creator: '张三',
        createTime: '2024.03.08 09:16:26'
      }
    ])

    const addPersonnelPermission = () => {
      ElMessage.info('新增预约人员权限功能开发中...')
    }

    const exportPersonnelList = () => {
      ElMessage.success('正在导出预约人员列表...')
    }

    const viewPersonnelDetails = (row) => {
      ElMessage.info(`查看人员详情: ${row.subject}`)
    }

    const viewRoomDetails = (row) => {
      ElMessage.info(`查看房间详情: ${row.subject}`)
    }

    const editPersonnelPermission = (row) => {
      ElMessage.info(`编辑权限设置: ${row.subject}`)
    }

    const deletePersonnelPermission = async (row) => {
      try {
        await ElMessageBox.confirm(`确认删除"${row.subject}"的权限设置吗？`, '删除确认')
        ElMessage.success('权限设置已删除')
      } catch {
        // 用户取消
      }
    }

    return {
      personnelPermissionData,
      addPersonnelPermission,
      exportPersonnelList,
      viewPersonnelDetails,
      viewRoomDetails,
      editPersonnelPermission,
      deletePersonnelPermission
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

.header-actions {
  display: flex;
  gap: 12px;
}

.personnel-text,
.rooms-text {
  line-height: 1.5;
  word-wrap: break-word;
  word-break: break-all;
}

/* 让表格内容可以换行显示 */
:deep(.el-table .cell) {
  white-space: normal !important;
  word-wrap: break-word;
  word-break: break-all;
  line-height: 1.5;
}

/* 调整表格行高 */
:deep(.el-table__row) {
  height: auto;
}

:deep(.el-table td) {
  padding: 12px 0;
}
</style>