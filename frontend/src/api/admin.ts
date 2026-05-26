import request from '../utils/request'
import type { ApiResponse, User } from '../types'

export function getAllUsers(): Promise<ApiResponse<User[]>> {
  return request({
    url: '/admin/users',
    method: 'get'
  })
}

export function updateUserStatus(id: number, status: 'ACTIVE' | 'DISABLED'): Promise<ApiResponse<User>> {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function updateUserRole(id: number, role: 'ADMIN' | 'USER'): Promise<ApiResponse<User>> {
  return request({
    url: `/admin/users/${id}/role`,
    method: 'put',
    params: { role }
  })
}

export function deleteUser(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

export interface DashboardStats {
  totalUsers: number
  activeUsers: number
  totalHabits: number
  totalChecks: number
}

export function getDashboardStats(): Promise<ApiResponse<DashboardStats>> {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}
