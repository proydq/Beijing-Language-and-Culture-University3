<template>
  <div class="organization-management">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo" @click="goToHome" style="cursor: pointer;">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">组织管理</span>
      </div>
      <div class="header-right">
        <div class="avatar">
          <el-icon size="20"><user /></el-icon>
        </div>
        <span class="username">系统管理员</span>
        <el-dropdown>
          <span class="dropdown-link">
            <el-icon><grid /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToPersonalCenter">个人中心</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="main-container">
      <!-- 左侧组织架构树 -->
      <div class="sidebar">
        <!-- 添加首页导航 -->
        <div class="nav-item" @click="goToHome">
          <el-icon><home /></el-icon>
          <span>首页</span>
        </div>
        
        <div class="tree-header">
          <h3>组织架构</h3>
          <div class="tree-actions">
            <el-button type="primary" size="small" @click="handleAddParent">同级</el-button>
            <el-button type="success" size="small" @click="handleAddChild">下级</el-button>
          </div>
        </div>
        <div class="tree-container">
          <el-tree
            :data="treeData"
            :props="treeProps"
            node-key="id"
            default-expand-all
            @node-click="handleTreeNodeClick"
            @node-contextmenu="handleNodeRightClick"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <el-icon v-if="data.type === 'company'" class="tree-icon company-icon"><office-building /></el-icon>
                <el-icon v-else-if="data.type === 'department'" class="tree-icon department-icon"><folder /></el-icon>
                <el-icon v-else class="tree-icon group-icon"><user /></el-icon>
                <span class="node-label">{{ node.label }}</span>
                <span v-if="data.count !== undefined" class="node-count">({{ data.count }})</span>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧内容区域 -->
      <div class="main-content">
        <!-- 面包屑导航 -->
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item @click="goToHome" style="cursor: pointer; color: #4A90E2;">首页</el-breadcrumb-item>
            <el-breadcrumb-item>组织管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <!-- 操作按钮区域 -->
        <div class="action-area">
          <div class="left-actions">
            <span class="title">组织信息</span>
          </div>
          <div class="right-actions">
            <el-button type="primary" @click="handleEdit">编辑</el-button>
            <el-button type="danger" @click="handleDelete">删除</el-button>
          </div>
        </div>

        <!-- 组织信息表单 -->
        <div class="form-area">
          <el-form ref="formRef" :model="orgForm" :rules="orgRules" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="组织名称" prop="name">
                  <el-input v-model="orgForm.name" :disabled="!editMode" placeholder="请输入组织名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="组织编码" prop="code">
                  <el-input v-model="orgForm.code" :disabled="!editMode" placeholder="请输入组织编码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="组织类型" prop="type">
                  <el-select v-model="orgForm.type" :disabled="!editMode" placeholder="请选择组织类型">
                    <el-option label="公司" value="company" />
                    <el-option label="部门" value="department" />
                    <el-option label="小组" value="group" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="负责人" prop="leader">
                  <el-input v-model="orgForm.leader" :disabled="!editMode" placeholder="请输入负责人" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="phone">
                  <el-input v-model="orgForm.phone" :disabled="!editMode" placeholder="请输入联系电话" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="orgForm.email" :disabled="!editMode" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="排序" prop="sort">
                  <el-input-number v-model="orgForm.sort" :disabled="!editMode" :min="0" placeholder="排序" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-select v-model="orgForm.status" :disabled="!editMode" placeholder="请选择状态">
                    <el-option label="启用" value="1" />
                    <el-option label="禁用" value="0" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="描述" prop="description">
                  <el-input 
                    v-model="orgForm.description" 
                    :disabled="!editMode" 
                    type="textarea" 
                    :rows="4"
                    placeholder="请输入组织描述" 
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 编辑模式下的操作按钮 -->
            <div v-if="editMode" class="form-actions">
              <el-button @click="handleCancel">取消</el-button>
              <el-button type="primary" @click="handleSave">保存</el-button>
            </div>
          </el-form>
        </div>

        <!-- 组织成员列表 -->
        <div class="members-area">
          <div class="members-header">
            <h3>组织成员</h3>
            <el-button type="primary" @click="handleAddMember">添加成员</el-button>
          </div>
          
          <div class="members-table">
            <el-table :data="membersData" style="width: 100%" stripe>
              <el-table-column prop="name" label="姓名" />
              <el-table-column prop="username" label="用户名" />
              <el-table-column prop="position" label="职位" />
              <el-table-column prop="phone" label="电话" />
              <el-table-column prop="email" label="邮箱" />
              <el-table-column label="状态">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
                    {{ scope.row.status === '1' ? '在职' : '离职' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="handleEditMember(scope.row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="handleRemoveMember(scope.row)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑组织弹窗 -->
    <el-dialog
      v-model="orgDialogVisible"
      :title="orgDialogTitle"
      width="600px"
      @close="handleOrgDialogClose"
    >
      <el-form ref="orgDialogFormRef" :model="orgDialogForm" :rules="orgRules" label-width="100px">
        <el-form-item label="组织名称" prop="name">
          <el-input v-model="orgDialogForm.name" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织编码" prop="code">
          <el-input v-model="orgDialogForm.code" placeholder="请输入组织编码" />
        </el-form-item>
        <el-form-item label="组织类型" prop="type">
          <el-select v-model="orgDialogForm.type" placeholder="请选择组织类型">
            <el-option label="公司" value="company" />
            <el-option label="部门" value="department" />
            <el-option label="小组" value="group" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="orgDialogForm.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="orgDialogForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="orgDialogForm.sort" :min="0" placeholder="排序" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="orgDialogForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入组织描述" 
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleOrgDialogClose">取消</el-button>
          <el-button type="primary" @click="handleOrgDialogSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'OrganizationManagement',
  setup() {
    const router = useRouter()
    const formRef = ref()
    const orgDialogFormRef = ref()
    const editMode = ref(false)
    const orgDialogVisible = ref(false)
    const orgDialogMode = ref('add') // add, edit

    // 组织表单数据
    const orgForm = reactive({
      id: 1,
      name: '总公司',
      code: 'COMPANY001',
      type: 'company',
      leader: '张总',
      phone: '400-123-4567',
      email: 'company@example.com',
      sort: 1,
      status: '1',
      description: '这是公司的总部组织'
    })

    // 弹窗表单数据
    const orgDialogForm = reactive({
      name: '',
      code: '',
      type: '',
      leader: '',
      phone: '',
      sort: 0,
      description: ''
    })

    // 表单验证规则
    const orgRules = {
      name: [
        { required: true, message: '请输入组织名称', trigger: 'blur' }
      ],
      code: [
        { required: true, message: '请输入组织编码', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择组织类型', trigger: 'change' }
      ]
    }

    // 组织架构树数据
    const treeData = ref([
      {
        id: 1,
        label: '总公司',
        type: 'company',
        count: 156,
        children: [
          {
            id: 2,
            label: '技术部',
            type: 'department',
            count: 45,
            children: [
              {
                id: 3,
                label: '前端组',
                type: 'group',
                count: 12
              },
              {
                id: 4,
                label: '后端组',
                type: 'group',
                count: 18
              }
            ]
          },
          {
            id: 5,
            label: '产品部',
            type: 'department',
            count: 32,
            children: [
              {
                id: 6,
                label: '产品经理组',
                type: 'group',
                count: 15
              },
              {
                id: 7,
                label: '设计组',
                type: 'group',
                count: 17
              }
            ]
          },
          {
            id: 8,
            label: '运营部',
            type: 'department',
            count: 28
          }
        ]
      }
    ])

    const treeProps = {
      children: 'children',
      label: 'label'
    }

    // 组织成员数据
    const membersData = ref([
      {
        id: 1,
        name: '张三',
        username: 'zhangsan',
        position: '技术总监',
        phone: '13800138001',
        email: 'zhangsan@example.com',
        status: '1'
      },
      {
        id: 2,
        name: '李四',
        username: 'lisi',
        position: '产品经理',
        phone: '13800138002',
        email: 'lisi@example.com',
        status: '1'
      },
      {
        id: 3,
        name: '王五',
        username: 'wangwu',
        position: '前端工程师',
        phone: '13800138003',
        email: 'wangwu@example.com',
        status: '0'
      }
    ])

    // 计算属性
    const orgDialogTitle = computed(() => {
      return orgDialogMode.value === 'add' ? '新增组织' : '编辑组织'
    })

    // 返回首页
    const goToHome = () => {
      router.push('/dashboard')
    }

    const goToPersonalCenter = () => {
      router.push('/personal-center')
    }

    const logout = () => {
      console.log('退出登录')
    }

    // 组织架构相关方法
    const handleTreeNodeClick = (data) => {
      console.log('选择组织节点:', data)
      // 这里可以加载对应组织的详细信息
    }

    const handleNodeRightClick = (event, node, data) => {
      console.log('右键点击节点:', data)
    }

    const handleAddParent = () => {
      orgDialogMode.value = 'add'
      resetOrgDialogForm()
      orgDialogVisible.value = true
    }

    const handleAddChild = () => {
      orgDialogMode.value = 'add'
      resetOrgDialogForm()
      orgDialogVisible.value = true
    }

    // 组织信息相关方法
    const handleEdit = () => {
      editMode.value = true
    }

    const handleCancel = () => {
      editMode.value = false
      // 重置表单数据
    }

    const handleSave = async () => {
      try {
        await formRef.value.validate()
        // 保存组织信息
        editMode.value = false
        ElMessage.success('保存成功')
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    const handleDelete = () => {
      ElMessageBox.confirm('确定要删除该组织吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
      })
    }

    // 组织成员相关方法
    const handleAddMember = () => {
      console.log('添加成员')
    }

    const handleEditMember = (row) => {
      console.log('编辑成员:', row)
    }

    const handleRemoveMember = (row) => {
      ElMessageBox.confirm(`确定要移除成员"${row.name}"吗？`, '确认移除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('移除成功')
      })
    }

    // 弹窗相关方法
    const handleOrgDialogClose = () => {
      orgDialogVisible.value = false
      resetOrgDialogForm()
    }

    const handleOrgDialogSubmit = async () => {
      try {
        await orgDialogFormRef.value.validate()
        // 提交组织信息
        ElMessage.success(orgDialogMode.value === 'add' ? '新增成功' : '编辑成功')
        handleOrgDialogClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    const resetOrgDialogForm = () => {
      Object.assign(orgDialogForm, {
        name: '',
        code: '',
        type: '',
        leader: '',
        phone: '',
        sort: 0,
        description: ''
      })
    }

    return {
      formRef,
      orgDialogFormRef,
      editMode,
      orgDialogVisible,
      orgDialogMode,
      orgForm,
      orgDialogForm,
      orgRules,
      treeData,
      treeProps,
      membersData,
      orgDialogTitle,
      goToHome,  // 新增返回首页方法
      goToPersonalCenter,
      logout,
      handleTreeNodeClick,
      handleNodeRightClick,
      handleAddParent,
      handleAddChild,
      handleEdit,
      handleCancel,
      handleSave,
      handleDelete,
      handleAddMember,
      handleEditMember,
      handleRemoveMember,
      handleOrgDialogClose,
      handleOrgDialogSubmit
    }
  }
}
</script>

<style scoped>
.organization-management {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: white;
  color: #4A90E2;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.05);
}

.title {
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.username {
  font-size: 16px;
  font-weight: 500;
}

.dropdown-link {
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.dropdown-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main-container {
  display: flex;
  height: calc(100vh - 70px);
}

.sidebar {
  width: 280px;
  background: white;
  border-right: 1px solid #e8e8e8;
  padding: 15px;
  flex-shrink: 0;
  overflow-y: auto;
}

.nav-item {
  padding: 12px 15px;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s;
  color: #666;
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item:hover {
  background-color: #f0f2f5;
  color: #4A90E2;
}

.tree-header {
  margin-top: 20px;
  margin-bottom: 15px;
}

.tree-header h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.tree-actions {
  display: flex;
  gap: 10px;
}

.tree-container {
  flex: 1;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.tree-icon {
  font-size: 16px;
}

.company-icon {
  color: #4A90E2;
}

.department-icon {
  color: #67C23A;
}

.group-icon {
  color: #E6A23C;
}

.node-label {
  flex: 1;
  font-size: 14px;
}

.node-count {
  font-size: 12px;
  color: #999;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.breadcrumb {
  margin-bottom: 20px;
  padding: 15px 0;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child)) {
  cursor: pointer;
}

.breadcrumb :deep(.el-breadcrumb__item:not(:last-child):hover) {
  color: #357ABD;
}

.action-area {
  background: white;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.left-actions .title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.right-actions {
  display: flex;
  gap: 10px;
}

.form-area {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.members-area {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.members-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.members-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.members-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>