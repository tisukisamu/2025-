import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'
import type { User, AuthResponse, LoginRequest, RegisterRequest } from '@/types'
import { message } from 'ant-design-vue'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<User | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isActive = computed(() => userInfo.value?.status === 'ACTIVE')
  const username = computed(() => userInfo.value?.username || '')
  const avatar = computed(() => userInfo.value?.avatar || '')
  const creditScore = computed(() => userInfo.value?.creditScore || 100)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const setUserInfo = (info: User) => {
    userInfo.value = info
    localStorage.setItem('user', JSON.stringify(info))
  }

  const loginAction = async (credentials: LoginRequest): Promise<boolean> => {
    loading.value = true
    try {
      const res = await authApi.login(credentials)
      const { token: jwtToken, ...info } = res.data
      setToken(jwtToken)
      setUserInfo(info as User)
      message.success('登录成功')
      return true
    } catch {
      return false
    } finally {
      loading.value = false
    }
  }

  const registerAction = async (data: RegisterRequest): Promise<boolean> => {
    loading.value = true
    try {
      const res = await authApi.register(data)
      const { token: jwtToken, ...info } = res.data
      setToken(jwtToken)
      setUserInfo(info as User)
      message.success('注册成功')
      return true
    } catch {
      return false
    } finally {
      loading.value = false
    }
  }

  const fetchUserInfo = async (): Promise<boolean> => {
    if (!token.value) return false
    try {
      const res = await authApi.getCurrentUser()
      setUserInfo(res.data as User)
      return true
    } catch {
      clearToken()
      return false
    }
  }

  const logout = () => {
    clearToken()
    message.success('已退出登录')
  }

  const checkPermission = (requiredRole?: string): boolean => {
    if (!isLoggedIn.value) return false
    if (!isActive.value) return false
    if (requiredRole === 'ADMIN') {
      return isAdmin.value
    }
    return true
  }

  const init = async () => {
    if (token.value) {
      const savedUser = localStorage.getItem('user')
      if (savedUser) {
        userInfo.value = JSON.parse(savedUser)
      }
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
    avatar,
    creditScore,
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
