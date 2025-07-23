<template>
  <div class="organization-content">
    <div class="org-layout">
      <!-- 左侧组织树 -->
      <div class="left-tree">
        <div class="tree-header">
          <h3>组织架构</h3>
          <el-input
            v-model="orgSearchKeyword"
            placeholder="搜索组织"
            size="small"
            clearable
            style="margin-top: 10px;"
          >
            <template #prefix>
              <el-icon><search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="tree-container">
          <el-tree
            ref="orgTreeRef"
            :data="orgTreeData"
            :props="{ children: 'children', label: 'name' }"
            node-key="id"
            show-checkbox
            @node-click="handleOrgTreeNodeClick"
          />
        </div>
      </div>

      <!-- 右侧详情和成员 -->
      <div class="right-content">
        <!-- 组织详情 -->
        <div class="detail-area">
          <div class="detail-header">
            <h3>{{ selectedOrgNode ? selectedOrgNode.name : '请选择组织' }}</h3>
            <div class="detail-actions">
              <el-button type="primary" size="small" @click="handleAddOrgParent">
                添加父级
              </el-button>
              <el-button type="success" size="small" @click="handleAddOrgChild">
                添加子级
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="handleEditOrg"
                :disabled="!selectedOrgNode"
              >
                编辑
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="handleDeleteOrg"
                :disabled="!selectedOrgNode"
              >
                删除
              </el-button>
            </div>
          </div>
          
          <div v-if="selectedOrgNode" class="org-form">
            <el-form
              ref="orgFormRef"
              :model="orgFormData"
              :rules="orgFormRules"
              label-width="100px"
              :disabled="!orgEditMode"
            >
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="组织名称:" prop="name">
                    <el-input v-model="orgFormData.name" placeholder="请输入组织名称" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="组织编码:" prop="code">
                    <el-input v-model="orgFormData.code" placeholder="请输入组织编码" />
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="组织描述:" prop="description">
                <el-input
                  v-model="orgFormData.description"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入组织描述"
                />
              </el-form-item>
              <el-form-item v-if="orgEditMode">
                <el-button type="primary" @click="handleSaveOrg">保存</el-button>
                <el-button @click="handleCancelOrgEdit">取消</el-button>
              </el-form-item>
            </el-form>
          </div>
          <div v-else class="empty-state">
            <el-icon size="48" color="#ccc"><folder /></el-icon>
            <p>请从左侧选择一个组织查看详情</p>
          </div>
        </div>

        <!-- 组织成员 -->
        <div v-if="selectedOrgNode" class="org-members-area">
          <div class="members-header">
            <h3>组织成员</h3>
            <el-button type="primary" size="small" @click="handleAddOrgMember">
              添加成员
            </el-button>
          </div>
          <div class="members-table">
            <el-table :data="orgMembersData" style="width: 100%;" border>
              <el-table-column prop="realName" label="姓名" width="120" />
              <el-table-column prop="jobNumber" label="工号" width="120" />
              <el-table-column prop="position" label="职务" width="150" />
              <el-table-column prop="phone" label="手机号" width="130" />
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button 
                    type="text" 
                    size="small" 
                    @click="handleRemoveOrgMember(row)"
                    style="color: #f56c6c;"
                  >
                    移除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>

    <!-- 组织新增/编辑对话框 -->
    <el-dialog
      v-model="orgDialogVisible"
      :title="orgDialogMode === 'add' ? '新增组织' : '编辑组织'"
      width="500px"
      @close="handleOrgDialogClose"
    >
      <el-form
        ref="orgDialogFormRef"
        :model="orgDialogFormData"
        :rules="orgDialogFormRules"
        label-width="100px"
      >
        <el-form-item label="组织名称:" prop="name">
          <el-input v-model="orgDialogFormData.name" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织编码:" prop="code">
          <el-input v-model="orgDialogFormData.code" placeholder="请输入组织编码" />
        </el-form-item>
        <el-form-item label="上级组织:" prop="parentId">
          <el-select 
            v-model="orgDialogFormData.parentId" 
            placeholder="请选择上级组织" 
            style="width: 100%;"
            clearable
          >
            <el-option label="无（根组织）" :value="null" />
            <el-option 
              v-for="org in flatOrgList" 
              :key="org.id" 
              :label="org.name" 
              :value="org.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="组织描述:" prop="description">
          <el-input
            v-model="orgDialogFormData.description"
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
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Folder } from '@element-plus/icons-vue'

