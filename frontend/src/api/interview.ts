import { get, post, put } from './request'
import type { Interview, InterviewDTO } from '@/types/interview'
import type { ApiResponse } from '@/types/common'

export const interviewApi = {
  getByApplication: (applicationId: number): Promise<ApiResponse<Interview[]>> => {
    return get(`/interviews/application/${applicationId}`)
  },

  getById: (id: number): Promise<ApiResponse<Interview>> => {
    return get(`/interviews/${id}`)
  },

  create: (data: InterviewDTO): Promise<ApiResponse<Interview>> => {
    return post('/interviews', data)
  },

  update: (id: number, data: InterviewDTO): Promise<ApiResponse<Interview>> => {
    return put(`/interviews/${id}`, data)
  },

  updateResult: (id: number, result: string, feedback?: string): Promise<ApiResponse<void>> => {
    const query = feedback 
      ? `/interviews/${id}/result?result=${result}&feedback=${encodeURIComponent(feedback)}`
      : `/interviews/${id}/result?result=${result}`
    return put(query)
  },
}
