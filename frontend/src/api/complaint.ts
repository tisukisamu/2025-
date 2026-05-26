import { get, post, put } from './request'
import type { ComplaintSuggestion, ComplaintSuggestionDTO } from '@/types/complaint'
import type { ApiResponse } from '@/types/common'

export const complaintApi = {
  create: (data: ComplaintSuggestionDTO): Promise<ApiResponse<ComplaintSuggestion>> => {
    return post('/complaints', data)
  },

  getMy: (): Promise<ApiResponse<ComplaintSuggestion[]>> => {
    return get('/complaints')
  },

  getAll: (): Promise<ApiResponse<ComplaintSuggestion[]>> => {
    return get('/admin/complaints')
  },

  handle: (id: number, result: string, status: string): Promise<ApiResponse<ComplaintSuggestion>> => {
    return put(`/admin/complaints/${id}/handle?result=${encodeURIComponent(result)}&status=${status}`)
  },
}
