import request from '../utils/request'
import type { User, ApiResponse, PageResponse, PageRequest } from '../types'

export function getUsers(params?: PageRequest): Promise<ApiResponse<PageResponse<User>>> {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

export function getUserById(id: number): Promise<ApiResponse<User>> {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

export function createUser(data: Partial<User>): Promise<ApiResponse<User>> {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

export function updateUser(id: number, data: Partial<User>): Promise<ApiResponse<User>> {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id: number): Promise<ApiResponse<void>> {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

export function updateProfile(data: Partial<User>): Promise<ApiResponse<User>> {
  return request({
    url: '/users/profile',
    method: 'put',
    data
  })
}

export function uploadAvatar(file: File): Promise<ApiResponse<{ url: string }>> {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/users/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
