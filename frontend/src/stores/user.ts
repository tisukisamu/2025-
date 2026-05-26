import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'
import { authApi, userApi } from '@/api'
import type { LoginRequest, UserCreateRequest } from '@/types/request'
import router from '@/router'
import { normalizeMediaUrl } from '@/utils/media'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(null)

  const normalizeAvatarUrl = (avatar?: string) => {
    return normalizeMediaUrl(avatar)
  }

  const normalizeUser = (rawUser: User | null) => {
    if (!rawUser) return rawUser
    return {
      ...rawUser,
      avatar: normalizeAvatarUrl(rawUser.avatar)
    }
  }

  const saveUserToStorage = (currentUser: User | null) => {
    if (!currentUser) {
      localStorage.removeItem('user')
      return
    }
    localStorage.setItem('user', JSON.stringify(currentUser))
  }

  const restoreUserFromStorage = () => {
    const raw = localStorage.getItem('user')
    if (!raw) return null
    try {
      return JSON.parse(raw) as User
    } catch {
      localStorage.removeItem('user')
      return null
    }
  }

  user.value = normalizeUser(restoreUserFromStorage())

  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role?.roleCode || '')
  const permissions = computed(() => user.value?.permissions || [])

  const login = async (data: LoginRequest) => {
    const res = await authApi.login(data)
    token.value = res.data.token
    user.value = normalizeUser(res.data.user)
    localStorage.setItem('token', res.data.token)
    saveUserToStorage(user.value)
    return res
  }

  const register = async (data: UserCreateRequest) => {
    return authApi.register(data)
  }

  const logout = async () => {
    try {
      await authApi.logout()
    } finally {
      token.value = ''
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      router.push('/login')
    }
  }

  const fetchUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await userApi.getInfo()
      user.value = normalizeUser(res.data)
      saveUserToStorage(user.value)
    } catch (error) {
      token.value = ''
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }

  const hasPermission = (permission: string) => {
    return permissions.value.includes(permission)
  }

  const hasRole = (role: string) => {
    return userRole.value === role
  }

  return {
    token,
    user,
    isLoggedIn,
    userRole,
    permissions,
    login,
    register,
    logout,
    fetchUserInfo,
    hasPermission,
    hasRole
  }
})
