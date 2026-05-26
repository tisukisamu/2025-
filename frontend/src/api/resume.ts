import { get, post, put, del } from './request'
import type { Resume, ResumeDTO } from '@/types/resume'
import type { ApiResponse } from '@/types/common'

export const resumeApi = {
  getMy: (): Promise<ApiResponse<Resume[]>> => {
    return get('/resumes')
  },

  getById: (id: number): Promise<ApiResponse<Resume>> => {
    return get(`/resumes/${id}`)
  },

  create: (data: ResumeDTO): Promise<ApiResponse<Resume>> => {
    return post('/resumes', data)
  },

  update: (id: number, data: ResumeDTO): Promise<ApiResponse<Resume>> => {
    return put(`/resumes/${id}`, data)
  },

  delete: (id: number): Promise<ApiResponse<void>> => {
    return del(`/resumes/${id}`)
  },
}
