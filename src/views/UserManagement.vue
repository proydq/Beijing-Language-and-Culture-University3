<template>
  <div class="user-management">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-left">
        <div class="logo" @click="goToHome" style="cursor: pointer">
          <el-icon size="24"><home-filled /></el-icon>
        </div>
        <span class="title">用户信息管理</span>
      </div>
      <div class="header-right">
        <div class="avatar">
          <el-avatar 
            v-if="userStore.userInfo.avatarUrl" 
            :src="userStore.userInfo.avatarUrl"
            :size="32"
            @error="handleAvatarError"
          >
            <el-icon v-if="avatarError" size="20"><user /></el-icon>
          </el-avatar>
          <el-icon v-else size="20"><user /></el-icon>
        </div>
        <span class="username">{{ userStore.displayName }}</span>
        <!-- 调试信息 -->
        <span style="display: none">
          {{ console.log('displayName:', userStore.displayName) }}
          {{ console.log('userInfo:', userStore.userInfo) }}
          {{ console.log('avatarUrl:', userStore.userInfo.avatarUrl) }}
        </span>
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
      <div class="sidebar" v-if="activeTab === 'userList'">
        <!-- 搜索框 -->
        <div class="search-header">
          <el-input v-model="searchKeyword" placeholder="请输入部门名称" clearable>
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
                <el-icon v-if="data.type === 'company'" class="tree-icon company-icon"
                  ><office-building
                /></el-icon>
                <el-icon v-else-if="data.type === 'department'" class="tree-icon department-icon"
                  ><folder
                /></el-icon>
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
                      style="width: 180px"
                    />
                  </el-form-item>
                  <el-form-item label="手机号:">
                    <el-input
                      v-model="searchForm.phone"
                      placeholder="请输入手机号"
                      clearable
                      style="width: 180px"
                    />
                  </el-form-item>
                  <el-form-item label="工号:">
                    <el-input
                      v-model="searchForm.jobNumber"
                      placeholder="请输入工号"
                      clearable
                      style="width: 150px"
                    />
                  </el-form-item>
                  <el-form-item label="状态:">
                    <el-select
                      v-model="searchForm.status"
                      placeholder="请选择状态"
                      clearable
                      style="width: 120px"
                    >
                      <el-option label="正常" value="NORMAL" />
                      <el-option label="禁用" value="DISABLED" />
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
                  <el-dropdown @command="handleSyncCommand">
                    <el-button type="success">
                      <el-icon><refresh /></el-icon>
                      同步<el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="syncNow">立即同步</el-dropdown-item>
                        <el-dropdown-item command="viewLogs">查看同步记录</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
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
                <el-table :data="tableData" style="width: 100%" border stripe>
                  <el-table-column type="index" label="序号" width="60" />
                  <el-table-column prop="avatarUrl" label="头像" width="180">
                    <template #default="{ row }">
                      <el-image
                        v-if="row.avatarUrl"
                        :src="row.avatarUrl"
                        style="width: 50px; height: 50px; border-radius: 50%"
                        fit="cover"
                        :preview-src-list="[row.avatarUrl]"
                      />
                      <span v-else style="color: #999">暂无头像</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="faceImageUrl" label="人脸识别" width="180">
                    <template #default="{ row }">
                      <el-image
                        v-if="row.faceImageUrl"
                        :src="row.faceImageUrl"
                        style="width: 50px; height: 50px; border-radius: 50%"
                        fit="cover"
                        :preview-src-list="[row.faceImageUrl]"
                      />
                      <span v-else style="color: #999">暂无人脸图片</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="realName" label="姓名" />
                  <el-table-column prop="gender" label="性别" />
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">
                        {{ row.status === 'NORMAL' ? '正常' : '禁用' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="phone" label="手机号" />
                  <el-table-column prop="departmentName" label="所属部门" />
                  <el-table-column prop="jobNumber" label="工号" />
                  <el-table-column prop="positionName" label="职务" />
                  <el-table-column prop="titleName" label="职称" />
                  <el-table-column prop="createTime" label="账号添加时间" width="160" />
                  <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                      <el-button type="text" size="small" @click="handleEdit(row)">编辑</el-button>
                      <el-button
                        type="text"
                        size="small"
                        @click="handleDelete(row)"
                        style="color: #f56c6c"
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
                  style="width: 200px"
                  @input="handleOrgSearch"
                >
                  <template #prefix>
                    <el-icon><search /></el-icon>
                  </template>
                </el-input>
              </div>

              <div style="display: flex; gap: 20px; height: calc(100vh - 300px)">
                <!-- 左侧组织树 -->
                <div
                  style="
                    width: 300px;
                    background: white;
                    border: 1px solid #e8e8e8;
                    border-radius: 6px;
                    padding: 15px;
                  "
                >
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
                <div style="flex: 1; display: flex; flex-direction: column; gap: 20px">
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
                    <div v-else style="text-align: center; padding: 40px 20px; color: #999">
                      <el-icon size="48" color="#ccc"><folder /></el-icon>
                      <p>请从左侧选择一个组织查看详情</p>
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
                      style="width: 200px"
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
                <div
                  style="
                    padding: 15px 20px;
                    border-bottom: 1px solid #e8e8e8;
                    display: flex;
                    justify-content: flex-end;
                    gap: 10px;
                  "
                >
                  <el-button type="primary" @click="handleAddPosition">
                    <el-icon><plus /></el-icon>
                    新增职务
                  </el-button>
                  <el-button type="warning" @click="handleImport">
                    <el-icon><upload /></el-icon>
                    导入
                  </el-button>
                  <el-button type="info" @click="handleExport">
                    <el-icon><download /></el-icon>
                    导出
                  </el-button>
                  <el-dropdown @command="handleSyncCommand">
                    <el-button type="success">
                      <el-icon><refresh /></el-icon>
                      同步<el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </el-button>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="syncNow">立即同步</el-dropdown-item>
                        <el-dropdown-item command="viewLogs">查看同步记录</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>

                <el-table :data="positionTableData" style="width: 100%" border stripe>
                  <el-table-column prop="name" label="职务名称" width="200" />
                  <el-table-column prop="description" label="职务描述" />
                  <el-table-column prop="createTime" label="创建时间" width="180" />
                  <el-table-column label="操作" width="200">
                    <template #default="{ row }">
                      <el-button type="text" size="small" @click="handleEditPosition(row)"
                        >编辑</el-button
                      >
                      <el-button
                        type="text"
                        size="small"
                        @click="handleDeletePosition(row)"
                        style="color: #f56c6c"
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
                      style="width: 200px"
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
                <div
                  style="
                    padding: 15px 20px;
                    border-bottom: 1px solid #e8e8e8;
                    display: flex;
                    justify-content: space-between;
                  "
                >
                  <div style="display: flex; gap: 10px; margin-left: auto">
                    <el-button type="primary" @click="handleAddTitle">
                      <el-icon><plus /></el-icon>
                      新增职称
                    </el-button>
                    <el-button type="warning" @click="handleImport">
                      <el-icon><upload /></el-icon>
                      导入
                    </el-button>
                    <el-button type="info" @click="handleExport">
                      <el-icon><download /></el-icon>
                      导出
                    </el-button>
                    <el-dropdown @command="handleSyncCommand">
                      <el-button type="success">
                        <el-icon><refresh /></el-icon>
                        同步<el-icon class="el-icon--right"><arrow-down /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item command="syncNow">立即同步</el-dropdown-item>
                          <el-dropdown-item command="viewLogs">查看同步记录</el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>
                </div>

                <el-table :data="titleTableData" style="width: 100%" border stripe>
                  <el-table-column prop="name" label="职称名称" width="200" />
                  <el-table-column prop="description" label="职称描述" />
                  <el-table-column prop="createTime" label="创建时间" width="180" />
                  <el-table-column label="操作" width="200">
                    <template #default="{ row }">
                      <el-button type="text" size="small" @click="handleEditTitle(row)"
                        >编辑</el-button
                      >
                      <el-button
                        type="text"
                        size="small"
                        @click="handleDeleteTitle(row)"
                        style="color: #f56c6c"
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
      width="700px"
      destroy-on-close
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="userForm" :rules="formRules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="头像" prop="avatar">
              <el-upload
                v-model:file-list="avatarList"
                list-type="picture-card"
                :auto-upload="false"
                :limit="1"
                :on-change="handleAvatarChange"
              >
                <el-icon><plus /></el-icon>
              </el-upload>
              <el-progress 
                v-if="avatarCalculating || avatarUploadProgress > 0"
                :percentage="avatarCalculating ? 50 : avatarUploadProgress"
                :status="avatarCalculating ? 'warning' : ''"
                style="margin-top: 10px"
              >
                <template #default="{ percentage }">
                  <span>{{ avatarCalculating ? '计算文件特征...' : `上传中 ${percentage}%` }}</span>
                </template>
              </el-progress>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="人脸识别照片" prop="faceImage">
              <el-upload
                v-model:file-list="faceList"
                list-type="picture-card"
                :auto-upload="false"
                :limit="1"
                :on-change="handleFaceChange"
              >
                <el-icon><plus /></el-icon>
              </el-upload>
              <el-progress 
                v-if="faceCalculating || faceUploadProgress > 0"
                :percentage="faceCalculating ? 50 : faceUploadProgress"
                :status="faceCalculating ? 'warning' : ''"
                style="margin-top: 10px"
              >
                <template #default="{ percentage }">
                  <span>{{ faceCalculating ? '计算文件特征...' : `上传中 ${percentage}%` }}</span>
                </template>
              </el-progress>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="userForm.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="userForm.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="department">
              <el-select v-model="userForm.department" placeholder="请选择部门" style="width: 100%">
                <el-option
                  v-for="item in departments"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工号" prop="jobNumber">
              <el-input v-model="userForm.jobNumber" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职务" prop="position">
              <el-select v-model="userForm.position" placeholder="请选择职务" style="width: 100%">
                <el-option
                  v-for="item in positions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职称" prop="jobTitle">
              <el-select v-model="userForm.jobTitle" placeholder="请选择职称" style="width: 100%">
                <el-option
                  v-for="item in titles"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="一卡通编号" prop="cardNumber">
              <el-input v-model="userForm.cardNumber" placeholder="请输入一卡通编号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="无感考勤编号" prop="attendanceNumber">
              <el-input v-model="userForm.attendanceNumber" placeholder="请输入无感考勤编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户类型" prop="userType">
              <el-select 
                v-model="userForm.userType" 
                placeholder="请选择用户类型"
                style="width: 100%"
                :loading="userTypesLoading"
              >
                <el-option
                  v-for="item in userTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
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

    <!-- 同步进度对话框 -->
    <el-dialog v-model="syncDialogVisible" title="同步进度" width="600px">
      <el-progress :percentage="syncProgress" :status="syncProgress < 100 ? 'active' : 'success'" />
      <p style="margin-top: 10px">本次同步耗时 {{ syncDuration }} 秒</p>
      <el-table :data="syncSummary" border stripe style="margin-top: 20px">
        <el-table-column prop="object" label="同步对象" />
        <el-table-column prop="auto" label="是否自动同步" />
        <el-table-column prop="lastTime" label="最近一次同步时间" />
      </el-table>
    </el-dialog>

    <!-- 同步记录对话框 -->
    <el-dialog v-model="syncLogDialogVisible" title="同步记录" width="600px">
      <el-table :data="syncLogData" border stripe style="width: 100%; margin-bottom: 20px">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="object" label="同步对象" />
        <el-table-column prop="status" label="同步状态" />
        <el-table-column prop="reason" label="失败原因" />
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column prop="duration" label="同步时长(分钟)" />
      </el-table>
      <el-pagination
        v-model:current-page="logCurrentPage"
        v-model:page-size="logPageSize"
        :page-sizes="[10, 20, 50]"
        :total="logTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleLogSizeChange"
        @current-change="handleLogCurrentChange"
      />
    </el-dialog>
  </div>
</template>

<script>
// 导入拆分后的逻辑
import { ref, onMounted } from 'vue'
import { useUserManagement } from '@/composables/useUserManagement.js'
import { useOrganizationManagement } from '@/composables/useOrganizationManagement.js'
import { usePositionManagement } from '@/composables/usePositionManagement.js'
import { useTitleManagement } from '@/composables/useTitleManagement.js'
import { useUserStore } from '@/stores/user'
import { userAPI } from '@/api/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'UserManagement',
  setup() {
    // 使用拆分后的逻辑
    const userManagement = useUserManagement()
    const organizationManagement = useOrganizationManagement()
    const positionManagement = usePositionManagement()
    const titleManagement = useTitleManagement()
    
    // 用户store
    const userStore = useUserStore()
    
    // 头像加载错误标志
    const avatarError = ref(false)
    
    // 处理头像加载错误
    const handleAvatarError = () => {
      avatarError.value = true
    }
    
    // 获取当前用户详情
    const fetchCurrentUserInfo = async () => {
      console.log('开始获取用户详情...')
      console.log('用户登录状态:', userStore.isLoggedIn)
      console.log('当前用户信息:', userStore.userInfo)
      
      if (userStore.isLoggedIn) {
        try {
          console.log('调用 getCurrentUser API...')
          const res = await userAPI.getCurrentUser()
          console.log('API 响应:', res)
          
          if (res.code === 200 || res.code === '0' || res.code === 0) {
            console.log('更新前的用户信息:', userStore.userInfo)
            
            // 更新用户信息（保留原有的权限等信息）
            const updatedData = {
              ...res.data,
              token: userStore.token,
              permissions: userStore.permissions,
              permissionDetails: userStore.permissionDetails,
              roles: userStore.roles,
              permissionTree: userStore.permissionTree
            }
            console.log('准备更新的数据:', updatedData)
            
            userStore.setUserData(updatedData)
            console.log('更新后的用户信息:', userStore.userInfo)
            
            // 重置头像错误标志
            avatarError.value = false
          } else {
            console.warn('API 返回非成功状态码:', res)
          }
        } catch (error) {
          console.error('获取用户信息失败:', error)
          console.error('错误详情:', error.response || error)
          // 静默失败，不显示错误提示
        }
      } else {
        console.log('用户未登录，跳过获取用户详情')
      }
    }
    
    // 页面加载时获取用户详情
    onMounted(() => {
      fetchCurrentUserInfo()
    })

    return {
      // 用户store
      userStore,
      avatarError,
      handleAvatarError,
      
      // 用户管理相关
      ...userManagement,

      // 组织架构管理相关
      ...organizationManagement,

      // 职务管理相关
      ...positionManagement,

      // 职称管理相关
      ...titleManagement,
    }
  },
}
</script>

<style scoped lang="scss">
@use 'sass:color';

// 颜色变量
$primary-color: #409EFF;
$success-color: #67C23A;
$warning-color: #E6A23C;
$danger-color: #F56C6C;
$info-color: #909399;

// 文字颜色
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$text-placeholder: #C0C4CC;

// 边框颜色
$border-base: #DCDFE6;
$border-light: #E4E7ED;
$border-lighter: #EBEEF5;
$border-extra-light: #F2F6FC;

// 背景颜色
$bg-color: #F5F7FA;
$bg-page: #F0F2F5;
$bg-white: #FFFFFF;

// 字体大小
$font-size-xs: 12px;
$font-size-sm: 13px;
$font-size-base: 14px;
$font-size-md: 16px;
$font-size-lg: 18px;
$font-size-xl: 20px;

// 间距
$spacing-xs: 5px;
$spacing-sm: 10px;
$spacing-md: 15px;
$spacing-lg: 20px;
$spacing-xl: 30px;

// 阴影
$box-shadow-base: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
$box-shadow-light: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

// 过渡
$transition-duration: 0.3s;

// 布局尺寸
$header-height: 60px;
$sidebar-width: 240px;

.user-management {
  min-height: 100vh;
  background-color: $bg-page;
}

// 顶部导航
.header {
  height: $header-height;
  background: $bg-white;
  border-bottom: 1px solid $border-light;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 $spacing-lg;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: $spacing-md;
}

.logo {
  width: 32px;
  height: 32px;
  border-radius: 4px;
  background: $primary-color;
  color: $bg-white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all $transition-duration;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $box-shadow-base;
  }
}

