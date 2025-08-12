import { useUserStore } from '@/stores/user'

/**
 * 权限指令
 * 使用方式：
 * v-permission="'USER_ADD'" - 单个权限
 * v-permission="['USER_ADD', 'USER_EDIT']" - 多个权限（满足任意一个即可）
 * v-permission.all="['USER_ADD', 'USER_EDIT']" - 多个权限（必须全部满足）
 */

function checkPermission(el, binding) {
  const { value, modifiers } = binding
  const userStore = useUserStore()

  if (!value) {
    console.warn('v-permission 指令需要提供权限值')
    return
  }

  let hasPermission = false

  if (typeof value === 'string') {
    // 单个权限
    hasPermission = userStore.hasPermission(value)
  } else if (Array.isArray(value)) {
    // 多个权限
    if (modifiers.all) {
      // 需要所有权限
      hasPermission = userStore.hasAllPermissions(...value)
    } else {
      // 只需要任意一个权限
      hasPermission = userStore.hasAnyPermission(...value)
    }
  } else {
    console.warn('v-permission 指令的值必须是字符串或数组')
    return
  }

  if (!hasPermission) {
    // 没有权限，移除元素
    el.style.display = 'none'
    // 或者可以选择完全移除元素
    // el.parentNode?.removeChild(el)
  } else {
    el.style.display = ''
  }
}

export default {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
}

// 角色指令
export const roleDirective = {
  mounted(el, binding) {
    const { value, modifiers } = binding
    const userStore = useUserStore()

    if (!value) {
      console.warn('v-role 指令需要提供角色值')
      return
    }

    let hasRole = false

    if (typeof value === 'string') {
      // 单个角色
      hasRole = userStore.hasRole(value)
    } else if (Array.isArray(value)) {
      // 多个角色
      if (modifiers.all) {
        // 需要所有角色
        hasRole = value.every(role => userStore.hasRole(role))
      } else {
        // 只需要任意一个角色
        hasRole = value.some(role => userStore.hasRole(role))
      }
    } else {
      console.warn('v-role 指令的值必须是字符串或数组')
      return
    }

    if (!hasRole) {
      el.style.display = 'none'
    } else {
      el.style.display = ''
    }
  },
  updated(el, binding) {
    // 重新执行mounted的逻辑
    roleDirective.mounted(el, binding)
  }
}