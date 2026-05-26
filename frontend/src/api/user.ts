import { request } from '@/utils/request'
import type { LoginRequest, UserCreateRequest, UserUpdateRequest, PasswordChangeRequest } from '@/types/request'
import type { LoginResponse } from '@/types/response'
import type { User } from '@/types'
import type { PageResult } from '@/types/response'

export const authApi = {
  login(data: LoginRequest) {
    return request.post<LoginResponse>('/auth/login', data)
  },

  register(data: UserCreateRequest) {
    return request.post<User>('/auth/register', data)
  },

  logout() {
    return request.post<void>('/auth/logout')
  }
}

export const userApi = {
  getInfo() {
    return request.get<User>('/user/info')
  },

  updateProfile(data: UserUpdateRequest) {
    return request.put<User>('/user/profile', data)
  },

  changePassword(data: PasswordChangeRequest) {
    return request.put<void>('/user/password', data)
  },

  getList(page: number = 0, size: number = 10) {
    return request.get<PageResult<User>>('/user/list', {
      params: { page, size }
    })
  },

  updateStatus(id: number, status: number) {
    return request.put<User>(`/user/${id}/status`, null, {
      params: { status }
    })
  },

  update(id: number, data: any) {
    return request.put<User>(`/user/${id}`, data)
  }
}
