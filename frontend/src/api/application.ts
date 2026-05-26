import { get, post, put } from './request'
import type { Application, ApplicationDTO } from '@/types/application'
import type { ApiResponse } from '@/types/common'

export const applicationApi = {
  getMy: (): Promise<ApiResponse<Application[]>> => {
    return get('/applications')
  },

  getById: (id: number): Promise<ApiResponse<Application>> => {
    return get(`/applications/${id}`)
  },

  getReceived: (): Promise<ApiResponse<Application[]>> => {
    return get('/applications/received')
  },

  create: (data: ApplicationDTO): Promise<ApiResponse<Application>> => {
    return post('/applications', data)
  },

  updateStatus: (id: number, status: string): Promise<ApiResponse<void>> => {
    return put(`/applications/${id}/status?status=${status}`)
  },
}
