import { useUserStore } from '../stores/user'

// 权限指令 v-permission="'ADMIN'"
export const permission = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const requiredRole = binding.value
    
    if (!userStore.checkPermission(requiredRole)) {
      el.parentNode?.removeChild(el)
    }
  }
}

// 角色指令 v-role="['ADMIN', 'USER']"
export const role = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const roles = Array.isArray(binding.value) ? binding.value : [binding.value]
    
    if (!roles.includes(userStore.userRole)) {
      el.parentNode?.removeChild(el)
    }
  }
}

// 登录状态指令 v-auth
export const auth = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const requireAuth = binding.value !== false
    
    if (requireAuth && !userStore.isLoggedIn) {
      el.parentNode?.removeChild(el)
    }
  }
}
