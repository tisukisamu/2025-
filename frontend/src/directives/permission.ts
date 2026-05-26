import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '../stores/user'

type RoleType = 'ADMIN' | 'USER'

interface PermissionHTMLElement extends HTMLElement {
  parentNode: Node & ParentNode | null
}

export const permission: Directive<PermissionHTMLElement, RoleType> = {
  mounted(el: PermissionHTMLElement, binding: DirectiveBinding<RoleType>) {
    const userStore = useUserStore()
    const requiredRole = binding.value
    
    if (!userStore.checkPermission(requiredRole)) {
      el.parentNode?.removeChild(el)
    }
  }
}

export const role: Directive<PermissionHTMLElement, RoleType | RoleType[]> = {
  mounted(el: PermissionHTMLElement, binding: DirectiveBinding<RoleType | RoleType[]>) {
    const userStore = useUserStore()
    const roles: RoleType[] = Array.isArray(binding.value) ? binding.value : [binding.value]
    
    if (!roles.includes(userStore.userRole as RoleType)) {
      el.parentNode?.removeChild(el)
    }
  }
}

export const auth: Directive<PermissionHTMLElement, boolean | undefined> = {
  mounted(el: PermissionHTMLElement, binding: DirectiveBinding<boolean | undefined>) {
    const userStore = useUserStore()
    const requireAuth = binding.value !== false
    
    if (requireAuth && !userStore.isLoggedIn) {
      el.parentNode?.removeChild(el)
    }
  }
}

export default {
  permission,
  role,
  auth
}