.title {
  font-size: $font-size-lg;
  font-weight: 500;
  color: $text-primary;
}

.header-right {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: $bg-color;
  color: $text-regular;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-size-base;
}

.username {
  font-size: $font-size-base;
  color: $text-regular;
}

.dropdown-link {
  cursor: pointer;
  padding: $spacing-xs;
  border-radius: 4px;
  color: $text-regular;
  transition: all $transition-duration;
  
  &:hover {
    background-color: $bg-color;
    color: $primary-color;
  }
}

// 主容器
.main-container {
  display: flex;
  height: calc(100vh - #{$header-height});
}

// 侧边栏
.sidebar {
  width: $sidebar-width;
  background: $bg-white;
  border-right: 1px solid $border-light;
  padding: $spacing-lg;
  flex-shrink: 0;
  overflow-y: auto;
  
  // 自定义滚动条
  &::-webkit-scrollbar {
    width: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background: $border-extra-light;
  }
  
  &::-webkit-scrollbar-thumb {
    background: $border-light;
    border-radius: 4px;
    
    &:hover {
      background: $border-base;
    }
  }
}

.search-header {
  margin-bottom: $spacing-lg;
}

.tree-container {
  min-height: 500px;
}

// 树形节点样式
.tree-node {
  display: flex;
  align-items: center;
  gap: $spacing-xs;
  width: 100%;
  padding: $spacing-xs 0;
}

.tree-icon {
  flex-shrink: 0;
  font-size: $font-size-md;
}

.company-icon {
  color: $primary-color;
}

.department-icon {
  color: $warning-color;
}

.group-icon {
  color: $success-color;
}

.node-label {
  flex: 1;
  font-size: $font-size-base;
  color: $text-primary;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.node-count {
  color: $text-secondary;
  font-size: $font-size-xs;
  flex-shrink: 0;
}

// 主内容区
.main-content {
  flex: 1;
  padding: $spacing-lg;
  overflow-y: auto;
  background: $bg-page;
}

// 标签页容器
.tabs {
  background: $bg-white;
  border-radius: 4px;
  padding: $spacing-lg;
  box-shadow: $box-shadow-light;
}

// 搜索区域
.search-area {
  background: $bg-color;
  padding: $spacing-lg;
  border-radius: 4px;
  margin-bottom: $spacing-lg;
}

.search-form {
  margin-bottom: 0;
}

// 列表头部
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-md;
  border-bottom: 1px solid $border-lighter;
}

.list-header h3 {
  margin: 0;
  color: $text-primary;
  font-size: $font-size-lg;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: $spacing-sm;
}

// 表格容器
.table-container {
  margin-bottom: $spacing-lg;
}

// 分页容器
.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-lg;
}

