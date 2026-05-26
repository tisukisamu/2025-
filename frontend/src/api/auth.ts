import request from '../utils/request'
import type { LoginRequest, RegisterRequest, AuthResponse, ApiResponse } from '../types'

export function login(data: LoginRequest): Promise<ApiResponse<AuthResponse>> {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data: RegisterRequest): Promise<ApiResponse<AuthResponse>> {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function getCurrentUser(): Promise<ApiResponse<AuthResponse>> {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

export function logout(): Promise<ApiResponse<void>> {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function refreshToken(): Promise<ApiResponse<AuthResponse>> {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}
