import request from '../utils/request'
import type { User, UserRole, UserStatus, ApiResponse, DashboardStats } from '../types'

export function findAllUsers(): Promise<ApiResponse<User[]>> {
  return request({
    url: '/admin/users',
    method: 'get'
  })
}

export function updateUserStatus(id: number, status: UserStatus): Promise<ApiResponse<User>> {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function updateUserRole(id: number, role: UserRole): Promise<ApiResponse<User>> {
  return request({
    url: `/admin/users/${id}/role`,
    method: 'put',
    data: { role }
  })
}

export function deleteUser(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

export function getDashboardStats(): Promise<ApiResponse<DashboardStats>> {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}