// 用户详情
.user-detail {
  padding: $spacing-lg 0;
}

// 对话框底部
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: $spacing-sm;
}

// 组织架构管理样式
.org-action-area {
  margin: $spacing-md 0;
  padding: $spacing-sm;
  background: $bg-color;
  border-radius: 4px;
  display: flex;
  gap: $spacing-sm;
}

.detail-area {
  background: $bg-white;
  border: 1px solid $border-light;
  border-radius: 4px;
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-md;
  border-bottom: 1px solid $border-lighter;
}

.detail-header h3 {
  margin: 0;
  color: $text-primary;
  font-size: $font-size-md;
  font-weight: 500;
}

.detail-actions {
  display: flex;
  gap: $spacing-sm;
}

.org-form {
  margin-top: $spacing-lg;
}

// 职务管理样式
.position-search-area {
  background: $bg-color;
  padding: $spacing-lg;
  border-radius: 4px;
  margin-bottom: $spacing-lg;
  display: flex;
  align-items: center;
}

.position-table-container {
  background: $bg-white;
  border: 1px solid $border-light;
  border-radius: 4px;
  margin-bottom: $spacing-lg;
}

.position-pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-lg;
}

// 职称管理样式
.title-search-area {
  background: $bg-color;
  padding: $spacing-lg;
  border-radius: 4px;
  margin-bottom: $spacing-lg;
  display: flex;
  align-items: center;
}

