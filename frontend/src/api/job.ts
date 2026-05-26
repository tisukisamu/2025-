import { get, post, put, del } from './request'
import type { Job, JobDTO, JobSearchParams } from '@/types/job'
import type { ApiResponse, PageResponse } from '@/types/common'

export const jobApi = {
  getAll: (page = 0, size = 10): Promise<ApiResponse<PageResponse<Job>>> => {
    return get(`/jobs?page=${page}&size=${size}`)
  },

  getById: (id: number): Promise<ApiResponse<Job>> => {
    return get(`/jobs/${id}`)
  },

  search: (params: JobSearchParams): Promise<ApiResponse<PageResponse<Job>>> => {
    const query = new URLSearchParams()
    if (params.title) query.append('title', params.title)
    if (params.location) query.append('location', params.location)
    if (params.education) query.append('education', params.education)
    if (params.experience) query.append('experience', params.experience)
    query.append('page', String(params.page || 0))
    query.append('size', String(params.size || 10))
    return get(`/jobs/search?${query.toString()}`)
  },

  getByCompany: (companyId: number): Promise<ApiResponse<Job[]>> => {
    return get(`/jobs/company/${companyId}`)
  },

  getMy: (): Promise<ApiResponse<Job[]>> => {
    return get('/jobs/my')
  },

  create: (data: JobDTO): Promise<ApiResponse<Job>> => {
    return post('/jobs', data)
  },

  update: (id: number, data: JobDTO): Promise<ApiResponse<Job>> => {
    return put(`/jobs/${id}`, data)
  },

  close: (id: number): Promise<ApiResponse<void>> => {
    return put(`/jobs/${id}/close`)
  },

  delete: (id: number): Promise<ApiResponse<void>> => {
    return del(`/jobs/${id}`)
  },
}
