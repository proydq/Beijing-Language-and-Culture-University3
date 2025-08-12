import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  // 用户基本信息
  const userInfo = ref({
    id: '',
    username: '',
    realName: '',
    customerId: ''
  })

  // 用户token
  const token = ref('')

  // 用户权限列表
  const permissions = ref([])

  // 用户权限详情
  const permissionDetails = ref([])

  // 用户角色列表
  const roles = ref([])
  
  // 权限树结构（用于生成菜单和路由）
  const permissionTree = ref([])

  // 是否已登录
  const isLoggedIn = computed(() => !!token.value)

  // 是否有某个权限
  const hasPermission = (permissionCode) => {
    if (!permissionCode) return true
    
    // 如果有对应的MANAGE权限，则拥有该模块的所有权限
    const module = permissionCode.split('_')[0]
    const managePermission = `${module}_MANAGE`
    
    return permissions.value.includes(permissionCode) || 
           permissions.value.includes(managePermission)
  }

  // 是否有任意一个权限
  const hasAnyPermission = (...permissionCodes) => {
    return permissionCodes.some(code => hasPermission(code))
  }

  // 是否有所有权限
  const hasAllPermissions = (...permissionCodes) => {
    return permissionCodes.every(code => hasPermission(code))
  }

  // 是否有某个角色
  const hasRole = (roleCode) => {
    return roles.value.includes(roleCode)
  }

  // 设置用户信息（登录时调用）
  const setUserData = (userData) => {
    token.value = userData.token || ''
    userInfo.value = {
      id: userData.userId || userData.id || '',
      username: userData.username || '',
      realName: userData.realName || '',
      customerId: userData.customerId || ''
    }
    permissions.value = userData.permissions || []
    permissionDetails.value = userData.permissionDetails || []
    roles.value = userData.roles || []
    permissionTree.value = userData.permissionTree || []

    // 保存到localStorage
    localStorage.setItem('userToken', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    localStorage.setItem('userPermissions', JSON.stringify(permissions.value))
    localStorage.setItem('userPermissionDetails', JSON.stringify(permissionDetails.value))
    localStorage.setItem('userRoles', JSON.stringify(roles.value))
    localStorage.setItem('userPermissionTree', JSON.stringify(permissionTree.value))
  }

  // 从localStorage加载用户信息
  const loadFromStorage = () => {
    const storedToken = localStorage.getItem('userToken')
    const storedUserInfo = localStorage.getItem('userInfo')
    const storedPermissions = localStorage.getItem('userPermissions')
    const storedPermissionDetails = localStorage.getItem('userPermissionDetails')
    const storedRoles = localStorage.getItem('userRoles')
    const storedPermissionTree = localStorage.getItem('userPermissionTree')

    if (storedToken) {
      token.value = storedToken
    }

    if (storedUserInfo) {
      try {
        userInfo.value = JSON.parse(storedUserInfo)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }

    if (storedPermissions) {
      try {
        permissions.value = JSON.parse(storedPermissions)
      } catch (e) {
        console.error('解析权限列表失败:', e)
      }
    }

    if (storedPermissionDetails) {
      try {
        permissionDetails.value = JSON.parse(storedPermissionDetails)
      } catch (e) {
        console.error('解析权限详情失败:', e)
      }
    }

    if (storedRoles) {
      try {
        roles.value = JSON.parse(storedRoles)
      } catch (e) {
        console.error('解析角色列表失败:', e)
      }
    }
    
    if (storedPermissionTree) {
      try {
        permissionTree.value = JSON.parse(storedPermissionTree)
      } catch (e) {
        console.error('解析权限树失败:', e)
      }
    }
  }

  // 清除用户信息（退出登录时调用）
  const clearUserData = () => {
    token.value = ''
    userInfo.value = {
      id: '',
      username: '',
      realName: '',
      customerId: ''
    }
    permissions.value = []
    permissionDetails.value = []
    roles.value = []
    permissionTree.value = []

    // 清除localStorage
    localStorage.removeItem('userToken')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userPermissions')
    localStorage.removeItem('userPermissionDetails')
    localStorage.removeItem('userRoles')
    localStorage.removeItem('userPermissionTree')
  }

  // 获取用户显示名称
  const displayName = computed(() => {
    return userInfo.value.realName || userInfo.value.username || '用户'
  })

  // 是否是管理员
  const isAdmin = computed(() => {
    return hasRole('admin') || hasRole('role001') || hasPermission('SYSTEM_MANAGE')
  })

  // 初始化时从localStorage加载
  loadFromStorage()

  return {
    // 状态
    userInfo,
    token,
    permissions,
    permissionDetails,
    roles,
    permissionTree,
    
    // 计算属性
    isLoggedIn,
    displayName,
    isAdmin,
    
    // 方法
    setUserData,
    clearUserData,
    loadFromStorage,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    hasRole
  }
})