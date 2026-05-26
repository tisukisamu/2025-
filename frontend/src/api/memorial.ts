import request from '../utils/request'
import type { Result, PageResponse, PageRequest } from '../types'
import type { MemorialRequest, MemorialResponse } from '../types/memorial'

export function getPublicMemorials(params: PageRequest): Promise<Result<PageResponse<MemorialResponse>>> {
  return request({
    url: '/memorials/public',
    method: 'get',
    params
  })
}

export function getPetMemorials(petId: number): Promise<Result<MemorialResponse[]>> {
  return request({
    url: `/memorials/pet/${petId}`,
    method: 'get'
  })
}

export function getMemorialById(id: number): Promise<Result<MemorialResponse>> {
  return request({
    url: `/memorials/${id}`,
    method: 'get'
  })
}

export function createMemorial(data: MemorialRequest): Promise<Result<MemorialResponse>> {
  return request({
    url: '/memorials',
    method: 'post',
    data
  })
}

export function updateMemorial(id: number, data: MemorialRequest): Promise<Result<MemorialResponse>> {
  return request({
    url: `/memorials/${id}`,
    method: 'put',
    data
  })
}

export function deleteMemorial(id: number): Promise<Result<void>> {
  return request({
    url: `/memorials/${id}`,
    method: 'delete'
  })
}
