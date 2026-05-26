import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '../api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const role = ref(localStorage.getItem('role') || '')
  const id = ref(Number(localStorage.getItem('userId')) || 0)
  const hasStore = ref(localStorage.getItem('hasStore') === 'true')
  const storeStatus = ref(Number(localStorage.getItem('storeStatus')) || 0)
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'ROLE_ADMIN')
  const isStore = computed(() => isAdmin.value || hasStore.value || role.value === 'ROLE_STORE')
  
  function setUser(userToken: string, userUsername: string, userRole: string, userNickname?: string, userId?: number, userHasStore?: boolean, userStoreStatus?: number) {
    token.value = userToken
    username.value = userUsername
    role.value = userRole
    if (userNickname) {
      nickname.value = userNickname
      localStorage.setItem('nickname', userNickname)
    }
    if (userId) {
      id.value = userId
      localStorage.setItem('userId', String(userId))
    }
    if (userHasStore !== undefined) {
      hasStore.value = Boolean(userHasStore)
      localStorage.setItem('hasStore', String(userHasStore))
    }
    if (userStoreStatus !== undefined) {
      storeStatus.value = Number(userStoreStatus)
      localStorage.setItem('storeStatus', String(userStoreStatus))
    }
    localStorage.setItem('token', userToken)
    localStorage.setItem('username', userUsername)
    localStorage.setItem('role', userRole)
  }

  function setUserInfo(user: User) {
    username.value = user.username
    nickname.value = user.nickname
    role.value = user.role
    id.value = user.id
    localStorage.setItem('username', user.username)
    localStorage.setItem('nickname', user.nickname)
    localStorage.setItem('role', user.role)
    localStorage.setItem('userId', String(user.id))
  }

  function logout() {
    token.value = ''
    username.value = ''
    nickname.value = ''
    role.value = ''
    id.value = 0
    hasStore.value = false
    storeStatus.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('nickname')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
    localStorage.removeItem('hasStore')
    localStorage.removeItem('storeStatus')
  }

  return {
    token,
    username,
    nickname,
    role,
    id,
    hasStore,
    storeStatus,
    isLoggedIn,
    isAdmin,
    isStore,
    setUser,
    setUserInfo,
    logout
  }
})
