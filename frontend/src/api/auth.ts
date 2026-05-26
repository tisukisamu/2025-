import request from '../utils/request'
import type { Result, PageResponse } from '../types'
import type { User, LoginRequest, RegisterRequest, AuthResponse } from '../types/user'

export function login(data: LoginRequest): Promise<Result<AuthResponse>> {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data: RegisterRequest): Promise<Result<AuthResponse>> {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function getCurrentUser(): Promise<Result<User>> {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

export function logout(): Promise<Result<void>> {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