export default {
  name: 'OrganizationContent',
  components: {
    Search,
    Folder
  },
  setup() {
    const orgTreeRef = ref()
    const orgFormRef = ref()
    const orgDialogFormRef = ref()
    const orgSearchKeyword = ref('')
    const selectedOrgNode = ref(null)
    const orgEditMode = ref(false)
    const orgDialogVisible = ref(false)
    const orgDialogMode = ref('add')

    // 组织树数据
    const orgTreeData = ref([
      {
        id: 1,
        name: '总公司',
        code: 'ROOT001',
        description: '总公司',
        children: [
          {
            id: 2,
            name: '技术部',
            code: 'TECH001',
            description: '技术研发部门',
            children: [
              { id: 3, name: '前端组', code: 'FE001', description: '前端开发组' },
              { id: 4, name: '后端组', code: 'BE001', description: '后端开发组' }
            ]
          },
          {
            id: 5,
            name: '行政部',
            code: 'ADMIN001',
            description: '行政管理部门',
            children: [
              { id: 6, name: '人事组', code: 'HR001', description: '人力资源组' },
              { id: 7, name: '财务组', code: 'FIN001', description: '财务管理组' }
            ]
          }
        ]
      }
    ])

    // 组织表单数据
    const orgFormData = reactive({
      name: '',
      code: '',
      description: ''
    })

    // 对话框表单数据
    const orgDialogFormData = reactive({
      name: '',
      code: '',
      parentId: null,
      description: ''
    })

    // 组织成员数据
    const orgMembersData = ref([
      {
        id: 1,
        realName: '张三',
        jobNumber: 'JS001',
        position: '前端开发工程师',
        phone: '13800138001'
      },
      {
        id: 2,
        realName: '李四',
        jobNumber: 'JS002',
        position: '后端开发工程师',
        phone: '13800138002'
      }
    ])

    // 表单验证规则
    const orgFormRules = {
      name: [
        { required: true, message: '请输入组织名称', trigger: 'blur' }
      ],
      code: [
        { required: true, message: '请输入组织编码', trigger: 'blur' }
      ]
    }

    const orgDialogFormRules = {
      name: [
        { required: true, message: '请输入组织名称', trigger: 'blur' }
      ],
      code: [
        { required: true, message: '请输入组织编码', trigger: 'blur' }
      ]
    }

    // 扁平化组织列表用于下拉选择
    const flatOrgList = computed(() => {
      const flatten = (nodes) => {
        let result = []
        for (const node of nodes) {
          result.push({ id: node.id, name: node.name })
          if (node.children) {
            result = result.concat(flatten(node.children))
          }
        }
        return result
      }
      return flatten(orgTreeData.value)
    })

    // 监听选中节点变化
    watch(selectedOrgNode, (newNode) => {
      if (newNode) {
        Object.assign(orgFormData, {
          name: newNode.name,
          code: newNode.code,
          description: newNode.description
        })
      }
    })

    // 事件处理方法
    const handleOrgTreeNodeClick = (data) => {
      selectedOrgNode.value = data
      orgEditMode.value = false
    }

    const handleOrgSearch = () => {
      console.log('搜索组织:', orgSearchKeyword.value)
    }

    const handleAddOrgParent = () => {
      orgDialogMode.value = 'add'
      Object.assign(orgDialogFormData, {
        name: '',
        code: '',
        parentId: null,
        description: ''
      })
      orgDialogVisible.value = true
    }

    const handleAddOrgChild = () => {
      orgDialogMode.value = 'add'
      Object.assign(orgDialogFormData, {
        name: '',
        code: '',
        parentId: selectedOrgNode.value?.id || null,
        description: ''
      })
      orgDialogVisible.value = true
    }

    const handleEditOrg = () => {
      if (!selectedOrgNode.value) return
      orgEditMode.value = true
    }

    const handleDeleteOrg = () => {
      if (!selectedOrgNode.value) return
      ElMessageBox.confirm(`确定要删除组织"${selectedOrgNode.value.name}"吗？`, '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        ElMessage.success('删除成功')
      })
    }

    const handleCancelOrgEdit = () => {
      orgEditMode.value = false
      // 重置表单数据
      if (selectedOrgNode.value) {
        Object.assign(orgFormData, {
          name: selectedOrgNode.value.name,
          code: selectedOrgNode.value.code,
          description: selectedOrgNode.value.description
        })
      }
    }

    const handleSaveOrg = async () => {
      try {
        await orgFormRef.value.validate()
        orgEditMode.value = false
        ElMessage.success('保存成功')
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    const handleAddOrgMember = () => {
      console.log('添加组织成员')
    }

    const handleRemoveOrgMember = (row) => {
      ElMessageBox.confirm(`确定要移除成员"${row.realName}"吗？`, '确认移除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const index = orgMembersData.value.findIndex(item => item.id === row.id)
        if (index !== -1) {
          orgMembersData.value.splice(index, 1)
        }
        ElMessage.success('移除成功')
      })
    }

    const handleOrgDialogClose = () => {
      orgDialogVisible.value = false
      Object.assign(orgDialogFormData, {
        name: '',
        code: '',
        parentId: null,
        description: ''
      })
    }

    const handleOrgDialogSubmit = async () => {
      try {
        await orgDialogFormRef.value.validate()
        ElMessage.success(orgDialogMode.value === 'add' ? '新增成功' : '编辑成功')
        handleOrgDialogClose()
      } catch (error) {
        console.log('表单验证失败:', error)
      }
    }

    return {
      orgTreeRef,
      orgFormRef,
      orgDialogFormRef,
      orgSearchKeyword,
      selectedOrgNode,
      orgEditMode,
      orgDialogVisible,
      orgDialogMode,
      orgTreeData,
      orgFormData,
      orgDialogFormData,
      orgMembersData,
      orgFormRules,
      orgDialogFormRules,
      flatOrgList,
      handleOrgTreeNodeClick,
      handleOrgSearch,
      handleAddOrgParent,
      handleAddOrgChild,
      handleEditOrg,
      handleDeleteOrg,
      handleCancelOrgEdit,
      handleSaveOrg,
      handleAddOrgMember,
      handleRemoveOrgMember,
      handleOrgDialogClose,
      handleOrgDialogSubmit
    }
  }
}
</script>

<style scoped>
.organization-content {
  height: 100%;
}

.org-layout {
  display: flex;
  height: 100%;
}

.left-tree {
  width: 300px;
  background: white;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.tree-header {
  padding: 20px 15px;
  border-bottom: 1px solid #e8e8e8;
}

.tree-header h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.tree-container {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
}

.right-content {
  flex: 1;
  padding: 20px;
  background: #f9f9f9;
  overflow-y: auto;
}

.detail-area {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 20px;
  margin-bottom: 20px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.detail-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.detail-actions {
  display: flex;
  gap: 10px;
}

.org-form {
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
}

.empty-state p {
  margin: 10px 0 0 0;
}

.org-members-area {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 20px;
}

.members-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

.members-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.members-table {
  margin-top: 15px;
}

:deep(.el-tree-node__content) {
  padding: 8px 0;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

:deep(.el-dialog__header) {
  background-color: #f8f9fa;
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8e8;
}
</style>