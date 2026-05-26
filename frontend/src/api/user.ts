import { get, put, del, post } from './request'
import type { User, UserDTO } from '@/types/user'
import type { ApiResponse } from '@/types/common'

export const getUsers = (): Promise<ApiResponse<User[]>> => {
  return get('/users')
}

export const getUserById = (id: number): Promise<ApiResponse<User>> => {
  return get(`/users/${id}`)
}

export const createUser = (data: UserDTO): Promise<ApiResponse<User>> => {
  return post('/users', data)
}

export const updateUser = (id: number, data: UserDTO): Promise<ApiResponse<User>> => {
  return put(`/users/${id}`, data)
}

export const updateMyProfile = (data: UserDTO): Promise<ApiResponse<User>> => {
  return put('/users/me/profile', data)
}

export const deleteUserById = (id: number): Promise<ApiResponse<void>> => {
  return del(`/users/${id}`)
}

export const userApi = {
  getList: getUsers,
  getById: getUserById,
  create: createUser,
  update: updateUser,
  updateMyProfile,
  delete: deleteUserById,
}