.title-table-container {
  background: $bg-white;
  border: 1px solid $border-light;
  border-radius: 4px;
  margin-bottom: $spacing-lg;
}

.title-pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-lg;
}

// 通用表格样式优化
:deep(.el-table) {
  border-radius: 4px;
  font-size: $font-size-base;
}

:deep(.el-table th) {
  background-color: $bg-color;
  color: $text-primary;
  font-weight: 500;
  padding: $spacing-sm 0;
}

:deep(.el-table td) {
  border-bottom: 1px solid $border-extra-light;
  color: $text-regular;
}

:deep(.el-table__row:hover > td) {
  background-color: $bg-color;
}

// 操作按钮样式
:deep(.el-button--text) {
  padding: $spacing-xs $spacing-sm;
  margin-right: $spacing-sm;
  color: $primary-color;
  
  &:last-child {
    margin-right: 0;
  }
  
  &:hover {
    color: color.adjust($primary-color, $lightness: -10%);
  }
}

// 分页样式优化
:deep(.el-pagination) {
  padding: $spacing-lg 0;
  
  .el-select .el-input {
    width: 100px;
  }
}

// 弹窗样式优化
:deep(.el-dialog) {
  border-radius: 4px;
  
  .el-dialog__header {
    background-color: $bg-color;
    padding: $spacing-lg;
    border-bottom: 1px solid $border-light;
    border-radius: 4px 4px 0 0;
  }
  
  .el-dialog__title {
    font-size: $font-size-lg;
    font-weight: 500;
    color: $text-primary;
  }
  
  .el-dialog__body {
    padding: $spacing-lg;
  }
}

