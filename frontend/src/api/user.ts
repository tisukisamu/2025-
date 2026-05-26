import request from '../utils/request'
import type { Result, PageResponse, PageRequest } from '../types'
import type { User } from '../types/user'

export function getUserList(): Promise<Result<User[]>> {
  return request({
    url: '/users',
    method: 'get'
  })
}

export function getUserById(id: number): Promise<Result<User>> {
  return request({
    url: `/users/${id}`,
    method: 'get'
  })
}

export function createUser(data: Partial<User>): Promise<Result<User>> {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

export function updateUser(id: number, data: Partial<User>): Promise<Result<User>> {
  return request({
    url: `/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id: number): Promise<Result<void>> {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}
