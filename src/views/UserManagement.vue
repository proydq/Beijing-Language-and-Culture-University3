<template>
  <div class="user-management">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo" @click="goToHome" style="cursor: pointer;">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">用户信息管理</span>
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
        <!-- 搜索框 -->
        <div class="search-header">
          <el-input
            v-model="searchKeyword"
            placeholder="请输入部门名称"
            clearable
          >
            <template #append>
              <el-button type="primary">搜索</el-button>
            </template>
          </el-input>
        </div>
        
        <div class="tree-container">
          <el-tree
            :data="treeData"
            :props="treeProps"
            node-key="id"
            default-expand-all
            :expand-on-click-node="false"
            @node-click="handleTreeNodeClick"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <el-icon v-if="data.type === 'company'" class="tree-icon company-icon"><office-building /></el-icon>
                <el-icon v-else-if="data.type === 'department'" class="tree-icon department-icon"><folder /></el-icon>
                <el-icon v-else class="tree-icon group-icon"><files /></el-icon>
                <span class="node-label">{{ data.name }}</span>
                <span class="node-count">({{ data.count || 0 }})</span>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 右侧主要内容区域 -->
      <div class="main-content">
        <div class="tabs">
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <!-- 用户列表标签页 -->
            <el-tab-pane label="用户列表" name="userList">
              <!-- 搜索区域 -->
              <div class="search-area">
                <el-form :model="searchForm" inline class="search-form">
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

              <!-- 操作区域 -->
              <div class="list-header">
                <h3>用户列表</h3>
                <div class="header-actions">
                  <el-button type="primary" @click="handleAdd">
                    <el-icon><plus /></el-icon>
                    新增用户
                  </el-button>
                  <el-button type="success" @click="handleSync">
                    <el-icon><refresh /></el-icon>
                    同步
                  </el-button>
                  <el-button type="warning" @click="handleImport">
                    <el-icon><upload /></el-icon>
                    导入
                  </el-button>
                  <el-button type="info" @click="handleExport">
                    <el-icon><download /></el-icon>
                    导出
                  </el-button>
                </div>
              </div>

              <!-- 用户表格 -->
              <div class="table-container">
                <el-table 
                  :data="tableData" 
                  style="width: 100%;"
                  border
                  stripe
                >
                  <el-table-column prop="realName" label="姓名" width="100" />
                  <el-table-column prop="gender" label="性别" width="80" />
                  <el-table-column prop="phone" label="手机号" width="130" />
                  <el-table-column prop="jobNumber" label="工号" width="120" />
                  <el-table-column prop="department" label="所属部门" width="120" />
                  <el-table-column prop="position" label="职务" width="120" />
                  <el-table-column prop="jobTitle" label="职称" width="120" />
                  <el-table-column prop="status" label="状态" width="80">
                    <template #default="{ row }">
                      <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
                        {{ row.status }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="createTime" label="创建时间" width="160" />
                  <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                      <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
                      <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
                      <el-button 
                        type="text" 
                        size="small" 
                        @click="handleDelete(row)" 
                        style="color: #f56c6c;"
                      >
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <!-- 分页 -->
              <div class="pagination-container">
                <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[10, 20, 50, 100]"
                  :total="total"
                  layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                />
              </div>
            </el-tab-pane>

            <!-- 组织架构管理标签页 -->
            <el-tab-pane label="组织架构管理" name="orgManagement">
              <div class="org-action-area">
                <el-input
                  v-model="orgSearchKeyword"
                  placeholder="搜索组织"
                  clearable
                  style="width: 200px;"
                  @input="handleOrgSearch"
                >
                  <template #prefix>
                    <el-icon><search /></el-icon>
                  </template>
                </el-input>
              </div>

              <div style="display: flex; gap: 20px; height: calc(100vh - 300px);">
                <!-- 左侧组织树 -->
                <div style="width: 300px; background: white; border: 1px solid #e8e8e8; border-radius: 6px; padding: 15px;">
                  <el-tree
                    ref="orgTreeRef"
                    :data="orgTreeData"
                    :props="{ children: 'children', label: 'name' }"
                    node-key="id"
                    default-expand-all
                    :expand-on-click-node="false"
                    @node-click="handleOrgTreeNodeClick"
                  />
                </div>

                <!-- 右侧详情和成员 -->
                <div style="flex: 1; display: flex; flex-direction: column; gap: 20px;">
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
                    <div v-else style="text-align: center; padding: 40px 20px; color: #999;">
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
            </el-tab-pane>

            <!-- 职务管理标签页 -->
            <el-tab-pane label="职务管理" name="positionManagement">
              <!-- 搜索区域 -->
              <div class="position-search-area">
                <el-form inline>
                  <el-form-item label="职务名称:">
                    <el-input 
                      v-model="positionSearchKeyword" 
                      placeholder="请输入职务名称" 
                      clearable 
                      style="width: 200px;"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handlePositionSearch">搜索</el-button>
                    <el-button @click="positionSearchKeyword = ''">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>

              <!-- 表格容器 -->
              <div class="position-table-container">
                <div style="padding: 15px 20px; border-bottom: 1px solid #e8e8e8;">
                  <el-button type="primary" @click="handleAddPosition">
                    <el-icon><plus /></el-icon>
                    新增职务
                  </el-button>
                </div>

                <el-table :data="positionTableData" style="width: 100%;" border stripe>
                  <el-table-column prop="name" label="职务名称" width="200" />
                  <el-table-column prop="description" label="职务描述" />
                  <el-table-column prop="createTime" label="创建时间" width="180" />
                  <el-table-column label="操作" width="200">
                    <template #default="{ row }">
                      <el-button type="text" size="small" @click="handleEditPosition(row)">编辑</el-button>
                      <el-button 
                        type="text" 
                        size="small" 
                        @click="handleDeletePosition(row)" 
                        style="color: #f56c6c;"
                      >
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <!-- 分页 -->
                <div class="position-pagination-container">
                  <el-pagination
                    v-model:current-page="positionCurrentPage"
                    v-model:page-size="positionPageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="positionTotal"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handlePositionSizeChange"
                    @current-change="handlePositionCurrentChange"
                  />
                </div>
              </div>
            </el-tab-pane>

            <!-- 职称管理标签页 -->
            <el-tab-pane label="职称管理" name="titleManagement">
              <!-- 搜索区域 -->
              <div class="title-search-area">
                <el-form inline>
                  <el-form-item label="职称名称:">
                    <el-input 
                      v-model="titleSearchKeyword" 
                      placeholder="请输入职称名称" 
                      clearable 
                      style="width: 200px;"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="handleTitleSearch">搜索</el-button>
                    <el-button @click="titleSearchKeyword = ''">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>

              <!-- 表格容器 -->
              <div class="title-table-container">
                <div style="padding: 15px 20px; border-bottom: 1px solid #e8e8e8;">
                  <el-button type="primary" @click="handleAddTitle">
                    <el-icon><plus /></el-icon>
                    新增职称
                  </el-button>
                </div>

                <el-table :data="titleTableData" style="width: 100%;" border stripe>
                  <el-table-column prop="name" label="职称名称" width="200" />
                  <el-table-column prop="description" label="职称描述" />
                  <el-table-column prop="createTime" label="创建时间" width="180" />
                  <el-table-column label="操作" width="200">
                    <template #default="{ row }">
                      <el-button type="text" size="small" @click="handleEditTitle(row)">编辑</el-button>
                      <el-button 
                        type="text" 
                        size="small" 
                        @click="handleDeleteTitle(row)" 
                        style="color: #f56c6c;"
                      >
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>

                <!-- 分页 -->
                <div class="title-pagination-container">
                  <el-pagination
                    v-model:current-page="titleCurrentPage"
                    v-model:page-size="titlePageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="titleTotal"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleTitleSizeChange"
                    @current-change="handleTitleCurrentChange"
                  />
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="用户详情" width="50%">
      <div v-if="currentUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ currentUser.realName }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ currentUser.gender }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="工号">{{ currentUser.jobNumber }}</el-descriptions-item>
          <el-descriptions-item label="所属部门">{{ currentUser.department }}</el-descriptions-item>
          <el-descriptions-item label="职务">{{ currentUser.position }}</el-descriptions-item>
          <el-descriptions-item label="职称">{{ currentUser.jobTitle }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentUser.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 编辑/新增用户对话框 -->
    <el-dialog 
      v-model="editDialogVisible" 
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="600px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名:" prop="realName">
              <el-input v-model="editForm.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别:" prop="gender">
              <el-radio-group v-model="editForm.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号:" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号:" prop="jobNumber">
              <el-input v-model="editForm.jobNumber" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门:" prop="department">
              <el-select v-model="editForm.department" placeholder="请选择部门" style="width: 100%;">
                <el-option label="技术部" value="技术部" />
                <el-option label="行政部" value="行政部" />
                <el-option label="销售部" value="销售部" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职务:" prop="position">
              <el-select v-model="editForm.position" placeholder="请选择职务" style="width: 100%;">
                <el-option label="前端工程师" value="前端工程师" />
                <el-option label="后端工程师" value="后端工程师" />
                <el-option label="人事专员" value="人事专员" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职称:" prop="jobTitle">
              <el-select v-model="editForm.jobTitle" placeholder="请选择职称" style="width: 100%;">
                <el-option label="初级工程师" value="初级工程师" />
                <el-option label="中级工程师" value="中级工程师" />
                <el-option label="高级工程师" value="高级工程师" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态:" prop="status">
              <el-radio-group v-model="editForm.status">
                <el-radio value="正常">正常</el-radio>
                <el-radio value="禁用">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveUser">确定</el-button>
        </div>
      </template>
    </el-dialog>

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

    <!-- 职务新增/编辑对话框 -->
    <el-dialog
      v-model="positionDialogVisible"
      :title="positionDialogMode === 'add' ? '新增职务' : '编辑职务'"
      width="500px"
      @close="handlePositionDialogClose"
    >
      <el-form
        ref="positionFormRef"
        :model="positionFormData"
        :rules="positionFormRules"
        label-width="100px"
      >
        <el-form-item label="职务名称:" prop="name">
          <el-input v-model="positionFormData.name" placeholder="请输入职务名称" />
        </el-form-item>
        <el-form-item label="职务描述:" prop="description">
          <el-input
            v-model="positionFormData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入职务描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handlePositionDialogClose">取消</el-button>
          <el-button type="primary" @click="handlePositionSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 职称新增/编辑对话框 -->
    <el-dialog
      v-model="titleDialogVisible"
      :title="titleDialogMode === 'add' ? '新增职称' : '编辑职称'"
      width="500px"
      @close="handleTitleDialogClose"
    >
      <el-form
        ref="titleFormRef"
        :model="titleFormData"
        :rules="titleFormRules"
        label-width="100px"
      >
        <el-form-item label="职称名称:" prop="name">
          <el-input v-model="titleFormData.name" placeholder="请输入职称名称" />
        </el-form-item>
        <el-form-item label="职称描述:" prop="description">
          <el-input
            v-model="titleFormData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入职称描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleTitleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleTitleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
// 导入拆分后的逻辑
import { useUserManagement } from '@/composables/useUserManagement.js'
import { useOrganizationManagement } from '@/composables/useOrganizationManagement.js'
import { usePositionManagement } from '@/composables/usePositionManagement.js'
import { useTitleManagement } from '@/composables/useTitleManagement.js'

export default {
  name: 'UserManagement',
  setup() {
    // 使用拆分后的逻辑
    const userManagement = useUserManagement()
    const organizationManagement = useOrganizationManagement()
    const positionManagement = usePositionManagement()
    const titleManagement = useTitleManagement()

    return {
      // 用户管理相关
      ...userManagement,
      
      // 组织架构管理相关
      ...organizationManagement,
      
      // 职务管理相关
      ...positionManagement,
      
      // 职称管理相关
      ...titleManagement
    }
  }
}
</script>

<style scoped>
/* 完全保持原始样式不变 */
.user-management {
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
  padding: 20px;
  flex-shrink: 0;
  overflow-y: auto;
}

.search-header {
  margin-bottom: 20px;
}

.tree-container {
  min-height: 500px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.tree-icon {
  flex-shrink: 0;
}

.company-icon {
  color: #4A90E2;
}

.department-icon {
  color: #F39C12;
}

.group-icon {
  color: #27AE60;
}

.node-label {
  flex: 1;
  font-size: 14px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.node-count {
  color: #999;
  font-size: 12px;
  flex-shrink: 0;
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: white;
}

.tabs {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.list-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.table-container {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
}

.user-detail {
  padding: 20px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 组织架构管理样式 */
.org-action-area {
  margin: 15px 0;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
  display: flex;
  gap: 10px;
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

/* 职务管理样式 */
.position-search-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.position-table-container {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  margin-bottom: 20px;
}

.position-pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 职称管理样式 */
.title-search-area {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 6px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.title-table-container {
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  margin-bottom: 20px;
}

.title-pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 通用表格样式优化 */
:deep(.el-table) {
  border-radius: 6px;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #333;
  font-weight: 600;
}

:deep(.el-table td) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__row:hover > td) {
  background-color: #f5f7fa;
}

/* 操作按钮样式 */
:deep(.el-button--text) {
  padding: 4px 8px;
  margin-right: 8px;
}

:deep(.el-button--text:last-child) {
  margin-right: 0;
}

/* 分页样式优化 */
:deep(.el-pagination) {
  padding: 20px 0;
}

:deep(.el-pagination .el-select .el-input) {
  width: 100px;
}

/* 弹窗样式优化 */
:deep(.el-dialog__header) {
  background-color: #f8f9fa;
  padding: 20px 24px;
  border-bottom: 1px solid #e8e8e8;
}

:deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

/* Element Plus 样式覆盖 */
:deep(.el-tree-node__content) {
  height: 36px;
  line-height: 36px;
}

:deep(.el-tree-node__expand-icon) {
  color: #666;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
}

:deep(.el-tabs__active-bar) {
  background-color: #4A90E2;
}

:deep(.el-tabs__item.is-active) {
  color: #4A90E2;
}

:deep(.el-button--primary) {
  background-color: #4A90E2;
  border-color: #4A90E2;
}

:deep(.el-button--primary:hover) {
  background-color: #357ABD;
  border-color: #357ABD;
}
</style>