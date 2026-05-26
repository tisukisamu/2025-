import { get, post } from './request'
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/types/user'
import type { ApiResponse } from '@/types/common'

export const login = (data: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
  return post('/auth/login', data)
}

export const register = (data: RegisterRequest): Promise<ApiResponse<AuthResponse>> => {
  return post('/auth/register', data)
}

export const getCurrentUser = (): Promise<ApiResponse<AuthResponse>> => {
  return get('/auth/me')
}

export const authApi = {
  login,
  register,
  getCurrentUser,
}
