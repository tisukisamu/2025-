import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getCurrentUser } from '../api/auth'
import { message } from 'ant-design-vue'
import type { User, LoginRequest, RegisterRequest, AuthResponse } from '../types'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)
  const loading = ref<boolean>(false)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isTeacher = computed(() => userInfo.value?.role === 'TEACHER')
  const isStudent = computed(() => userInfo.value?.role === 'STUDENT')
  const isActive = computed(() => userInfo.value?.status === 'ACTIVE')
  const username = computed(() => userInfo.value?.username || '')
  const userRole = computed(() => userInfo.value?.role || '')
  const realName = computed(() => userInfo.value?.realName || '')

  const setToken = (newToken: string): void => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = (): void => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  const setUserInfo = (info: User): void => {
    userInfo.value = info
  }

  const setAvatar = (avatarUrl: string): void => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, avatar: avatarUrl }
    }
  }

  const loginAction = async (credentials: LoginRequest): Promise<boolean> => {
    loading.value = true
    try {
      const res = await login(credentials)
      if (res.code === 200 && res.data) {
        const { token: jwtToken, user } = res.data
        setToken(jwtToken)
        setUserInfo(user)
        message.success('登录成功')
        return true
      }
      return false
    } catch (error) {
      return false
    } finally {
      loading.value = false
    }
  }

  const registerAction = async (data: RegisterRequest): Promise<boolean> => {
    loading.value = true
    try {
      const res = await register(data)
      if (res.code === 200 && res.data) {
        const { token: jwtToken, user } = res.data
        setToken(jwtToken)
        setUserInfo(user)
        message.success('注册成功')
        return true
      }
      return false
    } catch (error) {
      return false
    } finally {
      loading.value = false
    }
  }

  const fetchUserInfo = async (): Promise<boolean> => {
    if (!token.value) return false
    try {
      const res = await getCurrentUser()
      if (res.code === 200 && res.data) {
        setUserInfo(res.data.user)
        return true
      }
      return false
    } catch (error) {
      clearToken()
      return false
    }
  }

  const logout = (): void => {
    clearToken()
    message.success('已退出登录')
  }

  const checkPermission = (requiredRole: string): boolean => {
    if (!isLoggedIn.value) return false
    if (!isActive.value) return false
    if (requiredRole === 'ADMIN') {
      return isAdmin.value
    }
    if (requiredRole === 'TEACHER') {
      return isTeacher.value || isAdmin.value
    }
    return true
  }

  const hasRole = (role: string): boolean => {
    return userInfo.value?.role === role
  }

  const init = async (): Promise<void> => {
    if (token.value) {
      await fetchUserInfo()
    }
  }

  return {
    token,
    userInfo,
    loading,
    isLoggedIn,
    isAdmin,
    isTeacher,
    isStudent,
    isActive,
    username,
    userRole,
    realName,
    setToken,
    clearToken,
    setUserInfo,
    setAvatar,
    loginAction,
    registerAction,
    fetchUserInfo,
    logout,
    checkPermission,
    hasRole,
    init
  }
})
