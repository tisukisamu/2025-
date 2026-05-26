import request from '../utils/request'
import type { Result, PageResponse, PageRequest } from '../types'
import type { Pet, PetRequest, PetResponse } from '../types/pet'

export function getPetList(): Promise<Result<PetResponse[]>> {
  return request({
    url: '/pets',
    method: 'get'
  })
}

export function getPetPage(params: PageRequest): Promise<Result<PageResponse<PetResponse>>> {
  return request({
    url: '/pets/page',
    method: 'get',
    params
  })
}

export function getPetById(id: number): Promise<Result<PetResponse>> {
  return request({
    url: `/pets/${id}`,
    method: 'get'
  })
}

export function createPet(data: PetRequest): Promise<Result<PetResponse>> {
  return request({
    url: '/pets',
    method: 'post',
    data
  })
}

export function updatePet(id: number, data: PetRequest): Promise<Result<PetResponse>> {
  return request({
    url: `/pets/${id}`,
    method: 'put',
    data
  })
}

export function deletePet(id: number): Promise<Result<void>> {
  return request({
    url: `/pets/${id}`,
    method: 'delete'
  })
}
