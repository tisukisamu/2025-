import { get, post } from '@/utils/request'
import type { ApiResponse, AuthResponse, LoginRequest, RegisterRequest } from '@/types'

export const authApi = {
  login(data: LoginRequest): Promise<ApiResponse<AuthResponse>> {
    return post('/auth/login', data)
  },

  register(data: RegisterRequest): Promise<ApiResponse<AuthResponse>> {
    return post('/auth/register', data)
  },

  logout(): Promise<ApiResponse<void>> {
    return post('/auth/logout')
  },

  getCurrentUser(): Promise<ApiResponse<AuthResponse>> {
    return get('/auth/me')
  }
}

export default authApi
