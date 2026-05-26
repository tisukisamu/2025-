import { get, post, put } from './request'
import type { Company, CompanyDTO } from '@/types/company'
import type { ApiResponse } from '@/types/common'

export const companyApi = {
  getAll: (): Promise<ApiResponse<Company[]>> => {
    return get('/companies')
  },

  getById: (id: number): Promise<ApiResponse<Company>> => {
    return get(`/companies/${id}`)
  },

  getMy: (): Promise<ApiResponse<Company>> => {
    return get('/companies/my')
  },

  create: (data: CompanyDTO): Promise<ApiResponse<Company>> => {
    return post('/companies', data)
  },

  update: (id: number, data: CompanyDTO): Promise<ApiResponse<Company>> => {
    return put(`/companies/${id}`, data)
  },
}