// 表单样式
:deep(.el-form-item__label) {
  font-weight: normal;
  color: $text-primary;
  font-size: $font-size-base;
}

// Element Plus 组件样式覆盖
:deep(.el-tree-node__content) {
  height: 36px;
  line-height: 36px;
  
  &:hover {
    background-color: $bg-color;
  }
}

:deep(.el-tree-node__expand-icon) {
  color: $text-secondary;
}

// 标签页样式
:deep(.el-tabs__item) {
  font-size: $font-size-md;
  font-weight: 500;
  color: $text-regular;
  
  &.is-active {
    color: $primary-color;
  }
  
  &:hover {
    color: $primary-color;
  }
}

:deep(.el-tabs__active-bar) {
  background-color: $primary-color;
}

// 按钮主题覆盖
:deep(.el-button--primary) {
  background-color: $primary-color;
  border-color: $primary-color;
  
  &:hover,
  &:focus {
    background-color: color.adjust($primary-color, $lightness: -10%);
    border-color: color.adjust($primary-color, $lightness: -10%);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: auto;
    border-right: none;
    border-bottom: 1px solid $border-light;
  }
  
  .search-form {
    .el-form-item {
      display: block;
      margin-bottom: $spacing-sm;
    }
  }
  
  .header-actions {
    flex-wrap: wrap;
  }
  
  .dialog-footer {
    .el-button {
      margin-left: 0;
      margin-right: $spacing-sm;
    }
  }
}
</style>
