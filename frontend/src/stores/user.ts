import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getCurrentUser } from '../api/auth'
import { message } from 'ant-design-vue'
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<AuthResponse | null>(null)
  const loading = ref<boolean>(false)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isActive = computed(() => userInfo.value?.status === 'ACTIVE')
  const username = computed(() => userInfo.value?.username || '')
  const userRole = computed(() => userInfo.value?.role || '')

  const setToken = (newToken: string): void => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = (): void => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  const setUserInfo = (info: AuthResponse): void => {
    userInfo.value = info
  }

  const loginAction = async (credentials: LoginRequest): Promise<boolean> => {
    loading.value = true
    try {
      const res = await login(credentials)
      if (res.code === 200) {
        const { token: jwtToken, ...info } = res.data
        setToken(jwtToken)
        setUserInfo(res.data)
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
      if (res.code === 200) {
        const { token: jwtToken, ...info } = res.data
        setToken(jwtToken)
        setUserInfo(res.data)
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
      if (res.code === 200) {
        setUserInfo(res.data)
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

  const checkPermission = (requiredRole?: 'ADMIN' | 'USER'): boolean => {
    if (!isLoggedIn.value) return false
    if (!isActive.value) return false
    if (requiredRole === 'ADMIN') {
      return isAdmin.value
    }
    return true
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
    isActive,
    username,
    userRole,
    setToken,
    clearToken,
    setUserInfo,
    loginAction,
    registerAction,
    fetchUserInfo,
    logout,
    checkPermission,
    init
  }
})
