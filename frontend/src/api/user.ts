import request from '../utils/request'
import type { ApiResponse, User } from '../types'

export function getUsers(): Promise<ApiResponse<User[]>> {
  return request({
    url: '/admin/users',
    method: 'get'
  })
}

export function getUserProfile(): Promise<ApiResponse<User>> {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

export function updateUserProfile(data: Partial<User>): Promise<ApiResponse<User>> {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

export function updateUserStatus(id: number, status: string): Promise<ApiResponse<User>> {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function updateUserRole(id: number, role: string): Promise<ApiResponse<User>> {
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

export function updateAvatar(avatar: string): Promise<ApiResponse<User>> {
  return request({
    url: '/user/avatar',
    method: 'put',
    data: { avatar }
  })
}
